/*
 * Copyright 2018 The Context Mapper Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.contextmapper.dsl.validation;

import static org.contextmapper.dsl.validation.ValidationMessages.AGGREGATE_OWNER_NOT_TEAM_OF_CONTEXT;
import static org.contextmapper.dsl.validation.ValidationMessages.BOUNDED_CONTEXT_OWNER_DOES_NOT_REALIZE_CONTEXT;
import static org.contextmapper.dsl.validation.ValidationMessages.ONLY_TEAMS_CAN_REALIZE_OTHER_BOUNDED_CONTEXT;
import static org.contextmapper.dsl.validation.ValidationMessages.OWNER_BC_IS_NOT_TEAM;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLModelObjectsResolvingHelper;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

public class TeamSemanticsValidator extends AbstractDeclarativeValidator {

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}

	@Check
	public void onlyTeamsCanRealizeBoundedContexts(final BoundedContext boundedContext) {
		if (!boundedContext.getRealizedBoundedContexts().isEmpty() && !BoundedContextType.TEAM.equals(boundedContext.getType()))
			error(String.format(ONLY_TEAMS_CAN_REALIZE_OTHER_BOUNDED_CONTEXT, boundedContext.getName()), boundedContext,
					ContextMappingDSLPackage.Literals.BOUNDED_CONTEXT__REALIZED_BOUNDED_CONTEXTS);
	}

	@Check
	public void checkThatAggregateOwnersAreTeams(final Aggregate aggregate) {
		if (aggregate.getOwner() == null)
			return;

		BoundedContext owner = aggregate.getOwner();
		if (owner.getType() == null || !BoundedContextType.TEAM.equals(owner.getType()))
			error(String.format(OWNER_BC_IS_NOT_TEAM, owner.getName()), aggregate, ContextMappingDSLPackage.Literals.AGGREGATE__OWNER);
	}

	@Check
	public void checkThatBoundedContextOwnersAreTeams(final BoundedContext boundedContext) {
		if (boundedContext.getOwner() == null)
			return;

		BoundedContext owner = boundedContext.getOwner();
		if (owner.getType() == null || !BoundedContextType.TEAM.equals(owner.getType()))
			error(String.format(OWNER_BC_IS_NOT_TEAM, owner.getName()), boundedContext, ContextMappingDSLPackage.Literals.BOUNDED_CONTEXT__OWNER);
	}

	@Check
	public void checkThatBoundedContextOwnerRealizesContext(final BoundedContext boundedContext) {
		BoundedContext owner = boundedContext.getOwner();
		if (owner == null || !isTeam(owner))
			return;

		// without any realizing team, the owner alone is fine
		Set<String> realizingTeams = getNamesOfRealizingTeams(boundedContext);
		if (!realizingTeams.isEmpty() && !realizingTeams.contains(owner.getName()))
			warning(String.format(BOUNDED_CONTEXT_OWNER_DOES_NOT_REALIZE_CONTEXT, owner.getName(), boundedContext.getName(), String.join(", ", realizingTeams)), boundedContext,
					ContextMappingDSLPackage.Literals.BOUNDED_CONTEXT__OWNER);
	}

	@Check
	public void checkThatAggregateOwnerIsTeamOfContext(final Aggregate aggregate) {
		BoundedContext owner = aggregate.getOwner();
		if (owner == null || !isTeam(owner))
			return;

		BoundedContext boundedContext = EcoreUtil2.getContainerOfType(aggregate, BoundedContext.class);
		if (boundedContext == null)
			return;

		// the teams of a context are its owner and the teams realizing it; if none are modeled, there is nothing to check
		Set<String> teamsOfContext = getNamesOfRealizingTeams(boundedContext);
		if (boundedContext.getOwner() != null)
			teamsOfContext.add(boundedContext.getOwner().getName());
		if (!teamsOfContext.isEmpty() && !teamsOfContext.contains(owner.getName()))
			warning(String.format(AGGREGATE_OWNER_NOT_TEAM_OF_CONTEXT, owner.getName(), boundedContext.getName()), aggregate, ContextMappingDSLPackage.Literals.AGGREGATE__OWNER);
	}

	private boolean isTeam(BoundedContext boundedContext) {
		return BoundedContextType.TEAM.equals(boundedContext.getType());
	}

	private Set<String> getNamesOfRealizingTeams(BoundedContext boundedContext) {
		EObject root = EcoreUtil.getRootContainer(boundedContext);
		if (!(root instanceof ContextMappingModel) || root.eResource() == null)
			return new TreeSet<>();

		return new CMLModelObjectsResolvingHelper((ContextMappingModel) root).resolveAllObjectsOfType(BoundedContext.class).stream()
				.filter(team -> isTeam(team) && team.getRealizedBoundedContexts().stream().anyMatch(realized -> boundedContext.getName().equals(realized.getName())))
				.map(BoundedContext::getName).collect(Collectors.toCollection(TreeSet::new));
	}

}

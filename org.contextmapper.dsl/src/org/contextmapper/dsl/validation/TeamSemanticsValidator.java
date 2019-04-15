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

import static org.contextmapper.dsl.validation.ValidationMessages.ONLY_TEAMS_CAN_REALIZE_OTHER_BOUNDED_CONTEXT;
import static org.contextmapper.dsl.validation.ValidationMessages.OWNER_BC_IS_NOT_TEAM;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage;
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

}

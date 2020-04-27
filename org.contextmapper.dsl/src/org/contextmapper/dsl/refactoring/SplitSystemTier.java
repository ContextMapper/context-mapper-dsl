/*
 * Copyright 2020 The Context Mapper Project Team
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
package org.contextmapper.dsl.refactoring;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.DownstreamRole;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamRole;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class SplitSystemTier extends AbstractRefactoring implements Refactoring {

	private String systemExistingBoundedContextName;
	private String systemNewBoundedContextName;
	private String newTierName;
	private SplitBoundedContextRelationshipType relationshipType = SplitBoundedContextRelationshipType.EXISTING_CONTEXT_BECOMES_DOWNSTREAM;
	private ContextSplittingIntegrationType integrationType = ContextSplittingIntegrationType.CONFORMIST;
	private boolean copyDomainModel = false;
	private String newTierImplementationTechnology = "";
	private String newRelationshipImplementationTechnology = "";

	private RefactoringHelper helper;

	public SplitSystemTier(String systemExistingBoundedContextName, String systemNewBoundedContextName, String newTierName) {
		this.systemExistingBoundedContextName = systemExistingBoundedContextName;
		this.systemNewBoundedContextName = systemNewBoundedContextName;
		this.newTierName = newTierName;
		this.helper = new RefactoringHelper(this);
	}

	@Override
	protected void doRefactor() {
		checkPreconditions();

		BoundedContext systemContext = getAllBoundedContexts().stream().filter(bc -> bc.getName().equals(systemExistingBoundedContextName)).findFirst().get();
		systemContext.setName(systemNewBoundedContextName);

		BoundedContext newTierContext = createNewTierBC(systemContext);
		newTierContext.setName(newTierName);
		newTierContext.setType(BoundedContextType.SYSTEM);
		newTierContext.setImplementationTechnology(newTierImplementationTechnology);

		addElementToEList(model.getBoundedContexts(), newTierContext);
		createUpstreamDownstreamRelationship(systemContext, newTierContext);

		markResourceChanged(rootResource);
		saveResources();
	}

	private void createUpstreamDownstreamRelationship(BoundedContext existingContext, BoundedContext newContext) {
		UpstreamDownstreamRelationship relationship = ContextMappingDSLFactory.eINSTANCE.createUpstreamDownstreamRelationship();
		if (this.relationshipType == SplitBoundedContextRelationshipType.EXISTING_CONTEXT_BECOMES_DOWNSTREAM) {
			relationship.setDownstream(existingContext);
			relationship.setUpstream(newContext);
		} else {
			relationship.setDownstream(newContext);
			relationship.setUpstream(existingContext);
		}
		if (integrationType == ContextSplittingIntegrationType.CONFORMIST) {
			relationship.getDownstreamRoles().add(DownstreamRole.CONFORMIST);
		} else {
			relationship.getDownstreamRoles().add(DownstreamRole.ANTICORRUPTION_LAYER);
		}
		relationship.getUpstreamRoles().add(UpstreamRole.PUBLISHED_LANGUAGE);
		relationship.setImplementationTechnology(newRelationshipImplementationTechnology);
		ContextMap map = createOrGetContextMap();
		addElementToEList(map.getBoundedContexts(), newContext);
		addElementToEList(map.getBoundedContexts(), existingContext);
		addElementToEList(map.getRelationships(), relationship);
	}

	private BoundedContext createNewTierBC(BoundedContext existingTier) {
		if (this.copyDomainModel) {
			BoundedContext copiedContext = EcoreUtil.copy(existingTier);
			helper.adjustAggregateAndModuleNames(copiedContext, "_" + this.newTierName);
			return copiedContext;
		}
		return ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
	}

	private ContextMap createOrGetContextMap() {
		if (model.getMap() != null)
			return model.getMap();

		ContextMap newContextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		model.setMap(newContextMap);
		return newContextMap;
	}

	private void checkPreconditions() {
		Optional<BoundedContext> optSystemBC = getAllBoundedContexts().stream().filter(bc -> bc.getName().equals(systemExistingBoundedContextName)).findFirst();
		if (!optSystemBC.isPresent())
			throw new RefactoringInputException("A Bounded Context with the name '" + systemExistingBoundedContextName + "' does not exist!");
		BoundedContext systemBC = optSystemBC.get();
		if (systemBC.getType() != BoundedContextType.SYSTEM)
			throw new RefactoringInputException("The Bounded Context '" + systemExistingBoundedContextName + "' is not of the type FEATURE!");
		Set<String> allBCNames = getAllBoundedContexts().stream().map(bc -> bc.getName()).collect(Collectors.toSet());
		if (allBCNames.contains(systemNewBoundedContextName))
			throw new RefactoringInputException("A Bounded Context with the name '" + systemNewBoundedContextName + "' already exists in your model!");
		if (allBCNames.contains(newTierName))
			throw new RefactoringInputException("A Bounded Context with the name '" + newTierName + "' already exists in your model!");
	}

	public void setRelationshipType(SplitBoundedContextRelationshipType relationshipType) {
		this.relationshipType = relationshipType;
	}

	public void setIntegrationType(ContextSplittingIntegrationType integrationType) {
		this.integrationType = integrationType;
	}

	public void copyDomainModel(boolean copyDomainModel) {
		this.copyDomainModel = copyDomainModel;
	}

	public void setNewTierImplementationTechnology(String newTierImplementationTechnology) {
		this.newTierImplementationTechnology = newTierImplementationTechnology;
	}

	public void setNewRelationshipImplementationTechnology(String newRelationshipImplementationTechnology) {
		this.newRelationshipImplementationTechnology = newRelationshipImplementationTechnology;
	}

	public enum SplitBoundedContextRelationshipType {
		EXISTING_CONTEXT_BECOMES_UPSTREAM, EXISTING_CONTEXT_BECOMES_DOWNSTREAM
	}

}

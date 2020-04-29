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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.DownstreamRole;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.contextMappingDSL.SymmetricRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamRole;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.common.collect.Lists;

public class SplitSystemIntoSubsystems extends AbstractRefactoring implements Refactoring {

	private String systemExistingBoundedContextName;
	private String existingSubsystemName;
	private String newSubsystemName;
	private SplitBoundedContextRelationshipType relationshipType = SplitBoundedContextRelationshipType.NEW_CONTEXT_BECOMES_DOWNSTREAM;
	private ContextSplittingIntegrationType integrationType = ContextSplittingIntegrationType.CONFORMIST;
	private boolean copyDomainModel = false;
	private String newSubsystemImplementationTechnology = "";
	private String newRelationshipImplementationTechnology = "";

	private RefactoringHelper helper;

	public SplitSystemIntoSubsystems(String systemExistingBoundedContextName, String existingSubsystemName, String newSubsystemName) {
		this.systemExistingBoundedContextName = systemExistingBoundedContextName;
		this.existingSubsystemName = existingSubsystemName;
		this.newSubsystemName = newSubsystemName;
		this.helper = new RefactoringHelper(this);
	}

	@Override
	protected void doRefactor() {
		checkPreconditions();

		BoundedContext systemContext = getAllBoundedContexts().stream().filter(bc -> bc.getName().equals(systemExistingBoundedContextName)).findFirst().get();
		renameBoundedContext(systemContext.getName(), existingSubsystemName);

		BoundedContext newSubsystemContext = createNewSubsystemBC(systemContext);
		newSubsystemContext.setName(newSubsystemName);
		newSubsystemContext.setType(BoundedContextType.SYSTEM);
		newSubsystemContext.setImplementationTechnology(newSubsystemImplementationTechnology);

		addElementToEList(model.getBoundedContexts(), newSubsystemContext);
		createUpstreamDownstreamRelationship(systemContext, newSubsystemContext);

		markResourceChanged(rootResource);
		saveResources();
	}

	private void renameBoundedContext(String currentName, String newName) {
		List<BoundedContext> allInstances = getAllBoundedContexts().stream().filter(bc -> bc.getName().equals(systemExistingBoundedContextName)).collect(Collectors.toList());
		for (ContextMap contextMap : getAllContextMaps()) {
			allInstances.addAll(contextMap.getBoundedContexts().stream().filter(bc -> bc.getName().equals(systemExistingBoundedContextName)).collect(Collectors.toList()));
			allInstances.addAll(getAllRelationshipContextsByName(contextMap, currentName));
			markResourceChanged(contextMap);
		}
		for (BoundedContext bc : allInstances) {
			bc.setName(newName);
			markResourceChanged(bc);
		}
	}

	private List<BoundedContext> getAllRelationshipContextsByName(ContextMap map, String contextName) {
		List<BoundedContext> contexts = Lists.newLinkedList();
		for (Relationship rel : map.getRelationships()) {
			if (rel instanceof UpstreamDownstreamRelationship) {
				UpstreamDownstreamRelationship upDownRel = (UpstreamDownstreamRelationship) rel;
				if (upDownRel.getDownstream().getName().equals(contextName))
					contexts.add(upDownRel.getDownstream());
				if (upDownRel.getUpstream().getName().equals(contextName))
					contexts.add(upDownRel.getUpstream());
			} else if (rel instanceof SymmetricRelationship) {
				SymmetricRelationship symRel = (SymmetricRelationship) rel;
				if (symRel.getParticipant1().getName().equals(contextName))
					contexts.add(symRel.getParticipant1());
				if (symRel.getParticipant2().getName().equals(contextName))
					contexts.add(symRel.getParticipant2());
			}
		}
		return contexts;
	}

	private void createUpstreamDownstreamRelationship(BoundedContext existingContext, BoundedContext newContext) {
		UpstreamDownstreamRelationship relationship = ContextMappingDSLFactory.eINSTANCE.createUpstreamDownstreamRelationship();
		if (this.relationshipType == SplitBoundedContextRelationshipType.NEW_CONTEXT_BECOMES_DOWNSTREAM) {
			relationship.setDownstream(newContext);
			relationship.setUpstream(existingContext);
		} else {
			relationship.setDownstream(existingContext);
			relationship.setUpstream(newContext);
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

	private BoundedContext createNewSubsystemBC(BoundedContext existingSubsystem) {
		if (this.copyDomainModel) {
			BoundedContext copiedContext = EcoreUtil.copy(existingSubsystem);
			helper.adjustAggregateAndModuleNames(copiedContext, "_" + this.newSubsystemName);
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
		if (allBCNames.contains(existingSubsystemName))
			throw new RefactoringInputException("A Bounded Context with the name '" + existingSubsystemName + "' already exists in your model!");
		if (allBCNames.contains(newSubsystemName))
			throw new RefactoringInputException("A Bounded Context with the name '" + newSubsystemName + "' already exists in your model!");
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

	public void setNewSubsystemImplementationTechnology(String newSubsystemImplementationTechnology) {
		this.newSubsystemImplementationTechnology = newSubsystemImplementationTechnology;
	}

	public void setNewRelationshipImplementationTechnology(String newRelationshipImplementationTechnology) {
		this.newRelationshipImplementationTechnology = newRelationshipImplementationTechnology;
	}

	public enum SplitBoundedContextRelationshipType {
		NEW_CONTEXT_BECOMES_UPSTREAM("Upstream"), NEW_CONTEXT_BECOMES_DOWNSTREAM("Downstream");

		private String label;

		private SplitBoundedContextRelationshipType(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

		public static SplitBoundedContextRelationshipType byLabel(String label) {
			if (label != null && "Downstream".equals(label))
				return NEW_CONTEXT_BECOMES_DOWNSTREAM;
			return NEW_CONTEXT_BECOMES_UPSTREAM;
		}
	}

}

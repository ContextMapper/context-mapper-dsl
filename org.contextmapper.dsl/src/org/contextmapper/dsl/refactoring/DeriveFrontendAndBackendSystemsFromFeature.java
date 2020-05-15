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

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.DownstreamRole;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamRole;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class DeriveFrontendAndBackendSystemsFromFeature extends AbstractRefactoring implements SemanticCMLRefactoring {

	private String featureBoundedContextName;
	private ContextSplittingIntegrationType relationshipType;
	private boolean deriveViewModelInFrontend = true;
	private String frontendName;
	private String backendName;
	private String frontendImplTechnology;
	private String backendImplTechnology;
	private String relationshipImplTechnology;

	private RefactoringHelper helper;

	public DeriveFrontendAndBackendSystemsFromFeature(String featureBoundedContextName, ContextSplittingIntegrationType relationshipType) {
		this.featureBoundedContextName = featureBoundedContextName;
		this.relationshipType = relationshipType;
		this.frontendName = featureBoundedContextName + "Frontend";
		this.backendName = featureBoundedContextName + "Backend";
		this.helper = new RefactoringHelper(this);
	}

	@Override
	protected void doRefactor() {
		checkPreconditions();

		BoundedContext featureContext = getAllBoundedContexts().stream().filter(bc -> bc.getName().equals(featureBoundedContextName)).findFirst().get();

		BoundedContext backend = EcoreUtil.copy(featureContext);
		backend.setType(BoundedContextType.SYSTEM);
		backend.setName(backendName);
		backend.setImplementationTechnology(backendImplTechnology);
		addElementToEList(model.getBoundedContexts(), backend);
		helper.adjustAggregateAndModuleNames(backend, "Backend");

		BoundedContext frontend = EcoreUtil.copy(featureContext);
		frontend.setType(BoundedContextType.SYSTEM);
		frontend.setName(frontendName);
		frontend.setImplementationTechnology(frontendImplTechnology);
		addElementToEList(model.getBoundedContexts(), frontend);
		if (!deriveViewModelInFrontend) {
			frontend.getAggregates().clear();
			frontend.getModules().clear();
			addElementToEList(frontend.getAggregates(), createSampleViewModelAggregate());
		} else {
			helper.adjustAggregateAndModuleNames(frontend, "ViewModel");
		}

		ContextMap map = createOrGetContextMap();
		UpstreamDownstreamRelationship relationship = ContextMappingDSLFactory.eINSTANCE.createUpstreamDownstreamRelationship();
		relationship.setDownstream(frontend);
		relationship.setUpstream(backend);
		relationship.getUpstreamRoles().add(UpstreamRole.PUBLISHED_LANGUAGE);
		relationship.getDownstreamRoles().add(getDownstreamRole());
		relationship.setImplementationTechnology(relationshipImplTechnology);
		addElementsToEList(relationship.getUpstreamExposedAggregates(), helper.collectAggregates(backend));
		addElementToEList(map.getBoundedContexts(), frontend);
		addElementToEList(map.getBoundedContexts(), backend);
		addElementToEList(map.getRelationships(), relationship);
	}

	private Aggregate createSampleViewModelAggregate() {
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("ViewModel");
		aggregate.setComment("/* The view model of this frontend system has not been generated. " + System.lineSeparator()
				+ "	 * TODO: specify the view model now, using Aggregates, Entities, Value Objects, etc. */");
		return aggregate;
	}

	private ContextMap createOrGetContextMap() {
		if (model.getMap() != null)
			return model.getMap();

		ContextMap newContextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		model.setMap(newContextMap);
		return newContextMap;
	}

	private DownstreamRole getDownstreamRole() {
		if (this.relationshipType == ContextSplittingIntegrationType.ACL)
			return DownstreamRole.ANTICORRUPTION_LAYER;
		return DownstreamRole.CONFORMIST;
	}

	private void checkPreconditions() {
		Optional<BoundedContext> optFeatureBC = getAllBoundedContexts().stream().filter(bc -> bc.getName().equals(featureBoundedContextName)).findFirst();
		if (!optFeatureBC.isPresent())
			throw new RefactoringInputException("A Bounded Context with the name '" + featureBoundedContextName + "' does not exist!");
		BoundedContext featureBC = optFeatureBC.get();
		if (featureBC.getType() != BoundedContextType.FEATURE && featureBC.getType() != BoundedContextType.APPLICATION)
			throw new RefactoringInputException("The Bounded Context '" + featureBoundedContextName + "' is not of the type FEATURE!");
		Set<String> allBCNames = getAllBoundedContexts().stream().map(bc -> bc.getName()).collect(Collectors.toSet());
		if (allBCNames.contains(frontendName))
			throw new RefactoringInputException("A Bounded Context with the name '" + frontendName + "' already exists in your model!");
		if (allBCNames.contains(backendName))
			throw new RefactoringInputException("A Bounded Context with the name '" + backendName + "' already exists in your model!");
	}

	public void deriveViewModelInFronted(boolean derive) {
		this.deriveViewModelInFrontend = derive;
	}

	public void setFrontendName(String frontendName) {
		this.frontendName = frontendName;
	}

	public void setBackendName(String backendName) {
		this.backendName = backendName;
	}

	public void setFrontendImplementationTechnology(String technology) {
		this.frontendImplTechnology = technology;
	}

	public void setBackendImplementationTechnology(String technology) {
		this.backendImplTechnology = technology;
	}

	public void setRelationshipImplTechnology(String technology) {
		this.relationshipImplTechnology = technology;
	}

}

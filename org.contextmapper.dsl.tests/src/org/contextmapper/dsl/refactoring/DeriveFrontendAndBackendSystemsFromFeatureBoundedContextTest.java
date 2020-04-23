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

import static org.contextmapper.dsl.refactoring.DeriveFrontendAndBackendSystemsFromFeatureBoundedContext.FrontendBackendRelationshipType.ACL;
import static org.contextmapper.dsl.refactoring.DeriveFrontendAndBackendSystemsFromFeatureBoundedContext.FrontendBackendRelationshipType.CONFORMIST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.DownstreamRole;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamRole;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

public class DeriveFrontendAndBackendSystemsFromFeatureBoundedContextTest extends AbstractRefactoringTest {

	@ParameterizedTest
	@ValueSource(strings = { "SomeContextThatDoesNotExist" })
	@NullSource
	public void canCheckIfBoundedContextNameExists(String inputBoundedContextName) throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("derive-frontend-backend-from-feature-test-1-input.cml");
		DeriveFrontendAndBackendSystemsFromFeatureBoundedContext ar = new DeriveFrontendAndBackendSystemsFromFeatureBoundedContext(inputBoundedContextName, CONFORMIST);

		// when, then
		assertThrows(RefactoringInputException.class, () -> {
			ar.doRefactor(input);
		});
	}

	@Test
	public void canCheckThatInputBCIsFeature() throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("derive-frontend-backend-from-feature-test-2-input.cml");
		DeriveFrontendAndBackendSystemsFromFeatureBoundedContext ar = new DeriveFrontendAndBackendSystemsFromFeatureBoundedContext("TestSystem", ACL);

		// when, then
		assertThrows(RefactoringInputException.class, () -> {
			ar.doRefactor(input);
		});
	}

	@ParameterizedTest
	@ValueSource(strings = { "derive-frontend-backend-from-feature-test-3-input.cml", "derive-frontend-backend-from-feature-test-4-input.cml" })
	public void canCheckThatFrontendNameNotAlreadyExists(String inputFile) throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML(inputFile);
		DeriveFrontendAndBackendSystemsFromFeatureBoundedContext ar = new DeriveFrontendAndBackendSystemsFromFeatureBoundedContext("TestSystem", ACL);

		// when, then
		assertThrows(RefactoringInputException.class, () -> {
			ar.doRefactor(input);
		});
	}

	@ParameterizedTest
	@ValueSource(strings = { "derive-frontend-backend-from-feature-test-3-input.cml", "derive-frontend-backend-from-feature-test-4-input.cml" })
	public void canDeriveFrontendAndBackendSystemContexts(String inputFile) throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML(inputFile);
		DeriveFrontendAndBackendSystemsFromFeatureBoundedContext ar = new DeriveFrontendAndBackendSystemsFromFeatureBoundedContext("TestSystem", ACL);
		ar.setBackendName("TestBackend");
		ar.setFrontendName("TestFrontend");

		// when
		ar.doRefactor(input);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		Set<String> bcNames = model.getBoundedContexts().stream().map(bc -> bc.getName()).collect(Collectors.toSet());
		assertEquals(4, model.getBoundedContexts().size()); // model already contains two BCs
		assertTrue(bcNames.contains("TestSystem"));
		assertTrue(bcNames.contains("TestBackend"));
		assertTrue(bcNames.contains("TestFrontend"));

		BoundedContext frontend = model.getBoundedContexts().stream().filter(bc -> bc.getName().equals("TestFrontend")).findFirst().get();
		assertEquals(BoundedContextType.SYSTEM, frontend.getType());

		BoundedContext backend = model.getBoundedContexts().stream().filter(bc -> bc.getName().equals("TestBackend")).findFirst().get();
		assertEquals(BoundedContextType.SYSTEM, backend.getType());
	}

	@Test
	public void canDeriveUpstreamDownstreamRelationship() throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("derive-frontend-backend-from-feature-test-1-input.cml");
		DeriveFrontendAndBackendSystemsFromFeatureBoundedContext ar = new DeriveFrontendAndBackendSystemsFromFeatureBoundedContext("TestFeature", CONFORMIST);

		// when
		ar.doRefactor(input);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		assertNotNull(model.getMap());
		ContextMap map = model.getMap();
		assertEquals(1, map.getRelationships().size());
		UpstreamDownstreamRelationship relationship = (UpstreamDownstreamRelationship) map.getRelationships().get(0);
		assertEquals("TestFeatureFrontend", relationship.getDownstream().getName());
		assertEquals("TestFeatureBackend", relationship.getUpstream().getName());
		assertTrue(relationship.getUpstreamRoles().contains(UpstreamRole.PUBLISHED_LANGUAGE));
		assertTrue(relationship.getDownstreamRoles().contains(DownstreamRole.CONFORMIST));
		assertFalse(relationship.getDownstreamRoles().contains(DownstreamRole.ANTICORRUPTION_LAYER));

		Set<String> boundedContextsRegisteredOnContextMap = map.getBoundedContexts().stream().map(bc -> bc.getName()).collect(Collectors.toSet());
		assertTrue(boundedContextsRegisteredOnContextMap.contains("TestFeatureFrontend"));
		assertTrue(boundedContextsRegisteredOnContextMap.contains("TestFeatureBackend"));
	}

	@Test
	public void canChangeDownstreamRole() throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("derive-frontend-backend-from-feature-test-1-input.cml");
		DeriveFrontendAndBackendSystemsFromFeatureBoundedContext ar = new DeriveFrontendAndBackendSystemsFromFeatureBoundedContext("TestFeature", ACL);

		// when
		ar.doRefactor(input);

		// then
		UpstreamDownstreamRelationship relationship = (UpstreamDownstreamRelationship) reloadResource(input).getContextMappingModel().getMap().getRelationships().get(0);
		assertFalse(relationship.getDownstreamRoles().contains(DownstreamRole.CONFORMIST));
		assertTrue(relationship.getDownstreamRoles().contains(DownstreamRole.ANTICORRUPTION_LAYER));
	}

	@Test
	public void canCopyBackendDomainModel() throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("derive-frontend-backend-from-feature-test-5-input.cml");
		DeriveFrontendAndBackendSystemsFromFeatureBoundedContext ar = new DeriveFrontendAndBackendSystemsFromFeatureBoundedContext("TestFeature", ACL);
		ar.deriveViewModelInFronted(false);

		// when
		ar.doRefactor(input);

		// then
		BoundedContext backend = reloadResource(input).getContextMappingModel().getBoundedContexts().stream().filter(bc -> bc.getName().equals("TestFeatureBackend")).findFirst()
				.get();
		BoundedContext frontend = reloadResource(input).getContextMappingModel().getBoundedContexts().stream().filter(bc -> bc.getName().equals("TestFeatureFrontend")).findFirst()
				.get();
		assertNotNull(backend);
		assertNotNull(frontend);
		assertEquals(1, backend.getAggregates().size());
		assertEquals(1, backend.getModules().size());

		Aggregate aggregate = backend.getAggregates().get(0);
		SculptorModule module = backend.getModules().get(0);
		assertEquals("TestAggregateBackend", aggregate.getName());
		assertEquals(1, aggregate.getDomainObjects().size());
		assertEquals("TestModuleBackend", module.getName());
		assertEquals(1, module.getAggregates().size());
		Aggregate aggInModule = module.getAggregates().get(0);
		assertEquals("TestAggregateInModuleBackend_2", aggInModule.getName());
		assertEquals(1, aggInModule.getDomainObjects().size());
		assertEquals(1, frontend.getAggregates().size());
		assertEquals("ViewModel", frontend.getAggregates().get(0).getName());
		assertEquals(0, frontend.getModules().size());
	}

	@Test
	public void canDeriveViewModelForFrontend() throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("derive-frontend-backend-from-feature-test-5-input.cml");
		DeriveFrontendAndBackendSystemsFromFeatureBoundedContext ar = new DeriveFrontendAndBackendSystemsFromFeatureBoundedContext("TestFeature", ACL);

		// when
		ar.doRefactor(input);

		// then
		BoundedContext frontend = reloadResource(input).getContextMappingModel().getBoundedContexts().stream().filter(bc -> bc.getName().equals("TestFeatureFrontend")).findFirst()
				.get();
		assertNotNull(frontend);
		assertEquals(1, frontend.getAggregates().size());
		assertEquals(1, frontend.getModules().size());
		Aggregate aggregate = frontend.getAggregates().get(0);
		SculptorModule module = frontend.getModules().get(0);
		assertEquals("TestAggregateViewModel", aggregate.getName());
		assertEquals(1, aggregate.getDomainObjects().size());
		assertEquals("TestModuleViewModel", module.getName());
		assertEquals(1, module.getAggregates().size());
		Aggregate aggInModule = module.getAggregates().get(0);
		assertEquals("TestAggregateInModuleViewModel", aggInModule.getName());
		assertEquals(1, aggInModule.getDomainObjects().size());
	}

	@Test
	public void canAddExposedAggregatesToNewRelationship() throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("derive-frontend-backend-from-feature-test-5-input.cml");
		DeriveFrontendAndBackendSystemsFromFeatureBoundedContext ar = new DeriveFrontendAndBackendSystemsFromFeatureBoundedContext("TestFeature", ACL);

		// when
		ar.doRefactor(input);

		// then
		UpstreamDownstreamRelationship relationship = (UpstreamDownstreamRelationship) reloadResource(input).getContextMappingModel().getMap().getRelationships().get(0);
		assertNotNull(relationship);
		assertEquals(2, relationship.getUpstreamExposedAggregates().size());
		Set<String> exposedAggregateNames = relationship.getUpstreamExposedAggregates().stream().map(agg -> agg.getName()).collect(Collectors.toSet());
		assertTrue(exposedAggregateNames.contains("TestAggregateBackend"));
		assertTrue(exposedAggregateNames.contains("TestAggregateInModuleBackend_2"));
	}

	@Test
	public void canAddViewModelSampleIfNotGenerated() throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("derive-frontend-backend-from-feature-test-5-input.cml");
		DeriveFrontendAndBackendSystemsFromFeatureBoundedContext ar = new DeriveFrontendAndBackendSystemsFromFeatureBoundedContext("TestFeature", ACL);
		ar.deriveViewModelInFronted(false);

		// when
		ar.doRefactor(input);

		// then
		BoundedContext frontend = reloadResource(input).getContextMappingModel().getBoundedContexts().stream().filter(bc -> bc.getName().equals("TestFeatureFrontend")).findFirst()
				.get();
		assertNotNull(frontend);
		assertEquals(1, frontend.getAggregates().size());
		Aggregate sampleAggregate = frontend.getAggregates().get(0);
		assertEquals("ViewModel", sampleAggregate.getName());
	}

}

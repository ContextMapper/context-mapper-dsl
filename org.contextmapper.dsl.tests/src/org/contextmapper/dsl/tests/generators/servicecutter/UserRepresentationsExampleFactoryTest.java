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
package org.contextmapper.dsl.tests.generators.servicecutter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.servicecutter.input.userrepresentations.UserRepresentationsExampleFactory;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterUserRepresentationsModel;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserRepresentationsExampleFactoryTest {

	private UserRepresentationsExampleFactory factory;

	@BeforeEach
	public void prepare() {
		this.factory = new UserRepresentationsExampleFactory();
	}

	@Test
	void createDummyUseCaseIfNoDataAvailable() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();

		// when
		ServiceCutterUserRepresentationsModel userRepModel = factory.createExampleModel(model);

		// then
		assertEquals(1, userRepModel.getUseCases().size());
		assertEquals("NoDataFound", userRepModel.getUseCases().get(0).getName());
	}

	@Test
	void canCreateSampleUseCase() {
		// given
		ContextMappingModel model = createSampleModel1();

		// when
		ServiceCutterUserRepresentationsModel userRepModel = factory.createExampleModel(model);

		// then
		assertEquals(2, userRepModel.getUseCases().size());
		List<String> useCases = userRepModel.getUseCases().stream().map(uc -> uc.getName()).collect(Collectors.toList());
		assertTrue(useCases.contains("ViewTestEntity"));
		assertTrue(useCases.contains("UpdateTestEntity"));
	}

	@Test
	void canCreateSampleCompatibilities() {
		// given
		ContextMappingModel model = createSampleModel1();

		// when
		ServiceCutterUserRepresentationsModel userRepModel = factory.createExampleModel(model);

		// then
		assertNotNull(userRepModel.getCompatibilities());
		assertEquals(1, userRepModel.getCompatibilities().getAvailabilityCriticality().size());
		assertEquals(1, userRepModel.getCompatibilities().getConsistencyCriticality().size());
		assertEquals(1, userRepModel.getCompatibilities().getContentVolatility().size());
		assertEquals(1, userRepModel.getCompatibilities().getSecurityCriticality().size());
		assertEquals(1, userRepModel.getCompatibilities().getStorageSimilarity().size());
		assertEquals(1, userRepModel.getCompatibilities().getStructuralVolatility().size());
	}

	@Test
	void canCreateSampleRelatedGroups() {
		// given
		ContextMappingModel model = createSampleModel1();

		// when
		ServiceCutterUserRepresentationsModel userRepModel = factory.createExampleModel(model);

		// then
		assertEquals(1, userRepModel.getAggregates().size());
		assertEquals(1, userRepModel.getPredefinedServices().size());
		assertEquals(1, userRepModel.getSecurityAccessGroups().size());
		assertEquals(1, userRepModel.getSeparatedSecurityZones().size());
		assertEquals(1, userRepModel.getSharedOwnerGroups().size());
	}

	private ContextMappingModel createSampleModel1() {
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext.setName("TestBoundedContext");
		model.getBoundedContexts().add(boundedContext);
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		contextMap.getBoundedContexts().add(boundedContext);
		model.setMap(contextMap);
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		Entity entity = TacticdslFactory.eINSTANCE.createEntity();
		entity.setName("TestEntity");
		Attribute attribute = TacticdslFactory.eINSTANCE.createAttribute();
		attribute.setName("testAttribute");
		entity.getAttributes().add(attribute);
		aggregate.getDomainObjects().add(entity);
		boundedContext.getAggregates().add(aggregate);
		return model;
	}

}

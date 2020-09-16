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
package org.contextmapper.dsl.generators.servicecutter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.servicecutter.input.userrepresentations.UserRepresentationsBuilder;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.Aggregate;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.Compatibilities;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ContentVolatility;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.PredefinedService;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterUserRepresentationsModel;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.SharedOwnerGroup;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.StructuralVolatility;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.UseCase;
import org.junit.jupiter.api.Test;

public class UserRepresentationsBuilderTest extends AbstractCMLInputFileTest {

	@Test
	public void canGenerateUseCasesFromCML() throws IOException {
		// given
		ContextMappingModel inputModel = getOriginalResourceOfTestCML("user-representations-builder-test-1.cml").getContextMappingModel();

		// when
		UserRepresentationsBuilder builder = new UserRepresentationsBuilder(inputModel);
		ServiceCutterUserRepresentationsModel scModel = builder.build();

		// then
		assertEquals(1, scModel.getUseCases().size());
		UseCase useCase = scModel.getUseCases().get(0);
		assertEquals(10, useCase.getNanoentitiesRead().size());
		assertEquals(10, useCase.getNanoentitiesWritten().size());
	}

	@Test
	public void canGenerateAggregatesFromCML() throws IOException {
		// given
		ContextMappingModel inputModel = getOriginalResourceOfTestCML("user-representations-builder-test-2.cml").getContextMappingModel();

		// when
		UserRepresentationsBuilder builder = new UserRepresentationsBuilder(inputModel);
		ServiceCutterUserRepresentationsModel scModel = builder.build();

		// then
		assertEquals(1, scModel.getAggregates().size());
		Aggregate aggregate = scModel.getAggregates().get(0);
		assertEquals("TestAggregate", aggregate.getName());
		assertEquals(2, aggregate.getNanoentities().size());
	}

	@Test
	public void canGenerateEntitiesFromCML() throws IOException {
		// given
		ContextMappingModel inputModel = getOriginalResourceOfTestCML("user-representations-builder-test-2.cml").getContextMappingModel();

		// when
		UserRepresentationsBuilder builder = new UserRepresentationsBuilder(inputModel);
		ServiceCutterUserRepresentationsModel scModel = builder.build();

		// then
		assertEquals(2, scModel.getEntities().size());
	}

	@Test
	public void canGeneratePredefinedServicesFromCML() throws IOException {
		// given
		ContextMappingModel inputModel = getOriginalResourceOfTestCML("user-representations-builder-test-2.cml").getContextMappingModel();

		// when
		UserRepresentationsBuilder builder = new UserRepresentationsBuilder(inputModel);
		ServiceCutterUserRepresentationsModel scModel = builder.build();

		// then
		assertEquals(1, scModel.getPredefinedServices().size());
		PredefinedService predefinedService = scModel.getPredefinedServices().get(0);
		assertEquals("TestContext", predefinedService.getName());
		assertEquals(2, predefinedService.getNanoentities().size());
	}

	@Test
	public void canGenerateSharedOwnerGroupsFromCML() throws IOException {
		// given
		ContextMappingModel inputModel = getOriginalResourceOfTestCML("user-representations-builder-test-3.cml").getContextMappingModel();

		// when
		UserRepresentationsBuilder builder = new UserRepresentationsBuilder(inputModel);
		ServiceCutterUserRepresentationsModel scModel = builder.build();

		// then
		assertEquals(1, scModel.getSharedOwnerGroups().size());
		SharedOwnerGroup ownerGroup = scModel.getSharedOwnerGroups().get(0);
		assertEquals("TeamA", ownerGroup.getName());
		assertEquals(1, ownerGroup.getNanoentities().size());
		assertEquals("TestEntity.attribute1", ownerGroup.getNanoentities().get(0));
	}

	@Test
	public void canGenerateStructuralVolatilityCompatibilitiesFromCML() throws IOException {
		// given
		ContextMappingModel inputModel = getOriginalResourceOfTestCML("user-representations-builder-test-4.cml").getContextMappingModel();

		// when
		ServiceCutterUserRepresentationsModel scModel = new UserRepresentationsBuilder(inputModel).build();

		// then
		assertNotNull(scModel.getCompatibilities());
		Compatibilities compatibilities = scModel.getCompatibilities();
		assertFalse(compatibilities.getStructuralVolatility().isEmpty());
		assertEquals(3, compatibilities.getStructuralVolatility().size());
		StructuralVolatility normal = compatibilities.getStructuralVolatility().stream().filter(sv -> sv.getCharacteristic().equals("Normal")).findFirst().get();
		StructuralVolatility often = compatibilities.getStructuralVolatility().stream().filter(sv -> sv.getCharacteristic().equals("Often")).findFirst().get();
		StructuralVolatility rarely = compatibilities.getStructuralVolatility().stream().filter(sv -> sv.getCharacteristic().equals("Rarely")).findFirst().get();
		assertNotNull(normal);
		assertNotNull(often);
		assertNotNull(rarely);
		assertTrue(normal.getNanoentities().contains("Customer.testAttr1"));
		assertTrue(often.getNanoentities().contains("Customer.testAttr2"));
		assertTrue(rarely.getNanoentities().contains("Customer.testAttr3"));
	}
	
	@Test
	public void canGenerateContentVolatilityCompatibilitiesFromCML() throws IOException {
		// given
		ContextMappingModel inputModel = getOriginalResourceOfTestCML("user-representations-builder-test-5.cml").getContextMappingModel();

		// when
		ServiceCutterUserRepresentationsModel scModel = new UserRepresentationsBuilder(inputModel).build();

		// then
		assertNotNull(scModel.getCompatibilities());
		Compatibilities compatibilities = scModel.getCompatibilities();
		assertFalse(compatibilities.getContentVolatility().isEmpty());
		assertEquals(3, compatibilities.getContentVolatility().size());
		ContentVolatility normal = compatibilities.getContentVolatility().stream().filter(sv -> sv.getCharacteristic().equals("Regularly")).findFirst().get();
		ContentVolatility often = compatibilities.getContentVolatility().stream().filter(sv -> sv.getCharacteristic().equals("Often")).findFirst().get();
		ContentVolatility rarely = compatibilities.getContentVolatility().stream().filter(sv -> sv.getCharacteristic().equals("Rarely")).findFirst().get();
		assertNotNull(normal);
		assertNotNull(often);
		assertNotNull(rarely);
		assertTrue(normal.getNanoentities().contains("Customer.testAttr1"));
		assertTrue(often.getNanoentities().contains("Customer.testAttr2"));
		assertTrue(rarely.getNanoentities().contains("Customer.testAttr3"));
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/servicecutter/";
	}

}

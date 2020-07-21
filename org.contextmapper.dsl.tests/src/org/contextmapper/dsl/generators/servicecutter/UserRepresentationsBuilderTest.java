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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.servicecutter.input.userrepresentations.UserRepresentationsBuilder;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.Aggregate;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.PredefinedService;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterUserRepresentationsModel;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.SharedOwnerGroup;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.UseCase;
import org.eclipse.emf.ecore.resource.Resource;
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
		assertEquals(2, useCase.getNanoentitiesRead().size());
		assertEquals(2, useCase.getNanoentitiesWritten().size());
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
	public void canUpdateExistingSCLModel() throws IOException {
		// given
		ContextMappingModel inputModel = getOriginalResourceOfTestCML("user-representations-builder-test-3.cml").getContextMappingModel();
		Resource sclInput = getResourceCopyOfTestSCL("user-representations-builder-test-3.scl");
		ServiceCutterUserRepresentationsModel scInputModel = (ServiceCutterUserRepresentationsModel) sclInput.getContents().get(0);

		// when
		UserRepresentationsBuilder builder = new UserRepresentationsBuilder(inputModel, scInputModel);
		ServiceCutterUserRepresentationsModel scModel = builder.build();

		// then
		assertEquals(1, scModel.getSharedOwnerGroups().size());
		assertNotNull(scModel.getCompatibilities());
		assertFalse(scModel.getCompatibilities().getStructuralVolatility().isEmpty());
		assertFalse(scModel.getCompatibilities().getContentVolatility().isEmpty());
		assertFalse(scModel.getCompatibilities().getStructuralVolatility().isEmpty());
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/servicecutter/";
	}

}

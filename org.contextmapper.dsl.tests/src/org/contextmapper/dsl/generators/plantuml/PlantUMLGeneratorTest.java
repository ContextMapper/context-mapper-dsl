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
package org.contextmapper.dsl.generators.plantuml;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.generator.PlantUMLGenerator;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.contextmapper.dsl.generators.mocks.ContextMappingModelResourceMock;
import org.contextmapper.dsl.generators.mocks.IFileSystemAccess2Mock;
import org.contextmapper.dsl.generators.mocks.IGeneratorContextMock;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlantUMLGeneratorTest extends AbstractCMLInputFileTest {

	private PlantUMLGenerator generator;

	@BeforeEach
	public void prepare() {
		super.prepare();
		this.generator = new PlantUMLGenerator();
	}

	@Test
	void canCreatePlantUMLDiagramFiles() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Domain domain = ContextMappingDSLFactory.eINSTANCE.createDomain();
		Subdomain subdomain = ContextMappingDSLFactory.eINSTANCE.createSubdomain();
		domain.setName("TestDomain");
		subdomain.setName("TestSubdomain");
		domain.getSubdomains().add(subdomain);
		boundedContext.setName("TestContext");
		model.getBoundedContexts().add(boundedContext);
		contextMap.getBoundedContexts().add(boundedContext);
		model.setMap(contextMap);
		model.getDomains().add(domain);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_ContextMap.puml"));
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_BC_TestContext.puml"));
		assertFalse(filesystem.getGeneratedFilesSet().contains("testmodel_SD_TestSubdomain.puml"));
	}

	@Test
	void canCreateBoundedContextClassDiagramsWithoutContextMap() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext.setName("TestContext");
		model.getBoundedContexts().add(boundedContext);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_BC_TestContext.puml"));
	}

	@Test
	void canCreatePlantUMLDiagrmFiles4SubdomainIfEntitiesAvailable() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		Domain domain = ContextMappingDSLFactory.eINSTANCE.createDomain();
		Subdomain subdomain1 = ContextMappingDSLFactory.eINSTANCE.createSubdomain();
		domain.setName("TestDomain");
		subdomain1.setName("TestSubdomain1");
		Subdomain subdomain2 = ContextMappingDSLFactory.eINSTANCE.createSubdomain();
		subdomain2.setName("TestSubdomain2");
		domain.getSubdomains().add(subdomain1);
		domain.getSubdomains().add(subdomain2);
		subdomain2.getEntities().add(createTestEntity("TestEntity"));
		model.getDomains().add(domain);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertFalse(filesystem.getGeneratedFilesSet().contains("testmodel_SD_TestSubdomain1.puml"));
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_SD_TestSubdomain2.puml"));
	}

	@Test
	void canCreateStateDiagram4ApplicationFlowIfAvailable() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("state-diagram-generation-flow-test.cml").getContextMappingModel();

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_BC_InsuranceQuotes_QuoteFlow_StateDiagram.puml"));
	}

	@Test
	void canCreateClass4AggregateIfAvailable() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("class-diagram-generation-aggregate-test-1.cml").getContextMappingModel();

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_BC_InsuranceQuotes_QuoteRequest.puml"));
	}
	
	@Test
	void canCreateClass4ModuleIfAvailable() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("class-diagram-generation-module-test-1.cml").getContextMappingModel();

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_BC_InsuranceQuotes_QuoteRequest.puml"));
	}
	
	@Test
	void canCreateStateDiagram4AggregateIfAvailable() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("state-diagram-generation-aggregate-test-1.cml").getContextMappingModel();

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_BC_InsuranceQuotes_QuoteRequest_StateDiagram.puml"));
	}
	
	@Test
	void canCreateUseCaseDiagram4UseCasesIfAvailable() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("use-case-diagram-generation-test-1.cml").getContextMappingModel();

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_UseCases.puml"));
	}
	
	@Test
	void canCreateSequenceDiagram4UseCaseInteractionsIfAvailable() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("use-case-diagram-generation-test-1.cml").getContextMappingModel();

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_UseCase_Get_paid_for_car_accident_Interactions.puml"));
	}

	@Test
	void expectExceptionForEmptyResource() {
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		assertThrows(GeneratorInputException.class, () -> {
			this.generator.doGenerate(new ContextMappingModelResourceMock(null, "testmodel", "cml"), filesystem, new IGeneratorContextMock());
		});
	}

	@Test
	void expectExceptionForEmptyContextMappingModel() {
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		assertThrows(GeneratorInputException.class, () -> {
			this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());
		});
	}

	@Test
	void expectExceptionIfThereIsOnlyAnEmptyDomain() {
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		Domain emptyDomain = ContextMappingDSLFactory.eINSTANCE.createDomain();
		emptyDomain.setName("TestDomain");
		model.getDomains().add(emptyDomain);
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		assertThrows(GeneratorInputException.class, () -> {
			this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());
		});
	}

	private Entity createTestEntity(String name) {
		Entity testEntity = TacticdslFactory.eINSTANCE.createEntity();
		testEntity.setName(name);
		return testEntity;
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/generators/plantuml/";
	}

}

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
package org.contextmapper.dsl.standalone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.ContextMapGenerator;
import org.contextmapper.dsl.generator.contextmap.ContextMapFormat;
import org.contextmapper.dsl.refactoring.SplitBoundedContextByOwner;
import org.eclipse.xtext.resource.SaveOptions;
import org.junit.jupiter.api.Test;

public class StandaloneAPITest {

	@Test
	public void canLoadCMLResource() {
		// given
		StandaloneContextMapperAPI contextMapper = ContextMapperStandaloneSetup.getStandaloneAPI();

		// when
		CMLResource cml = contextMapper.loadCML("./integ-test-files/standalone/hello-world.cml");

		// then
		assertNotNull(cml);
		assertEquals(1, cml.getContextMappingModel().getBoundedContexts().size());
		assertEquals("HelloWorldContext", cml.getContextMappingModel().getBoundedContexts().get(0).getName());
	}

	@Test
	public void canLoadCMLResourceViaFile() {
		// given
		StandaloneContextMapperAPI contextMapper = ContextMapperStandaloneSetup.getStandaloneAPI();
		File inputFile = new File("./integ-test-files/standalone/hello-world.cml");

		// when
		CMLResource cml = contextMapper.loadCML(inputFile);

		// then
		assertNotNull(cml);
		assertEquals(1, cml.getContextMappingModel().getBoundedContexts().size());
		assertEquals("HelloWorldContext", cml.getContextMappingModel().getBoundedContexts().get(0).getName());
	}

	@Test
	public void canCreateNewCMLModel() throws IOException {
		// given
		StandaloneContextMapperAPI contextMapper = ContextMapperStandaloneSetup.getStandaloneAPI();
		String filename = "./out/test-new-cml-model.cml";
		File file = new File(filename);

		// when
		ensureFileDoesNotExist(file);
		CMLResource newCMLModel = contextMapper.createCML(filename);
		BoundedContext testContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		testContext.setName("TestContext");
		newCMLModel.getContextMappingModel().getBoundedContexts().add(testContext);
		newCMLModel.save(SaveOptions.defaultOptions().toOptionsMap());

		// then
		assertTrue(file.exists());
		assertEquals(System.lineSeparator() + System.lineSeparator() + "BoundedContext TestContext", FileUtils.readFileToString(file));
	}

	@Test
	public void canCreateNewCMLModelViaFile() throws IOException {
		// given
		StandaloneContextMapperAPI contextMapper = ContextMapperStandaloneSetup.getStandaloneAPI();
		File file = new File("./out/test-new-cml-model-via-file.cml");

		// when
		ensureFileDoesNotExist(file);
		CMLResource newCMLModel = contextMapper.createCML(file);
		BoundedContext testContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		testContext.setName("TestContext");
		newCMLModel.getContextMappingModel().getBoundedContexts().add(testContext);
		newCMLModel.save(SaveOptions.defaultOptions().toOptionsMap());

		// then
		assertTrue(file.exists());
		assertEquals(System.lineSeparator() + System.lineSeparator() + "BoundedContext TestContext", FileUtils.readFileToString(file));
	}

	@Test
	public void canCallGenerator() {
		// given
		StandaloneContextMapperAPI contextMapper = ContextMapperStandaloneSetup.getStandaloneAPI();
		CMLResource cml = contextMapper.loadCML("./integ-test-files/standalone/simple-context-map.cml");
		File expectedOutput = new File("./src-gen/simple-context-map_ContextMap.png");
		ensureFileDoesNotExist(expectedOutput);

		// when
		ContextMapGenerator generator = new ContextMapGenerator();
		generator.setContextMapFormats(ContextMapFormat.PNG);
		contextMapper.callGenerator(cml, generator);

		// then
		assertTrue(expectedOutput.exists());
	}

	@Test
	public void canGenerateIntoCustomDirectory() {
		// given
		StandaloneContextMapperAPI contextMapper = ContextMapperStandaloneSetup.getStandaloneAPI();
		CMLResource cml = contextMapper.loadCML("./integ-test-files/standalone/simple-context-map.cml");
		File expectedOutput = new File("./out/simple-context-map_ContextMap.png");
		ensureFileDoesNotExist(expectedOutput);

		// when
		ContextMapGenerator generator = new ContextMapGenerator();
		generator.setContextMapFormats(ContextMapFormat.PNG);
		contextMapper.callGenerator(cml, generator, "./out");

		// then
		assertTrue(expectedOutput.exists());
	}

	@Test
	public void canApplyRefactoring() throws IOException {
		// given
		File originalFile = new File("./integ-test-files/standalone/refactoring-test.cml");
		File cmlTestFile = new File("./out/refactoring-test.cml");
		ensureFileDoesNotExist(cmlTestFile);
		FileUtils.copyFile(originalFile, cmlTestFile);
		StandaloneContextMapperAPI contextMapper = ContextMapperStandaloneSetup.getStandaloneAPI();
		CMLResource cml = contextMapper.loadCML(cmlTestFile);

		// when
		contextMapper.applyRefactoring(cml, new SplitBoundedContextByOwner("PolicyManagementContext"));

		// then
		ContextMappingModel model = cml.getContextMappingModel();
		assertEquals(10, model.getBoundedContexts().size());
		assertNotNull(model.getBoundedContexts().stream().filter(bc -> bc.getName().equals("NewBoundedContext1")).findFirst().get());
	}

	private void ensureFileDoesNotExist(File file) {
		if (file.exists())
			file.delete();
	}

}

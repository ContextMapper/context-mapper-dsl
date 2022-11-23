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
package org.contextmapper.dsl.generators.servicecutter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.ServiceCutterInputGenerator;
import org.contextmapper.dsl.generators.mocks.ContextMappingModelResourceMock;
import org.contextmapper.dsl.generators.mocks.IFileSystemAccess2Mock;
import org.contextmapper.dsl.generators.mocks.IGeneratorContextMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceCutterInputGeneratorTest extends AbstractCMLInputFileTest {

	private ServiceCutterInputGenerator generator;

	@BeforeEach
	public void prepare() {
		super.prepare();
		this.generator = new ServiceCutterInputGenerator();
	}

	@Test
	void canCreateServiceCutterInputFile() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext.setName("TestContext");
		contextMap.getBoundedContexts().add(boundedContext);
		model.setMap(contextMap);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel.json"));
	}

	@Test
	void canSerializeCorrectly() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("DDD_Sample_Input.cml");
		ContextMappingModel model = input.getContextMappingModel();

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "DDD_Sample", "cml"), filesystem, new IGeneratorContextMock());

		// then
		String generatedJSON = filesystem.readTextFile("DDD_Sample.json").toString();
		File expectedResultFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/servicecutter/DDD_Sample_Expected_JSON_Input.json");
		String expectedResult = FileUtils.readFileToString(expectedResultFile);
		assertEquals(expectedResult, generatedJSON);
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/servicecutter/";
	}

}

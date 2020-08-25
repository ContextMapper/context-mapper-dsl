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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Paths;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.ServiceCutterUserRepresentationsExampleGenerator;
import org.contextmapper.dsl.generators.mocks.ContextMappingModelResourceMock;
import org.contextmapper.dsl.generators.mocks.IFileSystemAccess2Mock;
import org.contextmapper.dsl.generators.mocks.IGeneratorContextMock;
import org.eclipse.emf.common.util.URI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceCutterUserRepresentationsExampleGeneratorTest {

	private ServiceCutterUserRepresentationsExampleGenerator generator;
	private String dummyInputFilePath = "/integ-test-files/model.cml";
	private String resultFiltPath = "/integ-test-files/model_scl-syntax-example.scl";

	@BeforeEach
	public void prepare() {
		this.generator = new ServiceCutterUserRepresentationsExampleGenerator();
	}

	@Test
	void canCreateUserRepresentationsFile() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext.setName("TestContext");
		contextMap.getBoundedContexts().add(boundedContext);
		model.getBoundedContexts().add(boundedContext);
		model.setMap(contextMap);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(
				new ContextMappingModelResourceMock(model, URI.createFileURI(new File(Paths.get("").toAbsolutePath().toString(), dummyInputFilePath).getAbsolutePath())),
				filesystem, new IGeneratorContextMock());

		// then
		File resultFile = new File(Paths.get("").toAbsolutePath().toString(), resultFiltPath);
		assertTrue(resultFile.exists());
	}

	@AfterEach
	public void cleanup() {
		File resultFile = new File(Paths.get("").toAbsolutePath().toString(), resultFiltPath);
		if (resultFile.exists())
			resultFile.delete();
	}

}

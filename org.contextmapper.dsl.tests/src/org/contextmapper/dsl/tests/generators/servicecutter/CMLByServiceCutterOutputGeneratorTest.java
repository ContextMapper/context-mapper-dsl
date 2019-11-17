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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Paths;

import org.contextmapper.dsl.ContextMappingDSLStandaloneSetup;
import org.contextmapper.dsl.generator.CMLByServiceCutterOutputGenerator;
import org.contextmapper.dsl.generator.servicecutter.output.factory.ServiceCutterOutputModelFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CMLByServiceCutterOutputGeneratorTest {

	private CMLByServiceCutterOutputGenerator generator;
	private File integTestFile;
	private String resultFilePath = "/integ-test-files/ServiceCutter-Services.cml";

	@BeforeEach
	public void prepare() {
		this.generator = new CMLByServiceCutterOutputGenerator();
		this.integTestFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/ServiceCutter-Services.json");
	}

	@Test
	void testGenerator() {
		// given
		ContextMappingDSLStandaloneSetup.doSetup();
		URI uri = URI.createFileURI(this.integTestFile.getAbsolutePath());

		// when
		ServiceCutterOutputModelFactory modelFactory = new ServiceCutterOutputModelFactory();
		this.generator.doGenerate(new ResourceSetImpl(), uri, modelFactory.createFromJsonFile(this.integTestFile));

		// then
		File resultFile = new File(Paths.get("").toAbsolutePath().toString(), resultFilePath);
		assertTrue(resultFile.exists());
	}

	@AfterEach
	public void cleanup() {
		File resultFile = new File(Paths.get("").toAbsolutePath().toString(), resultFilePath);
		if (resultFile.exists())
			resultFile.delete();
	}

}

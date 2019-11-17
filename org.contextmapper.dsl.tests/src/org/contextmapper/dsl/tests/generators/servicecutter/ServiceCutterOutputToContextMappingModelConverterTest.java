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

import java.io.File;
import java.nio.file.Paths;

import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.servicecutter.output.converter.ServiceCutterOutputToContextMappingModelConverter;
import org.contextmapper.dsl.generator.servicecutter.output.factory.ServiceCutterOutputModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.hsr.servicecutter.api.model.SolverResult;

class ServiceCutterOutputToContextMappingModelConverterTest {

	private ServiceCutterOutputToContextMappingModelConverter converter;
	private File sampleFile;

	@BeforeEach
	public void prepare() {
		this.converter = new ServiceCutterOutputToContextMappingModelConverter();
		this.sampleFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/ServiceCutter-Services.json");
	}

	@Test
	void testWithSampleFile() {
		// given
		SolverResult solverResult = new ServiceCutterOutputModelFactory().createFromJsonFile(sampleFile);

		// when
		ContextMappingModel contextMappingModel = this.converter.convert(solverResult);
		ContextMap contextMap = contextMappingModel.getMap();
		
		// then
		assertEquals(3, contextMap.getBoundedContexts().size());
		assertEquals(2, contextMap.getRelationships().size());
	}

}

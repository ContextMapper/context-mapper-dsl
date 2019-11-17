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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.nio.file.Paths;

import org.contextmapper.dsl.generator.servicecutter.output.factory.ServiceCutterOutputModelFactory;
import org.contextmapper.dsl.generator.servicecutter.output.factory.ServiceCutterOutputModelReadingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.hsr.servicecutter.api.model.SolverResult;

class ServiceCutterOutputModelFactoryTest {

	private File integTestFile;
	private File integTestFileWrongInput;

	@BeforeEach
	public void prepare() {
		this.integTestFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/ServiceCutter-Services.json");
		this.integTestFileWrongInput = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/ServiceCutter-Output-Wrong.json");
	}

	@Test
	void canCreateFromFile() {
		// given
		ServiceCutterOutputModelFactory factory = new ServiceCutterOutputModelFactory();

		// when
		SolverResult solverResult = factory.createFromJsonFile(integTestFile);

		// then
		assertNotNull(solverResult);
		assertEquals(3, solverResult.getServices().size());
		assertEquals(2, solverResult.getRelations().size());
		assertEquals(2, solverResult.getUseCaseResponsibility().size());
	}

	@Test
	void throwExceptionForWrongFileInput() {
		ServiceCutterOutputModelFactory factory = new ServiceCutterOutputModelFactory();
		assertThrows(ServiceCutterOutputModelReadingException.class, () -> {
			factory.createFromJsonFile(integTestFileWrongInput);
		});
	}

}

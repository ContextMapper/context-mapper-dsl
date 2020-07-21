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
package org.contextmapper.dsl.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.contextmapper.dsl.AbstractDirectoryIntegrationTest;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.junit.jupiter.api.Test;

import ch.hsr.servicecutter.solver.SolverAlgorithm;
import ch.hsr.servicecutter.solver.SolverConfiguration;

public class ServiceCutterConfigHandlerTest extends AbstractDirectoryIntegrationTest {

	@Test
	public void cannotCreateConfig4NonExistingProjectDir() {
		assertThrows(ContextMapperApplicationException.class, () -> {
			new ServiceCutterConfigHandler(new File("/this-folder-should-not-exists"));
		});
	}

	@Test
	public void canCreateServiceCutterConfig() {
		// given
		ServiceCutterConfigHandler handler = new ServiceCutterConfigHandler(testDir);

		// when
		handler.createAndGetServiceCutterConfig();

		// then
		assertTrue(new File(testDir, ".servicecutter.yml").exists());
	}

	@Test
	public void canGetDefaultSolverConfiguration() {
		// given
		ServiceCutterConfigHandler handler = new ServiceCutterConfigHandler(testDir);

		// when
		SolverConfiguration config = handler.getServiceCutterSolverConfiguration();

		// then
		assertEquals(SolverAlgorithm.LEUNG, config.getAlgorithm());
	}

	@Test
	public void canCallCreationMethodTwice() {
		// given
		ServiceCutterConfigHandler handler = new ServiceCutterConfigHandler(testDir);

		// when
		handler.createAndGetServiceCutterConfig();
		handler.createAndGetServiceCutterConfig(); // multiple calls shall not change anything (and should not throw exception)

		// then
		assertTrue(new File(testDir, ".servicecutter.yml").exists());
	}

}

/*
 * Copyright 2019 The Context Mapper Project Team
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
package org.contextmapper.dsl.tests.generators.mdsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.contextmapper.dsl.generator.mdsl.ProtectedRegionContext;
import org.contextmapper.dsl.generator.mdsl.ProtectedRegionContextFactory;
import org.junit.jupiter.api.Test;

public class ProtectedRegionContextFactoryTest {

	@Test
	public void canCreateContext4NewMDSLFile() {
		// given
		ProtectedRegionContextFactory factory = new ProtectedRegionContextFactory();

		// when
		ProtectedRegionContext context = factory.createProtectedRegionContextForNewMDSLFile();

		// then
		assertEquals("", context.getProtectedDataTypeRegion());
		assertEquals("", context.getProtectedEndpointRegion());
		assertEquals("", context.getProtectedProviderRegion());
		assertEquals("", context.getProtectedClientRegion());
		assertEquals(0, context.getDataTypeIdentifiers().size());
		assertEquals(0, context.getEndpointIdentifiers().size());
		assertEquals(0, context.getProviderIdentifiers().size());
		assertEquals(0, context.getClientIdentifiers().size());
	}

	@Test
	public void canCreateContext4ExistingFile() throws IOException {
		// given
		ProtectedRegionContextFactory factory = new ProtectedRegionContextFactory();
		File expectedResultFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/mdsl/protected-region-identifier-test.mdsl");
		String mdslInputFile = FileUtils.readFileToString(expectedResultFile);

		// when
		ProtectedRegionContext context = factory.createProtectedRegionContextForExistingMDSLFile(mdslInputFile);

		// then
		assertEquals("data type Address P\ndata type Parameter1Type P", context.getProtectedDataTypeRegion());
		assertEquals(
				"endpoint type CustomersAggregate\n	exposes\n" + "		operation updateAddress\n			expecting\n				payload Address\n			delivering\n"
						+ "				payload ReturnType\n		operation anotherMethod\n			expecting\n				payload anotherMethodParameter",
				context.getProtectedEndpointRegion());
		assertEquals(
				"API provider CustomerManagementContextProvider\n"
						+ "	offers CustomersAggregate\n	at endpoint location \"http://localhost:8000\"\n		via protocol \"RESTful HTTP\"",
				context.getProtectedProviderRegion());
		assertEquals("API client ContractManagementContextClient\n" + "	consumes CustomersAggregate", context.getProtectedClientRegion());
		assertEquals(2, context.getDataTypeIdentifiers().size());
		assertEquals(1, context.getEndpointIdentifiers().size());
		assertEquals(1, context.getProviderIdentifiers().size());
		assertEquals(1, context.getClientIdentifiers().size());
	}

	@Test
	public void canCreateContext4ExistingFileWithoutProtectedSections() throws IOException {
		// given
		ProtectedRegionContextFactory factory = new ProtectedRegionContextFactory();
		File expectedResultFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/mdsl/protected-region-null-test.mdsl");
		String mdslInputFile = FileUtils.readFileToString(expectedResultFile);

		// when
		ProtectedRegionContext context = factory.createProtectedRegionContextForExistingMDSLFile(mdslInputFile);
		
		// then
		assertNull(context.getProtectedDataTypeRegion());
		assertNull(context.getProtectedEndpointRegion());
		assertNull(context.getProtectedProviderRegion());
		assertNull(context.getProtectedClientRegion());
		assertEquals(0, context.getDataTypeIdentifiers().size());
		assertEquals(0, context.getEndpointIdentifiers().size());
		assertEquals(0, context.getProviderIdentifiers().size());
		assertEquals(0, context.getClientIdentifiers().size());
	}

}

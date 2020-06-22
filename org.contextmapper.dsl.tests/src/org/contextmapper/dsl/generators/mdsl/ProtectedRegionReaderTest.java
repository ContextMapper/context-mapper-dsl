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
package org.contextmapper.dsl.generators.mdsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.contextmapper.dsl.generator.mdsl.ProtectedRegionIdentifier;
import org.contextmapper.dsl.generator.mdsl.ProtectedRegionReader;
import org.junit.jupiter.api.Test;

public class ProtectedRegionReaderTest {

	@Test
	void canIdentifyProtectedRegion() throws IOException {
		// given
		File expectedResultFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/mdsl/protected-region-reader-test.mdsl");
		String mdslInputFile = FileUtils.readFileToString(expectedResultFile);

		// when
		ProtectedRegionReader reader = new ProtectedRegionReader();
		boolean containsDataTypeRegion = reader.containsProtectedRegion(mdslInputFile, ProtectedRegionIdentifier.DATA_TYPE_REGION);
		boolean containsClientRegion = reader.containsProtectedRegion(mdslInputFile, ProtectedRegionIdentifier.CLIENT_REGION);

		// then
		assertTrue(containsDataTypeRegion);
		assertFalse(containsClientRegion);
	}

	@Test
	void canReadProtectedRegionContent() throws IOException {
		// given
		File expectedResultFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/mdsl/protected-region-reader-test.mdsl");
		String mdslInputFile = FileUtils.readFileToString(expectedResultFile);

		// when
		ProtectedRegionReader reader = new ProtectedRegionReader();
		String protectedRegion = reader.getProtectedRegionContent(mdslInputFile, ProtectedRegionIdentifier.DATA_TYPE_REGION);

		// then
		assertEquals("data type Parameter1Type P\ndata type YetAnotherDataType P", protectedRegion);
	}

	@Test
	void canGetIdentifiersInDataTypeProtectedRegion() throws IOException {
		// given
		File expectedResultFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/mdsl/protected-region-identifier-test.mdsl");
		String mdslInputFile = FileUtils.readFileToString(expectedResultFile);

		// when
		ProtectedRegionReader reader = new ProtectedRegionReader();
		String protectedRegion = reader.getProtectedRegionContent(mdslInputFile, ProtectedRegionIdentifier.DATA_TYPE_REGION);
		Set<String> identifiers = reader.getIdentifiersInProtectedRegion(protectedRegion, ProtectedRegionIdentifier.DATA_TYPE_REGION);

		// then
		assertEquals(2, identifiers.size());
		assertTrue(identifiers.contains("Address"));
		assertTrue(identifiers.contains("Parameter1Type"));
	}
	
	@Test
	void canGetIdentifiersInEndpointProtectedRegion() throws IOException {
		// given
		File expectedResultFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/mdsl/protected-region-identifier-test.mdsl");
		String mdslInputFile = FileUtils.readFileToString(expectedResultFile);

		// when
		ProtectedRegionReader reader = new ProtectedRegionReader();
		String protectedRegion = reader.getProtectedRegionContent(mdslInputFile, ProtectedRegionIdentifier.ENDPOINT_REGION);
		Set<String> identifiers = reader.getIdentifiersInProtectedRegion(protectedRegion, ProtectedRegionIdentifier.ENDPOINT_REGION);

		// then
		assertEquals(1, identifiers.size());
		assertTrue(identifiers.contains("Customers"));
	}
	
	@Test
	void canGetIdentifiersInProviderProtectedRegion() throws IOException {
		// given
		File expectedResultFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/mdsl/protected-region-identifier-test.mdsl");
		String mdslInputFile = FileUtils.readFileToString(expectedResultFile);

		// when
		ProtectedRegionReader reader = new ProtectedRegionReader();
		String protectedRegion = reader.getProtectedRegionContent(mdslInputFile, ProtectedRegionIdentifier.PROVIDER_REGION);
		Set<String> identifiers = reader.getIdentifiersInProtectedRegion(protectedRegion, ProtectedRegionIdentifier.PROVIDER_REGION);

		// then
		assertEquals(1, identifiers.size());
		assertTrue(identifiers.contains("CustomerManagementContextProvider"));
	}
	
	@Test
	void canGetIdentifiersInClientProtectedRegion() throws IOException {
		// given
		File expectedResultFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/mdsl/protected-region-identifier-test.mdsl");
		String mdslInputFile = FileUtils.readFileToString(expectedResultFile);

		// when
		ProtectedRegionReader reader = new ProtectedRegionReader();
		String protectedRegion = reader.getProtectedRegionContent(mdslInputFile, ProtectedRegionIdentifier.CLIENT_REGION);
		Set<String> identifiers = reader.getIdentifiersInProtectedRegion(protectedRegion, ProtectedRegionIdentifier.CLIENT_REGION);

		// then
		assertEquals(1, identifiers.size());
		assertTrue(identifiers.contains("ContractManagementContextClient"));
	}

}

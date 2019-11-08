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
package org.contextmapper.dsl.tests.generators.mdsl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.MDSLContractsGenerator;
import org.contextmapper.dsl.generator.mdsl.ProtectedRegionIdentifier;
import org.contextmapper.dsl.generator.mdsl.ProtectedRegionReader;
import org.contextmapper.dsl.tests.AbstractCMLInputFileTest;
import org.contextmapper.dsl.tests.generators.mocks.ContextMappingModelResourceMock;
import org.contextmapper.dsl.tests.generators.mocks.IFileSystemAccess2Mock;
import org.contextmapper.dsl.tests.generators.mocks.IGeneratorContextMock;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Iterators;

class MDSLContractsGeneratorTest extends AbstractCMLInputFileTest {

	@Test
	void canCreateMDSLFiles() throws IOException {
		// given
		String inputModelName = "basic-mdsl-model-test.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		List<ContextMappingModel> models = IteratorExtensions.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(input.getAllContents(), ContextMappingModel.class));
		ContextMappingModel model = models.get(0);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		new MDSLContractsGenerator().doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_CustomerManagementContextAPI.mdsl"));
	}

	@Test
	void canOverWriteFileAndPreserveProtectedRegion() throws IOException {
		// given
		String inputModelName = "overwrite-with-preserving-protected-region-test.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		List<ContextMappingModel> models = IteratorExtensions.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(input.getAllContents(), ContextMappingModel.class));
		ContextMappingModel model = models.get(0);
		File existingMDSLFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/mdsl/overwrite-with-preserving-protected-region-test.mdsl");
		String existingMDSLContent = FileUtils.readFileToString(existingMDSLFile);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		String mdslFileName = "testmodel_CustomerManagementContextAPI.mdsl";
		filesystem.storeFile(mdslFileName, existingMDSLContent);
		new MDSLContractsGenerator().doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains(mdslFileName));
		ProtectedRegionReader reader = new ProtectedRegionReader();
		String protectedSection = reader.getProtectedRegionContent(filesystem.readTextFile(mdslFileName).toString(), ProtectedRegionIdentifier.DATA_TYPE_REGION);
		Set<String> ids = reader.getIdentifiersInProtectedRegion(protectedSection, ProtectedRegionIdentifier.DATA_TYPE_REGION);
		assertTrue(ids.contains("MyPreservedDataType"));
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/mdsl/";
	}
}

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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.mdsl.MDSLAPIDescriptionCreator;
import org.contextmapper.dsl.generator.mdsl.MDSLModelCreator;
import org.contextmapper.dsl.generator.mdsl.ProtectedRegionContextFactory;
import org.contextmapper.dsl.generator.mdsl.model.ServiceSpecification;
import org.contextmapper.dsl.tests.AbstractCMLInputFileTest;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Iterators;

public class MDSLAPIDescriptionCreatorTest extends AbstractCMLInputFileTest {

	@Test
	void canGenerateMDSLText() throws IOException {
		testCMLInputAndMDSLOutputFiles("basic-mdsl-model-test");
	}

	@Test
	void canGenerateParameterTree() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-parameter-tree-test");
	}

	@Test
	void canGenerateParameterTreeDeep() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-parameter-tree-test-deep");
	}

	@Test
	void canHandleMixedParameterList() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-mixed-parameters-test");
	}

	@Test
	void canHandleListsInParameters() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-list-in-parameter");
	}

	@Test
	void canHandleListsInParameterTrees() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-parameter-tree-with-list-test");
	}

	@Test
	void canHandleListInReturnType() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-list-in-return-type");
	}

	@Test
	void canHandleListParameter() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-list-parameter-test");
	}

	@Test
	void canHandlePrimitiveTypesInParametersAndReturnTypes() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-basic-data-types-as-parameters");
	}

	@Test
	void canConvertCMLDateToMDSLString() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-date-to-string");
	}

	@Test
	void canAvoidStackOverflowDueToCyclicDataTypeResolution() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-cyclic-reference");
	}

	@Test
	void canHandleMethodsWithoutParameters() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-no-parameters");
	}
	
	@Test
	void canHandleNullableAttributes() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-nullable-attributes");
	}

	@Test
	void canCreateCommentInCaseThereIsNoOperationInAnAPI() throws IOException {
		// given
		String baseFilename = "mdsl-no-operation-in-one-api";
		String inputModelName = baseFilename + ".cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		List<ContextMappingModel> models = IteratorExtensions.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(input.getAllContents(), ContextMappingModel.class));
		MDSLModelCreator mdslCreator = new MDSLModelCreator(models.get(0).getMap());

		// when
		List<ServiceSpecification> serviceSpecifications = mdslCreator.createServiceSpecifications();
		MDSLAPIDescriptionCreator dslTextCreator = new TestMDSLAPIDescriptionCreator(new ProtectedRegionContextFactory().createProtectedRegionContextForNewMDSLFile());
		ServiceSpecification spec = serviceSpecifications.stream().filter(s -> s.getName().equals("MyBoundedContextAPI")).findFirst().get();
		String dslText = dslTextCreator.createAPIDescriptionText(spec, input.getURI().toFileString());

		// then
		File expectedResultFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/mdsl/" + baseFilename + ".mdsl");
		String expectedResult = FileUtils.readFileToString(expectedResultFile);
		assertEquals(expectedResult, dslText);
	}
	
	@Test
	void canUseServiceOperations() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-use-service-operations");
	}
	
	@Test
	void onlyUsePublicOperations() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-only-use-public-operations");
	}
	
	@Test
	void canCreateUpstreamDownstreamRoleComments() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-upstream-downstream-role-comments");
	}
	
	@Test
	void canCreateDomainVisionStatementComments() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-domain-vision-statements-test");
	}
	
	@Test
	void canCreateUsageContextCommunityAPI() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-usage-context-community-api");
	}
	
	@Test
	void canCreateUsageContextPublicAPI() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-usage-context-public-api");
	}
	
	@Test
	void canOverwriteOnlyUnprotectedParts() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-protected-regions", true);
	}
	
	@Test
	void canMatchMDSLPatterns() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-serves-as-test-1");
		testCMLInputAndMDSLOutputFiles("mdsl-serves-as-test-2");
	}
	
	@Test
	void canIgnoreCommentedTypesInProtectedRegion() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-protected-regions-do-not-match-commented-types", true);
	}
	
	@Test
	void canHandleEntitiesWithoutAttributes() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-entity-without-attributes");
	}
	
	@Test
	void canResolveSuperTypeAttributes() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-entity-extending-other-entity");
	}

	private void testCMLInputAndMDSLOutputFiles(String baseFilename) throws IOException {
		testCMLInputAndMDSLOutputFiles(baseFilename, false);
	}
	
	private void testCMLInputAndMDSLOutputFiles(String baseFilename, boolean overwriteExistingFile) throws IOException {
		// given
		String inputModelName = baseFilename + ".cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		List<ContextMappingModel> models = IteratorExtensions.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(input.getAllContents(), ContextMappingModel.class));
		MDSLModelCreator mdslCreator = new MDSLModelCreator(models.get(0).getMap());

		// when
		List<ServiceSpecification> serviceSpecifications = mdslCreator.createServiceSpecifications();
		MDSLAPIDescriptionCreator dslTextCreator;
		if (overwriteExistingFile) {
			File existingFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/mdsl/" + baseFilename + "-existing.mdsl");
			String existingFileContent = FileUtils.readFileToString(existingFile);
			dslTextCreator = new TestMDSLAPIDescriptionCreator(new ProtectedRegionContextFactory().createProtectedRegionContextForExistingMDSLFile(existingFileContent));
		} else {
			dslTextCreator = new TestMDSLAPIDescriptionCreator(new ProtectedRegionContextFactory().createProtectedRegionContextForNewMDSLFile());
		}
		String dslText = dslTextCreator.createAPIDescriptionText(serviceSpecifications.get(0), input.getURI().toFileString());

		// then
		File expectedResultFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/mdsl/" + baseFilename + ".mdsl");
		String expectedResult = FileUtils.readFileToString(expectedResultFile);
		assertEquals(expectedResult, dslText);
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/mdsl/";
	}

}

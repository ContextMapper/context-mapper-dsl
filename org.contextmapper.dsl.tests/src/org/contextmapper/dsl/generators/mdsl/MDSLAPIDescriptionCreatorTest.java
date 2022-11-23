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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.contextmapper.dsl.generator.mdsl.MDSLAPIDescriptionCreator;
import org.contextmapper.dsl.generator.mdsl.MDSLModelCreator;
import org.contextmapper.dsl.generator.mdsl.ProtectedRegionContextFactory;
import org.contextmapper.dsl.generator.mdsl.model.ServiceSpecification;
import org.eclipse.xtext.EcoreUtil2;
import org.junit.jupiter.api.Test;

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
		CMLResource input = getResourceCopyOfTestCML(inputModelName);
		MDSLModelCreator mdslCreator = new MDSLModelCreator(input.getContextMappingModel());

		// when
		List<ServiceSpecification> serviceSpecifications = mdslCreator.createServiceSpecifications();
		MDSLAPIDescriptionCreator dslTextCreator = new TestMDSLAPIDescriptionCreator(new ProtectedRegionContextFactory().createProtectedRegionContextForNewMDSLFile(),
				input.getURI().toFileString());
		ServiceSpecification spec = serviceSpecifications.stream().filter(s -> s.getName().equals("MyBoundedContextAPI")).findFirst().get();
		String dslText = dslTextCreator.createText(spec);

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

	@Test
	void canHandleEnums() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-enum-support");
	}

	@Test
	void canHandleReferencesToEmptyDomainObjects() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-reference-to-empty-domain-object-1");
	}

	@Test
	void canHandleDeepReferencesToEmptyDomainObjects() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-reference-to-empty-domain-object-2");
	}

	@Test
	void canHandleMultipleParameterReferencesToEmptyDomainObjects() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-reference-to-empty-domain-object-3");
	}

	@Test
	void respectApplicationLayerServices1() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-use-application-service-operations-1");
	}

	@Test
	void respectApplicationLayerServices2() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-use-application-service-operations-2");
	}

	@Test
	void respectApplicationLayerServices3() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-use-application-service-operations-3");
	}

	@Test
	void respectApplicationLayerCommands1() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-use-application-commands-1");
	}

	@Test
	void respectApplicationLayerCommands2() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-use-application-commands-2");
	}
	
	@Test
	void respectApplicationLayerName() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-use-application-name");
	}
	
	@Test
	void respectApplicationLayerFlowsSimple() throws IOException {
		testCMLInputAndMDSLOutputFiles("application-flow-example-simple");
	}
	
	@Test
	void respectApplicationLayerFlowsAdvanced() throws IOException {
		testCMLInputAndMDSLOutputFiles("application-flow-example");
	}
	
	@Test
	void unsupportedApplicationLayerFlow1() throws IOException {
		assertThrows(GeneratorInputException.class, () -> {
			testCMLInputAndMDSLOutputFiles("application-flow-example-2");
		});
	}
	
	@Test
	void anotherUnsupportedApplicationLayerFlow() throws IOException {
		assertThrows(GeneratorInputException.class, () -> {
			testCMLInputAndMDSLOutputFiles("application-flow-example-3");
		});
	}
	
	@Test
	void yetAnotherUnsupportedApplicationLayerFlow() throws IOException {
		assertThrows(GeneratorInputException.class, () -> {
			testCMLInputAndMDSLOutputFiles("application-flow-example-4");
		});
	}

	@Test
	void cannotGenerateAnyThingWithoutAggregateOrApplicationLayer1() {
		assertThrows(GeneratorInputException.class, () -> {
			testCMLInputAndMDSLOutputFiles("mdsl-nothing-to-generate-test-1");
		});
	}

	@Test
	void cannotGenerateAnyThingWithoutAggregateOrApplicationLayer2() {
		assertThrows(GeneratorInputException.class, () -> {
			testCMLInputAndMDSLOutputFiles("mdsl-nothing-to-generate-test-2");
		});
	}
	
	@Test
	void canGenerateHTTPBindingMachtingImplementationTechnologyOfMapRelation() throws IOException {
		testCMLInputAndMDSLOutputFiles("mdsl-http-binding");
	}

	private void testCMLInputAndMDSLOutputFiles(String baseFilename) throws IOException {
		testCMLInputAndMDSLOutputFiles(baseFilename, false);
	}

	private void testCMLInputAndMDSLOutputFiles(String baseFilename, boolean overwriteExistingFile) throws IOException {
		// given
		String inputModelName = baseFilename + ".cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);
		EcoreUtil2.resolveAll(input);
		MDSLModelCreator mdslCreator = new MDSLModelCreator(input.getContextMappingModel());

		// when
		List<ServiceSpecification> serviceSpecifications = mdslCreator.createServiceSpecifications();
		MDSLAPIDescriptionCreator dslTextCreator;
		if (overwriteExistingFile) {
			File existingFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/mdsl/" + baseFilename + "-existing.mdsl");
			String existingFileContent = FileUtils.readFileToString(existingFile);
			dslTextCreator = new TestMDSLAPIDescriptionCreator(new ProtectedRegionContextFactory().createProtectedRegionContextForExistingMDSLFile(existingFileContent),
					input.getURI().toFileString());
		} else {
			dslTextCreator = new TestMDSLAPIDescriptionCreator(new ProtectedRegionContextFactory().createProtectedRegionContextForNewMDSLFile(), input.getURI().toFileString());
		}
		String dslText = dslTextCreator.createText(serviceSpecifications.get(0));

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

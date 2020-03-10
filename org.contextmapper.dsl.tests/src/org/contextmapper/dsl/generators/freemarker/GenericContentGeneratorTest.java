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
package org.contextmapper.dsl.generators.freemarker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.generator.GenericContentGenerator;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.contextmapper.dsl.generators.mocks.IFileSystemAccess2Mock;
import org.contextmapper.dsl.generators.mocks.IGeneratorContextMock;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class GenericContentGeneratorTest extends AbstractCMLInputFileTest {

	@Test
	public void canHandleTemplateNotSet() {
		// given
		GenericContentGenerator generator = new GenericContentGenerator();

		// when, then
		Assertions.assertThrows(GeneratorInputException.class, () -> {
			generator.doGenerate(getSimpleCMLResource(), new IFileSystemAccess2Mock(), new IGeneratorContextMock());
		});
	}

	@Test
	public void canCheckIfTemplateExists() {
		// given
		File notExistingFile = new File("hopefully this file does not exist");
		GenericContentGenerator generator = new GenericContentGenerator();
		generator.setFreemarkerTemplateFile(notExistingFile);

		// when, then
		Assertions.assertThrows(GeneratorInputException.class, () -> {
			generator.doGenerate(getSimpleCMLResource(), new IFileSystemAccess2Mock(), new IGeneratorContextMock());
		});
	}

	@Test
	public void canEnsureThatTargetFileNameIsDefined() throws IOException {
		// given
		File templateFile = getCopyOfTestInputFile("simple-template.ftl");
		GenericContentGenerator generator = new GenericContentGenerator();
		generator.setFreemarkerTemplateFile(templateFile);

		// when, then
		Assertions.assertThrows(GeneratorInputException.class, () -> {
			generator.doGenerate(getSimpleCMLResource(), new IFileSystemAccess2Mock(), new IGeneratorContextMock());
		});
	}

	@Test
	public void canEnsureThatTargetFileNameIsNotEmpty() throws IOException {
		// given
		File templateFile = getCopyOfTestInputFile("simple-template.ftl");
		String targetFileName = "";
		GenericContentGenerator generator = new GenericContentGenerator();
		generator.setFreemarkerTemplateFile(templateFile);
		generator.setTargetFileName(targetFileName);

		// when, then
		Assertions.assertThrows(GeneratorInputException.class, () -> {
			generator.doGenerate(getSimpleCMLResource(), new IFileSystemAccess2Mock(), new IGeneratorContextMock());
		});
	}

	@Test
	public void canGenerateTextFile() throws IOException {
		testGenericFreemarkerGeneration("simple-template.ftl", "simple-context-map.cml", "testMap");
	}

	@Test
	public void canUseInstanceOfMethod() throws IOException {
		testGenericFreemarkerGeneration("instance-of-test-1.ftl", "instance-of-test-1.cml", "true");
	}

	@ParameterizedTest
	@CsvSource(value = { "complex-type-test-1.ftl:complex-type-test-1.cml:ReturnType", "complex-type-test-2.ftl:complex-type-test-2.cml:ReturnType",
			"complex-type-test-3.ftl:complex-type-test-3.cml:List<ReturnType>" }, delimiter = ':')
	public void canGetTypeOfComplexType(String template, String inputCML, String expectedOutput) throws IOException {
		testGenericFreemarkerGeneration(template, inputCML, expectedOutput);
	}

	@Test
	public void canFilterBoundedContextsThatAreNotTeams() throws IOException {
		testGenericFreemarkerGeneration("bounded-context-filter-test.ftl", "bounded-context-filter-test.cml", "TestSystem");
	}

	@Test
	public void canFilterTeams() throws IOException {
		testGenericFreemarkerGeneration("team-filter-test.ftl", "team-filter-test.cml", "TestTeam");
	}

	@ParameterizedTest
	@CsvSource(value = { "get-type-wrong-parameters-test.ftl:complex-type-test-1.cml", "get-type-wrong-parameter-type-test.ftl:complex-type-test-1.cml",
			"instance-of-wrong-parameters-test.ftl:instance-of-test-1.cml", "instance-of-second-parameter-no-class.ftl:instance-of-test-1.cml",
			"filter-bounded-contexts-wrong-parameters-test.ftl:filter-bounded-contexts-wrong-parameters-test.cml",
			"filter-bounded-contexts-wrong-parameter-type-test.ftl:filter-bounded-contexts-wrong-parameter-type-test.cml",
			"filter-teams-wrong-parameters-test.ftl:filter-teams-wrong-parameters-test.cml",
			"filter-teams-wrong-parameter-type-test.ftl:filter-teams-wrong-parameter-type-test.cml" }, delimiter = ':')
	public void canCheckInputParametersOfHelperFunctions(String template, String inputCML) throws IOException {
		// given
		GenericContentGenerator generator = new GenericContentGenerator();
		generator.setFreemarkerTemplateFile(getCopyOfTestInputFile(template));
		generator.setTargetFileName("output.txt");

		// when, then
		Assertions.assertThrows(ContextMapperApplicationException.class, () -> {
			generator.doGenerate(getResourceCopyOfTestCML(inputCML).getResource(), new IFileSystemAccess2Mock(), new IGeneratorContextMock());
		});
	}

	@Test
	public void canUseCustomDataVariables() throws IOException {
		// given
		GenericContentGenerator generator = new GenericContentGenerator();
		generator.setFreemarkerTemplateFile(getCopyOfTestInputFile("custom-variables-test-1.ftl"));
		generator.registerCustomModelProperty("projectName", "ContextMapper-Testproject");
		generator.setTargetFileName("output.txt");

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		generator.doGenerate(getSimpleCMLResource(), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("output.txt"));
		assertEquals("ContextMapper-Testproject", filesystem.readTextFile("output.txt"));
	}

	private void testGenericFreemarkerGeneration(String template, String inputCML, String expectedOutput) throws IOException {
		// given
		GenericContentGenerator generator = new GenericContentGenerator();
		generator.setFreemarkerTemplateFile(getCopyOfTestInputFile(template));
		generator.setTargetFileName("output.txt");

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		generator.doGenerate(getResourceCopyOfTestCML(inputCML).getResource(), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("output.txt"));
		assertEquals(expectedOutput, filesystem.readTextFile("output.txt"));
	}

	private Resource getSimpleCMLResource() throws IOException {
		return getResourceCopyOfTestCML("simple-context-map.cml").getResource();
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/freemarker/";
	}

}

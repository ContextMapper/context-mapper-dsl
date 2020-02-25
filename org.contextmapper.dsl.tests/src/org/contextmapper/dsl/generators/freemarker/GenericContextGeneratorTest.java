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
import org.contextmapper.dsl.generator.GenericContentGenerator;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.contextmapper.dsl.generators.mocks.IFileSystemAccess2Mock;
import org.contextmapper.dsl.generators.mocks.IGeneratorContextMock;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GenericContextGeneratorTest extends AbstractCMLInputFileTest {

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
		// given
		GenericContentGenerator generator = new GenericContentGenerator();
		generator.setFreemarkerTemplateFile(getCopyOfTestInputFile("simple-template.ftl"));
		generator.setTargetFileName("output.txt");

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		generator.doGenerate(getSimpleCMLResource(), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("output.txt"));
		assertEquals("testMap", filesystem.readTextFile("output.txt"));
	}

	private Resource getSimpleCMLResource() throws IOException {
		return getResourceCopyOfTestCML("simple-context-map.cml").getResource();
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/freemarker/";
	}

}

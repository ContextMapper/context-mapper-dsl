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
package org.contextmapper.dsl.generators.sketchminer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.generator.SketchMinerGenerator;
import org.contextmapper.dsl.generators.mocks.ContextMappingModelResourceMock;
import org.contextmapper.dsl.generators.mocks.IFileSystemAccess2Mock;
import org.contextmapper.dsl.generators.mocks.IGeneratorContextMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SketchMinerGeneratorTest extends AbstractCMLInputFileTest {

	private SketchMinerGenerator generator;

	@BeforeEach
	public void prepare() {
		super.prepare();
		this.generator = new SketchMinerGenerator();
	}

	@ParameterizedTest
	@ValueSource(strings = { "simple-sequence-test-1" })
	public void canGenerateFilesForBoundedContextFlows(String inputFileName) throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML(inputFileName + ".cml").getContextMappingModel();

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_BC_TestContext_Flow1.sketch_miner"));
	}

	@Test
	public void cannotGenerateFileIfNoFlowExists() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("no-flow-existing-test-1.cml").getContextMappingModel();

		// when, then
		assertThrows(ContextMapperApplicationException.class, () -> {
			IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
			this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());
		});
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/sketchminer/";
	}

}

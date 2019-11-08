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
package org.contextmapper.dsl.tests.generators.contextmap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.ContextMapGenerator;
import org.contextmapper.dsl.generator.contextmap.ContextMapFormat;
import org.contextmapper.dsl.tests.generators.mocks.ContextMappingModelResourceMock;
import org.contextmapper.dsl.tests.generators.mocks.IFileSystemAccess2Mock;
import org.contextmapper.dsl.tests.generators.mocks.IGeneratorContextMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContextMapGeneratorTest {

	private ContextMapGenerator generator;

	@BeforeEach
	public void prepare() {
		this.generator = new ContextMapGenerator();
	}

	@Test
	void canGeneratePNGFile() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		model.setMap(contextMap);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_ContextMap.png"));
	}

	@Test
	void canGenerateSVGFile() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		model.setMap(contextMap);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.setContextMapFormat(ContextMapFormat.SVG);
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_ContextMap.svg"));
	}

	@Test
	void canGenerateDOTFile() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		model.setMap(contextMap);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.setContextMapFormat(ContextMapFormat.DOT);
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_ContextMap.dot"));
	}

	@Test
	void canChangeLabelSpacingFactor() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		model.setMap(contextMap);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		TestGraphvizContextMapGenerator graphvizGenerator = new TestGraphvizContextMapGenerator();
		this.generator = new TestContextMapGenerator(graphvizGenerator);
		this.generator.setLabelSpacingFactor(11);
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_ContextMap.png"));
		assertEquals(11, graphvizGenerator.getLabelSpacingFactor());
	}

	@Test
	void cannotSetWidthSmallerThanOne() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			this.generator.setWidth(0);
		});
	}

	@Test
	void cannotSetHeightSmallerThanOne() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			this.generator.setHeight(0);
		});
	}

	@Test
	void canSetCustomWidth() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		model.setMap(contextMap);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		TestGraphvizContextMapGenerator graphvizGenerator = new TestGraphvizContextMapGenerator();
		this.generator = new TestContextMapGenerator(graphvizGenerator);
		this.generator.setWidth(111);
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_ContextMap.png"));
		assertEquals(111, graphvizGenerator.getWidth());
	}

	@Test
	void canSetCustomHeight() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		model.setMap(contextMap);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		TestGraphvizContextMapGenerator graphvizGenerator = new TestGraphvizContextMapGenerator();
		this.generator = new TestContextMapGenerator(graphvizGenerator);
		this.generator.setHeight(111);
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_ContextMap.png"));
		assertEquals(111, graphvizGenerator.getHeight());
	}

	@Test
	void canUseWidthIfLastSet() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		model.setMap(contextMap);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		TestGraphvizContextMapGenerator graphvizGenerator = new TestGraphvizContextMapGenerator();
		this.generator = new TestContextMapGenerator(graphvizGenerator);
		this.generator.setHeight(111);
		this.generator.setWidth(111);
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_ContextMap.png"));
		assertEquals(111, graphvizGenerator.getWidth());
		assertFalse(graphvizGenerator.getHeight() == 111);
	}

	@Test
	void canUseHeightIfLastSet() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		model.setMap(contextMap);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		TestGraphvizContextMapGenerator graphvizGenerator = new TestGraphvizContextMapGenerator();
		this.generator = new TestContextMapGenerator(graphvizGenerator);
		this.generator.setWidth(111);
		this.generator.setHeight(111);
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_ContextMap.png"));
		assertEquals(111, graphvizGenerator.getHeight());
		assertFalse(graphvizGenerator.getWidth() == 111);
	}

	@Test
	void canTestIfGraphvizIsInstalled() {
		assertTrue(generator.isGraphvizInstalled());
	}

	private class TestContextMapGenerator extends ContextMapGenerator {
		private TestGraphvizContextMapGenerator generator;

		TestContextMapGenerator(TestGraphvizContextMapGenerator generator) {
			this.generator = generator;
		}

		@Override
		protected org.contextmapper.contextmap.generator.ContextMapGenerator createContextMapGenerator() {
			return generator;
		}
	}

	private class TestGraphvizContextMapGenerator extends org.contextmapper.contextmap.generator.ContextMapGenerator {
		public int getLabelSpacingFactor() {
			return labelSpacingFactor;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}
}

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
package org.contextmapper.dsl.cml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.ContextMappingDSLStandaloneSetup;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.Test;

public class CommentSerializationTest extends AbstractCMLInputFileTest {

	@Test
	public void canSerializeTopComment_SL() throws IOException {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		ContextMap map = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		map.setName("TestMap");
		model.setMap(map);

		// when
		model.setTopComment("// test SL comment");
		File testFile = new File(testDir, "SL-comment-test.cml");
		new ContextMappingDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
		Resource testResource = new ResourceSetImpl().createResource(URI.createURI(testFile.toURI().toString()));
		testResource.getContents().add(model);
		testResource.save(null);

		// then
		String expectedCML = "// test SL comment" + System.lineSeparator() + "ContextMap TestMap {"
				+ System.lineSeparator() + "}" + System.lineSeparator() + System.lineSeparator();
		assertEquals(expectedCML, FileUtils.readFileToString(testFile));
	}

	@Test
	public void canSerializeTopComment_ML() throws IOException {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		ContextMap map = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		map.setName("TestMap");
		model.setMap(map);

		// when
		model.setTopComment("/* test SL comment" + System.lineSeparator() + " * with another line */");
		File testFile = new File(testDir, "SL-comment-test.cml");
		new ContextMappingDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
		Resource testResource = new ResourceSetImpl().createResource(URI.createURI(testFile.toURI().toString()));
		testResource.getContents().add(model);
		testResource.save(null);

		// then
		String expectedCML = "/* test SL comment" + System.lineSeparator() + " * with another line */"
				+ System.lineSeparator() + "ContextMap TestMap {" + System.lineSeparator() + "}"
				+ System.lineSeparator() + System.lineSeparator();
		assertEquals(expectedCML, FileUtils.readFileToString(testFile));
	}

	@Test
	public void canSerializeBCComment_SL() throws IOException {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		BoundedContext testContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		testContext.setName("TestContext");
		model.getBoundedContexts().add(testContext);

		// when
		model.setTopComment("// test SL comment");
		testContext.setComment("// test BC comment");
		File testFile = new File(testDir, "SL-BC-comment-test.cml");
		new ContextMappingDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
		Resource testResource = new ResourceSetImpl().createResource(URI.createURI(testFile.toURI().toString()));
		testResource.getContents().add(model);
		testResource.save(null);

		// then
		String expectedCML = "// test SL comment" + System.lineSeparator() + System.lineSeparator()
				+ "// test BC comment" + System.lineSeparator() + "BoundedContext TestContext";
		assertEquals(expectedCML, FileUtils.readFileToString(testFile));
	}

	@Test
	public void canSerializeBCComment_ML() throws IOException {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		BoundedContext testContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		testContext.setName("TestContext");
		model.getBoundedContexts().add(testContext);

		// when
		model.setTopComment("// test SL comment");
		testContext.setComment("/* test BC comment ML" + System.lineSeparator() + " * with second line */");
		File testFile = new File(testDir, "SL-BC-comment-test.cml");
		new ContextMappingDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
		Resource testResource = new ResourceSetImpl().createResource(URI.createURI(testFile.toURI().toString()));
		testResource.getContents().add(model);
		testResource.save(null);

		// then
		String expectedCML = "// test SL comment" + System.lineSeparator() + System.lineSeparator()
				+ "/* test BC comment ML" + System.lineSeparator() + " * with second line */" + System.lineSeparator()
				+ "BoundedContext TestContext";
		assertEquals(expectedCML, FileUtils.readFileToString(testFile));
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/";
	}

}

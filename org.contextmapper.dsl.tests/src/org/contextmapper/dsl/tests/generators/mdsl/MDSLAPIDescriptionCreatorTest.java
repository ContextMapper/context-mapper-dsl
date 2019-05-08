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
import org.contextmapper.dsl.generator.mdsl.model.ServiceSpecification;
import org.contextmapper.dsl.tests.AbstractCMLInputFileTest;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Iterators;

public class MDSLAPIDescriptionCreatorTest extends AbstractCMLInputFileTest {

	@Test
	void canGenerateMDSLText() throws IOException {
		// given
		String inputModelName = "basic-mdsl-model-test.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		List<ContextMappingModel> models = IteratorExtensions.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(input.getAllContents(), ContextMappingModel.class));
		MDSLModelCreator mdslCreator = new MDSLModelCreator(models.get(0).getMap());

		// when
		List<ServiceSpecification> serviceSpecifications = mdslCreator.createServiceSpecifications();
		MDSLAPIDescriptionCreator dslTextCreator = new MDSLAPIDescriptionCreator();
		String dslText = dslTextCreator.createAPIDescriptionText(serviceSpecifications.get(0));

		// then
		File expectedResultFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/mdsl/basic-mdsl-model-test.mdsl");
		String expectedResult = FileUtils.readFileToString(expectedResultFile);
		assertEquals(expectedResult, dslText);
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/mdsl/";
	}

}

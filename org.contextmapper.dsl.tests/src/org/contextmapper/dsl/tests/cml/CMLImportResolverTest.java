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
package org.contextmapper.dsl.tests.cml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLImportResolver;
import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.tests.AbstractCMLInputFileTest;
import org.junit.jupiter.api.Test;

public class CMLImportResolverTest extends AbstractCMLInputFileTest {

	@Test
	public void canResolveImportedResources() throws IOException {
		// given
		CMLResourceContainer input = getOriginalResourceOfTestCML("simple-import-test.cml");

		// when
		Set<CMLResourceContainer> importedResources = new CMLImportResolver().resolveImportedResources(input);

		// then
		assertEquals(1, importedResources.size());
		ContextMappingModel model = (ContextMappingModel) importedResources.iterator().next().getResource().getContents().get(0);
		Set<String> bcNames = model.getBoundedContexts().stream().map(bc -> bc.getName()).collect(Collectors.toSet());
		assertTrue(bcNames.contains("anotherContext"));
		assertTrue(bcNames.contains("yetAnotherContext"));
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/imports/";
	}

}

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
package org.contextmapper.dsl;

import static org.contextmapper.dsl.validation.ValidationMessages.BOUNDED_CONTEXT_NAME_NOT_UNIQUE;

import java.io.IOException;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.jupiter.api.Test;

public class UniquenessAcrossFilesTest extends AbstractCMLInputFileTest {

	@Test
	public void canValidateUniquenessAcrossFiles() throws IOException {
		// given
		CMLResourceContainer input = getOriginalResourceOfTestCML("uniqueness-validation-test.cml");

		// when, then
		new ValidationTestHelper().assertError(input.getResource(), ContextMappingDSLPackage.Literals.BOUNDED_CONTEXT, "",
				String.format(BOUNDED_CONTEXT_NAME_NOT_UNIQUE, "anotherContext"));
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/imports/";
	}

}

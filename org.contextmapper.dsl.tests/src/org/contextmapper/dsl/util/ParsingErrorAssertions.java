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
package org.contextmapper.dsl.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;

public class ParsingErrorAssertions {

	public static void assertThatNoParsingErrorsOccurred(EObject parsingResultObject) {
		assertNotNull(parsingResultObject);
		EList<Diagnostic> errors = parsingResultObject.eResource().getErrors();
		assertTrue(errors.isEmpty(),
				"Unexpected errors: " + errors.stream()
				.map(Diagnostic::getMessage)
				.collect(Collectors.joining(", ")));
	}

	public static void assertThatNoValidationErrorsOccurred(EObject parsingResultObject) {
		ValidationTestHelper helper = new ValidationTestHelper();
		helper.assertNoErrors(parsingResultObject);
	}

}

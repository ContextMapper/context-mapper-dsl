package org.contextmapper.dsl.tests.util;

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

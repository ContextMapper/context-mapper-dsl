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
package org.contextmapper.dsl.exception;

import java.util.Set;

public class RefactoringSerializationException extends ContextMapperApplicationException {

	public RefactoringSerializationException(Set<String> errorMessages) {
		super(getErrorMessage(errorMessages));
	}

	private static String getErrorMessage(Set<String> errorMessages) {
		String message = "This refactoring cannot be applied given your input model and parameters. It leads to the following errors:" + System.lineSeparator() + System.lineSeparator();
		for (String errorMessage : errorMessages)
			message = message + mapErrorMessage(errorMessage) + System.lineSeparator();
		return message;
	}

	private static String mapErrorMessage(String errorMessage) {
		if (errorMessage.startsWith("No EObjectDescription could be found in Scope") && errorMessage.contains("EStructuralFeature: tacticdsl::Reference.domainObjectType"))
			return handleNoEObjectDescriptionError(errorMessage);
		return errorMessage;
	}

	private static String handleNoEObjectDescriptionError(String errorMessage) {
		return "The refactoring creates a new domain object with a name that already exists. As the name is referenced from another domain object, serialization is not possible as it leads to an ambiguous reference. "
				+ System.lineSeparator() + System.lineSeparator() + "(" + errorMessage + ")";
	}

}

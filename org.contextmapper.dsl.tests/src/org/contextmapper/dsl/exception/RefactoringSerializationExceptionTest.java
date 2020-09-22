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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Set;

import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.refactoring.DeriveSubdomainFromUserRequirements;
import org.contextmapper.dsl.refactoring.SemanticCMLRefactoring;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;

public class RefactoringSerializationExceptionTest extends AbstractCMLInputFileTest {

	@Test
	public void canHandleNoEObjectDescriptionSerializationError() throws IOException {
		// given
		CMLResource cmlResource = getResourceCopyOfTestCML("NoEObjectDescriptionExceptionTest.cml");
		Set<String> userStories = Sets.newHashSet();
		userStories.add("UC1Ads");
		SemanticCMLRefactoring ar = new DeriveSubdomainFromUserRequirements("TestDomain", "TestSubdomain", userStories);

		// when, then
		assertThrows(RefactoringSerializationException.class, () -> {
			ar.refactor(cmlResource);
			ar.persistChanges(serializer);
		});
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/exceptions/";
	}

}

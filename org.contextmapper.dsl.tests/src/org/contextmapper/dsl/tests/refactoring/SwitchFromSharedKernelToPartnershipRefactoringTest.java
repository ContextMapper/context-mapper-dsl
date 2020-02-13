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
package org.contextmapper.dsl.tests.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Partnership;
import org.contextmapper.dsl.refactoring.SwitchFromSharedKernelToPartnershipRefactoring;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SwitchFromSharedKernelToPartnershipRefactoringTest extends AbstractRefactoringTest {

	@Test
	public void canSwitchFromSharedKernelToPartnership() throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("switch-from-sharedkernel-to-partnership-test-1-input.cml");

		// when
		new SwitchFromSharedKernelToPartnershipRefactoring("CustomerManagement", "AnotherContext").doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		ContextMap map = model.getMap();
		assertEquals(2, model.getBoundedContexts().size());
		assertEquals(2, map.getBoundedContexts().size());
		assertEquals(1, map.getRelationships().size());
		assertTrue(map.getRelationships().get(0) instanceof Partnership);
		Partnership partnership = (Partnership) map.getRelationships().get(0);
		assertEquals("CustomerManagement", partnership.getParticipant1().getName());
		assertEquals("AnotherContext", partnership.getParticipant2().getName());
	}

	@Test
	void canCheck4NullBoundedContext1() throws IOException {
		// given
		String boundedContext1 = "TestContext";
		String boundedContext2 = null;
		CMLResourceContainer input = getResourceCopyOfTestCML("switch-from-sharedkernel-to-partnership-precondition-checks-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new SwitchFromSharedKernelToPartnershipRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

	@Test
	void canCheck4NullBoundedContext2() throws IOException {
		// given
		String boundedContext1 = null;
		String boundedContext2 = "TestContext";
		CMLResourceContainer input = getResourceCopyOfTestCML("switch-from-sharedkernel-to-partnership-precondition-checks-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new SwitchFromSharedKernelToPartnershipRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

	@Test
	void canCheck4EqualBoundedContexts() throws IOException {
		// given
		String boundedContext1 = "TestContext";
		String boundedContext2 = "TestContext";
		CMLResourceContainer input = getResourceCopyOfTestCML("switch-from-sharedkernel-to-partnership-precondition-checks-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new SwitchFromSharedKernelToPartnershipRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

	@Test
	void canCheck4MultipleSKRelationships() throws IOException {
		// given
		String boundedContext1 = "CustomerManagement";
		String boundedContext2 = "AnotherContext";
		CMLResourceContainer input = getResourceCopyOfTestCML("switch-from-sharedkernel-to-partnership-precondition-checks-multiple-rels-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new SwitchFromSharedKernelToPartnershipRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

	@Test
	void canCheck4NoRelationshipAvailable() throws IOException {
		// given
		String boundedContext1 = "TestContext1";
		String boundedContext2 = "TestContext2";
		CMLResourceContainer input = getResourceCopyOfTestCML("switch-from-sharedkernel-to-partnership-precondition-checks-no-rel-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new SwitchFromSharedKernelToPartnershipRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

}

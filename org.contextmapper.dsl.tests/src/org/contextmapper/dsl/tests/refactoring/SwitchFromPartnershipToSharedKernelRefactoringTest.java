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
import java.util.List;

import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.SharedKernel;
import org.contextmapper.dsl.refactoring.SwitchFromPartnershipToSharedKernelRefactoring;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Iterators;

public class SwitchFromPartnershipToSharedKernelRefactoringTest extends AbstractRefactoringTest {

	@Test
	public void canSwitchFromPartnershipToSharedKernel() throws IOException {
		// given
		Resource input = getResourceCopyOfTestCML("switch-from-partnership-to-sharedkernel-test-1-input.cml");

		// when
		new SwitchFromPartnershipToSharedKernelRefactoring("CustomerManagement", "AnotherContext").doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		ContextMap map = contextMappingModels.get(0).getMap();
		assertEquals(2, contextMappingModels.get(0).getBoundedContexts().size());
		assertEquals(2, map.getBoundedContexts().size());
		assertEquals(1, map.getRelationships().size());
		assertTrue(map.getRelationships().get(0) instanceof SharedKernel);
		SharedKernel sharedKernel = (SharedKernel) map.getRelationships().get(0);
		assertEquals("CustomerManagement", sharedKernel.getParticipant1().getName());
		assertEquals("AnotherContext", sharedKernel.getParticipant2().getName());
	}

	@Test
	void canCheck4NullBoundedContext1() throws IOException {
		// given
		String boundedContext1 = "TestContext";
		String boundedContext2 = null;
		Resource input = getResourceCopyOfTestCML("switch-from-partnership-to-sharedkernel-precondition-checks-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new SwitchFromPartnershipToSharedKernelRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

	@Test
	void canCheck4NullBoundedContext2() throws IOException {
		// given
		String boundedContext1 = null;
		String boundedContext2 = "TestContext";
		Resource input = getResourceCopyOfTestCML("switch-from-partnership-to-sharedkernel-precondition-checks-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new SwitchFromPartnershipToSharedKernelRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

	@Test
	void canCheck4EqualBoundedContexts() throws IOException {
		// given
		String boundedContext1 = "TestContext";
		String boundedContext2 = "TestContext";
		Resource input = getResourceCopyOfTestCML("switch-from-partnership-to-sharedkernel-precondition-checks-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new SwitchFromPartnershipToSharedKernelRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

	@Test
	void canCheck4MultipleSKRelationships() throws IOException {
		// given
		String boundedContext1 = "CustomerManagement";
		String boundedContext2 = "AnotherContext";
		Resource input = getResourceCopyOfTestCML("switch-from-partnership-to-sharedkernel-precondition-checks-multiple-rels-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new SwitchFromPartnershipToSharedKernelRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

	@Test
	void canCheck4NoRelationshipAvailable() throws IOException {
		// given
		String boundedContext1 = "TestContext1";
		String boundedContext2 = "TestContext2";
		Resource input = getResourceCopyOfTestCML("switch-from-partnership-to-sharedkernel-precondition-checks-no-rel-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new SwitchFromPartnershipToSharedKernelRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

}

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
package org.contextmapper.dsl.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.refactoring.ChangePartnershipToUpstreamDownstreamRefactoring;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Iterators;

public class ChangePartnershipToUpstreamDownstreamRefactoringTest extends AbstractRefactoringTest {

	@Test
	void canChangePartnershipToUpstreamDownstream() throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("change-partnership-to-upstream-downstream-test-1-input.cml");

		// when
		new ChangePartnershipToUpstreamDownstreamRefactoring("CustomerManagement", "AnotherContext").doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getResource().getAllContents(), ContextMappingModel.class));
		ContextMap map = contextMappingModels.get(0).getMap();
		BoundedContext upstreamContext = map.getBoundedContexts().stream().filter(bc -> bc.getName().equals("CustomerManagement")).findFirst().get();
		BoundedContext downstreamContext = map.getBoundedContexts().stream().filter(bc -> bc.getName().equals("AnotherContext")).findFirst().get();
		assertEquals(1, map.getRelationships().size());

		Relationship rel = map.getRelationships().get(0);
		assertTrue(rel instanceof UpstreamDownstreamRelationship);

		UpstreamDownstreamRelationship upDown = (UpstreamDownstreamRelationship) rel;
		assertTrue(upDown.getUpstream().equals(upstreamContext));
		assertTrue(upDown.getDownstream().equals(downstreamContext));
	}

	@Test
	void canCheck4NullBoundedContext1() throws IOException {
		// given
		String boundedContext1 = "TestContext";
		String boundedContext2 = null;
		CMLResourceContainer input = getResourceCopyOfTestCML("change-partnership-to-upstream-downstream-precondition-checks-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new ChangePartnershipToUpstreamDownstreamRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

	@Test
	void canCheck4NullBoundedContext2() throws IOException {
		// given
		String boundedContext1 = null;
		String boundedContext2 = "TestContext";
		CMLResourceContainer input = getResourceCopyOfTestCML("change-partnership-to-upstream-downstream-precondition-checks-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new ChangePartnershipToUpstreamDownstreamRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

	@Test
	void canCheck4EqualBoundedContexts() throws IOException {
		// given
		String boundedContext1 = "TestContext";
		String boundedContext2 = "TestContext";
		CMLResourceContainer input = getResourceCopyOfTestCML("change-partnership-to-upstream-downstream-precondition-checks-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new ChangePartnershipToUpstreamDownstreamRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

	@Test
	void canCheck4MultiplePartnershipRelationships() throws IOException {
		// given
		String boundedContext1 = "CustomerManagement";
		String boundedContext2 = "AnotherContext";
		CMLResourceContainer input = getResourceCopyOfTestCML("change-partnership-to-upstream-downstream-precondition-checks-multiple-rels-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new ChangePartnershipToUpstreamDownstreamRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

	@Test
	void canCheck4NoRelationshipAvailable() throws IOException {
		// given
		String boundedContext1 = "TestContext1";
		String boundedContext2 = "TestContext2";
		CMLResourceContainer input = getResourceCopyOfTestCML("change-partnership-to-upstream-downstream-precondition-checks-no-rel-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new ChangePartnershipToUpstreamDownstreamRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

}

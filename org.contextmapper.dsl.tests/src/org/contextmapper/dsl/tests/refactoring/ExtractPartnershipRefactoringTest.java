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
import java.util.stream.Stream;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.refactoring.ExtractPartnershipRefactoring;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.Iterators;

public class ExtractPartnershipRefactoringTest extends AbstractRefactoringTest {

	@ParameterizedTest
	@MethodSource("createExtractPartnershipParameters")
	void canExtractPartnership(String inputFile, String resultingNewBC) throws IOException {
		// given
		Resource input = getResourceCopyOfTestCML(inputFile);

		// when
		new ExtractPartnershipRefactoring("CustomerManagement", "AnotherContext").doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		ContextMap map = contextMappingModels.get(0).getMap();
		BoundedContext bc1 = map.getBoundedContexts().stream().filter(bc -> bc.getName().equals("CustomerManagement")).findFirst().get();
		BoundedContext bc2 = map.getBoundedContexts().stream().filter(bc -> bc.getName().equals("AnotherContext")).findFirst().get();
		BoundedContext newBC = map.getBoundedContexts().stream().filter(bc -> bc.getName().equals(resultingNewBC)).findFirst().get();
		assertEquals(2, map.getRelationships().size());
		assertEquals(3, map.getBoundedContexts().size());
		
		Relationship rel1 = map.getRelationships().get(0);
		Relationship rel2 = map.getRelationships().get(1);
		assertTrue(rel1 instanceof UpstreamDownstreamRelationship);
		assertTrue(rel2 instanceof UpstreamDownstreamRelationship);
		
		UpstreamDownstreamRelationship upDown1 = (UpstreamDownstreamRelationship) rel1;
		UpstreamDownstreamRelationship upDown2 = (UpstreamDownstreamRelationship) rel2;
		assertTrue(upDown1.getUpstream().equals(newBC));
		assertTrue(upDown1.getDownstream().equals(bc1));
		assertTrue(upDown2.getUpstream().equals(newBC));
		assertTrue(upDown2.getDownstream().equals(bc2));
	}
	
	private static Stream<Arguments> createExtractPartnershipParameters() {
		return Stream.of(Arguments.of("extract-partnership-test-1-input.cml", "CustomerManagement_AnotherContext_Partnership"),
				Arguments.of("extract-partnership-test-2-input.cml", "CustomerManagement_AnotherContext_Partnership_1"));
	}

	@Test
	void canCheck4NullBoundedContext1() throws IOException {
		// given
		String boundedContext1 = "TestContext";
		String boundedContext2 = null;
		Resource input = getResourceCopyOfTestCML("extract-partnership-precondition-checks-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new ExtractPartnershipRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

	@Test
	void canCheck4NullBoundedContext2() throws IOException {
		// given
		String boundedContext1 = null;
		String boundedContext2 = "TestContext";
		Resource input = getResourceCopyOfTestCML("extract-partnership-precondition-checks-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new ExtractPartnershipRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

	@Test
	void canCheck4EqualBoundedContexts() throws IOException {
		// given
		String boundedContext1 = "TestContext";
		String boundedContext2 = "TestContext";
		Resource input = getResourceCopyOfTestCML("extract-partnership-precondition-checks-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new ExtractPartnershipRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

	@Test
	void canCheck4MultiplePartnershipRelationships() throws IOException {
		// given
		String boundedContext1 = "CustomerManagement";
		String boundedContext2 = "AnotherContext";
		Resource input = getResourceCopyOfTestCML("extract-partnership-precondition-checks-multiple-rels-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new ExtractPartnershipRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

	@Test
	void canCheck4NoRelationshipAvailable() throws IOException {
		// given
		String boundedContext1 = "TestContext1";
		String boundedContext2 = "TestContext2";
		Resource input = getResourceCopyOfTestCML("extract-partnership-precondition-checks-no-rel-input.cml");

		// when, then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			new ExtractPartnershipRefactoring(boundedContext1, boundedContext2).doRefactor(input);
		});
	}

}

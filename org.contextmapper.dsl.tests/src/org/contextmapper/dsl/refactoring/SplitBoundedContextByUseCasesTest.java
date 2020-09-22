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
package org.contextmapper.dsl.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.refactoring.SplitBoundedContextByFeatures;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.jupiter.api.Test;

public class SplitBoundedContextByUseCasesTest extends AbstractRefactoringTest {

	@Test
	void canSplitWithSingleUseCases() throws IOException {
		// given
		String inputModelName = "split-bc-by-use-cases-test-1-input.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		SplitBoundedContextByFeatures ar = new SplitBoundedContextByFeatures("CustomerManagement");
		ar.refactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(2, model.getBoundedContexts().size());
		List<String> boundedContextNames = model.getBoundedContexts().stream().map(bc -> bc.getName()).collect(Collectors.toList());
		assertTrue(boundedContextNames.contains("CustomerManagement"));
		assertTrue(boundedContextNames.contains("NewBoundedContext1"));
	}

	@Test
	void canSplitWithMultipleAggregatesPerUseCase() throws IOException {
		// given
		String inputModelName = "split-bc-by-use-cases-test-2-input.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		SplitBoundedContextByFeatures ar = new SplitBoundedContextByFeatures("CustomerManagement");
		ar.refactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(2, model.getBoundedContexts().size());
		List<String> boundedContextNames = model.getBoundedContexts().stream().map(bc -> bc.getName()).collect(Collectors.toList());
		assertTrue(boundedContextNames.contains("CustomerManagement"));
		assertTrue(boundedContextNames.contains("NewBoundedContext1"));
	}

	@Test
	void canSplitIfThereIsNothingToSplit() throws IOException {
		// given
		String inputModelName = "split-bc-by-use-cases-test-3-input.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		SplitBoundedContextByFeatures ar = new SplitBoundedContextByFeatures("CustomerManagement");
		ar.refactor(input);

		// then
		assertEquals(1, input.getContextMappingModel().getBoundedContexts().size());
	}

	@Test
	void canSplitAndFixExposedAggregatesInContextMapRelationships() throws IOException {
		// given
		String inputModelName = "split-bc-by-use-cases-test-4-input.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		SplitBoundedContextByFeatures ar = new SplitBoundedContextByFeatures("CustomerManagement");
		ar.refactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(3, model.getBoundedContexts().size());
		List<String> boundedContextNames = model.getBoundedContexts().stream().map(bc -> bc.getName()).collect(Collectors.toList());
		assertTrue(boundedContextNames.contains("CustomerManagement"));
		assertTrue(boundedContextNames.contains("NewBoundedContext1"));

		List<UpstreamDownstreamRelationship> relationships = model.getMap().getRelationships().stream()
				.filter(rel -> rel instanceof UpstreamDownstreamRelationship).map(rel -> (UpstreamDownstreamRelationship) rel).collect(Collectors.toList());
		assertEquals(2, relationships.size());
		assertEquals(1, relationships.get(0).getUpstreamExposedAggregates().size());
		assertEquals(1, relationships.get(1).getUpstreamExposedAggregates().size());
	}
	
	@Test
	void canUpdateContextMapInDifferentFile() throws IOException {
		// given
		CMLResource mainResource = getResourceCopyOfTestCML("split-bc-by-use-cases-test-5-input-2.cml");
		ResourceSet additionalResources = getResourceSetOfTestCMLFiles("split-bc-by-use-cases-test-5-input-1.cml");
		
		// when
		SplitBoundedContextByFeatures ar = new SplitBoundedContextByFeatures("CustomerManagement");
		ar.refactor(mainResource, additionalResources);
		ar.persistChanges(serializer);
		CMLResource contextMapResource = new CMLResource(
				additionalResources.getResources().stream().filter(r -> r.getURI().toString().endsWith("split-bc-by-use-cases-test-5-input-1.cml")).findFirst().get());
		contextMapResource = reloadResource(contextMapResource);
		
		// then
		ContextMappingModel model = contextMapResource.getContextMappingModel();
		assertEquals(2, model.getMap().getRelationships().size());
		UpstreamDownstreamRelationship rel1 = (UpstreamDownstreamRelationship) model.getMap().getRelationships().get(0);
		UpstreamDownstreamRelationship rel2 = (UpstreamDownstreamRelationship) model.getMap().getRelationships().get(1);
		assertEquals(1, rel1.getUpstreamExposedAggregates().size());
		assertEquals(1, rel2.getUpstreamExposedAggregates().size());
	}

}

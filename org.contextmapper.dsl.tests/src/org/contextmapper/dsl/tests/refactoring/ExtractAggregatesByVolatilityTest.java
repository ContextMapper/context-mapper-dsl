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
package org.contextmapper.dsl.tests.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.LikelihoodForChange;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.refactoring.ExtractAggregatesByVolatility;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Iterators;

public class ExtractAggregatesByVolatilityTest extends AbstractRefactoringTest {

	@Test
	void canExtractAggregatesWhichAreLikelyToChange() throws IOException {
		// given
		String inputModelName = "extract-aggregates-likely-to-change-test-1-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);

		// when
		ExtractAggregatesByVolatility ar = new ExtractAggregatesByVolatility("CustomerManagement", LikelihoodForChange.OFTEN);
		ar.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(2, contextMappingModels.get(0).getBoundedContexts().size());
		Optional<BoundedContext> bc = contextMappingModels.get(0).getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement")).findFirst();
		Optional<BoundedContext> newBC = contextMappingModels.get(0).getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement_Volatility_OFTEN")).findFirst();

		assertTrue(bc.isPresent());
		assertTrue(newBC.isPresent());
		assertEquals(1, bc.get().getAggregates().size());
		assertEquals(2, newBC.get().getAggregates().size());
	}

	@Test
	void canFixExposedAggregates() throws IOException {
		// given
		String inputModelName = "extract-aggregates-likely-to-change-test-5-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);

		// when
		ExtractAggregatesByVolatility ar = new ExtractAggregatesByVolatility("CustomerManagement", LikelihoodForChange.OFTEN);
		ar.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(3, contextMappingModels.get(0).getBoundedContexts().size());
		Optional<BoundedContext> bc = contextMappingModels.get(0).getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement")).findFirst();
		Optional<BoundedContext> newBC = contextMappingModels.get(0).getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement_Volatility_OFTEN")).findFirst();

		assertTrue(bc.isPresent());
		assertTrue(newBC.isPresent());
		assertEquals(1, bc.get().getAggregates().size());
		assertEquals(2, newBC.get().getAggregates().size());

		List<UpstreamDownstreamRelationship> upDownRels = contextMappingModels.get(0).getMap().getRelationships().stream()
				.filter(rel -> rel instanceof UpstreamDownstreamRelationship).map(rel -> (UpstreamDownstreamRelationship) rel).collect(Collectors.toList());
		assertEquals(2, upDownRels.size());
		assertEquals(0, upDownRels.get(0).getUpstreamExposedAggregates().size());
		assertEquals(1, upDownRels.get(1).getUpstreamExposedAggregates().size());
	}

	@Test
	void noErrorsIfThereAreNoAggregates() throws IOException {
		// given
		String inputModelName = "extract-aggregates-likely-to-change-test-2-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);

		// when
		ExtractAggregatesByVolatility ar = new ExtractAggregatesByVolatility("CustomerManagement", LikelihoodForChange.OFTEN);
		ar.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(1, contextMappingModels.get(0).getBoundedContexts().size());
	}

	@Test
	void doNotCreateNewBCIfThereIsOnlyOneAggregate() throws IOException {
		// given
		String inputModelName = "extract-aggregates-likely-to-change-test-3-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);

		// when
		ExtractAggregatesByVolatility ar = new ExtractAggregatesByVolatility("CustomerManagement", LikelihoodForChange.OFTEN);
		ar.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(1, contextMappingModels.get(0).getBoundedContexts().size());
	}

	@Test
	void noErrorsIfThereAreNoAggregatesToExtract() throws IOException {
		// given
		String inputModelName = "extract-aggregates-likely-to-change-test-4-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);

		// when
		ExtractAggregatesByVolatility ar = new ExtractAggregatesByVolatility("CustomerManagement", LikelihoodForChange.OFTEN);
		ar.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(1, contextMappingModels.get(0).getBoundedContexts().size());
	}

}

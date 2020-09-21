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
import java.util.Optional;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.contextMappingDSL.Volatility;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.jupiter.api.Test;

public class ExtractAggregatesByVolatilityTest extends AbstractRefactoringTest {

	@Test
	void canExtractAggregatesWhichAreLikelyToChange() throws IOException {
		// given
		String inputModelName = "extract-aggregates-likely-to-change-test-1-input.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		ExtractAggregatesByVolatility ar = new ExtractAggregatesByVolatility("CustomerManagement", Volatility.OFTEN);
		ar.refactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(2, model.getBoundedContexts().size());
		Optional<BoundedContext> bc = model.getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement")).findFirst();
		Optional<BoundedContext> newBC = model.getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement_Volatility_OFTEN")).findFirst();

		assertTrue(bc.isPresent());
		assertTrue(newBC.isPresent());
		assertEquals(1, bc.get().getAggregates().size());
		assertEquals(2, newBC.get().getAggregates().size());
	}

	@Test
	void canFixExposedAggregates() throws IOException {
		// given
		String inputModelName = "extract-aggregates-likely-to-change-test-5-input.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		ExtractAggregatesByVolatility ar = new ExtractAggregatesByVolatility("CustomerManagement", Volatility.OFTEN);
		ar.refactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(3, model.getBoundedContexts().size());
		Optional<BoundedContext> bc = model.getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement")).findFirst();
		Optional<BoundedContext> newBC = model.getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement_Volatility_OFTEN")).findFirst();

		assertTrue(bc.isPresent());
		assertTrue(newBC.isPresent());
		assertEquals(1, bc.get().getAggregates().size());
		assertEquals(2, newBC.get().getAggregates().size());

		List<UpstreamDownstreamRelationship> upDownRels = model.getMap().getRelationships().stream().filter(rel -> rel instanceof UpstreamDownstreamRelationship)
				.map(rel -> (UpstreamDownstreamRelationship) rel).collect(Collectors.toList());
		assertEquals(2, upDownRels.size());
		assertEquals(0, upDownRels.get(0).getUpstreamExposedAggregates().size());
		assertEquals(1, upDownRels.get(1).getUpstreamExposedAggregates().size());
	}

	@Test
	void noErrorsIfThereAreNoAggregates() throws IOException {
		// given
		String inputModelName = "extract-aggregates-likely-to-change-test-2-input.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		ExtractAggregatesByVolatility ar = new ExtractAggregatesByVolatility("CustomerManagement", Volatility.OFTEN);
		ar.refactor(input);

		// then
		assertEquals(1, input.getContextMappingModel().getBoundedContexts().size());
	}

	@Test
	void doNotCreateNewBCIfThereIsOnlyOneAggregate() throws IOException {
		// given
		String inputModelName = "extract-aggregates-likely-to-change-test-3-input.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		ExtractAggregatesByVolatility ar = new ExtractAggregatesByVolatility("CustomerManagement", Volatility.OFTEN);
		ar.refactor(input);

		// then
		assertEquals(1, input.getContextMappingModel().getBoundedContexts().size());
	}

	@Test
	void noErrorsIfThereAreNoAggregatesToExtract() throws IOException {
		// given
		String inputModelName = "extract-aggregates-likely-to-change-test-4-input.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		ExtractAggregatesByVolatility ar = new ExtractAggregatesByVolatility("CustomerManagement", Volatility.OFTEN);
		ar.refactor(input);

		// then
		assertEquals(1, input.getContextMappingModel().getBoundedContexts().size());
	}

	@Test
	void canHandleContextMapInDifferentFile() throws IOException {
		// given
		CMLResource mainResource = getResourceCopyOfTestCML("extract-aggregates-likely-to-change-test-6-input-2.cml");
		ResourceSet additionalResources = getResourceSetOfTestCMLFiles("extract-aggregates-likely-to-change-test-6-input-1.cml");

		// when
		ExtractAggregatesByVolatility ar = new ExtractAggregatesByVolatility("CustomerManagement", Volatility.OFTEN);
		ar.refactor(mainResource, additionalResources);
		ar.persistChanges();
		CMLResource contextMapResource = new CMLResource(
				additionalResources.getResources().stream().filter(r -> r.getURI().toString().endsWith("extract-aggregates-likely-to-change-test-6-input-1.cml")).findFirst().get());
		contextMapResource = reloadResource(contextMapResource);

		// then
		ContextMap contextMap = contextMapResource.getContextMappingModel().getMap();
		List<UpstreamDownstreamRelationship> upDownRels = contextMap.getRelationships().stream().filter(rel -> rel instanceof UpstreamDownstreamRelationship)
				.map(rel -> (UpstreamDownstreamRelationship) rel).collect(Collectors.toList());
		assertEquals(2, upDownRels.size());
		assertEquals(0, upDownRels.get(0).getUpstreamExposedAggregates().size());
		assertEquals(1, upDownRels.get(1).getUpstreamExposedAggregates().size());
	}

}

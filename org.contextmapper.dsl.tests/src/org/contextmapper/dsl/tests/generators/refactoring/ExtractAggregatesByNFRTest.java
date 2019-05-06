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
package org.contextmapper.dsl.tests.generators.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.refactoring.ExtractAggregatesByNFR;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class ExtractAggregatesByNFRTest extends AbstractRefactoringTest {

	@Test
	void canExtractAggregatesByGivenInputList() throws IOException {
		// given
		String inputModelName = "extract-aggregates-by-nfr-test-1-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);

		// when
		List<String> aggregatesToExtract = Arrays.asList(new String[] { "Customers", "Addresses" });
		ExtractAggregatesByNFR ar = new ExtractAggregatesByNFR("CustomerManagement", "CustomerManagement_Extracted", aggregatesToExtract);
		ar.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(3, contextMappingModels.get(0).getBoundedContexts().size());
		Optional<BoundedContext> bc = contextMappingModels.get(0).getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement")).findFirst();
		Optional<BoundedContext> newBC = contextMappingModels.get(0).getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement_Extracted")).findFirst();

		assertTrue(bc.isPresent());
		assertTrue(newBC.isPresent());
		assertEquals(1, bc.get().getAggregates().size());
		assertEquals(2, newBC.get().getAggregates().size());
	}

	@Test
	void canExtractAggregatesAndFixExposedReferencesInContextMap() throws IOException {
		// given
		String inputModelName = "extract-aggregates-by-nfr-test-2-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);

		// when
		List<String> aggregatesToExtract = Arrays.asList(new String[] { "Addresses" });
		ExtractAggregatesByNFR ar = new ExtractAggregatesByNFR("CustomerManagement", "CustomerManagement_Extracted", aggregatesToExtract);
		ar.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(3, contextMappingModels.get(0).getBoundedContexts().size());
		assertEquals(3, contextMappingModels.get(0).getMap().getRelationships().size());

		Optional<BoundedContext> bc = contextMappingModels.get(0).getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement")).findFirst();
		Optional<BoundedContext> newBC = contextMappingModels.get(0).getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement_Extracted")).findFirst();

		assertTrue(bc.isPresent());
		assertTrue(newBC.isPresent());
		assertEquals(2, bc.get().getAggregates().size());
		assertEquals(1, newBC.get().getAggregates().size());

		List<UpstreamDownstreamRelationship> relationships = contextMappingModels.get(0).getMap().getRelationships().stream()
				.filter(rel -> rel instanceof UpstreamDownstreamRelationship).map(rel -> (UpstreamDownstreamRelationship) rel).collect(Collectors.toList());
		UpstreamDownstreamRelationship rel1 = relationships.get(0);
		UpstreamDownstreamRelationship rel2 = relationships.get(1);
		assertEquals("CustomerManagement", rel1.getUpstream().getName());
		assertEquals("CustomerManagement_Extracted", rel2.getUpstream().getName());
		assertEquals("AnotherContext", rel1.getDownstream().getName());
		assertEquals("AnotherContext", rel2.getDownstream().getName());
		assertEquals(1, rel1.getUpstreamExposedAggregates().size());
		assertEquals("Customers", rel1.getUpstreamExposedAggregates().get(0).getName());
		assertEquals(1, rel2.getUpstreamExposedAggregates().size());
		assertEquals("Addresses", rel2.getUpstreamExposedAggregates().get(0).getName());
	}

	@Test
	void noErrorIfNoAggregatesGivenAsParameter() throws IOException {
		// given
		String inputModelName = "extract-aggregates-by-nfr-test-1-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);

		// when
		List<String> aggregatesToExtract = Lists.newArrayList();
		ExtractAggregatesByNFR ar = new ExtractAggregatesByNFR("CustomerManagement", "CustomerManagement_Extracted", aggregatesToExtract);
		ar.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(2, contextMappingModels.get(0).getBoundedContexts().size());
		Optional<BoundedContext> bc = contextMappingModels.get(0).getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement")).findFirst();

		assertTrue(bc.isPresent());
		assertEquals(3, bc.get().getAggregates().size());
	}
	
	@Test
	void noErrorIfContextMapIsNull() throws IOException {
		// given
		String inputModelName = "extract-aggregates-by-nfr-test-3-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);

		// when
		List<String> aggregatesToExtract = Arrays.asList(new String[] { "Customers", "Addresses" });
		ExtractAggregatesByNFR ar = new ExtractAggregatesByNFR("CustomerManagement", "CustomerManagement_Extracted", aggregatesToExtract);
		ar.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(2, contextMappingModels.get(0).getBoundedContexts().size());
		Optional<BoundedContext> bc = contextMappingModels.get(0).getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement")).findFirst();
		Optional<BoundedContext> newBC = contextMappingModels.get(0).getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement_Extracted")).findFirst();

		assertTrue(bc.isPresent());
		assertTrue(newBC.isPresent());
		assertEquals(1, bc.get().getAggregates().size());
		assertEquals(2, newBC.get().getAggregates().size());
	}

	@Test
	void noErrorIfAggregateIsGivenWhichDoesNotExist() throws IOException {
		// given
		String inputModelName = "extract-aggregates-by-nfr-test-1-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);

		// when
		List<String> aggregatesToExtract = Arrays.asList(new String[] { "Customers", "Addresses", "ThisAggregateDoesNotExist" });
		ExtractAggregatesByNFR ar = new ExtractAggregatesByNFR("CustomerManagement", "CustomerManagement_Extracted", aggregatesToExtract);
		ar.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(3, contextMappingModels.get(0).getBoundedContexts().size());
		Optional<BoundedContext> bc = contextMappingModels.get(0).getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement")).findFirst();
		Optional<BoundedContext> newBC = contextMappingModels.get(0).getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement_Extracted")).findFirst();

		assertTrue(bc.isPresent());
		assertTrue(newBC.isPresent());
		assertEquals(1, bc.get().getAggregates().size());
		assertEquals(2, newBC.get().getAggregates().size());
	}

}

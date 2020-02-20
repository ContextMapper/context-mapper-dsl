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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.refactoring.ExtractAggregatesByCohesion;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

public class ExtractAggregatesByCohesionTest extends AbstractRefactoringTest {

	@Test
	void canExtractAggregatesByGivenInputList() throws IOException {
		// given
		String inputModelName = "extract-aggregates-by-nfr-test-1-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);

		// when
		List<String> aggregatesToExtract = Arrays.asList(new String[] { "Customers", "Addresses" });
		ExtractAggregatesByCohesion ar = new ExtractAggregatesByCohesion("CustomerManagement", "CustomerManagement_Extracted", aggregatesToExtract);
		ar.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(3, model.getBoundedContexts().size());
		Optional<BoundedContext> bc = model.getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement")).findFirst();
		Optional<BoundedContext> newBC = model.getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement_Extracted")).findFirst();

		assertTrue(bc.isPresent());
		assertTrue(newBC.isPresent());
		assertEquals(1, bc.get().getAggregates().size());
		assertEquals(2, newBC.get().getAggregates().size());
	}

	@Test
	void canExtractAggregatesAndFixExposedReferencesInContextMap() throws IOException {
		// given
		String inputModelName = "extract-aggregates-by-nfr-test-2-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);

		// when
		List<String> aggregatesToExtract = Arrays.asList(new String[] { "Addresses" });
		ExtractAggregatesByCohesion ar = new ExtractAggregatesByCohesion("CustomerManagement", "CustomerManagement_Extracted", aggregatesToExtract);
		ar.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(3, model.getBoundedContexts().size());
		assertEquals(3, model.getMap().getRelationships().size());

		Optional<BoundedContext> bc = model.getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement")).findFirst();
		Optional<BoundedContext> newBC = model.getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement_Extracted")).findFirst();

		assertTrue(bc.isPresent());
		assertTrue(newBC.isPresent());
		assertEquals(2, bc.get().getAggregates().size());
		assertEquals(1, newBC.get().getAggregates().size());

		List<UpstreamDownstreamRelationship> relationships = model.getMap().getRelationships().stream().filter(rel -> rel instanceof UpstreamDownstreamRelationship)
				.map(rel -> (UpstreamDownstreamRelationship) rel).collect(Collectors.toList());
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
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);

		// when
		List<String> aggregatesToExtract = Lists.newArrayList();
		ExtractAggregatesByCohesion ar = new ExtractAggregatesByCohesion("CustomerManagement", "CustomerManagement_Extracted", aggregatesToExtract);
		ar.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(2, model.getBoundedContexts().size());
		Optional<BoundedContext> bc = model.getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement")).findFirst();

		assertTrue(bc.isPresent());
		assertEquals(3, bc.get().getAggregates().size());
	}

	@Test
	void noErrorIfContextMapIsNull() throws IOException {
		// given
		String inputModelName = "extract-aggregates-by-nfr-test-3-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);

		// when
		List<String> aggregatesToExtract = Arrays.asList(new String[] { "Customers", "Addresses" });
		ExtractAggregatesByCohesion ar = new ExtractAggregatesByCohesion("CustomerManagement", "CustomerManagement_Extracted", aggregatesToExtract);
		ar.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(2, model.getBoundedContexts().size());
		Optional<BoundedContext> bc = model.getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement")).findFirst();
		Optional<BoundedContext> newBC = model.getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement_Extracted")).findFirst();

		assertTrue(bc.isPresent());
		assertTrue(newBC.isPresent());
		assertEquals(1, bc.get().getAggregates().size());
		assertEquals(2, newBC.get().getAggregates().size());
	}

	@Test
	void noErrorIfAggregateIsGivenWhichDoesNotExist() throws IOException {
		// given
		String inputModelName = "extract-aggregates-by-nfr-test-1-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);

		// when
		List<String> aggregatesToExtract = Arrays.asList(new String[] { "Customers", "Addresses", "ThisAggregateDoesNotExist" });
		ExtractAggregatesByCohesion ar = new ExtractAggregatesByCohesion("CustomerManagement", "CustomerManagement_Extracted", aggregatesToExtract);
		ar.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(3, model.getBoundedContexts().size());
		Optional<BoundedContext> bc = model.getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement")).findFirst();
		Optional<BoundedContext> newBC = model.getBoundedContexts().stream().filter(b -> b.getName().equals("CustomerManagement_Extracted")).findFirst();

		assertTrue(bc.isPresent());
		assertTrue(newBC.isPresent());
		assertEquals(1, bc.get().getAggregates().size());
		assertEquals(2, newBC.get().getAggregates().size());
	}

	@Test
	void canHandleContextMapInDifferentFile() throws IOException {
		// given
		CMLResourceContainer mainResource = getResourceCopyOfTestCML("extract-aggregates-by-nfr-test-4-input-2.cml");
		ResourceSet additionalResources = getResourceSetOfTestCMLFiles("extract-aggregates-by-nfr-test-4-input-1.cml");

		// when
		List<String> aggregatesToExtract = Arrays.asList(new String[] { "Addresses" });
		ExtractAggregatesByCohesion ar = new ExtractAggregatesByCohesion("CustomerManagement", "CustomerManagement_Extracted", aggregatesToExtract);
		ar.doRefactor(mainResource, additionalResources);
		CMLResourceContainer contextMapResource = new CMLResourceContainer(
				additionalResources.getResources().stream().filter(r -> r.getURI().toString().endsWith("extract-aggregates-by-nfr-test-4-input-1.cml")).findFirst().get());
		contextMapResource = reloadResource(contextMapResource);

		// then
		ContextMap contextMap = contextMapResource.getContextMappingModel().getMap();
		assertEquals(2, contextMap.getRelationships().size());
	}

}

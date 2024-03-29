package org.contextmapper.dsl.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.jupiter.api.Test;

public class SplitAggregateByEntitiesTest extends AbstractRefactoringTest {

	@Test
	void canSplitWithTwoAggregates() throws IOException {
		// given
		String inputModelName = "split-agg-by-entities-test-1-input.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);
		SplitAggregateByEntitiesRefactoring refactoring = new SplitAggregateByEntitiesRefactoring("Customers");

		// when
		refactoring.refactor(input);
		refactoring.persistChanges(serializer);

		// then
		BoundedContext bc = reloadResource(input).getContextMappingModel().getBoundedContexts().get(0);
		assertEquals(2, bc.getAggregates().size());

		for (Aggregate aggregate : bc.getAggregates()) {
			assertEquals(1, aggregate.getDomainObjects().size());
		}

		List<String> aggregateNames = bc.getAggregates().stream().map(a -> a.getName()).collect(Collectors.toList());
		assertTrue(aggregateNames.contains("Customers"));
		assertTrue(aggregateNames.contains("Account"));

		for (Aggregate aggregate : bc.getAggregates()) {
			SimpleDomainObject obj = aggregate.getDomainObjects().get(0);
			if (obj instanceof DomainObject)
				assertTrue(((DomainObject) obj).isAggregateRoot());
		}
	}

	@Test
	void canRefactorIfAggregateDoesNotExist() throws IOException {
		// given
		String inputModelName = "split-agg-by-entities-test-1-input.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);
		SplitAggregateByEntitiesRefactoring refactoring = new SplitAggregateByEntitiesRefactoring("ThisAggregateDoesNotExist");

		// when
		refactoring.refactor(input);

		// then
		BoundedContext bc = reloadResource(input).getContextMappingModel().getBoundedContexts().get(0);
		assertEquals(1, bc.getAggregates().size());
		List<String> aggregateNames = bc.getAggregates().stream().map(a -> a.getName()).collect(Collectors.toList());
		assertTrue(aggregateNames.contains("Customers"));
	}

	@Test
	void canSplitInModule() throws IOException {
		// given
		String inputModelName = "split-agg-by-entities-test-2-input.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);
		SplitAggregateByEntitiesRefactoring refactoring = new SplitAggregateByEntitiesRefactoring("Customers");

		// when
		refactoring.refactor(input);
		refactoring.persistChanges(serializer);

		// then
		BoundedContext bc = reloadResource(input).getContextMappingModel().getBoundedContexts().get(0);

		SculptorModule testModule = bc.getModules().get(0);

		assertEquals(2, testModule.getAggregates().size());

		for (Aggregate aggregate : testModule.getAggregates()) {
			assertEquals(1, aggregate.getDomainObjects().size());
		}

		List<String> aggregateNames = testModule.getAggregates().stream().map(a -> a.getName()).collect(Collectors.toList());
		assertTrue(aggregateNames.contains("Customers"));
		assertTrue(aggregateNames.contains("Account"));
	}

	@Test
	void canFixExposedAggregatesInContextMap() throws IOException {
		// given
		String inputModelName = "split-agg-by-entities-test-3-input.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);
		SplitAggregateByEntitiesRefactoring refactoring = new SplitAggregateByEntitiesRefactoring("Customers");

		// when
		refactoring.refactor(input);
		refactoring.persistChanges(serializer);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		Optional<BoundedContext> optionalCustomerBC = model.getBoundedContexts().stream().filter(bc -> bc.getName().equals("CustomerManagement")).findFirst();

		assertTrue(optionalCustomerBC.isPresent());
		BoundedContext customerBC = optionalCustomerBC.get();

		assertEquals(2, customerBC.getAggregates().size());
		List<String> aggregateNames = customerBC.getAggregates().stream().map(a -> a.getName()).collect(Collectors.toList());
		assertTrue(aggregateNames.contains("Customers"));
		assertTrue(aggregateNames.contains("Account"));

		UpstreamDownstreamRelationship relationship = (UpstreamDownstreamRelationship) model.getMap().getRelationships().get(0);
		List<String> upstreamExposedAggregates = relationship.getUpstreamExposedAggregates().stream().map(a -> a.getName()).collect(Collectors.toList());
		assertTrue(upstreamExposedAggregates.contains("Customers"));
		assertTrue(upstreamExposedAggregates.contains("Account"));
	}

	@Test
	void canHandleContextMapInDifferentFile() throws IOException {
		// given
		CMLResource mainResource = getResourceCopyOfTestCML("split-agg-by-entities-test-4-input-2.cml");
		ResourceSet additionalResources = getResourceSetOfTestCMLFiles("split-agg-by-entities-test-4-input-1.cml");

		// when
		SplitAggregateByEntitiesRefactoring ar = new SplitAggregateByEntitiesRefactoring("Customers");
		ar.refactor(mainResource, additionalResources);
		ar.persistChanges(serializer);
		CMLResource contextMapResource = new CMLResource(
				additionalResources.getResources().stream().filter(r -> r.getURI().toString().endsWith("split-agg-by-entities-test-4-input-1.cml")).findFirst().get());
		contextMapResource = reloadResource(contextMapResource);
		
		// then
		ContextMap contextMap = contextMapResource.getContextMappingModel().getMap();
		assertEquals(2, contextMap.getBoundedContexts().size());
		UpstreamDownstreamRelationship rel = (UpstreamDownstreamRelationship) contextMap.getRelationships().get(0);
		assertEquals(2, rel.getUpstreamExposedAggregates().size());
	}

}

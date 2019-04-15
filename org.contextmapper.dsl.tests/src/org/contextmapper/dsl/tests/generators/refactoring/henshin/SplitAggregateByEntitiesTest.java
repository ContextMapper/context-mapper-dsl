package org.contextmapper.dsl.tests.generators.refactoring.henshin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.refactoring.henshin.SplitAggregateByEntitiesRefactoring;
import org.contextmapper.dsl.tests.generators.refactoring.AbstractRefactoringTest;
import org.contextmapper.tactic.dsl.tacticdsl.Aggregate;
import org.contextmapper.tactic.dsl.tacticdsl.Module;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Iterators;

public class SplitAggregateByEntitiesTest extends AbstractRefactoringTest {

	@Test
	void canSplitWithTwoAggregates() throws IOException {
		// given
		String inputModelName = "split-agg-by-entities-test-1-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		SplitAggregateByEntitiesRefactoring refactoring = new SplitAggregateByEntitiesRefactoring("Customers");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		BoundedContext bc = contextMappingModels.get(0).getBoundedContexts().get(0);
		assertEquals(2, bc.getAggregates().size());

		for (Aggregate aggregate : bc.getAggregates()) {
			assertEquals(1, aggregate.getDomainObjects().size());
		}

		List<String> aggregateNames = bc.getAggregates().stream().map(a -> a.getName()).collect(Collectors.toList());
		assertTrue(aggregateNames.contains("Customers"));
		assertTrue(aggregateNames.contains("NewAggregate1"));
	}

	@Test
	void canRefactorIfAggregateDoesNotExist() throws IOException {
		// given
		String inputModelName = "split-agg-by-entities-test-1-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		SplitAggregateByEntitiesRefactoring refactoring = new SplitAggregateByEntitiesRefactoring("ThisAggregateDoesNotExist");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		BoundedContext bc = contextMappingModels.get(0).getBoundedContexts().get(0);
		assertEquals(1, bc.getAggregates().size());
		List<String> aggregateNames = bc.getAggregates().stream().map(a -> a.getName()).collect(Collectors.toList());
		assertTrue(aggregateNames.contains("Customers"));
	}

	@Test
	void canSplitInModule() throws IOException {
		// given
		String inputModelName = "split-agg-by-entities-test-2-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		SplitAggregateByEntitiesRefactoring refactoring = new SplitAggregateByEntitiesRefactoring("Customers");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		BoundedContext bc = contextMappingModels.get(0).getBoundedContexts().get(0);
		
		Module testModule = bc.getModules().get(0);
		
		assertEquals(2, testModule.getAggregates().size());

		for (Aggregate aggregate : testModule.getAggregates()) {
			assertEquals(1, aggregate.getDomainObjects().size());
		}

		List<String> aggregateNames = testModule.getAggregates().stream().map(a -> a.getName()).collect(Collectors.toList());
		assertTrue(aggregateNames.contains("Customers"));
		assertTrue(aggregateNames.contains("NewAggregate1"));
	}

}

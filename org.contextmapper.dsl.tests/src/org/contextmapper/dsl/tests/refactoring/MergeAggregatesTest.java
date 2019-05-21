package org.contextmapper.dsl.tests.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.KnowledgeLevel;
import org.contextmapper.dsl.contextMappingDSL.LikelihoodForChange;
import org.contextmapper.dsl.contextMappingDSL.Module;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.refactoring.MergeAggregatesRefactoring;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Iterators;

public class MergeAggregatesTest extends AbstractRefactoringTest {

	@Test
	void canMergeAggregates() throws IOException {
		// given
		String inputModelName = "merge-aggregates-test-1-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("Customers", "Addresses");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(1, contextMappingModels.get(0).getBoundedContexts().size());

		BoundedContext bc = contextMappingModels.get(0).getBoundedContexts().get(0);
		assertEquals("CustomerManagement", bc.getName());

		List<String> aggregateNames = bc.getAggregates().stream().map(a -> a.getName()).collect(Collectors.toList());
		assertEquals(1, aggregateNames.size());
		assertTrue(aggregateNames.contains("Customers"));

		Aggregate agg = bc.getAggregates().get(0);
		assertEquals(3, agg.getDomainObjects().size());
		List<String> domainObjectNames = agg.getDomainObjects().stream().map(o -> o.getName()).collect(Collectors.toList());
		assertTrue(domainObjectNames.contains("Customer"));
		assertTrue(domainObjectNames.contains("Account"));
		assertTrue(domainObjectNames.contains("Address"));
	}

	@Test
	void canMergeAggregatesInModule() throws IOException {
		// given
		String inputModelName = "merge-aggregates-test-2-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("Customers", "Addresses");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(1, contextMappingModels.get(0).getBoundedContexts().size());

		BoundedContext bc = contextMappingModels.get(0).getBoundedContexts().get(0);
		assertEquals("CustomerManagement", bc.getName());

		Module module = bc.getModules().get(0);
		List<String> aggregateNames = module.getAggregates().stream().map(a -> a.getName()).collect(Collectors.toList());
		assertEquals(1, aggregateNames.size());
		assertTrue(aggregateNames.contains("Customers"));

		Aggregate agg = module.getAggregates().get(0);
		assertEquals(3, agg.getDomainObjects().size());
		List<String> domainObjectNames = agg.getDomainObjects().stream().map(o -> o.getName()).collect(Collectors.toList());
		assertTrue(domainObjectNames.contains("Customer"));
		assertTrue(domainObjectNames.contains("Account"));
		assertTrue(domainObjectNames.contains("Address"));
	}

	@Test
	void doesNotThrowExceptionIfAggregatesAreTheSame() throws IOException {
		// given
		String inputModelName = "merge-aggregates-test-1-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("Customers", "Customers");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(1, contextMappingModels.get(0).getBoundedContexts().size());
		assertEquals(2, contextMappingModels.get(0).getBoundedContexts().get(0).getAggregates().size());
	}

	@Test
	void doesNotThrowExceptionIfFirstAggregateDoesNotExist() throws IOException {
		// given
		String inputModelName = "merge-aggregates-test-1-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("ThisAggregateDoesNotExist", "Customers");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(1, contextMappingModels.get(0).getBoundedContexts().size());
		assertEquals(2, contextMappingModels.get(0).getBoundedContexts().get(0).getAggregates().size());
	}

	@Test
	void doesNotThrowExceptionIfSecondAggregateDoesNotExist() throws IOException {
		// given
		String inputModelName = "merge-aggregates-test-1-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("Customers", "ThisAggregateDoesNotExist");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(1, contextMappingModels.get(0).getBoundedContexts().size());
		assertEquals(2, contextMappingModels.get(0).getBoundedContexts().get(0).getAggregates().size());
	}

	@Test
	void canMergeAndUpdateContextMap() throws IOException {
		// given
		String inputModelName = "merge-aggregates-test-3-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("Customers", "Addresses");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(2, contextMappingModels.get(0).getBoundedContexts().size());

		BoundedContext bc = contextMappingModels.get(0).getBoundedContexts().get(0);
		assertEquals("CustomerManagement", bc.getName());

		List<String> aggregateNames = bc.getAggregates().stream().map(a -> a.getName()).collect(Collectors.toList());
		assertEquals(1, aggregateNames.size());
		assertTrue(aggregateNames.contains("Customers"));

		Aggregate agg = bc.getAggregates().get(0);
		assertEquals(3, agg.getDomainObjects().size());
		List<String> domainObjectNames = agg.getDomainObjects().stream().map(o -> o.getName()).collect(Collectors.toList());
		assertTrue(domainObjectNames.contains("Customer"));
		assertTrue(domainObjectNames.contains("Account"));
		assertTrue(domainObjectNames.contains("Address"));

		ContextMap map = contextMappingModels.get(0).getMap();
		assertEquals(2, map.getRelationships().size());
		Optional<UpstreamDownstreamRelationship> relationshipOpt = map.getRelationships().stream().filter(rel -> rel instanceof UpstreamDownstreamRelationship)
				.map(rel -> (UpstreamDownstreamRelationship) rel).findFirst();
		assertTrue(relationshipOpt.isPresent());
		UpstreamDownstreamRelationship relationship = relationshipOpt.get();
		List<String> exposedAggregates = relationship.getUpstreamExposedAggregates().stream().map(a -> a.getName()).collect(Collectors.toList());
		assertEquals(1, exposedAggregates.size());
		assertTrue(exposedAggregates.contains("Customers"));
	}

	@Test
	void expectExceptionIfThereAreDuplicateEntityNames() throws IOException {
		// given
		String inputModelName = "merge-aggregates-test-4-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("Customers", "Addresses");

		// when / then
		Assertions.assertThrows(RefactoringInputException.class, () -> {
			refactoring.doRefactor(input);
		});
	}

	@Test
	void canTakeAttributesFromFirstAggregateByDefault() throws IOException {
		// given
		String inputModelName = "merge-aggregates-test-5-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("agg1", "agg2");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(3, contextMappingModels.get(0).getBoundedContexts().size());
		BoundedContext bc = contextMappingModels.get(0).getBoundedContexts().stream().filter(b -> b.getName().equals("AnotherContext")).findFirst().get();
		assertEquals(1, bc.getAggregates().size());
		Aggregate aggregate = bc.getAggregates().get(0);
		assertEquals(KnowledgeLevel.CONCRETE, aggregate.getKnowledgeLevel());
		assertEquals(LikelihoodForChange.NORMAL, aggregate.getLikelihoodForChange());
		assertEquals("agg1", aggregate.getName());
		assertEquals("TeamA", aggregate.getOwner().getName());
	}

	@Test
	void canTakeAttributesFromSecondAggregate() throws IOException {
		// given
		String inputModelName = "merge-aggregates-test-5-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("agg1", "agg2", true);

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(3, contextMappingModels.get(0).getBoundedContexts().size());
		BoundedContext bc = contextMappingModels.get(0).getBoundedContexts().stream().filter(b -> b.getName().equals("AnotherContext")).findFirst().get();
		assertEquals(1, bc.getAggregates().size());
		Aggregate aggregate = bc.getAggregates().get(0);
		assertEquals(KnowledgeLevel.META, aggregate.getKnowledgeLevel());
		assertEquals(LikelihoodForChange.OFTEN, aggregate.getLikelihoodForChange());
		assertEquals("agg2", aggregate.getName());
		assertEquals("TeamB", aggregate.getOwner().getName());
	}

	@Test
	void canHandleAggregateRoots() throws IOException {
		// given
		String inputModelName = "merge-aggregates-test-6-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("agg1", "agg2", true);

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(1, contextMappingModels.get(0).getBoundedContexts().size());
		BoundedContext bc = contextMappingModels.get(0).getBoundedContexts().get(0);
		assertEquals(1, bc.getAggregates().size());
		Aggregate aggregate = bc.getAggregates().get(0);
		List<DomainObject> aggregateRoots = aggregate.getDomainObjects().stream().filter(o -> o instanceof DomainObject).map(o -> (DomainObject) o).filter(o -> o.isAggregateRoot())
				.collect(Collectors.toList());
		assertEquals(1, aggregateRoots.size());
	}

}

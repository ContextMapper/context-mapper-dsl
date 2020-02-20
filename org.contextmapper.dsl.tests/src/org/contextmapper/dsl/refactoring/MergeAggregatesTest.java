package org.contextmapper.dsl.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.KnowledgeLevel;
import org.contextmapper.dsl.contextMappingDSL.LikelihoodForChange;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.refactoring.MergeAggregatesRefactoring;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MergeAggregatesTest extends AbstractRefactoringTest {

	@Test
	void canMergeAggregates() throws IOException {
		// given
		String inputModelName = "merge-aggregates-test-1-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("Customers", "Addresses");

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getBoundedContexts().size());

		BoundedContext bc = model.getBoundedContexts().get(0);
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
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("Customers", "Addresses");

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getBoundedContexts().size());

		BoundedContext bc = model.getBoundedContexts().get(0);
		assertEquals("CustomerManagement", bc.getName());

		SculptorModule module = bc.getModules().get(0);
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
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("Customers", "Customers");

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getBoundedContexts().size());
		assertEquals(2, model.getBoundedContexts().get(0).getAggregates().size());
	}

	@Test
	void doesNotThrowExceptionIfFirstAggregateDoesNotExist() throws IOException {
		// given
		String inputModelName = "merge-aggregates-test-1-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("ThisAggregateDoesNotExist", "Customers");

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getBoundedContexts().size());
		assertEquals(2, model.getBoundedContexts().get(0).getAggregates().size());
	}

	@Test
	void doesNotThrowExceptionIfSecondAggregateDoesNotExist() throws IOException {
		// given
		String inputModelName = "merge-aggregates-test-1-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("Customers", "ThisAggregateDoesNotExist");

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getBoundedContexts().size());
		assertEquals(2, model.getBoundedContexts().get(0).getAggregates().size());
	}

	@Test
	void canMergeAndUpdateContextMap() throws IOException {
		// given
		String inputModelName = "merge-aggregates-test-3-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("Customers", "Addresses");

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(2, model.getBoundedContexts().size());

		BoundedContext bc = model.getBoundedContexts().get(0);
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

		ContextMap map = model.getMap();
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
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
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
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("agg1", "agg2");

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(3, model.getBoundedContexts().size());
		BoundedContext bc = model.getBoundedContexts().stream().filter(b -> b.getName().equals("AnotherContext")).findFirst().get();
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
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("agg1", "agg2", true);

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(3, model.getBoundedContexts().size());
		BoundedContext bc = model.getBoundedContexts().stream().filter(b -> b.getName().equals("AnotherContext")).findFirst().get();
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
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeAggregatesRefactoring refactoring = new MergeAggregatesRefactoring("agg1", "agg2", true);

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getBoundedContexts().size());
		BoundedContext bc = model.getBoundedContexts().get(0);
		assertEquals(1, bc.getAggregates().size());
		Aggregate aggregate = bc.getAggregates().get(0);
		List<DomainObject> aggregateRoots = aggregate.getDomainObjects().stream().filter(o -> o instanceof DomainObject).map(o -> (DomainObject) o).filter(o -> o.isAggregateRoot())
				.collect(Collectors.toList());
		assertEquals(1, aggregateRoots.size());
	}

	@Test
	void canHandleContextMapInDifferentFile() throws IOException {
		// given
		CMLResourceContainer mainResource = getResourceCopyOfTestCML("merge-aggregates-test-7-input-2.cml");
		ResourceSet additionalResources = getResourceSetOfTestCMLFiles("merge-aggregates-test-7-input-1.cml");

		// when
		MergeAggregatesRefactoring ar = new MergeAggregatesRefactoring("Customers", "Addresses");
		ar.doRefactor(mainResource, additionalResources);
		CMLResourceContainer contextMapResource = new CMLResourceContainer(
				additionalResources.getResources().stream().filter(r -> r.getURI().toString().endsWith("merge-aggregates-test-7-input-1.cml")).findFirst().get());
		contextMapResource = reloadResource(contextMapResource);

		// then
		ContextMap contextMap = contextMapResource.getContextMappingModel().getMap();
		assertEquals(1, contextMap.getRelationships().size());
		UpstreamDownstreamRelationship rel = (UpstreamDownstreamRelationship) contextMap.getRelationships().get(0);
		assertEquals(1, rel.getUpstreamExposedAggregates().size());
	}

}

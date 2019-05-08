package org.contextmapper.dsl.tests.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.SymmetricRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.refactoring.MergeBoundedContextsRefactoring;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Iterators;

public class MergeBoundedContextsTest extends AbstractRefactoringTest {

	@Test
	void canMergeBoundedContexts() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-1-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "AnotherContext");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(1, contextMappingModels.get(0).getBoundedContexts().size());
		assertEquals("CustomerManagement", contextMappingModels.get(0).getBoundedContexts().get(0).getName());

		List<String> aggregateNames = contextMappingModels.get(0).getBoundedContexts().get(0).getAggregates().stream().map(a -> a.getName()).collect(Collectors.toList());
		assertTrue(aggregateNames.contains("Customers"));
		assertTrue(aggregateNames.contains("Addresses"));
		assertTrue(aggregateNames.contains("AnotherAggregate"));
	}

	@Test
	void doNotMergeIfBC1DoesNotExist() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-1-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("ThisBCDoesNotExist", "AnotherContext");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(2, contextMappingModels.get(0).getBoundedContexts().size());
	}

	@Test
	void doNotMergeIfBC2DoesNotExist() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-1-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "ThisBCDoesNotExist");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(2, contextMappingModels.get(0).getBoundedContexts().size());
	}

	@Test
	void doNotMergeIfOnlyOneContextGiven() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-1-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "CustomerManagement");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(2, contextMappingModels.get(0).getBoundedContexts().size());
	}

	@Test
	void canMergeWithModules() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-2-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "AnotherContext");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(1, contextMappingModels.get(0).getBoundedContexts().size());
		assertEquals("CustomerManagement", contextMappingModels.get(0).getBoundedContexts().get(0).getName());

		List<String> moduleNames = contextMappingModels.get(0).getBoundedContexts().get(0).getModules().stream().map(a -> a.getName()).collect(Collectors.toList());
		assertTrue(moduleNames.contains("mod1"));
		assertTrue(moduleNames.contains("mod2"));
	}

	@Test
	void doesRemoveBC2FromContextMap() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-3-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "AnotherContext");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(1, contextMappingModels.get(0).getMap().getBoundedContexts().size());
	}

	@Test
	void doesRemoveRelationshipsBetweenTheGivenBCs() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-4-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "AnotherContext");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(0, contextMappingModels.get(0).getMap().getRelationships().size());
	}

	@Test
	void doesFixAsymRelationshipsFromBC2ToBC1() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-5-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "AnotherContext");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(2, contextMappingModels.get(0).getMap().getRelationships().size());

		UpstreamDownstreamRelationship relationship1 = (UpstreamDownstreamRelationship) contextMappingModels.get(0).getMap().getRelationships().get(0);
		UpstreamDownstreamRelationship relationship2 = (UpstreamDownstreamRelationship) contextMappingModels.get(0).getMap().getRelationships().get(1);
		assertEquals("DeptColletion", relationship1.getUpstream().getName());
		assertEquals("CustomerManagement", relationship1.getDownstream().getName());
		assertEquals("CustomerManagement", relationship2.getUpstream().getName());
		assertEquals("DeptColletion", relationship2.getDownstream().getName());
	}
	
	@Test
	void doesFixSymRelationshipsFromBC2ToBC1() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-6-input.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "AnotherContext");

		// when
		refactoring.doRefactor(input);

		// then
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(reloadResource(input).getAllContents(), ContextMappingModel.class));
		assertEquals(2, contextMappingModels.get(0).getMap().getRelationships().size());

		SymmetricRelationship relationship1 = (SymmetricRelationship) contextMappingModels.get(0).getMap().getRelationships().get(0);
		SymmetricRelationship relationship2 = (SymmetricRelationship) contextMappingModels.get(0).getMap().getRelationships().get(1);
		assertEquals("DeptColletion", relationship1.getParticipant1().getName());
		assertEquals("CustomerManagement", relationship1.getParticipant2().getName());
		assertEquals("CustomerManagement", relationship2.getParticipant1().getName());
		assertEquals("DeptColletion", relationship2.getParticipant2().getName());
	}

}

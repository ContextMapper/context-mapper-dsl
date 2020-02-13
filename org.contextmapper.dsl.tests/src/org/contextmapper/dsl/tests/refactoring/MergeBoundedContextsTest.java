package org.contextmapper.dsl.tests.refactoring;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.KnowledgeLevel;
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
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "AnotherContext");

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getBoundedContexts().size());
		assertEquals("CustomerManagement", model.getBoundedContexts().get(0).getName());

		List<String> aggregateNames = model.getBoundedContexts().get(0).getAggregates().stream().map(a -> a.getName()).collect(Collectors.toList());
		assertTrue(aggregateNames.contains("Customers"));
		assertTrue(aggregateNames.contains("Addresses"));
		assertTrue(aggregateNames.contains("AnotherAggregate"));
	}

	@Test
	void doNotMergeIfBC1DoesNotExist() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-1-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("ThisBCDoesNotExist", "AnotherContext");

		// when
		refactoring.doRefactor(input);

		// then
		assertEquals(2, input.getContextMappingModel().getBoundedContexts().size());
	}

	@Test
	void doNotMergeIfBC2DoesNotExist() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-1-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "ThisBCDoesNotExist");

		// when
		refactoring.doRefactor(input);

		// then
		assertEquals(2, input.getContextMappingModel().getBoundedContexts().size());
	}

	@Test
	void doNotMergeIfOnlyOneContextGiven() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-1-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "CustomerManagement");

		// when
		refactoring.doRefactor(input);

		// then
		assertEquals(2, input.getContextMappingModel().getBoundedContexts().size());
	}

	@Test
	void canMergeWithModules() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-2-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "AnotherContext");

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getBoundedContexts().size());
		assertEquals("CustomerManagement", model.getBoundedContexts().get(0).getName());

		List<String> moduleNames = model.getBoundedContexts().get(0).getModules().stream().map(a -> a.getName()).collect(Collectors.toList());
		assertTrue(moduleNames.contains("mod1"));
		assertTrue(moduleNames.contains("mod2"));
	}

	@Test
	void doesRemoveBC2FromContextMap() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-3-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "AnotherContext");

		// when
		refactoring.doRefactor(input);

		// then
		assertEquals(1, input.getContextMappingModel().getMap().getBoundedContexts().size());
	}

	@Test
	void doesRemoveRelationshipsBetweenTheGivenBCs() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-4-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "AnotherContext");

		// when
		refactoring.doRefactor(input);

		// then
		assertEquals(0, input.getContextMappingModel().getMap().getRelationships().size());
	}

	@Test
	void doesFixAsymRelationshipsFromBC2ToBC1() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-5-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "AnotherContext");

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(2, model.getMap().getRelationships().size());

		UpstreamDownstreamRelationship relationship1 = (UpstreamDownstreamRelationship) model.getMap().getRelationships().get(0);
		UpstreamDownstreamRelationship relationship2 = (UpstreamDownstreamRelationship) model.getMap().getRelationships().get(1);
		assertEquals("DeptColletion", relationship1.getUpstream().getName());
		assertEquals("CustomerManagement", relationship1.getDownstream().getName());
		assertEquals("CustomerManagement", relationship2.getUpstream().getName());
		assertEquals("DeptColletion", relationship2.getDownstream().getName());
	}

	@Test
	void doesFixSymRelationshipsFromBC2ToBC1() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-6-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "AnotherContext");

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(2, model.getMap().getRelationships().size());

		SymmetricRelationship relationship1 = (SymmetricRelationship) model.getMap().getRelationships().get(0);
		SymmetricRelationship relationship2 = (SymmetricRelationship) model.getMap().getRelationships().get(1);
		assertEquals("DeptColletion", relationship1.getParticipant1().getName());
		assertEquals("CustomerManagement", relationship1.getParticipant2().getName());
		assertEquals("CustomerManagement", relationship2.getParticipant1().getName());
		assertEquals("DeptColletion", relationship2.getParticipant2().getName());
	}

	@Test
	void canMergeImplementationTechnology() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-7-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "AnotherContext");

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getBoundedContexts().size());
		BoundedContext bc = model.getBoundedContexts().get(0);
		assertEquals("Java, Scala", bc.getImplementationTechnology());
	}

	@Test
	void canMergeEmptyImplementationTechnologies() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-8-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "AnotherContext");

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getBoundedContexts().size());
		BoundedContext bc = model.getBoundedContexts().get(0);
		assertEquals("", bc.getImplementationTechnology());
	}

	@Test
	void canMergeNotSetImplementationTechnologies() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-9-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "AnotherContext");

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getBoundedContexts().size());
		BoundedContext bc = model.getBoundedContexts().get(0);
		assertNull(bc.getImplementationTechnology());
	}

	@Test
	void canMergeTeamsRealizedContexts() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-10-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("TeamA", "TeamB");

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(3, model.getBoundedContexts().size());
		BoundedContext teamBC = model.getBoundedContexts().stream().filter(bc -> bc.getName().equals("TeamA")).findFirst().get();
		assertEquals(2, teamBC.getRealizedBoundedContexts().size());
		List<String> realizedBCNames = teamBC.getRealizedBoundedContexts().stream().map(bc -> bc.getName()).collect(Collectors.toList());
		assertTrue(realizedBCNames.contains("CustomerManagement"));
		assertTrue(realizedBCNames.contains("AnotherContext"));
	}

	@Test
	void canTakeAttributesFromFirstBoundedContextByDefault() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-11-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "AnotherContext");

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getBoundedContexts().size());
		BoundedContext bc = model.getBoundedContexts().get(0);
		assertEquals("CustomerDomainVisionStatement", bc.getDomainVisionStatement());
		assertEquals(KnowledgeLevel.META, bc.getKnowledgeLevel());
		assertEquals("CustomerManagement", bc.getName());
		assertEquals(BoundedContextType.SYSTEM, bc.getType());
	}

	
	@Test
	void canTakeAttributesFromSecondBoundedContext() throws IOException {
		// given
		String inputModelName = "merge-bounded-contexts-test-11-input.cml";
		CMLResourceContainer input = getResourceCopyOfTestCML(inputModelName);
		MergeBoundedContextsRefactoring refactoring = new MergeBoundedContextsRefactoring("CustomerManagement", "AnotherContext", true);

		// when
		refactoring.doRefactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getBoundedContexts().size());
		BoundedContext bc = model.getBoundedContexts().get(0);
		assertEquals("AnotherDomainVisionStatement", bc.getDomainVisionStatement());
		assertEquals(KnowledgeLevel.CONCRETE, bc.getKnowledgeLevel());
		assertEquals("AnotherContext", bc.getName());
		assertEquals(BoundedContextType.FEATURE, bc.getType());
	}

}

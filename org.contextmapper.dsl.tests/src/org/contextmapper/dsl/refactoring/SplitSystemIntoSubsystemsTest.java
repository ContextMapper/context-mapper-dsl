/*
 * Copyright 2020 The Context Mapper Project Team
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

import static org.contextmapper.dsl.refactoring.ContextSplittingIntegrationType.ACL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.DownstreamRole;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.contextMappingDSL.SymmetricRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamRole;
import org.contextmapper.dsl.refactoring.SplitSystemIntoSubsystems.SplitBoundedContextRelationshipType;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

public class SplitSystemIntoSubsystemsTest extends AbstractRefactoringTest {

	@ParameterizedTest
	@ValueSource(strings = { "SomeContextThatDoesNotExist" })
	@NullSource
	public void canCheckIfBoundedContextNameExists(String inputBoundedContextName) throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("split-system-tier-test-1-input.cml");
		SplitSystemIntoSubsystems ar = new SplitSystemIntoSubsystems(inputBoundedContextName, "NewName4ExistingTier", "NewTierName");

		// when, then
		assertThrows(RefactoringInputException.class, () -> {
			ar.refactor(input);
		});
	}

	@Test
	public void canCheckThatInputBCIsSystem() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("split-system-tier-test-2-input.cml");
		SplitSystemIntoSubsystems ar = new SplitSystemIntoSubsystems("TestBackend", "TestBackendLogic", "TestBackendDatabase");

		// when, then
		assertThrows(RefactoringInputException.class, () -> {
			ar.refactor(input);
		});
	}

	@Test
	public void canSplitTier() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("split-system-tier-test-1-input.cml");
		SplitSystemIntoSubsystems ar = new SplitSystemIntoSubsystems("TestBackend", "TestBackendLogic", "TestBackendDatabase");

		// when
		ar.refactor(input);
		ar.persistChanges(serializer);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		assertEquals(2, model.getBoundedContexts().size());
		Set<String> contextNames = model.getBoundedContexts().stream().map(bc -> bc.getName()).collect(Collectors.toSet());
		assertTrue(contextNames.contains("TestBackendLogic"));
		assertTrue(contextNames.contains("TestBackendDatabase"));
	}

	@ParameterizedTest
	@ValueSource(strings = { "split-system-tier-test-3" })
	public void canSplitTierIfNewSystemNamesAlreadyExist(String testBasefileName) throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML(testBasefileName + "-input.cml");
		SplitSystemIntoSubsystems ar = new SplitSystemIntoSubsystems("TestBackend", "TestBackendLogic", "TestBackendDatabase");

		// when
		ar.refactor(input);
		ar.persistChanges(serializer);

		// then
		String dslText = FileUtils.readFileToString(new File(input.getURI().toFileString()), "UTF-8");
		String expectedResult = FileUtils.readFileToString(new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/refactorings/" + testBasefileName + "-output.cml"));
		assertEquals(expectedResult, dslText);
	}

	@ParameterizedTest
	@ValueSource(strings = { "split-system-tier-test-4" })
	public void canSplitTierIfNewSystemNamesAndContextMapAlreadyExist(String testBasefileName) throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML(testBasefileName + "-input.cml");
		SplitSystemIntoSubsystems ar = new SplitSystemIntoSubsystems("TestBackendLogic", "TestBackendLogic", "TestBackendDatabase");

		// when
		ar.refactor(input);
		ar.persistChanges(serializer);

		// then
		String dslText = FileUtils.readFileToString(new File(input.getURI().toFileString()), "UTF-8");
		String expectedResult = FileUtils.readFileToString(new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/refactorings/" + testBasefileName + "-output.cml"));
		assertEquals(expectedResult, dslText);
	}

	@Test
	public void canCreateUpstreamDownstreamRelationship() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("split-system-tier-test-1-input.cml");
		SplitSystemIntoSubsystems ar = new SplitSystemIntoSubsystems("TestBackend", "TestBackendLogic", "TestBackendDatabase");

		// when
		ar.refactor(input);
		ar.persistChanges(serializer);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		assertNotNull(model.getMap());
		assertEquals(1, model.getMap().getRelationships().size());
		UpstreamDownstreamRelationship relationship = (UpstreamDownstreamRelationship) model.getMap().getRelationships().get(0);
		assertEquals("TestBackendDatabase", relationship.getDownstream().getName());
		assertEquals("TestBackendLogic", relationship.getUpstream().getName());
		assertTrue(relationship.getUpstreamRoles().contains(UpstreamRole.PUBLISHED_LANGUAGE));
		assertTrue(relationship.getDownstreamRoles().contains(DownstreamRole.CONFORMIST));
	}

	@Test
	public void canAddExposedAggregatesToRelationship() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("split-system-tier-test-8-input.cml");
		SplitSystemIntoSubsystems ar = new SplitSystemIntoSubsystems("TestBackend", "TestBackendLogic", "TestBackendDatabase");
		ar.copyDomainModel(true);

		// when
		ar.refactor(input);
		ar.persistChanges(serializer);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		assertNotNull(model.getMap());
		assertEquals(1, model.getMap().getRelationships().size());
		UpstreamDownstreamRelationship relationship = (UpstreamDownstreamRelationship) model.getMap().getRelationships().get(0);
		assertEquals(1, relationship.getUpstreamExposedAggregates().size());
		assertEquals("TestAggregate", relationship.getUpstreamExposedAggregates().get(0).getName());
	}

	@Test
	public void canSwitchUpstreamDownstreamRoleInNewRelationship() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("split-system-tier-test-5-input.cml");
		SplitSystemIntoSubsystems ar = new SplitSystemIntoSubsystems("TestBackend", "TestBackendLogic", "TestBackendDatabase");
		ar.setRelationshipType(SplitBoundedContextRelationshipType.NEW_CONTEXT_BECOMES_UPSTREAM);

		// when
		ar.refactor(input);
		ar.persistChanges(serializer);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		assertNotNull(model.getMap());
		assertEquals(1, model.getMap().getRelationships().size());
		UpstreamDownstreamRelationship relationship = (UpstreamDownstreamRelationship) model.getMap().getRelationships().get(0);
		assertEquals("TestBackendLogic", relationship.getDownstream().getName());
		assertEquals("TestBackendDatabase", relationship.getUpstream().getName());
		assertTrue(relationship.getUpstreamRoles().contains(UpstreamRole.PUBLISHED_LANGUAGE));
		assertTrue(relationship.getDownstreamRoles().contains(DownstreamRole.CONFORMIST));
	}

	@Test
	public void canSwitchIntegrationTypeToACL() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("split-system-tier-test-5-input.cml");
		SplitSystemIntoSubsystems ar = new SplitSystemIntoSubsystems("TestBackend", "TestBackendLogic", "TestBackendDatabase");
		ar.setIntegrationType(ACL);

		// when
		ar.refactor(input);
		ar.persistChanges(serializer);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		assertNotNull(model.getMap());
		assertEquals(1, model.getMap().getRelationships().size());
		UpstreamDownstreamRelationship relationship = (UpstreamDownstreamRelationship) model.getMap().getRelationships().get(0);
		assertEquals("TestBackendDatabase", relationship.getDownstream().getName());
		assertEquals("TestBackendLogic", relationship.getUpstream().getName());
		assertTrue(relationship.getUpstreamRoles().contains(UpstreamRole.PUBLISHED_LANGUAGE));
		assertTrue(relationship.getDownstreamRoles().contains(DownstreamRole.ANTICORRUPTION_LAYER));
	}

	@Test
	public void canCopyDomainModel() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("split-system-tier-test-6-input.cml");
		SplitSystemIntoSubsystems ar = new SplitSystemIntoSubsystems("TestBackend", "TestBackendLogic", "TestBackendDatabase");
		ar.copyDomainModel(true);

		// when
		ar.refactor(input);
		ar.persistChanges(serializer);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		BoundedContext copiedBC = model.getBoundedContexts().stream().filter(bc -> bc.getName().equals("TestBackendDatabase")).findFirst().get();
		assertNotNull(copiedBC);
		assertEquals(1, copiedBC.getAggregates().size());
		assertEquals("TestAggregate_TestBackendDatabase", copiedBC.getAggregates().get(0).getName());
		assertEquals(1, copiedBC.getModules().size());
		assertEquals("TestModule_TestBackendDatabase", copiedBC.getModules().get(0).getName());
		assertEquals(1, copiedBC.getAggregates().get(0).getDomainObjects().size());
		assertEquals("TestEntity_TestBackendDatabase", copiedBC.getAggregates().get(0).getDomainObjects().get(0).getName());
		SculptorModule module = copiedBC.getModules().get(0);
		assertEquals(1, module.getAggregates().size());
		assertEquals(1, module.getAggregates().get(0).getDomainObjects().size());
		assertEquals("TestAggregateInModule_TestBackendDatabase", module.getAggregates().get(0).getName());
		assertEquals("TestEntityInModule_TestBackendDatabase", module.getAggregates().get(0).getDomainObjects().get(0).getName());
	}

	@Test
	public void canDefineImplementationTechnologies() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("split-system-tier-test-6-input.cml");
		SplitSystemIntoSubsystems ar = new SplitSystemIntoSubsystems("TestBackend", "TestBackendLogic", "TestBackendDatabase");
		ar.setNewSubsystemImplementationTechnology("Oracle DB");
		ar.setNewRelationshipImplementationTechnology("JDBC");

		// when
		ar.refactor(input);
		ar.persistChanges(serializer);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		BoundedContext newTier = model.getBoundedContexts().stream().filter(bc -> bc.getName().equals("TestBackendDatabase")).findFirst().get();
		UpstreamDownstreamRelationship newRelationship = (UpstreamDownstreamRelationship) model.getMap().getRelationships().get(0);
		assertEquals("Oracle DB", newTier.getImplementationTechnology());
		assertEquals("JDBC", newRelationship.getImplementationTechnology());
	}

	@Test
	public void canGetRelationshipTypeLabel() {
		// given
		SplitBoundedContextRelationshipType upstream = SplitBoundedContextRelationshipType.NEW_CONTEXT_BECOMES_UPSTREAM;
		SplitBoundedContextRelationshipType downstream = SplitBoundedContextRelationshipType.NEW_CONTEXT_BECOMES_DOWNSTREAM;

		// when
		String upstreamLabel = upstream.getLabel();
		String downstreamLabel = downstream.getLabel();

		// then
		assertEquals("Upstream", upstreamLabel);
		assertEquals("Downstream", downstreamLabel);
	}

	@ParameterizedTest
	@CsvSource({ "Upstream, NEW_CONTEXT_BECOMES_UPSTREAM", "Downstream, NEW_CONTEXT_BECOMES_DOWNSTREAM" })
	public void canGetRelationshipTypeByLabel(String inputLabel, String expectedType) {
		// given
		String label = inputLabel;

		// when
		SplitBoundedContextRelationshipType type = SplitBoundedContextRelationshipType.byLabel(label);

		// then
		assertEquals(SplitBoundedContextRelationshipType.valueOf(expectedType), type);
	}

	@Test
	public void relationshipTypeCanHandleNullValue() {
		// given
		String label = null;

		// when
		SplitBoundedContextRelationshipType type = SplitBoundedContextRelationshipType.byLabel(label);

		// then
		assertEquals(SplitBoundedContextRelationshipType.NEW_CONTEXT_BECOMES_UPSTREAM, type);
	}

	@Test
	public void canRenameContextInRelationships() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("split-system-tier-test-7-input.cml");
		SplitSystemIntoSubsystems ar = new SplitSystemIntoSubsystems("TestBackend", "TestBackendLogic", "TestBackendDatabase");

		// when
		ar.refactor(input);
		ar.persistChanges(serializer);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		assertEquals(3, model.getBoundedContexts().size());
		assertTrue(getBoundedContextNames(model.getBoundedContexts()).contains("TestBackendLogic"));
		assertFalse(getBoundedContextNames(model.getBoundedContexts()).contains("TestBackend"));

		Set<String> namesOnContextMap = getBoundedContextNames(model.getMap().getBoundedContexts());
		assertTrue(namesOnContextMap.contains("TestBackendLogic"));
		assertFalse(namesOnContextMap.contains("TestBackend"));

		UpstreamDownstreamRelationship upDownRel1 = model.getMap().getRelationships().stream().filter(r -> r.getName().equals("upDownTestRel1")).map(r -> (UpstreamDownstreamRelationship) r)
				.findFirst().get();
		UpstreamDownstreamRelationship upDownRel2 = model.getMap().getRelationships().stream().filter(r -> r.getName().equals("upDownTestRel2")).map(r -> (UpstreamDownstreamRelationship) r)
				.findFirst().get();
		SymmetricRelationship symRel1 = model.getMap().getRelationships().stream().filter(r -> r.getName().equals("symTestRel1")).map(r -> (SymmetricRelationship) r).findFirst().get();
		SymmetricRelationship symRel2 = model.getMap().getRelationships().stream().filter(r -> r.getName().equals("symTestRel2")).map(r -> (SymmetricRelationship) r).findFirst().get();
		assertEquals("TestBackendLogic", upDownRel1.getUpstream().getName());
		assertEquals("TestBackendLogic", upDownRel2.getDownstream().getName());
		assertEquals("TestBackendLogic", symRel1.getParticipant1().getName());
		assertEquals("TestBackendLogic", symRel2.getParticipant2().getName());
	}

	private Set<String> getBoundedContextNames(List<BoundedContext> contexts) {
		return contexts.stream().map(bc -> bc.getName()).collect(Collectors.toSet());
	}

}

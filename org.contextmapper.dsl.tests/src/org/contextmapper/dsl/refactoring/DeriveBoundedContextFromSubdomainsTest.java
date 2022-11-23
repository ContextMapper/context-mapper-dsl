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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Parameter;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.Service;
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.google.common.collect.Sets;

public class DeriveBoundedContextFromSubdomainsTest extends AbstractRefactoringTest {

	@ParameterizedTest
	@ValueSource(strings = { "derive-bc-from-subdomain-test-1-input.cml", "derive-bc-from-subdomain-test-2-input.cml", "derive-bc-from-subdomain-test-3-input.cml",
			"derive-bc-from-subdomain-test-4-input.cml", "derive-bc-from-subdomain-test-5-input.cml" })
	public void canDeriveBoundedContextFromSubdomain(String inputFile) throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML(inputFile);

		// when
		Set<String> subdomains = Sets.newHashSet(Arrays.asList(new String[] { "CustomerDomain" }));
		DeriveBoundedContextFromSubdomains ar = new DeriveBoundedContextFromSubdomains("NewTestBC", subdomains);
		ar.refactor(input);
		ar.persistChanges(serializer);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		assertEquals(1, model.getBoundedContexts().size());
		assertNotNull(model.getBoundedContexts().get(0));

		BoundedContext bc = model.getBoundedContexts().get(0);
		assertEquals("NewTestBC", bc.getName());
		assertEquals(BoundedContextType.FEATURE, bc.getType());
		assertEquals(1, bc.getAggregates().size());
		assertNotNull(bc.getAggregates().get(0));

		Aggregate aggregate = bc.getAggregates().get(0);
		assertEquals("CustomerDomainAggregate", aggregate.getName());
		assertEquals(1, aggregate.getDomainObjects().size());
		assertNotNull(aggregate.getDomainObjects().get(0));
		assertTrue(aggregate.getDomainObjects().get(0) instanceof Entity);

		Entity entity = (Entity) aggregate.getDomainObjects().get(0);
		assertEquals("Customer", entity.getName());
		assertFalse(entity.isAggregateRoot());
		assertEquals(1, entity.getAttributes().size());

		Attribute idAttr = entity.getAttributes().get(0);
		assertNotNull(idAttr);
		assertEquals("customerId", idAttr.getName());
		assertEquals("String", idAttr.getType());
		assertTrue(idAttr.isKey());
	}

	@ParameterizedTest
	@ValueSource(strings = { "derive-bc-from-subdomain-test-1-input.cml", "derive-bc-from-subdomain-test-6-input.cml", "derive-bc-from-subdomain-test-7-input.cml" })
	public void canCopyAndEnhanceServices(String inputFile) throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML(inputFile);

		// when
		Set<String> subdomains = Sets.newHashSet(Arrays.asList(new String[] { "CustomerDomain" }));
		DeriveBoundedContextFromSubdomains ar = new DeriveBoundedContextFromSubdomains("NewTestBC", subdomains);
		ar.refactor(input);
		ar.persistChanges(serializer);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		assertEquals(1, model.getBoundedContexts().size());
		assertNotNull(model.getBoundedContexts().get(0));

		BoundedContext bc = model.getBoundedContexts().get(0);
		assertEquals("NewTestBC", bc.getName());
		assertEquals(BoundedContextType.FEATURE, bc.getType());
		assertEquals(1, bc.getAggregates().size());
		assertNotNull(bc.getAggregates().get(0));

		Aggregate aggregate = bc.getAggregates().get(0);
		assertEquals(1, aggregate.getServices().size());

		Service service = aggregate.getServices().get(0);
		assertEquals("CustomerService", service.getName());
		assertEquals(2, service.getOperations().size());

		ServiceOperation operation = service.getOperations().stream().filter(o -> o.getName().equals("createCustomer")).findFirst().get();
		assertEquals("createCustomer", operation.getName());
		assertEquals("boolean", operation.getReturnType().getType());
		assertEquals(1, operation.getParameters().size());

		Parameter parameter = operation.getParameters().get(0);
		assertEquals("createCustomerInput", parameter.getName());
		assertEquals("CreateCustomerInput", parameter.getParameterType().getType());
	}

	@Test
	public void canHandleDuplicateAggregateNameInOtherContext() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("derive-bc-from-subdomain-duplicate-aggregate-name-test-1-input.cml");

		// when
		Set<String> subdomains = Sets.newHashSet(Arrays.asList(new String[] { "CustomerDomain" }));
		DeriveBoundedContextFromSubdomains ar = new DeriveBoundedContextFromSubdomains("NewTestBC", subdomains);
		ar.refactor(input);
		ar.persistChanges(serializer);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		assertEquals(2, model.getBoundedContexts().size());

		BoundedContext generatedContext = model.getBoundedContexts().stream().filter(bc -> bc.getName().equals("NewTestBC")).findFirst().get();
		assertNotNull(generatedContext);
		assertEquals(1, generatedContext.getAggregates().size());
		assertEquals("CustomerDomainAggregate_2", generatedContext.getAggregates().get(0).getName());
	}

	@Test
	public void canCopyEntityAttributesAndReferences() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("derive-bc-from-subdomain-entity-attributes-test-1-input.cml");

		// when
		Set<String> subdomains = Sets.newHashSet(Arrays.asList(new String[] { "CustomerDomain" }));
		DeriveBoundedContextFromSubdomains ar = new DeriveBoundedContextFromSubdomains("NewTestBC", subdomains);
		ar.refactor(input);
		ar.persistChanges(serializer);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		assertEquals(1, model.getBoundedContexts().size());
		assertNotNull(model.getBoundedContexts().get(0));

		BoundedContext bc = model.getBoundedContexts().get(0);
		assertEquals("NewTestBC", bc.getName());
		assertEquals(BoundedContextType.FEATURE, bc.getType());
		assertEquals(1, bc.getAggregates().size());
		assertNotNull(bc.getAggregates().get(0));

		Aggregate aggregate = bc.getAggregates().get(0);
		assertEquals(2, aggregate.getDomainObjects().size());

		Entity entity = (Entity) aggregate.getDomainObjects().stream().filter(e -> e.getName().equals("Customer")).findAny().get();
		assertEquals(3, entity.getAttributes().size());
		assertEquals(2, entity.getReferences().size());
	}

	@ParameterizedTest
	@ValueSource(strings = { "derive-bc-from-subdomain-test-9-input.cml" })
	public void canCreateServiceOperationParametersFromFeatures(String inputFile) throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML(inputFile);

		// when
		Set<String> subdomains = Sets.newHashSet(Arrays.asList(new String[] { "CustomerDomain" }));
		DeriveBoundedContextFromSubdomains ar = new DeriveBoundedContextFromSubdomains("NewTestBC", subdomains);
		ar.refactor(input);
		ar.persistChanges(serializer);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		assertEquals(1, model.getBoundedContexts().size());
		assertNotNull(model.getBoundedContexts().get(0));

		BoundedContext bc = model.getBoundedContexts().get(0);
		assertEquals("NewTestBC", bc.getName());
		assertEquals(BoundedContextType.FEATURE, bc.getType());
		assertEquals(1, bc.getAggregates().size());
		assertNotNull(bc.getAggregates().get(0));

		Aggregate aggregate = bc.getAggregates().get(0);
		assertEquals(1, aggregate.getServices().size());

		Service service = aggregate.getServices().get(0);
		assertEquals("CustomerService", service.getName());
		assertEquals(2, service.getOperations().size());

		ServiceOperation createOperation = service.getOperations().stream().filter(o -> o.getName().equals("createCustomer")).findFirst().get();
		assertEquals("createCustomer", createOperation.getName());
		assertEquals("String", createOperation.getReturnType().getType());
		assertEquals(1, createOperation.getParameters().size());

		Parameter parameter = createOperation.getParameters().get(0);
		assertEquals("customer", parameter.getName());
		assertEquals("Customer", parameter.getParameterType().getDomainObjectType().getName());

		ServiceOperation readOperation = service.getOperations().stream().filter(o -> o.getName().equals("readCustomer")).findFirst().get();
		assertEquals("readCustomer", readOperation.getName());
		assertEquals("Customer", readOperation.getReturnType().getDomainObjectType().getName());
		assertEquals(1, readOperation.getParameters().size());

		parameter = readOperation.getParameters().get(0);
		assertEquals("id", parameter.getName());
		assertEquals("String", parameter.getParameterType().getType());
	}

	@ParameterizedTest
	@ValueSource(strings = { "derive-bc-from-subdomain-test-10-input.cml" })
	public void canCreateServiceOperationParametersFromFeaturesInModule(String inputFile) throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML(inputFile);

		// when
		Set<String> subdomains = Sets.newHashSet(Arrays.asList(new String[] { "CustomerDomain" }));
		DeriveBoundedContextFromSubdomains ar = new DeriveBoundedContextFromSubdomains("NewTestBC", subdomains);
		ar.refactor(input);
		ar.persistChanges(serializer);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		assertEquals(1, model.getBoundedContexts().size());
		assertNotNull(model.getBoundedContexts().get(0));

		BoundedContext bc = model.getBoundedContexts().get(0);
		assertEquals("NewTestBC", bc.getName());
		assertEquals(BoundedContextType.FEATURE, bc.getType());
		assertEquals(1, bc.getModules().get(0).getAggregates().size());
		assertNotNull(bc.getModules().get(0).getAggregates().get(0));

		Aggregate aggregate = bc.getModules().get(0).getAggregates().get(0);
		assertEquals(1, aggregate.getServices().size());

		Service service = aggregate.getServices().get(0);
		assertEquals("CustomerService", service.getName());
		assertEquals(2, service.getOperations().size());

		ServiceOperation createOperation = service.getOperations().stream().filter(o -> o.getName().equals("createCustomer")).findFirst().get();
		assertEquals("createCustomer", createOperation.getName());
		assertEquals("String", createOperation.getReturnType().getType());
		assertEquals(1, createOperation.getParameters().size());

		Parameter parameter = createOperation.getParameters().get(0);
		assertEquals("customer", parameter.getName());
		assertEquals("Customer", parameter.getParameterType().getDomainObjectType().getName());

		ServiceOperation readOperation = service.getOperations().stream().filter(o -> o.getName().equals("readCustomer")).findFirst().get();
		assertEquals("readCustomer", readOperation.getName());
		assertEquals("Customer", readOperation.getReturnType().getDomainObjectType().getName());
		assertEquals(1, readOperation.getParameters().size());

		parameter = readOperation.getParameters().get(0);
		assertEquals("id", parameter.getName());
		assertEquals("String", parameter.getParameterType().getType());
	}

	@Test
	public void canThrowExceptionIfNoCorrectSubdomainNameIsProvided() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("derive-bc-from-subdomain-test-1-input.cml");

		// when, then
		Set<String> subdomains = Sets.newHashSet(Arrays.asList(new String[] { "JustSomeTestDomainNotExisting" }));
		DeriveBoundedContextFromSubdomains ar = new DeriveBoundedContextFromSubdomains("NewTestBC", subdomains);
		assertThrows(RefactoringInputException.class, () -> {
			ar.refactor(input);
		});
	}

	@Test
	public void canThrowExceptionIfEntityAlreadyExistsInOtherBoundedContext() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("derive-bc-from-subdomain-test-8-input.cml");

		// when, then
		Set<String> subdomains = Sets.newHashSet(Arrays.asList(new String[] { "CustomerDomain" }));
		DeriveBoundedContextFromSubdomains ar = new DeriveBoundedContextFromSubdomains("CustomerManagementContext", subdomains);
		assertThrows(ContextMapperApplicationException.class, () -> {
			ar.refactor(input);
		});
	}

	@Test
	public void canHandleClaimsIntegrationTest() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("derive-bc-from-subdomain-test-11-input.cml");

		// when
		Set<String> subdomains = Sets.newHashSet(Arrays.asList(new String[] { "ClaimsManagement" }));
		DeriveBoundedContextFromSubdomains ar = new DeriveBoundedContextFromSubdomains("ClaimsManagement", subdomains);
		ar.refactor(input);
		ar.persistChanges(serializer);

		// then
		String dslText = FileUtils.readFileToString(new File(input.getURI().toFileString()), "UTF-8");
		String expectedResult = FileUtils.readFileToString(new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/refactorings/derive-bc-from-subdomain-test-11-output.cml"));
		assertEquals(expectedResult, dslText);
	}

}

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

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Parameter;
import org.contextmapper.tactic.dsl.tacticdsl.Service;
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.google.common.collect.Sets;

public class DeriveBoundedContextFromSubdomainsTest extends AbstractRefactoringTest {

	@ParameterizedTest
	@ValueSource(strings = { "derive-bc-from-subdomain-test-1-input.cml" })
	public void canDeriveBoundedContextFromSubdomain(String inputFile) throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML(inputFile);

		// when
		Set<String> subdomains = Sets.newHashSet(Arrays.asList(new String[] { "CustomerDomain" }));
		DeriveBoundedContextFromSubdomains ar = new DeriveBoundedContextFromSubdomains("NewTestBC", subdomains);
		ar.doRefactor(input);

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

		Attribute attr = entity.getAttributes().get(0);
		assertNotNull(attr);
		assertEquals("customerId", attr.getName());
		assertEquals("CustomerID", attr.getType());
	}

	@Test
	public void canCopyAndEnhanceServices() throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("derive-bc-from-subdomain-test-1-input.cml");

		// when
		Set<String> subdomains = Sets.newHashSet(Arrays.asList(new String[] { "CustomerDomain" }));
		DeriveBoundedContextFromSubdomains ar = new DeriveBoundedContextFromSubdomains("NewTestBC", subdomains);
		ar.doRefactor(input);

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
		assertEquals(1, service.getOperations().size());

		ServiceOperation operation = service.getOperations().get(0);
		assertEquals("createCustomer", operation.getName());
		assertEquals("CreateCustomerOutput", operation.getReturnType().getType());
		assertEquals(1, operation.getParameters().size());

		Parameter parameter = operation.getParameters().get(0);
		assertEquals("input", parameter.getName());
		assertEquals("CreateCustomerInput", parameter.getParameterType().getType());
	}

	@Test
	public void canFindUniqueBoundedContextName() throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("derive-bc-from-subdomain-test-3-input.cml");

		// when
		Set<String> subdomains = Sets.newHashSet(Arrays.asList(new String[] { "CustomerDomain", "AnotherDomain" }));
		DeriveBoundedContextFromSubdomains ar = new DeriveBoundedContextFromSubdomains("NewTestBC", subdomains);
		ar.doRefactor(input);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		assertEquals(2, model.getBoundedContexts().size());
		Set<String> bcNames = model.getBoundedContexts().stream().map(bc -> bc.getName()).collect(Collectors.toSet());
		assertTrue(bcNames.contains("NewTestBC"));
		assertTrue(bcNames.contains("NewTestBC_2"));
	}

	@Test
	public void canThrowExceptionIfNoCorrectSubdomainNameIsProvided() throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("derive-bc-from-subdomain-test-1-input.cml");

		// when, then
		Set<String> subdomains = Sets.newHashSet(Arrays.asList(new String[] { "JustSomeTestDomainNotExisting" }));
		DeriveBoundedContextFromSubdomains ar = new DeriveBoundedContextFromSubdomains("NewTestBC", subdomains);
		assertThrows(RefactoringInputException.class, () -> {
			ar.doRefactor(input);
		});
	}

}

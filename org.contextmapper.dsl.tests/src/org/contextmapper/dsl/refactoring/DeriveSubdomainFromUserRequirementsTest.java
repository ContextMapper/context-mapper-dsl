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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.CollectionType;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.Service;
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.google.common.collect.Sets;

public class DeriveSubdomainFromUserRequirementsTest extends AbstractRefactoringTest {

	@ParameterizedTest
	@ValueSource(strings = { "derive-subdomain-from-user-story-test-1-input.cml", "derive-subdomain-from-user-story-test-2-input.cml",
			"derive-subdomain-from-user-story-test-3-input.cml", "derive-subdomain-from-user-story-test-4-input.cml", "derive-subdomain-from-user-story-test-5-input.cml",
			"derive-subdomain-from-user-story-test-6-input.cml" })
	public void canDeriveSubdomainFromUserStory(String inputFile) throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML(inputFile);

		// when
		Set<String> userStories = Sets.newHashSet(Arrays.asList(new String[] { "US1_Create", "Story_to_be_Ignored", "UseCase_to_be_Ignored" }));
		DeriveSubdomainFromUserRequirements ar = new DeriveSubdomainFromUserRequirements("InsuranceDomain", "Customers", userStories);
		ar.refactor(input);
		ar.persistChanges();

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		assertEquals(1, model.getDomains().size());
		assertNotNull(model.getDomains().get(0));

		Domain domain = model.getDomains().get(0);
		assertEquals(1, domain.getSubdomains().size());
		assertNotNull(domain.getSubdomains().get(0));

		Subdomain subdomain = domain.getSubdomains().get(0);
		Set<String> supportedFeatures = subdomain.getSupportedFeatures().stream().map(f -> f.getName()).collect(Collectors.toSet());
		assertTrue(supportedFeatures.contains("US1_Create"));
		assertEquals("Customers", subdomain.getName());
		assertEquals("Aims at promoting the following benefit for a Insurance Employee: I am able to manage the customer data and offer contracts.",
				subdomain.getDomainVisionStatement());
		assertEquals(1, subdomain.getEntities().size());
		assertEquals(1, subdomain.getServices().size());

		Entity entity = subdomain.getEntities().get(0);
		assertEquals("Customer", entity.getName());

		Service service = subdomain.getServices().get(0);
		assertEquals("US1_CreateService", service.getName());
		assertEquals(1, service.getOperations().size());

		ServiceOperation operation = service.getOperations().get(0);
		assertEquals("createCustomer", operation.getName());
	}

	@Test
	public void canMergeDomainVisionStatements() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("derive-subdomain-from-user-story-test-existing-dvs-1.cml");

		// when
		Set<String> userStories = Sets.newHashSet(Arrays.asList(new String[] { "US1_Create", "Story_to_be_Ignored" }));
		DeriveSubdomainFromUserRequirements ar = new DeriveSubdomainFromUserRequirements("InsuranceDomain", "Customers", userStories);
		ar.refactor(input);
		ar.persistChanges();

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		assertEquals(1, model.getDomains().size());
		assertNotNull(model.getDomains().get(0));

		Domain domain = model.getDomains().get(0);
		assertEquals(1, domain.getSubdomains().size());
		assertNotNull(domain.getSubdomains().get(0));

		Subdomain subdomain = domain.getSubdomains().get(0);
		assertEquals("Customers", subdomain.getName());
		assertEquals("existing dvs 1; existing dvs 2; Aims at promoting the following benefit for a Insurance Employee: I am able to manage the customer data and offer contracts.",
				subdomain.getDomainVisionStatement());
	}

	@ParameterizedTest
	@ValueSource(strings = { "" })
	@NullSource
	public void canThrowExceptionIfNoDomainNameProvided(String domainName) throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("derive-subdomain-from-user-story-test-1-input.cml");

		// when, then
		Set<String> userStories = Sets.newHashSet(Arrays.asList(new String[] { "US1_Create" }));
		DeriveSubdomainFromUserRequirements ar = new DeriveSubdomainFromUserRequirements(domainName, "Customers", userStories);
		assertThrows(RefactoringInputException.class, () -> {
			ar.refactor(input);
		});
	}

	@ParameterizedTest
	@ValueSource(strings = { "" })
	@NullSource
	public void canThrowExceptionIfNoSubdomainNameProvided(String subdomainName) throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("derive-subdomain-from-user-story-test-1-input.cml");

		// when, then
		Set<String> userStories = Sets.newHashSet(Arrays.asList(new String[] { "US1_Create" }));
		DeriveSubdomainFromUserRequirements ar = new DeriveSubdomainFromUserRequirements("InsuranceDomain", subdomainName, userStories);
		assertThrows(RefactoringInputException.class, () -> {
			ar.refactor(input);
		});
	}

	@Test
	public void canThrowExceptionIfNoRequirementsProvided() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("derive-subdomain-from-user-story-test-1-input.cml");

		// when, then
		Set<String> userStories = Sets.newHashSet(Arrays.asList(new String[] { "not-existing-story" }));
		DeriveSubdomainFromUserRequirements ar = new DeriveSubdomainFromUserRequirements("InsuranceDomain", "Customers", userStories);
		assertThrows(RefactoringInputException.class, () -> {
			ar.refactor(input);
		});
	}

	@ParameterizedTest
	@ValueSource(strings = { "derive-subdomain-from-user-story-test-7-input.cml", "derive-subdomain-from-user-story-test-8-input.cml" })
	public void canCreateEntityAttributes(String inputFile) throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML(inputFile);

		// when
		Set<String> userStories = Sets.newHashSet(Arrays.asList(new String[] { "US1_Create" }));
		DeriveSubdomainFromUserRequirements ar = new DeriveSubdomainFromUserRequirements("InsuranceDomain", "Customers", userStories);
		ar.refactor(input);
		ar.persistChanges();

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		Subdomain subdomain = model.getDomains().get(0).getSubdomains().get(0);
		assertNotNull(subdomain);
		Entity customerEntity = subdomain.getEntities().stream().filter(e -> e.getName().equals("Customer")).findFirst().get();
		assertNotNull(customerEntity);
		Attribute firstNameAttribute = customerEntity.getAttributes().stream().filter(a -> a.getName().equals("firstname")).findFirst().get();
		Attribute lastNameAttribute = customerEntity.getAttributes().stream().filter(a -> a.getName().equals("lastname")).findFirst().get();
		assertNotNull(firstNameAttribute);
		assertNotNull(lastNameAttribute);
	}

	@ParameterizedTest
	@ValueSource(strings = { "derive-subdomain-from-user-story-test-9-input.cml", "derive-subdomain-from-user-story-test-12-input.cml" })
	public void canCreateContainmentReference(String inputFile) throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML(inputFile);

		// when
		Set<String> userStories = Sets.newHashSet(Arrays.asList(new String[] { "US1_Create" }));
		DeriveSubdomainFromUserRequirements ar = new DeriveSubdomainFromUserRequirements("InsuranceDomain", "Customers", userStories);
		ar.refactor(input);
		ar.persistChanges();

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		Subdomain subdomain = model.getDomains().get(0).getSubdomains().get(0);
		assertNotNull(subdomain);
		Entity customerEntity = subdomain.getEntities().stream().filter(e -> e.getName().equals("Customer")).findFirst().get();
		assertNotNull(customerEntity);
		assertEquals(1, customerEntity.getReferences().size());
		Reference addressReference = customerEntity.getReferences().stream().filter(r -> r.getName().equals("addressList")).findFirst().get();
		assertNotNull(addressReference);
		assertEquals(CollectionType.LIST, addressReference.getCollectionType());
		assertEquals("Address", addressReference.getDomainObjectType().getName());
	}

	@ParameterizedTest
	@ValueSource(strings = { "derive-subdomain-from-user-story-test-10-input.cml", "derive-subdomain-from-user-story-test-11-input.cml" })
	public void canIgnoreEmptyReferences(String inputFile) throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML(inputFile);

		// when
		Set<String> userStories = Sets.newHashSet(Arrays.asList(new String[] { "US1_Create" }));
		DeriveSubdomainFromUserRequirements ar = new DeriveSubdomainFromUserRequirements("InsuranceDomain", "Customers", userStories);
		ar.refactor(input);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		Subdomain subdomain = model.getDomains().get(0).getSubdomains().get(0);
		assertNotNull(subdomain);
		Entity customerEntity = subdomain.getEntities().stream().filter(e -> e.getName().equals("Customer")).findFirst().get();
		assertNotNull(customerEntity);
		assertEquals(0, customerEntity.getReferences().size());
	}

}

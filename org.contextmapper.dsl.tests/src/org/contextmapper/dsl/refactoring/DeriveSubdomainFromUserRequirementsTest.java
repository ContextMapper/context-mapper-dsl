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

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
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
		CMLResourceContainer input = getResourceCopyOfTestCML(inputFile);

		// when
		Set<String> userStories = Sets.newHashSet(Arrays.asList(new String[] { "US1_Create", "Story_to_be_Ignored" }));
		DeriveSubdomainFromUserRequirements ar = new DeriveSubdomainFromUserRequirements("InsuranceDomain", "Customers", userStories);
		ar.doRefactor(input);

		// then
		ContextMappingModel model = reloadResource(input).getContextMappingModel();
		assertEquals(1, model.getDomains().size());
		assertNotNull(model.getDomains().get(0));

		Domain domain = model.getDomains().get(0);
		assertEquals(1, domain.getSubdomains().size());
		assertNotNull(domain.getSubdomains().get(0));

		Subdomain subdomain = domain.getSubdomains().get(0);
		assertEquals("Customers", subdomain.getName());
		assertEquals(1, subdomain.getEntities().size());
		assertEquals(1, subdomain.getServices().size());

		Entity entity = subdomain.getEntities().get(0);
		assertEquals("Customer", entity.getName());

		Service service = subdomain.getServices().get(0);
		assertEquals("CustomerService", service.getName());
		assertEquals(1, service.getOperations().size());

		ServiceOperation operation = service.getOperations().get(0);
		assertEquals("createCustomer", operation.getName());
	}

	@ParameterizedTest
	@ValueSource(strings = { "" })
	@NullSource
	public void canThrowExceptionIfNoDomainNameProvided(String domainName) throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("derive-subdomain-from-user-story-test-1-input.cml");

		// when, then
		Set<String> userStories = Sets.newHashSet(Arrays.asList(new String[] { "US1_Create" }));
		DeriveSubdomainFromUserRequirements ar = new DeriveSubdomainFromUserRequirements(domainName, "Customers", userStories);
		assertThrows(RefactoringInputException.class, () -> {
			ar.doRefactor(input);
		});
	}

	@ParameterizedTest
	@ValueSource(strings = { "" })
	@NullSource
	public void canThrowExceptionIfNoSubdomainNameProvided(String subdomainName) throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("derive-subdomain-from-user-story-test-1-input.cml");

		// when, then
		Set<String> userStories = Sets.newHashSet(Arrays.asList(new String[] { "US1_Create" }));
		DeriveSubdomainFromUserRequirements ar = new DeriveSubdomainFromUserRequirements("InsuranceDomain", subdomainName, userStories);
		assertThrows(RefactoringInputException.class, () -> {
			ar.doRefactor(input);
		});
	}

	@Test
	public void canThrowExceptionIfNoRequirementsProvided() throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("derive-subdomain-from-user-story-test-1-input.cml");

		// when, then
		Set<String> userStories = Sets.newHashSet(Arrays.asList(new String[] { "not-existing-story" }));
		DeriveSubdomainFromUserRequirements ar = new DeriveSubdomainFromUserRequirements("InsuranceDomain", "Customers", userStories);
		assertThrows(RefactoringInputException.class, () -> {
			ar.doRefactor(input);
		});
	}

}

/*
 * Copyright 2023 The Context Mapper Project Team
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
package org.contextmapper.dsl.cml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.junit.jupiter.api.Test;

public class CMLModelDomainAndSubdomainResolverTest extends AbstractCMLInputFileTest {

	@Test
	public void canResolveSubDomainsInSingleFile() throws IOException {
		// given
		CMLResource input = getOriginalResourceOfTestCML("simple-single-file-test.cml");

		// when
		Set<Subdomain> declaredSubdomains = new CMLModelDomainAndSubdomainResolver(input.getContextMappingModel())
				.resolveAllSubdomains();
		Set<String> subdomainNames = declaredSubdomains.stream().map(Subdomain::getName).collect(Collectors.toSet());

		// then
		assertEquals(3, declaredSubdomains.size());
		assertTrue(subdomainNames.contains("TestSubDomain1"));
		assertTrue(subdomainNames.contains("TestSubDomain2"));
		assertTrue(subdomainNames.contains("TestSubDomain3"));
	}

	@Test
	public void canResolveImportedSubdomains() throws IOException {
		// given
		CMLResource input = getOriginalResourceOfTestCML("root-file-with-imports-test-1.cml");

		// when
		Set<Subdomain> declaredSubdomains = new CMLModelDomainAndSubdomainResolver(input.getContextMappingModel())
				.resolveAllSubdomains();
		Set<String> subdomainNames = declaredSubdomains.stream().map(Subdomain::getName).collect(Collectors.toSet());

		// then
		assertEquals(2, declaredSubdomains.size());
		assertTrue(subdomainNames.contains("DirectSubdomain"));
		assertTrue(subdomainNames.contains("OtherSubdomain"));
	}

	@Test
	public void canResolveImportedDomains() throws IOException {
		// given
		CMLResource input = getOriginalResourceOfTestCML("root-file-with-imports-test-2.cml");

		// when
		Set<Subdomain> declaredSubdomains = new CMLModelDomainAndSubdomainResolver(input.getContextMappingModel())
				.resolveAllSubdomains();
		Set<String> subdomainNames = declaredSubdomains.stream().map(Subdomain::getName).collect(Collectors.toSet());

		// then
		assertEquals(3, declaredSubdomains.size());
		assertTrue(subdomainNames.contains("DirectSubdomain"));
		assertTrue(subdomainNames.contains("OtherSubdomain1"));
		assertTrue(subdomainNames.contains("OtherSubdomain2"));
	}

	@Test
	public void canResolveDomain4Subdomain() throws IOException {
		// given
		CMLResource input = getOriginalResourceOfTestCML("simple-single-file-test.cml");
		ContextMappingModel model = input.getContextMappingModel();
		CMLModelDomainAndSubdomainResolver resolver = new CMLModelDomainAndSubdomainResolver(model);
		Set<Subdomain> allSubdomains = resolver.resolveAllSubdomains();
		Subdomain subdomain1 = allSubdomains.stream().filter(s -> s.getName().equals("TestSubDomain1")).findAny().get();
		Subdomain subdomain3 = allSubdomains.stream().filter(s -> s.getName().equals("TestSubDomain3")).findAny().get();

		// when
		Domain domain1 = resolver.resolveDomain4Subdomain(subdomain1.getName());
		Domain domain2 = resolver.resolveDomain4Subdomain(subdomain3.getName());

		// then
		assertEquals("TestDomain", domain1.getName());
		assertEquals("OtherDomain", domain2.getName());
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/subdomain-resolving/";
	}

}

/*
 * Copyright 2018 The Context Mapper Project Team
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
package org.contextmapper.dsl

import com.google.inject.Inject
import java.util.stream.Collectors
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.contextMappingDSL.Domain
import org.contextmapper.dsl.contextMappingDSL.SubDomainType
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.util.ParsingErrorAssertions.*
import static org.junit.jupiter.api.Assertions.*
import static org.contextmapper.dsl.validation.ValidationMessages.*
import org.contextmapper.dsl.tests.ContextMappingDSLInjectorProvider

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class DomainDSLParsingTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper
	
	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

	@Test
	def void canDefineDomain() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext

			Domain Insurance
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

	}

	@Test
	def void canDefineSubdomains() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext

			Domain Insurance {
				Subdomain CoreDomain
				Subdomain support1
				Subdomain generic
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		
		val Domain domain = result.domains.get(0);
		assertEquals(3, domain.subdomains.size);

		val subdomainNames = domain.subdomains.stream.map[name].collect(Collectors.toList);
		assertTrue(subdomainNames.contains("CoreDomain"));
		assertTrue(subdomainNames.contains("support1"));
		assertTrue(subdomainNames.contains("generic"));
	}

	@Test
	def void canMapSubdomainToBoundedContexts() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext implements CoreDomain

			Domain Insurance {
				Subdomain CoreDomain {
					type = CORE_DOMAIN
				}
				Subdomain support1 {
					type = SUPPORTING_DOMAIN
				}
				Subdomain generic {
					type = GENERIC_SUBDOMAIN
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.boundedContexts.get(0).implementedDomainParts.size);
		assertEquals("CoreDomain", result.boundedContexts.get(0).implementedDomainParts.get(0).name);
	}

	@Test
	def void canMapSubdomainsToBoundedContexts() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext implements CoreDomain, support1

			Domain Insurance {
				Subdomain CoreDomain {
					type = CORE_DOMAIN
				}
				Subdomain support1 {
					type = SUPPORTING_DOMAIN
				}
				Subdomain generic {
					type = GENERIC_SUBDOMAIN
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(2, result.boundedContexts.get(0).implementedDomainParts.size);

		val subdomainNames = result.boundedContexts.get(0).implementedDomainParts.stream.map[name].collect(Collectors.toList);
		assertTrue(subdomainNames.contains("CoreDomain"));
		assertTrue(subdomainNames.contains("support1"));
	}

	@Test
	def void canDefineVisionStatementOnDomain() {
		// given
		val String dslSnippet = '''
			Domain Insurance {
				domainVisionStatement = "my domain vision for this domain"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals("my domain vision for this domain", result.domains.get(0).domainVisionStatement);
	}

	@Test
	def void canDefineVisionStatementOnSubdomain() {
		// given
		val String dslSnippet = '''
			Domain Insurance {
				Subdomain CoreDomain {
					type = CORE_DOMAIN
					domainVisionStatement = "my domain vision for this subdomain"
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals("my domain vision for this subdomain", result.domains.get(0).subdomains.get(0).domainVisionStatement);
	}
	
	@Test
	def void canImplementWholeDomain() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext implements Insurance
			
			Domain Insurance {
				Subdomain CoreDomain {
					type = CORE_DOMAIN
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals("Insurance", result.boundedContexts.get(0).implementedDomainParts.get(0).name);
	}
	
	@Test
	def void cannotImplementSubdomainAlreadyImplementedByDomain() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext implements Insurance, CoreDomain
			
			Domain Insurance {
				Subdomain CoreDomain {
					type = CORE_DOMAIN
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.BOUNDED_CONTEXT, "",
			String.format(ALREADY_IMPLEMENTED_SUBDOMAIN, "CoreDomain", "Insurance"));
	} 
	
	@Test
	def void canWarnIfMultipleDomainsAreImplemented() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext implements Insurance, Banking
			
			Domain Insurance {
				Subdomain CoreDomain {
					type = CORE_DOMAIN
				}
			}
			Domain Banking
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertWarning(result, ContextMappingDSLPackage.Literals.BOUNDED_CONTEXT, "", MULTIPLE_DOMAINS_IMPLEMENTED);
	}

	@Test
	def void canAddEntityToSubdomain() {
		// given
		val String dslSnippet = '''
			Domain Insurance {
				Subdomain CoreDomain {
					type = CORE_DOMAIN
					domainVisionStatement = "my domain vision for this subdomain"
	
					Entity MyCoreEntity {
						String attr1
						string attr2
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.domains.get(0).subdomains.get(0).entities.size);
		assertEquals("MyCoreEntity", result.domains.get(0).subdomains.get(0).entities.get(0).name);
		assertEquals(2, result.domains.get(0).subdomains.get(0).entities.get(0).attributes.size);
	}
	
	@Test
	def void canAddServiceToSubdomain() {
		// given
		val String dslSnippet = '''
			Domain Insurance {
				Subdomain CoreDomain {
					type = CORE_DOMAIN
					domainVisionStatement = "my domain vision for this subdomain"
	
					Service MyTestService
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.domains.get(0).subdomains.get(0).services.size);
		assertEquals("MyTestService", result.domains.get(0).subdomains.get(0).services.get(0).name);
	}
	
	@Test
	def void canDefineAttributesWithoutEqualSign() {
		// given
		val String dslSnippet = '''
			Domain Insurance {
				Subdomain CoreDomain {
					type CORE_DOMAIN
					domainVisionStatement "my domain vision for this subdomain"
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals("my domain vision for this subdomain", result.domains.get(0).subdomains.get(0).domainVisionStatement);
		assertEquals(SubDomainType.CORE_DOMAIN, result.domains.get(0).subdomains.get(0).type);
	}
	
	@Test
	def void canReferenceSupportedFeature() {
		// given
		val String dslSnippet = '''
			Domain Insurance {
				Subdomain CustomerManagement supports Manage_Customers {
					type CORE_DOMAIN
				}
			}
			UserStory Manage_Customers
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var subdomain = result.domains.get(0).subdomains.get(0);
		assertEquals(1, subdomain.supportedFeatures.size);
		assertEquals("Manage_Customers", subdomain.supportedFeatures.get(0).name);
	}
	
	@Test
	def void canReferenceSupportedFeatures() {
		// given
		val String dslSnippet = '''
			Domain Insurance {
				Subdomain CustomerManagement supports Manage_Customers, Manage_Contracts {
					type CORE_DOMAIN
				}
			}
			UserStory Manage_Customers
			UserStory Manage_Contracts
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var subdomain = result.domains.get(0).subdomains.get(0);
		assertEquals(2, subdomain.supportedFeatures.size);
		assertEquals("Manage_Customers", subdomain.supportedFeatures.get(0).name);
		assertEquals("Manage_Contracts", subdomain.supportedFeatures.get(1).name);
	}
}

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
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.util.ParsingErrorAssertions.*
import static org.contextmapper.dsl.validation.ValidationMessages.*
import org.contextmapper.dsl.tests.ContextMappingDSLInjectorProvider

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class UniquenessValidatorTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

	@Test
	def void cannotDefineDuplicateBC() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
			}
			BoundedContext ContextA {
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.BOUNDED_CONTEXT, "",
			String.format(BOUNDED_CONTEXT_NAME_NOT_UNIQUE, "ContextA"));
	}

	@Test
	def void cannotDefineDuplicateDomain() {
		// given
		val String dslSnippet = '''
			Domain TestDomain {
			}
			Domain TestDomain {
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.DOMAIN, "",
			String.format(DOMAIN_NOT_UNIQUE, "TestDomain"));
	}

	@Test
	def void cannotDefineSubdomainWithDomainName() {
		// given
		val String dslSnippet = '''
			Domain TestDomain {
				Subdomain TestDomain {
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.SUBDOMAIN, "",
			String.format(SUBDOMAIN_NAME_EQUALS_DOMAIN_NAME, "TestDomain"));
	}

	@Test
	def void cannotDefineDuplicateSubdomain() {
		// given
		val String dslSnippet = '''
			Domain TestDomain {
				Subdomain TestSubdomain
			}
			Domain AnotherTestDomain {
				Subdomain TestSubdomain
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.SUBDOMAIN, "",
			String.format(SUBDOMAIN_OBJECT_NOT_UNIQUE, "TestSubdomain"));
	}

	@Test
	def void cannotDefineDuplicateModule() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
				Module Mod
				Module Mod
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.SCULPTOR_MODULE, "",
			String.format(MODULE_NAME_NOT_UNIQUE, "Mod"));
	}

	@Test
	def void cannotDefineDuplicateAggregate() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
				Aggregate agg
				Aggregate agg
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.AGGREGATE, "",
			String.format(AGGREGATE_NAME_NOT_UNIQUE, "agg"));
	}

	@Test
	def void cannotDefineDuplicateUseCases() {
		// given
		val String dslSnippet = '''
			UseCase uc
			UseCase uc
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.USER_REQUIREMENT, "",
			String.format(USE_CASE_NAME_NOT_UNIQUE, "uc"));
	}

	@Test
	def void canDefineDuplicateEntityInDifferentAggregates() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
				Aggregate agg1 {
					Entity Account
				}
				Aggregate agg2 {
					Entity Account
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
	}

	@Test
	def void cannotDefineDuplicateDomainObjectWithinAggregate() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
				Aggregate agg1 {
					Entity Account
					Entity Account
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, TacticdslPackage.Literals.SIMPLE_DOMAIN_OBJECT, "",
			String.format(DOMAIN_OBJECT_NOT_UNIQUE, "Account"));
	}

	@Test
	def void cannotDefineDuplicateDomainObjectWithinModule() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
				Module Mod {
					Entity Account
					Entity Account
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, TacticdslPackage.Literals.SIMPLE_DOMAIN_OBJECT, "",
			String.format(DOMAIN_OBJECT_NOT_UNIQUE, "Account"));
	}

	@Test
	def void cannotDefineDuplicateDomainObjectWithinSubdomain() {
		// given
		val String dslSnippet = '''
			Domain TestDomain {
				Subdomain TestSubDomain {
					Entity Account
					Entity Account
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, TacticdslPackage.Literals.SIMPLE_DOMAIN_OBJECT, "",
			String.format(DOMAIN_OBJECT_NOT_UNIQUE, "Account"));
	}

	@Test
	def void cannotDefineDuplicateServiceInBoundedContext() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
				Aggregate AggregateA {
					Service TestService
					Service TestService
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, TacticdslPackage.Literals.SERVICE_REPOSITORY_OPTION, "",
			String.format(SERVICE_NAME_NOT_UNIQUE_IN_BC, "TestService"));
	}

	@Test
	def void cannotDefineDuplicateServiceInSubdomain() {
		// given
		val String dslSnippet = '''
			Domain TestDomain {
				Subdomain TestSubdomain {
					Service TestService
					Service TestService
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, TacticdslPackage.Literals.SERVICE_REPOSITORY_OPTION, "",
			String.format(SERVICE_NAME_NOT_UNIQUE_IN_SUBDOMAIN, "TestService"));
	}

}

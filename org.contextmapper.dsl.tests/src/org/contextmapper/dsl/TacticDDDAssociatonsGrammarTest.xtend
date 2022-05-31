/*
 * Copyright 2022 The Context Mapper Project Team
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
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.tests.ContextMappingDSLInjectorProvider
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.util.ParsingErrorAssertions.*
import static org.junit.jupiter.api.Assertions.*
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class TacticDDDAssociatonsGrammarTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

	@Test
	def void canDefineAssociationBetweenDomainObjects() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Entity Customer {
						-- "lives at" @Address
					}
					
					Entity Address
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val DomainObject customer = result.boundedContexts.get(0).aggregates.get(0).domainObjects.get(0) as DomainObject;

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, customer.associations.size);
		assertEquals("lives at", customer.associations.get(0).description);
		assertEquals("Address", customer.associations.get(0).domainObjectType.name);
	}
	
	@Test
	def void canDefineAssociationLabelOnReference() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Entity Customer {
						- @Address address -- "address"
					}
					
					Entity Address
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val DomainObject customer = result.boundedContexts.get(0).aggregates.get(0).domainObjects.get(0) as DomainObject;

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, customer.references.size);
		assertEquals("address", customer.references.get(0).associationLabel);
	}
	
	@Test
	def void cannotDefineAssociationFromBCToSubdomain() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Entity Customer {
						-- "lives at" @Address
					}
				}
			}
			
			Domain TestDomain {
				Subdomain TestSubdomain {
					Entity Address
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);

		// then
		assertThatNoParsingErrorsOccurred(result);
		this.validationTestHelper.assertError(result, TacticdslPackage.Literals.ASSOCIATION, "org.eclipse.xtext.diagnostics.Diagnostic.Linking", 
        	"Couldn't resolve reference to SimpleDomainObject 'Address'.");
	}
	
	@Test
	def void cannotDefineAssociationFromSubdomainToBC() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Entity Address
				}
			}
			
			Domain TestDomain {
				Subdomain TestSubdomain {
					Entity Customer {
						-- "lives at" @Address
					}
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);

		// then
		assertThatNoParsingErrorsOccurred(result);
		this.validationTestHelper.assertError(result, TacticdslPackage.Literals.ASSOCIATION, "org.eclipse.xtext.diagnostics.Diagnostic.Linking", 
        	"Couldn't resolve reference to SimpleDomainObject 'Address'.");
	}

}

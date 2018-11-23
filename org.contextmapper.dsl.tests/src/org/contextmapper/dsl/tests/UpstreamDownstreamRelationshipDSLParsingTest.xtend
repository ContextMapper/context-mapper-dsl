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
package org.contextmapper.dsl.tests

import com.google.inject.Inject
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.contextMappingDSL.CustomerSupplierRelationship
import org.contextmapper.dsl.contextMappingDSL.DownstreamRole
import org.contextmapper.dsl.contextMappingDSL.Relationship
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship
import org.contextmapper.dsl.contextMappingDSL.UpstreamRole
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.tests.util.ParsingErrorAssertions.*
import static org.contextmapper.dsl.validation.ValidationMessages.*
import static org.junit.jupiter.api.Assertions.*

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class UpstreamDownstreamRelationshipDSLParsingTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

	@Test
	def void canDefineUpstreamDownstream() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains testContext
				contains anotherTestContext
			
				testContext Upstream-Downstream anotherTestContext {
				upstream implements PUBLISHED_LANGUAGE, OPEN_HOST_SERVICE
				downstream implements CONFORMIST
				}
			}
			
			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(UpstreamDownstreamRelationship))

		val UpstreamDownstreamRelationship upstreamDownstreamRelationship = relationship as UpstreamDownstreamRelationship
		assertEquals("testContext", upstreamDownstreamRelationship.upstream.name)
		assertEquals("anotherTestContext", upstreamDownstreamRelationship.downstream.name)

		assertTrue(upstreamDownstreamRelationship.upstreamRoles.contains(UpstreamRole.OPEN_HOST_SERVICE))
		assertTrue(upstreamDownstreamRelationship.upstreamRoles.contains(UpstreamRole.PUBLISHED_LANGUAGE))

		assertTrue(upstreamDownstreamRelationship.downstreamRoles.contains(DownstreamRole.CONFORMIST))
	}

	@Test
	def void canDefineUpstreamDownstreamInAlternativeSyntaxLeft() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains testContext
				contains anotherTestContext
			
				testContext <- anotherTestContext : Upstream-Downstream {
				upstream implements PUBLISHED_LANGUAGE, OPEN_HOST_SERVICE
				downstream implements CONFORMIST
				}
			}
			
			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(UpstreamDownstreamRelationship))

		val UpstreamDownstreamRelationship upstreamDownstreamRelationship = relationship as UpstreamDownstreamRelationship
		assertEquals("testContext", upstreamDownstreamRelationship.upstream.name)
		assertEquals("anotherTestContext", upstreamDownstreamRelationship.downstream.name)

		assertTrue(upstreamDownstreamRelationship.upstreamRoles.contains(UpstreamRole.OPEN_HOST_SERVICE))
		assertTrue(upstreamDownstreamRelationship.upstreamRoles.contains(UpstreamRole.PUBLISHED_LANGUAGE))

		assertTrue(upstreamDownstreamRelationship.downstreamRoles.contains(DownstreamRole.CONFORMIST))
	}

	@Test
	def void canDefineUpstreamDownstreamInAlternativeSyntaxRight() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains testContext
				contains anotherTestContext
			
				anotherTestContext -> testContext : Upstream-Downstream {
				upstream implements PUBLISHED_LANGUAGE, OPEN_HOST_SERVICE
				downstream implements CONFORMIST
				}
			}
			
			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(UpstreamDownstreamRelationship))

		val UpstreamDownstreamRelationship upstreamDownstreamRelationship = relationship as UpstreamDownstreamRelationship
		assertEquals("testContext", upstreamDownstreamRelationship.upstream.name)
		assertEquals("anotherTestContext", upstreamDownstreamRelationship.downstream.name)

		assertTrue(upstreamDownstreamRelationship.upstreamRoles.contains(UpstreamRole.OPEN_HOST_SERVICE))
		assertTrue(upstreamDownstreamRelationship.upstreamRoles.contains(UpstreamRole.PUBLISHED_LANGUAGE))

		assertTrue(upstreamDownstreamRelationship.downstreamRoles.contains(DownstreamRole.CONFORMIST))
	}

	@Test
	def void canDefineCustomerSupplier() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 contains testContext
				 contains anotherTestContext
			
				 anotherTestContext Customer-Supplier testContext
			}
			
			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(CustomerSupplierRelationship))

		val CustomerSupplierRelationship customerSupplierRelationship = relationship as CustomerSupplierRelationship
		assertEquals("testContext", customerSupplierRelationship.upstream.name)
		assertEquals("anotherTestContext", customerSupplierRelationship.downstream.name)
	}

	@Test
	def void canDefineCustomerSupplierInAlternativeSyntaxRight() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 contains testContext
				 contains anotherTestContext
			
				 anotherTestContext -> testContext : Customer-Supplier
			}
			
			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(CustomerSupplierRelationship))

		val CustomerSupplierRelationship customerSupplierRelationship = relationship as CustomerSupplierRelationship
		assertEquals("testContext", customerSupplierRelationship.upstream.name)
		assertEquals("anotherTestContext", customerSupplierRelationship.downstream.name)
	}

	@Test
	def void canDefineCustomerSupplierInAlternativeSyntaxLeft() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 contains testContext
				 contains anotherTestContext
			
				 testContext <- anotherTestContext : Customer-Supplier
			}
			
			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(CustomerSupplierRelationship))

		val CustomerSupplierRelationship customerSupplierRelationship = relationship as CustomerSupplierRelationship
		assertEquals("testContext", customerSupplierRelationship.upstream.name)
		assertEquals("anotherTestContext", customerSupplierRelationship.downstream.name)
	}

	@Test
	def void throwValidationErrorIfCustomerSupplierImplementsOHS() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains testContext
				contains anotherTestContext
			
				anotherTestContext Customer-Supplier testContext {
				supplier implements OPEN_HOST_SERVICE
				}
			}
			
			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.UPSTREAM_DOWNSTREAM_RELATIONSHIP, "",
			CUSTOMER_SUPPLIER_WITH_OHS_ERROR_MESSAGE);
	}

	@Test
	def void throwValidationErrorIfCustomerSupplierImplementsACL() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 contains testContext
				 contains anotherTestContext
			
				 anotherTestContext Customer-Supplier testContext {
					customer implements ANTICORRUPTION_LAYER
				 }
			}
			
			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertWarning(result, ContextMappingDSLPackage.Literals.UPSTREAM_DOWNSTREAM_RELATIONSHIP,
			"", CUSTOMER_SUPPLIER_WITH_ACL_WARNING_MESSAGE);
	}

	@Test
	def void throwValidationErrorIfCustomerSupplierImplementsConformist() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 contains testContext
				 contains anotherTestContext
			
				 anotherTestContext Customer-Supplier testContext {
					customer implements CONFORMIST
				 }
			}
			
			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.UPSTREAM_DOWNSTREAM_RELATIONSHIP, "",
			CUSTOMER_SUPPLIER_WITH_CONFORMIST_ERROR_MESSAGE);
	}

	@Test
	def void expectRelationshipContextsBePartOfMap() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 contains anotherTestContext
			
				 anotherTestContext Customer-Supplier testContext
			}
			
			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.CUSTOMER_SUPPLIER_RELATIONSHIP, "",
			String.format(RELATIONSHIP_CONTEXT_NOT_ON_MAP_ERROR_MESSAGE, "testContext"));
	}

	@Test
	def void canGiveUpstreamDownstreamRelationshipName() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains testContext
				contains anotherTestContext
			
				@myRelName
				anotherTestContext <- testContext : Upstream-Downstream
			}
			
			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals("myRelName", result.map.relationships.get(0).name);
	}

	@Test
	def void canGiveCustomerSupplierRelationshipName() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains testContext
				contains anotherTestContext
			
				@myRelName
				anotherTestContext <- testContext : Customer-Supplier
			}
			
			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals("myRelName", result.map.relationships.get(0).name);
	}

	@Test
	def void canDefineTechnology() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains testContext
				contains anotherTestContext
			
				anotherTestContext <- testContext : Customer-Supplier {
				implementationTechnology = "RESTful HTTP"
				}
			}
			
			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals("RESTful HTTP", result.map.relationships.get(0).implementationTechnology);
	}

}

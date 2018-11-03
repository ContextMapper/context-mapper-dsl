package org.contextmapper.dsl.tests

import com.google.inject.Inject
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.contextMappingDSL.CustomerSupplierRelationship
import org.contextmapper.dsl.contextMappingDSL.DownstreamRoles
import org.contextmapper.dsl.contextMappingDSL.Relationship
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship
import org.contextmapper.dsl.contextMappingDSL.UpstreamRoles
import org.contextmapper.dsl.validation.BoundedContextRelationshipSemanticsValidator
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.tests.util.ParsingErrorAssertions.*
import static org.junit.jupiter.api.Assertions.*

import static org.contextmapper.dsl.validation.ValidationMessages.*

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
				 testContext
				 anotherTestContext

				 Upstream-Downstream {
					testContext as Upstream
						implements PUBLISHED_LANGUAGE, OPEN_HOST_SERVICE
					anotherTestContext as Downstream
						implements CONFORMIST
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
		assertEquals("testContext", upstreamDownstreamRelationship.upstream.context.name)
		assertEquals("anotherTestContext", upstreamDownstreamRelationship.downstream.context.name)

		assertTrue(upstreamDownstreamRelationship.upstream.roles.contains(UpstreamRoles.OPEN_HOST_SERVICE))
		assertTrue(upstreamDownstreamRelationship.upstream.roles.contains(UpstreamRoles.PUBLISHED_LANGUAGE))

		assertTrue(upstreamDownstreamRelationship.downstream.roles.contains(DownstreamRoles.CONFORMIST))
	}

	@Test
	def void canDefineCustomerSupplier() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 testContext
				 anotherTestContext

				 Customer-Supplier {
					testContext as Upstream
					anotherTestContext as Downstream
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
		assertTrue(relationship.class.interfaces.contains(CustomerSupplierRelationship))

		val CustomerSupplierRelationship customerSupplierRelationship = relationship as CustomerSupplierRelationship
		assertEquals("testContext", customerSupplierRelationship.upstream.context.name)
		assertEquals("anotherTestContext", customerSupplierRelationship.downstream.context.name)
	}

	@Test
	def void throwValidationErrorIfCustomerSupplierImplementsOHS() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 testContext
				 anotherTestContext

				 Customer-Supplier {
					testContext as Upstream
						implements OPEN_HOST_SERVICE
					anotherTestContext as Downstream
					}
			}

			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.UPSTREAM_CONTEXT, "",
			CUSTOMER_SUPPLIER_NOT_ALLOW_OHS_ACL_CONFORMIST_ERROR_MESSAGE);
	}

	@Test
	def void throwValidationErrorIfCustomerSupplierImplementsACL() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 testContext
				 anotherTestContext

				 Customer-Supplier {
					testContext as Upstream
					anotherTestContext as Downstream
						implements ANTICORRUPTION_LAYER
				 }
			}

			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.DOWNSTREAM_CONTEXT, "",
			CUSTOMER_SUPPLIER_NOT_ALLOW_OHS_ACL_CONFORMIST_ERROR_MESSAGE);
	}

	@Test
	def void throwValidationErrorIfCustomerSupplierImplementsConformist() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 testContext
				 anotherTestContext

				 Customer-Supplier {
					testContext as Upstream
					anotherTestContext as Downstream
						implements CONFORMIST
				 }
			}

			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.DOWNSTREAM_CONTEXT, "",
			CUSTOMER_SUPPLIER_NOT_ALLOW_OHS_ACL_CONFORMIST_ERROR_MESSAGE);
	}

	@Test
	def void expectRelationshipContextsBePartOfMap() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 anotherTestContext

				 Customer-Supplier {
					testContext as Upstream
					anotherTestContext as Downstream
				 }
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

}

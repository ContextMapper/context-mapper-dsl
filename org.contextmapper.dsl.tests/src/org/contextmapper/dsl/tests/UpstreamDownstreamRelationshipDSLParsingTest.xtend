package org.contextmapper.dsl.tests

import com.google.inject.Inject
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.contextMappingDSL.CustomerSupplierRelationship
import org.contextmapper.dsl.contextMappingDSL.Relationship
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.tests.util.ParsingErrorAssertions.*
import static org.junit.jupiter.api.Assertions.*
import org.contextmapper.dsl.contextMappingDSL.DownstreamRoles
import org.contextmapper.dsl.contextMappingDSL.UpstreamRoles

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class UpstreamDownstreamRelationshipDSLParsingTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

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
						implements OPEN_HOST_SERVICE
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

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(CustomerSupplierRelationship))

		val CustomerSupplierRelationship customerSupplierRelationship = relationship as CustomerSupplierRelationship
		assertEquals("testContext", customerSupplierRelationship.upstream.context.name)
		assertEquals("anotherTestContext", customerSupplierRelationship.downstream.context.name)

		assertTrue(customerSupplierRelationship.upstream.roles.contains(UpstreamRoles.OPEN_HOST_SERVICE))

		assertTrue(customerSupplierRelationship.downstream.roles.contains(DownstreamRoles.ANTICORRUPTION_LAYER))
	}

}

package org.contextmapper.dsl.tests

import com.google.inject.Inject
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.contextMappingDSL.Partnership
import org.contextmapper.dsl.contextMappingDSL.Relationship
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.tests.util.ParsingErrorAssertions.*
import static org.junit.jupiter.api.Assertions.*
import org.contextmapper.dsl.contextMappingDSL.SharedKernel

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class SymmetricRelationshipDSLParsingTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	@Test
	def void canDefinePartnership() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 testContext
				 anotherTestContext

				 Partnership {
					testContext
					anotherTestContext
				 }
			}

			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertEquals(1, result.map.relationships.size);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(Partnership))

		val Partnership partnership = relationship as Partnership;
		assertEquals("testContext", partnership.participant1.name);
		assertEquals("anotherTestContext", partnership.participant2.name);
	}

	@Test
	def void canDefineSharedKernel() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 testContext
				 anotherTestContext

				 Shared Kernel {
					testContext
					anotherTestContext
				 }
			}

			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertEquals(1, result.map.relationships.size);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(SharedKernel))

		val SharedKernel sharedKernel = relationship as SharedKernel;
		assertEquals("testContext", sharedKernel.participant1.name);
		assertEquals("anotherTestContext", sharedKernel.participant2.name);
	}
}

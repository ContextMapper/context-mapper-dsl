package org.contextmapper.dsl.tests

import com.google.inject.Inject
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.tests.util.ParsingErrorAssertions.*
import static org.junit.jupiter.api.Assertions.*
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType
import java.util.stream.Collectors

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class BoundedContextDSLParsingTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	@Test
	def void canDefineBoundedContext() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertEquals(1, result.boundedContexts.size);
		assertEquals("testContext", result.boundedContexts.get(0).name);
	}

	@Test
	def void canDefineContextType() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				type = FEATURE
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertEquals(BoundedContextType.FEATURE, result.boundedContexts.get(0).type);
	}

	@Test
	def void canDefineContextResponsibility() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				responsibilities = resp1
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertEquals(1, result.boundedContexts.get(0).responsibilities.size)
		assertEquals("resp1", result.boundedContexts.get(0).responsibilities.get(0).name)
	}

	@Test
	def void canDefineContextResponsibilities() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				responsibilities = resp1, resp2
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertEquals(2, result.boundedContexts.get(0).responsibilities.size)

		val responsibilities = result.boundedContexts.get(0).responsibilities.stream.map[name].collect(Collectors.toList);
		assertTrue(responsibilities.contains("resp1"));
		assertTrue(responsibilities.contains("resp2"));
	}
}

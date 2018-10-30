package org.contextmapper.dsl.tests

import com.google.inject.Inject
import java.util.ArrayList
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.tests.util.ParsingErrorAssertions.*
import static org.junit.jupiter.api.Assertions.*
import java.util.stream.Collectors
import org.contextmapper.dsl.contextMappingDSL.ContextMapState

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class ContextMapDSLParsingTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	@Test
	def void canDefineContextMap() {
		// given
		val String dslSnippet = '''
			ContextMap {}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertNotNull(result.map);
	}

	@Test
	def void canAddBoundedContextToMapByValue() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 testContext
				 anotherTestContext
			}

			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertNotNull(result.map);
		assertEquals(2, result.map.boundedContexts.size);

		val contextNames = result.map.boundedContexts.stream.map[name].collect(Collectors.toList);
		assertTrue(contextNames.contains("testContext"));
		assertTrue(contextNames.contains("anotherTestContext"));
	}

	@Test
	def void canAddDomainVisionStatement() {
		// given
		val String dslSnippet = '''
			ContextMap {
				domainVisionStatement = "this is a short description stating the vision of my project ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertEquals("this is a short description stating the vision of my project ...", result.map.domainVisionStatement);
	}

	@Test
	def void canDefineContextMapState() {
		// given
		val String dslSnippet = '''
			ContextMap {
				state = AS_IS
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertEquals(ContextMapState.AS_IS, result.map.contextMapState);
	}
}

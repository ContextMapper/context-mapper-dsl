package org.contextmapper.dsl.tests

import com.google.inject.Inject
import java.util.stream.Collectors
import org.contextmapper.dsl.contextMappingDSL.ContextMapState
import org.contextmapper.dsl.contextMappingDSL.ContextMapType
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
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
class ContextMapDSLParsingTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

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
		assertThatNoValidationErrorsOccurred(result);
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
		assertThatNoValidationErrorsOccurred(result);
		assertNotNull(result.map);
		assertEquals(2, result.map.boundedContexts.size);

		val contextNames = result.map.boundedContexts.stream.map[name].collect(Collectors.toList);
		assertTrue(contextNames.contains("testContext"));
		assertTrue(contextNames.contains("anotherTestContext"));
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
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(ContextMapState.AS_IS, result.map.state);
	}

	@Test
	def void canDefineContextMapType() {
		// given
		val String dslSnippet = '''
			ContextMap {
				type = SYSTEM_LANDSCAPE
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(ContextMapType.SYSTEM_LANDSCAPE, result.map.type);
	}

	@Test
	def void throwErrorIfBoundedContextsOnOrganizationalMapAreNotOfTypeTeam() {
		// given
		val String dslSnippet = '''
			ContextMap {
				type = ORGANIZATIONAL

				notATeam
			}

			BoundedContext notATeam {
				type = SYSTEM
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.CONTEXT_MAP, "",
			String.format(ORGANIZATIONAL_MAP_CONTEXT_IS_NOT_TYPE_TEAM, "SYSTEM"));
	}

	@Test
	def void throwErrorIfTeamBoundedContextIsOnSystemLandscape() {
		// given
		val String dslSnippet = '''
			ContextMap {
				type = SYSTEM_LANDSCAPE

				aTeam
			}

			BoundedContext aTeam {
				type = TEAM
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.CONTEXT_MAP, "",
			String.format(SYSTEM_LANDSCAPE_MAP_CONTAINS_TEAM));
	}
}

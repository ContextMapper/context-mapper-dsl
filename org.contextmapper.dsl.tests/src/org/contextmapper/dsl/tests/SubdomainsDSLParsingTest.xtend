package org.contextmapper.dsl.tests

import com.google.inject.Inject
import java.util.stream.Collectors
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.tests.util.ParsingErrorAssertions.*
import static org.junit.jupiter.api.Assertions.*

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class SubdomainsDSLParsingTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	@Test
	def void canDefineSubdomains() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext

			CoreDomain core
			SupportingDomain support1
			GenericSubdomain generic
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertEquals(3, result.subdomains.size);

		val subdomainNames = result.subdomains.stream.map[name].collect(Collectors.toList);
		assertTrue(subdomainNames.contains("core"));
		assertTrue(subdomainNames.contains("support1"));
		assertTrue(subdomainNames.contains("generic"));
	}

	@Test
	def void canMapSubdomainToBoundedContexts() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext implements core

			CoreDomain core
			SupportingDomain support1
			GenericSubdomain generic
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertEquals(1, result.boundedContexts.get(0).implementedSubdomains.size);
		assertEquals("core", result.boundedContexts.get(0).implementedSubdomains.get(0).name);
	}

	@Test
	def void canMapSubdomainsToBoundedContexts() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext implements core, support1

			CoreDomain core
			SupportingDomain support1
			GenericSubdomain generic
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertEquals(2, result.boundedContexts.get(0).implementedSubdomains.size);

		val subdomainNames = result.boundedContexts.get(0).implementedSubdomains.stream.map[name].collect(Collectors.toList);
		assertTrue(subdomainNames.contains("core"));
		assertTrue(subdomainNames.contains("support1"));
	}
}

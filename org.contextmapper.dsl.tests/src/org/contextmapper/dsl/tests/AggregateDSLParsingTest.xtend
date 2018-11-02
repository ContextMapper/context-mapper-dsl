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
import org.contextmapper.tactic.dsl.tacticdsl.KnowledgeLevel

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class AggregateDSLParsingTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	@Test
	def void canAddAggregateToBoundedContext() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				Aggregate myAggregate {}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertEquals(1, result.boundedContexts.get(0).aggregates.size)
		assertEquals("myAggregate", result.boundedContexts.get(0).aggregates.get(0).name)
	}

	@Test
	def void canDefineAggregateResponsibilities() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				Aggregate myAggregate {
					responsibilities = CalcCustomerRisk{"can calculate customer risks..."}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertEquals(1, result.boundedContexts.get(0).aggregates.get(0).responsibilities.size);
		assertEquals("CalcCustomerRisk", result.boundedContexts.get(0).aggregates.get(0).responsibilities.get(0).name);
		assertEquals("can calculate customer risks...", result.boundedContexts.get(0).aggregates.get(0).responsibilities.get(0).description);
	}

	@Test
	def void canDefineAggregateKnowledgeLevel() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				Aggregate myAggregate {
					knowledgeLevel = CONCRETE
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertEquals(KnowledgeLevel.CONCRETE, result.boundedContexts.get(0).aggregates.get(0).knowledgeLevel);
	}
}

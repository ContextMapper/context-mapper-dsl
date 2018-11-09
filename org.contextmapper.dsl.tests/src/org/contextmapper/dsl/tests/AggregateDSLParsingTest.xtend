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
		assertThatNoValidationErrorsOccurred(result);
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
		assertThatNoValidationErrorsOccurred(result);
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
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(KnowledgeLevel.CONCRETE, result.boundedContexts.get(0).aggregates.get(0).knowledgeLevel);
	}
}

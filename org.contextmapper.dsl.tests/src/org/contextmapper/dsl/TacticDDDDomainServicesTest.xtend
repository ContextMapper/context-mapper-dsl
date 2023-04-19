/*
 * Copyright 2023 The Context Mapper Project Team
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
package org.contextmapper.dsl

import com.google.inject.Inject
import org.contextmapper.dsl.contextMappingDSL.Aggregate
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.tests.ContextMappingDSLInjectorProvider
import org.contextmapper.tactic.dsl.tacticdsl.Service
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.util.ParsingErrorAssertions.*
import static org.junit.jupiter.api.Assertions.*

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class TacticDDDDomainServicesTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	@Test
	def void canDefineServiceInAggregate_forBackwardCompatibility() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Service TestService {
					}
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.boundedContexts.get(0).aggregates.get(0).services.size);
		assertEquals("TestService", (result.boundedContexts.get(0).aggregates.get(0).services.get(0) as Service).name);
	}

	@Test
	def void canDefineServiceInBoundedContextAfterAggregates() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
				}
				Service TestService {
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.boundedContexts.get(0).aggregates.size);
		assertEquals(1, result.boundedContexts.get(0).domainServices.size);
		assertEquals("TestAggregate", (result.boundedContexts.get(0).aggregates.get(0) as Aggregate).name);
		assertEquals("TestService", (result.boundedContexts.get(0).domainServices.get(0) as Service).name);
	}

	@Test
	def void canDefineServiceInBoundedContextBeforeAggregates() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Service TestService {
				}
				Aggregate TestAggregate {
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.boundedContexts.get(0).aggregates.size);
		assertEquals(1, result.boundedContexts.get(0).domainServices.size);
		assertEquals("TestAggregate", (result.boundedContexts.get(0).aggregates.get(0) as Aggregate).name);
		assertEquals("TestService", (result.boundedContexts.get(0).domainServices.get(0) as Service).name);
	}

}

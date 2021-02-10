/*
 * Copyright 2020 The Context Mapper Project Team
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
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.tests.ContextMappingDSLInjectorProvider
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.util.ParsingErrorAssertions.*
import static org.junit.jupiter.api.Assertions.*

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class TacticDDDDomainObjectsTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

	@Test
	def void canDefineUniqueAttribute() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Entity TestEntity {
						String testAttr unique;
					}
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val DomainObject entity = result.boundedContexts.get(0).aggregates.get(0).domainObjects.get(0) as DomainObject;

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, entity.attributes.size);
		assertTrue(entity.attributes.get(0).unique);
	}

	@Test
	def void attributeIsNotUniqueByDefault() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Entity TestEntity {
						String testAttr;
					}
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val DomainObject entity = result.boundedContexts.get(0).aggregates.get(0).domainObjects.get(0) as DomainObject;

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, entity.attributes.size);
		assertFalse(entity.attributes.get(0).unique);
	}
	
	@Test
	def void canDefineAttributeNotUnique_Syntax1() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Entity TestEntity {
						String testAttr not unique;
					}
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val DomainObject entity = result.boundedContexts.get(0).aggregates.get(0).domainObjects.get(0) as DomainObject;

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, entity.attributes.size);
		assertFalse(entity.attributes.get(0).unique);
	}
	
	@Test
	def void canDefineAttributeNotUnique_Syntax2() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Entity TestEntity {
						String testAttr !unique;
					}
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val DomainObject entity = result.boundedContexts.get(0).aggregates.get(0).domainObjects.get(0) as DomainObject;

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, entity.attributes.size);
		assertFalse(entity.attributes.get(0).unique);
	}

}

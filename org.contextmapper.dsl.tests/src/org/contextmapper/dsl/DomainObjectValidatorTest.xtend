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
package org.contextmapper.dsl

import com.google.inject.Inject
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.tests.ContextMappingDSLInjectorProvider
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.util.ParsingErrorAssertions.*
import static org.contextmapper.dsl.validation.ValidationMessages.*
import org.contextmapper.dsl.validation.DomainObjectValidator

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class DomainObjectValidatorTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

	@Test
	def void canGiveHintToCreateValueObject4ID() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
				Aggregate TestAggregate {
					Entity TestEntity {
						String testId // string as ID --> this can be improved
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertIssue(result, TacticdslPackage.Literals.ATTRIBUTE,
			DomainObjectValidator.ID_IS_PRIMITIVE_CODE, Severity.INFO, PRIMITIVE_ID_TYPE);
	}

	@Test
	def void canIgnoreEntityIdAlreadyVOCase() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
				Aggregate TestAggregate {
					ValueObject TestVO {
						String testId
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
	}

	@Test
	def void canIgnoreEntityIdInSubdomain() {
		// given
		val String dslSnippet = '''
			Domain TestDomain {
				Subdomain TestSubdomain {
					Entity TestEntity {
						String testId
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
	}

}

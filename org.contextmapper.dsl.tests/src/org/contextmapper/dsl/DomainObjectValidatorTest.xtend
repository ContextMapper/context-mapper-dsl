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

	@Test
	def void canCreateWarning4DuplicateObjectNames() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
				Aggregate TestAggregate1 {
					Entity TestEntity {
						
					}
				}
				Aggregate TestAggregate2 {
					Entity TestEntity {
						
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertIssue(result, TacticdslPackage.Literals.SIMPLE_DOMAIN_OBJECT, "", Severity.WARNING,
			String.format(DOMAIN_OBJECT_NAME_ALREADY_EXISTS, "TestEntity"));
	}

	@Test
	def void canCreateWarning4AmbiguousReference() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
				Aggregate TestAggregate1 {
					Entity TestEntity {
						
					}
				}
				Aggregate TestAggregate2 {
					Entity TestEntity {
						
					}
					Entity TestEntity2 {
						- TestEntity ref
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertIssue(result, TacticdslPackage.Literals.REFERENCE, "", Severity.WARNING,
			String.format(REFERENCE_IS_AMBIGUOUS, "TestEntity"));
	}
	
	@Test
	def void canCreateWarning4UnreachableReference1() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
				Aggregate TestAggregate1 {
					Entity TestEntity
				}
			}
			BoundedContext ContextB {
				Aggregate TestAggregate2 {
					Entity TestEntity2 {
						- TestEntity ref
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertIssue(result, TacticdslPackage.Literals.REFERENCE, "", Severity.WARNING,
			String.format(REFERENCE_TO_NOT_REACHABLE_TYPE, "TestEntity"));
	}
	
	@Test
	def void canCreateWarning4UnreachableReference2() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains ContextA, ContextB
				
				ContextB -> ContextA
			}
			BoundedContext ContextA {
				Aggregate TestAggregate1 {
					Entity TestEntity
				}
			}
			BoundedContext ContextB {
				Aggregate TestAggregate2 {
					Entity TestEntity2 {
						- TestEntity ref
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertIssue(result, TacticdslPackage.Literals.REFERENCE, "", Severity.WARNING,
			String.format(REFERENCE_TO_NOT_REACHABLE_TYPE, "TestEntity"));
	}
	
	@Test
	def void canCreateWarning4UnreachableReference3() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains ContextA, ContextB
				
				ContextB <- ContextA {
					exposedAggregates TestAggregate3
				}
			}
			BoundedContext ContextA {
				Aggregate TestAggregate1 {
					Entity TestEntity
				}
				
				Aggregate TestAggregate3
			}
			BoundedContext ContextB {
				Aggregate TestAggregate2 {
					Entity TestEntity2 {
						- TestEntity ref
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertIssue(result, TacticdslPackage.Literals.REFERENCE, "", Severity.WARNING,
			String.format(REFERENCE_TO_NOT_REACHABLE_TYPE, "TestEntity"));
	}
	
	@Test
	def void doNotCreateWarning4UnreachableReferenceIfRelExists1() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains ContextA, ContextB
				
				ContextA -> ContextB
			}
			BoundedContext ContextA {
				Aggregate TestAggregate1 {
					Entity TestEntity
				}
			}
			BoundedContext ContextB {
				Aggregate TestAggregate2 {
					Entity TestEntity2 {
						- TestEntity ref
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
	def void doNotCreateWarning4UnreachableReferenceIfRelExists2() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains ContextA, ContextB
				
				ContextA <-> ContextB
			}
			BoundedContext ContextA {
				Aggregate TestAggregate1 {
					Entity TestEntity
				}
			}
			BoundedContext ContextB {
				Aggregate TestAggregate2 {
					Entity TestEntity2 {
						- TestEntity ref
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
	def void doNotCreateWarning4UnreachableReferenceIfRelExists3() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains ContextA, ContextB
				
				ContextB <-> ContextA
			}
			BoundedContext ContextA {
				Aggregate TestAggregate1 {
					Entity TestEntity
				}
			}
			BoundedContext ContextB {
				Aggregate TestAggregate2 {
					Entity TestEntity2 {
						- TestEntity ref
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

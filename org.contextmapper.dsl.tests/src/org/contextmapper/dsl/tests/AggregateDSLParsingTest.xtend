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
import java.util.stream.Collectors
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.contextMappingDSL.KnowledgeLevel
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.tests.util.ParsingErrorAssertions.*
import static org.contextmapper.dsl.validation.ValidationMessages.*
import static org.junit.jupiter.api.Assertions.*
import org.contextmapper.dsl.contextMappingDSL.LikelihoodForChange
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class AggregateDSLParsingTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

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
					responsibilities = "can calculate customer risks..."
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.boundedContexts.get(0).aggregates.get(0).responsibilities.size);
		assertEquals("can calculate customer risks...", result.boundedContexts.get(0).aggregates.get(0).responsibilities.get(0));
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
	
	@Test
	def void canAssignUsesCases() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				Aggregate myAggregate {
					useCases = testUseCase1, testUseCase2
				}
			}
			
			UseCase testUseCase1
			UseCase testUseCase2
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(2, result.boundedContexts.get(0).aggregates.get(0).useCases.size);
		val useCases = result.boundedContexts.get(0).aggregates.get(0).useCases.stream.map[name].collect(Collectors.toList);
		assertTrue(useCases.contains("testUseCase1"));
		assertTrue(useCases.contains("testUseCase2"));
	}
	
	@Test
	def void canAssignOwner() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				Aggregate myAggregate {
					owner = teamA
				}
			}
			
			BoundedContext teamA {
				type = TEAM
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals("teamA", result.boundedContexts.get(0).aggregates.get(0).owner.name);
	}
	
	@Test
	def void throwErrorIfOwnerContextIsNotTeam() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				Aggregate myAggregate {
					owner = teamA
				}
			}
			
			BoundedContext teamA {
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.AGGREGATE, "",
			String.format(OWNER_BC_IS_NOT_TEAM, "teamA"));
	}
	
	@Test
	def void canDefineLikelihoodForChange() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				Aggregate myAggregate {
					likelihoodForChange = OFTEN
				}
			}
		''';
			// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertEquals(LikelihoodForChange.OFTEN, result.boundedContexts.get(0).aggregates.get(0).likelihoodForChange);
	}
	
	@Test
	def void throwErrorIfAggregateContainsMultipleRoots() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				Aggregate myAggregate {
					Entity MyFirstEntity {
						aggregateRoot
					}
					Entity MySecondEntity {
						aggregateRoot
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, TacticdslPackage.Literals.ENTITY, "",
			String.format(AGGREGATE_CAN_ONLY_HAVE_ONE_AGGREGATE_ROOT, "myAggregate"));
	}
	
	@Test
	def void canDefineAttributesWithoutEqualSign() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				Aggregate myAggregate {
					likelihoodForChange OFTEN
					responsibilities "can calculate customer risks..."
					knowledgeLevel CONCRETE
					useCases testUseCase1, testUseCase2
					owner teamA
				}
			}
			
			BoundedContext teamA {
				type TEAM
			}
			
			UseCase testUseCase1
			UseCase testUseCase2
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertEquals(LikelihoodForChange.OFTEN, result.boundedContexts.get(0).aggregates.get(0).likelihoodForChange);
		assertEquals("can calculate customer risks...", result.boundedContexts.get(0).aggregates.get(0).responsibilities.get(0));
		assertEquals(KnowledgeLevel.CONCRETE, result.boundedContexts.get(0).aggregates.get(0).knowledgeLevel);
		val useCases = result.boundedContexts.get(0).aggregates.get(0).useCases.stream.map[name].collect(Collectors.toList);
		assertTrue(useCases.contains("testUseCase1"));
		assertTrue(useCases.contains("testUseCase2"));
		assertEquals("teamA", result.boundedContexts.get(0).aggregates.get(0).owner.name);
	}
}

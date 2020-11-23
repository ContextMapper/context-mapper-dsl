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
import org.contextmapper.tactic.dsl.tacticdsl.DomainObjectOperation
import org.contextmapper.tactic.dsl.tacticdsl.Entity
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation
import org.contextmapper.tactic.dsl.tacticdsl.StateTransition
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.util.ParsingErrorAssertions.*
import static org.contextmapper.dsl.validation.ValidationMessages.*
import static org.junit.jupiter.api.Assertions.*

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class TacticDDDOperationsGrammarAndValidationTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

	@Test
	def void canParseServiceOperationReadKeyword() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Service TestService {
						void testOperation() : read-only;
					}
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val ServiceOperation operation = result.boundedContexts.get(0).aggregates.get(0).services.get(0).operations.
			get(0);

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertTrue(operation.isReadOnlyOperation);
		assertFalse(operation.isWriteOperation);
	}

	@Test
	def void canParseServiceOperationWriteKeyword() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Service TestService {
						void testOperation() : write;
					}
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val ServiceOperation operation = result.boundedContexts.get(0).aggregates.get(0).services.get(0).operations.
			get(0);

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertFalse(operation.isReadOnlyOperation);
		assertTrue(operation.isWriteOperation);
	}

	@Test
	def void canParseServiceOperationWriteStateTransition() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Service TestService {
						void testOperation() : write [STATE1 -> STATE2];
					}
					
					enum States {
						aggregateLifecycle
						
						STATE1, STATE2
					}
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val ServiceOperation operation = result.boundedContexts.get(0).aggregates.get(0).services.get(0).operations.
			get(0);
		val StateTransition stateTransition = operation.stateTransition;

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertFalse(operation.isReadOnlyOperation);
		assertTrue(operation.isWriteOperation);
		assertEquals("STATE1", stateTransition.from.get(0).name);
		assertEquals("STATE2", stateTransition.target.to.get(0).value.name);
	}

	@Test
	def void canParseServiceOperationWriteStateTransition_MultipleFroms() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Service TestService {
						void testOperation() : write [STATE1, STATE2 -> STATE3];
					}
					
					enum States {
						aggregateLifecycle
						
						STATE1, STATE2, STATE3
					}
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val ServiceOperation operation = result.boundedContexts.get(0).aggregates.get(0).services.get(0).operations.
			get(0);
		val StateTransition stateTransition = operation.stateTransition;

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertFalse(operation.isReadOnlyOperation);
		assertTrue(operation.isWriteOperation);
		assertEquals("STATE1", stateTransition.from.get(0).name);
		assertEquals("STATE2", stateTransition.from.get(1).name);
		assertEquals("STATE3", stateTransition.target.to.get(0).value.name);
	}

	@Test
	def void canParseServiceOperationWriteStateTransition_NoFroms() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Service TestService {
						void testOperation() : write [-> STATE3];
					}
					
					enum States {
						aggregateLifecycle
						
						STATE1, STATE2, STATE3
					}
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val ServiceOperation operation = result.boundedContexts.get(0).aggregates.get(0).services.get(0).operations.
			get(0);
		val StateTransition stateTransition = operation.stateTransition;

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertFalse(operation.isReadOnlyOperation);
		assertTrue(operation.isWriteOperation);
		assertEquals(0, stateTransition.from.size);
		assertEquals("STATE3", stateTransition.target.to.get(0).value.name);
	}

	@Test
	def void canParseServiceOperationWriteStateTransition_MultipleTos() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Service TestService {
						void testOperation() : write [STATE1 -> STATE2 X STATE3];
					}
					
					enum States {
						aggregateLifecycle
						
						STATE1, STATE2, STATE3
					}
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val ServiceOperation operation = result.boundedContexts.get(0).aggregates.get(0).services.get(0).operations.
			get(0);
		val StateTransition stateTransition = operation.stateTransition;

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertFalse(operation.isReadOnlyOperation);
		assertTrue(operation.isWriteOperation);
		assertEquals("STATE1", stateTransition.from.get(0).name);
		assertEquals("STATE2", stateTransition.target.to.get(0).value.name);
		assertEquals("STATE3", stateTransition.target.to.get(1).value.name);
	}

	@Test
	def void canParseDomainObjectOperationReadKeyword() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Entity TestRoot {
						aggregateRoot
						def void testOperation() : read-only;
					}
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val Entity entity = result.boundedContexts.get(0).aggregates.get(0).domainObjects.get(0) as Entity;
		val DomainObjectOperation operation = entity.operations.get(0);

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertTrue(operation.isReadOnlyOperation);
		assertFalse(operation.isWriteOperation);
	}

	@Test
	def void canParseDomainObjectOperationWriteKeyword() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Entity TestEntity {
						aggregateRoot
						def void testOperation() : write;
					}
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val Entity entity = result.boundedContexts.get(0).aggregates.get(0).domainObjects.get(0) as Entity;
		val DomainObjectOperation operation = entity.operations.get(0);

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertFalse(operation.isReadOnlyOperation);
		assertTrue(operation.isWriteOperation);
	}

	@Test
	def void canParseDomainObjectOperationWriteStateTransition() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Entity TestEntity {
						aggregateRoot
						def void testOperation() : write [STATE1 -> STATE2];
					}
					
					enum States {
						aggregateLifecycle
						
						STATE1, STATE2
					}
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val Entity entity = result.boundedContexts.get(0).aggregates.get(0).domainObjects.get(0) as Entity;
		val DomainObjectOperation operation = entity.operations.get(0);
		val StateTransition stateTransition = operation.stateTransition;

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertFalse(operation.isReadOnlyOperation);
		assertTrue(operation.isWriteOperation);
		assertEquals("STATE1", stateTransition.from.get(0).name);
		assertEquals("STATE2", stateTransition.target.to.get(0).value.name);
	}

	@Test
	def void canParseDomainObjectOperationWriteStateTransition_MultipleFroms() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Entity TestEntity {
						aggregateRoot
						def void testOperation() : write [STATE1, STATE2 -> STATE3];
					}
					
					enum States {
						aggregateLifecycle
						
						STATE1, STATE2, STATE3
					}
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val Entity entity = result.boundedContexts.get(0).aggregates.get(0).domainObjects.get(0) as Entity;
		val DomainObjectOperation operation = entity.operations.get(0);
		val StateTransition stateTransition = operation.stateTransition;

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertFalse(operation.isReadOnlyOperation);
		assertTrue(operation.isWriteOperation);
		assertEquals("STATE1", stateTransition.from.get(0).name);
		assertEquals("STATE2", stateTransition.from.get(1).name);
		assertEquals("STATE3", stateTransition.target.to.get(0).value.name);
	}

	@Test
	def void canParseDomainObjectOperationWriteStateTransition_NoFroms() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Entity TestEntity {
						def void testOperation() : write [-> STATE3];
					}
					
					enum States {
						aggregateLifecycle
						
						STATE1, STATE2, STATE3
					}
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val Entity entity = result.boundedContexts.get(0).aggregates.get(0).domainObjects.get(0) as Entity;
		val DomainObjectOperation operation = entity.operations.get(0);
		val StateTransition stateTransition = operation.stateTransition;

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertFalse(operation.isReadOnlyOperation);
		assertTrue(operation.isWriteOperation);
		assertEquals(0, stateTransition.from.size);
		assertEquals("STATE3", stateTransition.target.to.get(0).value.name);
	}

	@Test
	def void canParseDomainObjectOperationWriteStateTransition_MultipleTos() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Entity TestEntity {
						def void testOperation() : write [STATE1 -> STATE2 X STATE3];
					}
					
					enum States {
						aggregateLifecycle
						
						STATE1, STATE2, STATE3
					}
				}
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val Entity entity = result.boundedContexts.get(0).aggregates.get(0).domainObjects.get(0) as Entity;
		val DomainObjectOperation operation = entity.operations.get(0);
		val StateTransition stateTransition = operation.stateTransition;

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertFalse(operation.isReadOnlyOperation);
		assertTrue(operation.isWriteOperation);
		assertEquals("STATE1", stateTransition.from.get(0).name);
		assertEquals("STATE2", stateTransition.target.to.get(0).value.name);
		assertEquals("STATE3", stateTransition.target.to.get(1).value.name);
	}

	@Test
	def void cannotUseToStateThatDoesNotBelongToAggregateInEntity1() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Entity TestEntity {
						def void testOperation() : write [STATE1 -> STATE2 X OTHERSTATE];
					}
					
					enum States {
						aggregateLifecycle
						
						STATE1, STATE2, STATE3
					}
					enum OtherStates {
						OTHERSTATE
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, TacticdslPackage.Literals.STATE_TRANSITION_TARGET, "",
			String.format(STATE_VALUE_DOES_NOT_BELONG_TO_AGGREGATE, "OTHERSTATE", "TestAggregate"));
	}

	@Test
	def void cannotUseToStateThatDoesNotBelongToAggregateInEntity2() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Entity TestEntity {
						def void testOperation() : write [OTHERSTATE -> STATE2];
					}
					
					enum States {
						aggregateLifecycle
						
						STATE1, STATE2, STATE3
					}
					enum OtherStates {
						OTHERSTATE
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, TacticdslPackage.Literals.STATE_TRANSITION, "",
			String.format(STATE_VALUE_DOES_NOT_BELONG_TO_AGGREGATE, "OTHERSTATE", "TestAggregate"));
	}

	@Test
	def void cannotUseToStateThatDoesNotBelongToAggregateInService1() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Service TestService {
						void testOperation() : write [STATE1 -> STATE2 X OTHERSTATE];
					}
					
					enum States {
						aggregateLifecycle
						
						STATE1, STATE2, STATE3
					}
					enum OtherStates {
						OTHERSTATE
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, TacticdslPackage.Literals.STATE_TRANSITION_TARGET, "",
			String.format(STATE_VALUE_DOES_NOT_BELONG_TO_AGGREGATE, "OTHERSTATE", "TestAggregate"));
	}

	@Test
	def void cannotUseToStateThatDoesNotBelongToAggregateInService2() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Aggregate TestAggregate {
					Service TestService {
						void testOperation() : write [OTHERSTATE -> STATE2];
					}
					
					enum States {
						aggregateLifecycle
						
						STATE1, STATE2, STATE3
					}
					enum OtherStates {
						OTHERSTATE
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, TacticdslPackage.Literals.STATE_TRANSITION, "",
			String.format(STATE_VALUE_DOES_NOT_BELONG_TO_AGGREGATE, "OTHERSTATE", "TestAggregate"));
	}
	
	@Test
	def void ignoreModules4Now() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Module TestModule {
					Service TestService {
						void testOperation() : write [OTHERSTATE -> STATE2];
					}
					
					enum States {
						aggregateLifecycle
						
						STATE1, STATE2, STATE3
					}
					enum OtherStates {
						OTHERSTATE
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

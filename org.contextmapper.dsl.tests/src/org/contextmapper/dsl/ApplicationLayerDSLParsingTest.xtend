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
import org.contextmapper.dsl.contextMappingDSL.CommandInvokation
import org.contextmapper.dsl.contextMappingDSL.CommandInvokationStep
import org.contextmapper.dsl.contextMappingDSL.ConcurrentCommandInvokation
import org.contextmapper.dsl.contextMappingDSL.ConcurrentOperationInvokation
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.contextMappingDSL.DomainEventProductionStep
import org.contextmapper.dsl.contextMappingDSL.ExclusiveAlternativeCommandInvokation
import org.contextmapper.dsl.contextMappingDSL.ExclusiveAlternativeEventProduction
import org.contextmapper.dsl.contextMappingDSL.ExclusiveAlternativeOperationInvokation
import org.contextmapper.dsl.contextMappingDSL.InclusiveAlternativeCommandInvokation
import org.contextmapper.dsl.contextMappingDSL.InclusiveAlternativeEventProduction
import org.contextmapper.dsl.contextMappingDSL.InclusiveAlternativeOperationInvokation
import org.contextmapper.dsl.contextMappingDSL.MultipleEventProduction
import org.contextmapper.dsl.contextMappingDSL.OperationInvokation
import org.contextmapper.dsl.contextMappingDSL.SingleCommandInvokation
import org.contextmapper.dsl.contextMappingDSL.SingleEventProduction
import org.contextmapper.dsl.contextMappingDSL.SingleOperationInvokation
import org.contextmapper.dsl.tests.ContextMappingDSLInjectorProvider
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
class ApplicationLayerDSLParsingTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

	@Test
	def void applicationIsNullIfNotDefined() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertNull(result.boundedContexts.get(0).application);
	}

	@Test
	def void canDefineApplicationLayerInBoundedContext() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Command TestCommand // needs at least some content
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertNotNull(result.boundedContexts.get(0).application);
	}

	@Test
	def void canDefineApplicationServices() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Service TestService {
						void exampleOperation();
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.boundedContexts.get(0).application.services.size);
		assertEquals(1, result.boundedContexts.get(0).application.services.get(0).operations.size);
	}

	@Test
	def void canDefineApplicationCommands() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Command TestCommand
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.boundedContexts.get(0).application.commands.size);
	}

	@Test
	def void canDefineFlow() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Command TestCommand
					
					Flow {
						command TestCommand emits event TestEvent
					}
				}
				
				Aggregate TestAggregate {
					DomainEvent TestEvent
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.boundedContexts.get(0).application.flows.size);
		assertEquals(1, result.boundedContexts.get(0).application.flows.get(0).steps.size);
	}

	@Test
	def void canDefineEventProductionFlowStepWithCommand() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Command TestCommand
					
					Flow {
						command TestCommand emits event TestEvent
					}
				}
				
				Aggregate TestAggregate {
					DomainEvent TestEvent
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var DomainEventProductionStep flowStep = result.boundedContexts.get(0).application.flows.get(0).steps.
			get(0) as DomainEventProductionStep;
		assertEquals("TestCommand", flowStep.action.command.name);
	}

	@Test
	def void canDefineEventProductionFlowStepWithOperation() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Service TestService {
						void testOperation();
					}
					
					Flow {
						operation testOperation emits event TestEvent
					}
				}
				
				Aggregate TestAggregate {
					DomainEvent TestEvent
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var DomainEventProductionStep flowStep = result.boundedContexts.get(0).application.flows.get(0).steps.
			get(0) as DomainEventProductionStep;
		assertEquals("testOperation", flowStep.action.operation.name);
		assertEquals(1, flowStep.eventProduction.events.size);
		assertTrue(flowStep.eventProduction instanceof SingleEventProduction);
		assertEquals("TestEvent", flowStep.eventProduction.events.get(0).name);
	}

	@Test
	def void canDefineEventProductionFlowStepWithMultipleEvents() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Service TestService {
						void testOperation();
					}
					
					Flow {
						operation testOperation emits event TestEvent1 + TestEvent2 + TestEvent3
					}
				}
				
				Aggregate TestAggregate {
					DomainEvent TestEvent1
					DomainEvent TestEvent2
					DomainEvent TestEvent3
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var DomainEventProductionStep flowStep = result.boundedContexts.get(0).application.flows.get(0).steps.
			get(0) as DomainEventProductionStep;
		assertEquals("testOperation", flowStep.action.operation.name);
		assertTrue(flowStep.eventProduction instanceof MultipleEventProduction);
		assertEquals(3, flowStep.eventProduction.events.size);
	}

	@Test
	def void canDefineEventProductionFlowStepWithExclusiveAlternativeEvents() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Service TestService {
						void testOperation();
					}
					
					Flow {
						operation testOperation emits event TestEvent1 X TestEvent2 x TestEvent3
					}
				}
				
				Aggregate TestAggregate {
					DomainEvent TestEvent1
					DomainEvent TestEvent2
					DomainEvent TestEvent3
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var DomainEventProductionStep flowStep = result.boundedContexts.get(0).application.flows.get(0).steps.
			get(0) as DomainEventProductionStep;
		assertEquals("testOperation", flowStep.action.operation.name);
		assertTrue(flowStep.eventProduction instanceof ExclusiveAlternativeEventProduction);
		assertEquals(3, flowStep.eventProduction.events.size);
	}

	@Test
	def void canDefineEventProductionFlowStepWithInclusiveAlternativeEvents() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Service TestService {
						void testOperation();
					}
					
					Flow {
						operation testOperation emits event TestEvent1 O TestEvent2 o TestEvent3
					}
				}
				
				Aggregate TestAggregate {
					DomainEvent TestEvent1
					DomainEvent TestEvent2
					DomainEvent TestEvent3
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var DomainEventProductionStep flowStep = result.boundedContexts.get(0).application.flows.get(0).steps.
			get(0) as DomainEventProductionStep;
		assertEquals("testOperation", flowStep.action.operation.name);
		assertTrue(flowStep.eventProduction instanceof InclusiveAlternativeEventProduction);
		assertEquals(3, flowStep.eventProduction.events.size);
	}

	@Test
	def void canDefineEventProductionFlowStepWithReferenceToAggregate() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Service TestService {
						void testOperation();
					}
					
					Flow {
						operation testOperation delegates to TestAggregate emits event TestEvent1 X TestEvent2
					}
				}
				
				Aggregate TestAggregate {
					DomainEvent TestEvent1
					DomainEvent TestEvent2
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var DomainEventProductionStep flowStep = result.boundedContexts.get(0).application.flows.get(0).steps.
			get(0) as DomainEventProductionStep;
		assertEquals("TestAggregate", flowStep.aggregate.name);
	}

	@Test
	def void canDefineEventProductionFlowStepWithReferenceToAggregateAndStateTransition() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Service TestService {
						void testOperation();
					}
					
					Flow {
						operation testOperation delegates to TestAggregate [STATE1 -> STATE2] emits event TestEvent1 X TestEvent2
					}
				}
				
				Aggregate TestAggregate {
					DomainEvent TestEvent1
					DomainEvent TestEvent2
					
					enum States {
						aggregateLifecycle
						
						STATE1, STATE2
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var DomainEventProductionStep flowStep = result.boundedContexts.get(0).application.flows.get(0).steps.
			get(0) as DomainEventProductionStep;
		assertEquals("TestAggregate", flowStep.aggregate.name);
		assertNotNull(flowStep.stateTransition);
		assertEquals("STATE1", flowStep.stateTransition.from.get(0).name);
		assertEquals("STATE2", flowStep.stateTransition.target.to.get(0).name);
	}

	@Test
	def void canDefineEventProductionFlowStepWithReferenceToAggregateAndStateTransitionWithMultipleSourceStates() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Service TestService {
						void testOperation();
					}
					
					Flow {
						operation testOperation delegates to TestAggregate [STATE1, STATE2 -> STATE3] emits event TestEvent1 X TestEvent2
					}
				}
				
				Aggregate TestAggregate {
					DomainEvent TestEvent1
					DomainEvent TestEvent2
					
					enum States {
						aggregateLifecycle
						
						STATE1, STATE2, STATE3
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var DomainEventProductionStep flowStep = result.boundedContexts.get(0).application.flows.get(0).steps.
			get(0) as DomainEventProductionStep;
		assertEquals("TestAggregate", flowStep.aggregate.name);
		assertNotNull(flowStep.stateTransition);
		assertEquals("STATE1", flowStep.stateTransition.from.get(0).name);
		assertEquals("STATE2", flowStep.stateTransition.from.get(1).name);
		assertEquals("STATE3", flowStep.stateTransition.target.to.get(0).name);
	}

	@Test
	def void canDefineEventProductionFlowStepWithReferenceToAggregateAndAlternativeStateTransition() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Service TestService {
						void testOperation();
					}
					
					Flow {
						operation testOperation delegates to TestAggregate [STATE1 -> STATE2 X STATE3] emits event TestEvent1 X TestEvent2
					}
				}
				
				Aggregate TestAggregate {
					DomainEvent TestEvent1
					DomainEvent TestEvent2
					
					enum States {
						aggregateLifecycle
						
						STATE1, STATE2, STATE3
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var DomainEventProductionStep flowStep = result.boundedContexts.get(0).application.flows.get(0).steps.
			get(0) as DomainEventProductionStep;
		assertEquals("TestAggregate", flowStep.aggregate.name);
		assertNotNull(flowStep.stateTransition);
		assertEquals("STATE1", flowStep.stateTransition.from.get(0).name);
		assertEquals("STATE2", flowStep.stateTransition.target.to.get(0).name);
		assertEquals("STATE3", flowStep.stateTransition.target.to.get(1).name);
	}

	@Test
	def void canDefineCommandInvokationFlowStepWithOperation() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Service TestService {
						void testOperation();
					}
					
					Flow {
						event TestEvent triggers operation testOperation
					}
				}
				
				Aggregate TestAggregate {
					DomainEvent TestEvent
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var CommandInvokationStep flowStep = result.boundedContexts.get(0).application.flows.get(0).steps.
			get(0) as CommandInvokationStep;
		var OperationInvokation action = flowStep.action as OperationInvokation;

		assertTrue(action instanceof SingleOperationInvokation);
		assertEquals("TestEvent", flowStep.event.name);
		assertEquals(1, action.operations.size);
		assertEquals("testOperation", action.operations.get(0).name);
	}

	@Test
	def void canDefineCommandInvokationFlowStepWithMultipleOperations() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Service TestService {
						void testOperation1();
						void testOperation2();
					}
					
					Flow {
						event TestEvent triggers operation testOperation1 + testOperation2
					}
				}
				
				Aggregate TestAggregate {
					DomainEvent TestEvent
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var CommandInvokationStep flowStep = result.boundedContexts.get(0).application.flows.get(0).steps.
			get(0) as CommandInvokationStep;
		var OperationInvokation action = flowStep.action as OperationInvokation;

		assertTrue(action instanceof ConcurrentOperationInvokation);
		assertEquals("TestEvent", flowStep.event.name);
		assertEquals(2, action.operations.size);
		assertEquals("testOperation1", action.operations.get(0).name);
		assertEquals("testOperation2", action.operations.get(1).name);
	}

	@Test
	def void canDefineCommandInvokationFlowStepWithExclusiveAlternativeOperations() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Service TestService {
						void testOperation1();
						void testOperation2();
					}
					
					Flow {
						event TestEvent triggers operation testOperation1 X testOperation2
					}
				}
				
				Aggregate TestAggregate {
					DomainEvent TestEvent
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var CommandInvokationStep flowStep = result.boundedContexts.get(0).application.flows.get(0).steps.
			get(0) as CommandInvokationStep;
		var OperationInvokation action = flowStep.action as OperationInvokation;

		assertTrue(action instanceof ExclusiveAlternativeOperationInvokation);
		assertEquals("TestEvent", flowStep.event.name);
		assertEquals(2, action.operations.size);
		assertEquals("testOperation1", action.operations.get(0).name);
		assertEquals("testOperation2", action.operations.get(1).name);
	}

	@Test
	def void canDefineCommandInvokationFlowStepWithInclusiveAlternativeOperations() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Service TestService {
						void testOperation1();
						void testOperation2();
					}
					
					Flow {
						event TestEvent triggers operation testOperation1 O testOperation2
					}
				}
				
				Aggregate TestAggregate {
					DomainEvent TestEvent
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var CommandInvokationStep flowStep = result.boundedContexts.get(0).application.flows.get(0).steps.
			get(0) as CommandInvokationStep;
		var OperationInvokation action = flowStep.action as OperationInvokation;

		assertTrue(action instanceof InclusiveAlternativeOperationInvokation);
		assertEquals("TestEvent", flowStep.event.name);
		assertEquals(2, action.operations.size);
		assertEquals("testOperation1", action.operations.get(0).name);
		assertEquals("testOperation2", action.operations.get(1).name);
	}

	@Test
	def void canDefineCommandInvokationFlowStepWithCommand() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Command TestCommand
					
					Flow {
						event TestEvent triggers command TestCommand
					}
				}
				
				Aggregate TestAggregate {
					DomainEvent TestEvent
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var CommandInvokationStep flowStep = result.boundedContexts.get(0).application.flows.get(0).steps.
			get(0) as CommandInvokationStep;
		var CommandInvokation action = flowStep.action as CommandInvokation;

		assertTrue(action instanceof SingleCommandInvokation);
		assertEquals("TestEvent", flowStep.event.name);
		assertEquals(1, action.commands.size);
		assertEquals("TestCommand", action.commands.get(0).name);
	}

	@Test
	def void canDefineCommandInvokationFlowStepWithMultipleCommand() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Command TestCommand1
					Command TestCommand2
					
					Flow {
						event TestEvent triggers command TestCommand1 + TestCommand2
					}
				}
				
				Aggregate TestAggregate {
					DomainEvent TestEvent
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var CommandInvokationStep flowStep = result.boundedContexts.get(0).application.flows.get(0).steps.
			get(0) as CommandInvokationStep;
		var CommandInvokation action = flowStep.action as CommandInvokation;

		assertTrue(action instanceof ConcurrentCommandInvokation);
		assertEquals("TestEvent", flowStep.event.name);
		assertEquals(2, action.commands.size);
		assertEquals("TestCommand1", action.commands.get(0).name);
		assertEquals("TestCommand2", action.commands.get(1).name);
	}

	@Test
	def void canDefineCommandInvokationFlowStepWithExclusiveAlternativeCommands() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Command TestCommand1
					Command TestCommand2
					
					Flow {
						event TestEvent triggers command TestCommand1 X TestCommand2
					}
				}
				
				Aggregate TestAggregate {
					DomainEvent TestEvent
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var CommandInvokationStep flowStep = result.boundedContexts.get(0).application.flows.get(0).steps.
			get(0) as CommandInvokationStep;
		var CommandInvokation action = flowStep.action as CommandInvokation;

		assertTrue(action instanceof ExclusiveAlternativeCommandInvokation);
		assertEquals("TestEvent", flowStep.event.name);
		assertEquals(2, action.commands.size);
		assertEquals("TestCommand1", action.commands.get(0).name);
		assertEquals("TestCommand2", action.commands.get(1).name);
	}

	@Test
	def void canDefineCommandInvokationFlowStepWithInclusiveAlternativeCommands() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
				Application {
					Command TestCommand1
					Command TestCommand2
					
					Flow {
						event TestEvent triggers command TestCommand1 O TestCommand2
					}
				}
				
				Aggregate TestAggregate {
					DomainEvent TestEvent
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		var CommandInvokationStep flowStep = result.boundedContexts.get(0).application.flows.get(0).steps.
			get(0) as CommandInvokationStep;
		var CommandInvokation action = flowStep.action as CommandInvokation;

		assertTrue(action instanceof InclusiveAlternativeCommandInvokation);
		assertEquals("TestEvent", flowStep.event.name);
		assertEquals(2, action.commands.size);
		assertEquals("TestCommand1", action.commands.get(0).name);
		assertEquals("TestCommand2", action.commands.get(1).name);
	}

}

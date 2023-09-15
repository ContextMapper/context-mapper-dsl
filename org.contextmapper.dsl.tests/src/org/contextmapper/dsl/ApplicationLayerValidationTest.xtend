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
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.tests.ContextMappingDSLInjectorProvider
import org.contextmapper.dsl.validation.ApplicationFlowSemanticsValidator
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
import static org.contextmapper.dsl.validation.ValidationMessages.COORDINATION_STEP_SERVICE_NOT_ON_STEP_CONTEXT_APPLICATION
import static org.contextmapper.dsl.validation.ValidationMessages.COORDINATION_STEP_CONTEXT_NOT_ON_MAP
import static org.contextmapper.dsl.validation.ValidationMessages.COORDINATION_STEP_OPERATION_NOT_ON_STEP_SERVICE
import static org.contextmapper.dsl.validation.ValidationMessages.COORDINATION_STEP_OPERATION_IS_AMBIGUOUS
import org.contextmapper.dsl.validation.ApplicationCoordinationSemanticsValidator

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class ApplicationLayerValidationTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

	@Test
	def void flowStepCannotReferCommandInOtherContext() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
				Application {
					Flow TestFlow {
						command CommandB emits event EventA
					}
				}
				
				Aggregate AggA {
					DomainEvent EventA
				}
			}
			BoundedContext ContextB {
				Aggregate AggB {
					Command CommandB
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.EITHER_COMMAND_OR_OPERATION, "",
			String.format(COMMAND_OR_OPERATION_IS_NOT_PART_OF_BOUNDED_CONTEXT, "CommandB", "ContextA"));
	}

	@Test
	def void flowStepCannotReferOperationInOtherContext() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
				Application {
					Flow TestFlow {
						operation operationB emits event EventA
					}
				}
				
				Aggregate AggA {
					DomainEvent EventA
				}
			}
			BoundedContext ContextB {
				Aggregate AggB {
					Service ServiceB {
						void operationB();
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.EITHER_COMMAND_OR_OPERATION, "",
			String.format(COMMAND_OR_OPERATION_IS_NOT_PART_OF_BOUNDED_CONTEXT, "operationB", "ContextA"));
	}

	@Test
	def void flowStepCannotReferCommandInvokationInOtherContext() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
				Application {
					Flow TestFlow {
						event EventA triggers command CommandB
					}
				}
				
				Aggregate AggA {
					DomainEvent EventA
				}
			}
			BoundedContext ContextB {
				Aggregate AggB {
					Command CommandB
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.COMMAND_INVOKATION, "",
			String.format(COMMAND_OR_OPERATION_IS_NOT_PART_OF_BOUNDED_CONTEXT, "CommandB", "ContextA"));
	}

	@Test
	def void flowStepCannotReferOperationInvokationInOtherContext() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
				Application {
					Flow TestFlow {
						event EventA triggers operation operationB
					}
				}
				
				Aggregate AggA {
					DomainEvent EventA
				}
			}
			BoundedContext ContextB {
				Aggregate AggB {
					Service ServiceB {
						void operationB();
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.OPERATION_INVOKATION, "",
			String.format(COMMAND_OR_OPERATION_IS_NOT_PART_OF_BOUNDED_CONTEXT, "operationB", "ContextA"));
	}

	@Test
	def void cannotUseFromStateThatDoesNotBelongToAggregate() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
				Application {
					Command CommandA
					
					Flow TestFlow {
						command CommandA delegates to AggA [STATE3 -> STATE1] emits event EventA
					}
				}
				
				Aggregate AggA {
					DomainEvent EventA
					
					enum States {
						aggregateLifecycle
						STATE1, STATE2
					}
					enum AnotherEnum {
						STATE3
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, TacticdslPackage.Literals.STATE_TRANSITION, "",
			String.format(STATE_VALUE_DOES_NOT_BELONG_TO_AGGREGATE, "STATE3", "AggA"));
	}

	@Test
	def void cannotUseToStateThatDoesNotBelongToAggregate() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
				Application {
					Command CommandA
					
					Flow TestFlow {
						command CommandA delegates to AggA [STATE1 -> STATE3] emits event EventA
					}
				}
				
				Aggregate AggA {
					DomainEvent EventA
					
					enum States {
						aggregateLifecycle
						STATE1, STATE2
					}
					enum AnotherEnum {
						STATE3
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, TacticdslPackage.Literals.STATE_TRANSITION_TARGET, "",
			String.format(STATE_VALUE_DOES_NOT_BELONG_TO_AGGREGATE, "STATE3", "AggA"));
	}

	@Test
	def void canOfferSketchMinerLink() {
		// given
		val String dslSnippet = '''
			BoundedContext ContextA {
				Application {
					Flow TestFlow {
						command CommandB emits event EventA
					}
				}
				
				Aggregate AggA {
					DomainEvent EventA
					Command CommandB
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertIssue(result, ContextMappingDSLPackage.Literals.FLOW,
			ApplicationFlowSemanticsValidator.SKETCH_MINER_INFO_ID, Severity.INFO, VISUALIZE_FLOW_WITH_SKETCH_MINER);
	}
	
	@Test
	def void coordinationStepCannotReferenceContextWithoutRelationship() {
		// given
		val String dslSnippet = '''
			ContextMap TestMap {
				contains ContextA
				contains ContextB
			}
			BoundedContext ContextA {
				Application {
					Coordination TestCoordination {
						ContextB::TestService::testOperation;
					}
				}
			}
			BoundedContext ContextB {
				Application {
					Service TestService {
						testOperation;
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.COORDINATION_STEP , "",
			String.format(COORDINATION_STEP_CONTEXT_NOT_REACHABLE, "ContextB"));
	}
	
	@Test
	def void coordinationStepCannotReferenceDomainService() {
		// given
		val String dslSnippet = '''
			ContextMap TestMap {
				contains TestContext
			}
			BoundedContext TestContext {
				Application {
					Coordination TestCoordination {
						TestContext::TestService::testOperation;
					}
				}
				
				Aggregate TestAggregate {
					Service TestService {
						testOperation;
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.COORDINATION_STEP, "",
			String.format(COORDINATION_STEP_SERVICE_NOT_ON_STEP_CONTEXT_APPLICATION, "TestService", "TestContext"));
	}
	
	@Test
	def void coordinationStepCannotReferenceBoundedContextService() {
		// given
		val String dslSnippet = '''
			ContextMap TestMap {
				contains TestContext
			}
			BoundedContext TestContext {
				Application {
					Coordination TestCoordination {
						TestContext::TestService::testOperation;
					}
				}
				
				Service TestService {
					testOperation;
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.COORDINATION_STEP, "",
			String.format(COORDINATION_STEP_SERVICE_NOT_ON_STEP_CONTEXT_APPLICATION, "TestService", "TestContext"));
	}
	
	@Test
	def void coordinationStepCannotReferenceServiceOutOfStepContext() {
		// given
		val String dslSnippet = '''
			ContextMap TestMap {
				contains ContextA
				contains ContextB
			}
			BoundedContext ContextA {
				Application {
					Coordination TestCoordination {
						ContextA::TestService::testOperation;
					}
				}
			}
			BoundedContext ContextB {
				Application {
					Service TestService {
						testOperation;
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.COORDINATION_STEP, "",
			String.format(COORDINATION_STEP_SERVICE_NOT_ON_STEP_CONTEXT_APPLICATION, "TestService", "ContextA"));
	}
	
	@Test
	def void coordinationStepCannotReferenceOperationOutOfStepService() {
		// given
		val String dslSnippet = '''
			ContextMap TestMap {
				contains TestContext
			}
			BoundedContext TestContext {
				Application {
					Coordination TestCoordination {
						TestContext::ServiceA::operationB;
					}
					Service ServiceA {
						operationA;
					}
					Service ServiceB {
						operationB;
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.COORDINATION_STEP, "",
			String.format(COORDINATION_STEP_OPERATION_NOT_ON_STEP_SERVICE, "operationB", "ServiceA", "TestContext"));
	}
	
	@Test
	def void coordinationStepWarnsIfOperationReferenceIsNotUniqueInStepService() {
		// given
		val String dslSnippet = '''
			ContextMap TestMap {
				contains TestContext
			}
			BoundedContext TestContext {
				Application {
					Coordination TestCoordination {
						TestContext::TestService::testOperation;
					}
					Service TestService {
						testOperation;
						testOperation;
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertWarning(result, ContextMappingDSLPackage.Literals.COORDINATION_STEP, "",
			String.format(COORDINATION_STEP_OPERATION_IS_AMBIGUOUS, "testOperation", "TestService"));
	}
	
	@Test
	def void canOfferSketchMinerLinkInCoordination() {
		// given
		val String dslSnippet = '''
			ContextMap TestMap {
				contains TestContext
			}
			BoundedContext TestContext {
				Application {
					Coordination TestCoordination {
						TestContext::TestService::testOperation;
					}
					Service TestService {
						testOperation;
					}
				}
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertIssue(result, ContextMappingDSLPackage.Literals.COORDINATION,
			ApplicationCoordinationSemanticsValidator.SKETCH_MINER_INFO_ID, Severity.INFO, VISUALIZE_COORDINATION_WITH_SKETCH_MINER);
	}
	
}

/*
 * Copyright 2024 The Context Mapper Project Team
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
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.util.ParsingErrorAssertions.*
import static org.junit.jupiter.api.Assertions.*
import org.contextmapper.dsl.contextMappingDSL.PRIORITY
import org.contextmapper.dsl.contextMappingDSL.IMPACT

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class ValueRegisterDSLParsingTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	@Test
	def void canDefineValueRegister() {
		// given
		val String dslSnippet = '''
			ValueRegister TestRegister
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.valueRegisters.size);
		assertEquals("TestRegister", result.valueRegisters.get(0).name);
	}
	
	@Test
	def void canDefineValueRegisterForBoundedContext() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext
			ValueRegister TestRegister for TestContext
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.valueRegisters.size);
		assertEquals("TestRegister", result.valueRegisters.get(0).name);
		assertEquals("TestContext", result.valueRegisters.get(0).context.name);
	}
	
	@Test
	def void canDefineValue() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext
			ValueRegister TestRegister for TestContext {
				Value Privacy
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.valueRegisters.get(0).values.size);
		assertEquals("Privacy", result.valueRegisters.get(0).values.get(0).name);
		assertEquals(false, result.valueRegisters.get(0).values.get(0).isCoreValue);
	}
	
	@Test
	def void canDefineCoreValue() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext
			ValueRegister TestRegister for TestContext {
				Value Privacy {
					isCore
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.valueRegisters.get(0).values.size);
		assertEquals("Privacy", result.valueRegisters.get(0).values.get(0).name);
		assertEquals(true, result.valueRegisters.get(0).values.get(0).isCoreValue);
	}
	
	@Test
	def void canAddDemonstratorToValue1() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext
			ValueRegister TestRegister for TestContext {
				Value Privacy {
					isCore
					demonstrator "right to be alone"
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.valueRegisters.get(0).values.size);
		assertEquals("Privacy", result.valueRegisters.get(0).values.get(0).name);
		assertEquals("right to be alone", result.valueRegisters.get(0).values.get(0).demonstrators.get(0));
	}
	
	@Test
	def void canAddDemonstratorToValue2() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext
			ValueRegister TestRegister for TestContext {
				Value Privacy {
					isCore
					demonstrator = "right to be alone"
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.valueRegisters.get(0).values.size);
		assertEquals("Privacy", result.valueRegisters.get(0).values.get(0).name);
		assertEquals("right to be alone", result.valueRegisters.get(0).values.get(0).demonstrators.get(0));
	}
	
	@Test
	def void canAddMultipleDemonstratorsToValue() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext
			ValueRegister TestRegister for TestContext {
				Value Privacy {
					isCore
					demonstrator "right to be alone"
					demonstrator "right to keep private things secret"
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.valueRegisters.get(0).values.size);
		assertEquals("Privacy", result.valueRegisters.get(0).values.get(0).name);
		assertEquals("right to be alone", result.valueRegisters.get(0).values.get(0).demonstrators.get(0));
		assertEquals("right to keep private things secret", result.valueRegisters.get(0).values.get(0).demonstrators.get(1));
	}
	
	@Test
	def void canLinkValueToStakeholder() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext
			Stakeholders of TestContext {
				Stakeholder TestUser
			}
			ValueRegister TestRegister for TestContext {
				Value Privacy {
					isCore
					demonstrator "right to be alone"
					Stakeholder TestUser
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.valueRegisters.get(0).values.size);
		assertEquals("Privacy", result.valueRegisters.get(0).values.get(0).name);
		assertEquals("TestUser", result.valueRegisters.get(0).values.get(0).elicitations.get(0).stakeholder.name);
	}
	
	@Test
	def void canDefineValuePriorityForStakeholder1() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext
			Stakeholders of TestContext {
				Stakeholder TestUser
			}
			ValueRegister TestRegister for TestContext {
				Value Privacy {
					isCore
					demonstrator "right to be alone"
					Stakeholder TestUser {
						priority HIGH
					}
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.valueRegisters.get(0).values.size);
		assertEquals("Privacy", result.valueRegisters.get(0).values.get(0).name);
		assertEquals("TestUser", result.valueRegisters.get(0).values.get(0).elicitations.get(0).stakeholder.name);
		assertEquals(PRIORITY.HIGH, result.valueRegisters.get(0).values.get(0).elicitations.get(0).priority);
	}
	
	@Test
	def void canDefineValuePriorityForStakeholder2() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext
			Stakeholders of TestContext {
				Stakeholder TestUser
			}
			ValueRegister TestRegister for TestContext {
				Value Privacy {
					isCore
					demonstrator "right to be alone"
					Stakeholder TestUser {
						priority = HIGH
					}
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.valueRegisters.get(0).values.size);
		assertEquals("Privacy", result.valueRegisters.get(0).values.get(0).name);
		assertEquals("TestUser", result.valueRegisters.get(0).values.get(0).elicitations.get(0).stakeholder.name);
		assertEquals(PRIORITY.HIGH, result.valueRegisters.get(0).values.get(0).elicitations.get(0).priority);
	}
	
	@Test
	def void canDefineValueImpactForStakeholder1() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext
			Stakeholders of TestContext {
				Stakeholder TestUser
			}
			ValueRegister TestRegister for TestContext {
				Value Privacy {
					isCore
					demonstrator "right to be alone"
					Stakeholder TestUser {
						impact HIGH
					}
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.valueRegisters.get(0).values.size);
		assertEquals("Privacy", result.valueRegisters.get(0).values.get(0).name);
		assertEquals("TestUser", result.valueRegisters.get(0).values.get(0).elicitations.get(0).stakeholder.name);
		assertEquals(IMPACT.HIGH, result.valueRegisters.get(0).values.get(0).elicitations.get(0).impact);
	}
	
	@Test
	def void canDefineValueImpactForStakeholder2() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext
			Stakeholders of TestContext {
				Stakeholder TestUser
			}
			ValueRegister TestRegister for TestContext {
				Value Privacy {
					isCore
					demonstrator "right to be alone"
					Stakeholder TestUser {
						impact = HIGH
					}
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.valueRegisters.get(0).values.size);
		assertEquals("Privacy", result.valueRegisters.get(0).values.get(0).name);
		assertEquals("TestUser", result.valueRegisters.get(0).values.get(0).elicitations.get(0).stakeholder.name);
		assertEquals(IMPACT.HIGH, result.valueRegisters.get(0).values.get(0).elicitations.get(0).impact);
	}
	
	@Test
	def void canDefineValuePriorityAndImpactForStakeholder1() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext
			Stakeholders of TestContext {
				Stakeholder TestUser
			}
			ValueRegister TestRegister for TestContext {
				Value Privacy {
					isCore
					demonstrator "right to be alone"
					Stakeholder TestUser {
						priority LOW
						impact HIGH
					}
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.valueRegisters.get(0).values.size);
		assertEquals("Privacy", result.valueRegisters.get(0).values.get(0).name);
		assertEquals("TestUser", result.valueRegisters.get(0).values.get(0).elicitations.get(0).stakeholder.name);
		assertEquals(PRIORITY.LOW, result.valueRegisters.get(0).values.get(0).elicitations.get(0).priority);
		assertEquals(IMPACT.HIGH, result.valueRegisters.get(0).values.get(0).elicitations.get(0).impact);
	}
	
	@Test
	def void canDefineValuePriorityAndImpactForStakeholder2() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext
			Stakeholders of TestContext {
				Stakeholder TestUser
			}
			ValueRegister TestRegister for TestContext {
				Value Privacy {
					isCore
					demonstrator "right to be alone"
					Stakeholder TestUser {
						impact HIGH
						priority LOW
					}
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.valueRegisters.get(0).values.size);
		assertEquals("Privacy", result.valueRegisters.get(0).values.get(0).name);
		assertEquals("TestUser", result.valueRegisters.get(0).values.get(0).elicitations.get(0).stakeholder.name);
		assertEquals(IMPACT.HIGH, result.valueRegisters.get(0).values.get(0).elicitations.get(0).impact);
		assertEquals(PRIORITY.LOW, result.valueRegisters.get(0).values.get(0).elicitations.get(0).priority);
	}
	
	@Test
	def void canDefineConsequencesForStakeholder() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext
			Stakeholders of TestContext {
				Stakeholder TestUser
			}
			ValueRegister TestRegister for TestContext {
				Value Privacy {
					isCore
					demonstrator "right to be alone"
					Stakeholder TestUser {
						impact HIGH
						priority LOW
						consequences
							good "somehow respected"
							bad "but user has to provide too much information"
					}
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.valueRegisters.get(0).values.size);
		assertEquals("Privacy", result.valueRegisters.get(0).values.get(0).name);
		assertEquals("TestUser", result.valueRegisters.get(0).values.get(0).elicitations.get(0).stakeholder.name);
		assertEquals(2, result.valueRegisters.get(0).values.get(0).elicitations.get(0).consequences.size);
		assertEquals("good", result.valueRegisters.get(0).values.get(0).elicitations.get(0).consequences.get(0).type);
		assertEquals("somehow respected", result.valueRegisters.get(0).values.get(0).elicitations.get(0).consequences.get(0).consequence);
		assertEquals("bad", result.valueRegisters.get(0).values.get(0).elicitations.get(0).consequences.get(1).type);
		assertEquals("but user has to provide too much information", result.valueRegisters.get(0).values.get(0).elicitations.get(0).consequences.get(1).consequence);
	}
	
	@Test
	def void canDefineActionForConsequencesForStakeholder() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext
			Stakeholders of TestContext {
				Stakeholder TestUser
			}
			ValueRegister TestRegister for TestContext {
				Value Privacy {
					isCore
					demonstrator "right to be alone"
					Stakeholder TestUser {
						impact HIGH
						priority LOW
						consequences
							bad "but user has to provide too much information"
								action "ask for less information" ACT
					}
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.valueRegisters.get(0).values.size);
		assertEquals("Privacy", result.valueRegisters.get(0).values.get(0).name);
		assertEquals("TestUser", result.valueRegisters.get(0).values.get(0).elicitations.get(0).stakeholder.name);
		assertEquals(1, result.valueRegisters.get(0).values.get(0).elicitations.get(0).consequences.size);
		assertEquals("bad", result.valueRegisters.get(0).values.get(0).elicitations.get(0).consequences.get(0).type);
		assertEquals("but user has to provide too much information", result.valueRegisters.get(0).values.get(0).elicitations.get(0).consequences.get(0).consequence);
		assertEquals("ask for less information", result.valueRegisters.get(0).values.get(0).elicitations.get(0).consequences.get(0).action.action);
		assertEquals("ACT", result.valueRegisters.get(0).values.get(0).elicitations.get(0).consequences.get(0).action.type);
	}
	
}

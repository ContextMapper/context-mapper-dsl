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
import org.contextmapper.dsl.contextMappingDSL.INFLUENCE
import org.contextmapper.dsl.contextMappingDSL.INTEREST
import org.contextmapper.dsl.contextMappingDSL.Stakeholder
import org.contextmapper.dsl.contextMappingDSL.StakeholderGroup
import org.contextmapper.dsl.tests.ContextMappingDSLInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.util.ParsingErrorAssertions.*
import static org.junit.jupiter.api.Assertions.*

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class StakeholderDSLParsingTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	@Test
	def void canDefineStakeholderContainerWithoutContext() {
		// given
		val String dslSnippet = '''
			Stakeholders {}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.stakeholders.size);
		assertEquals(0, result.stakeholders.get(0).contexts.size);
	}
	
	@Test
	def void canDefineStakeholderWithoutContext() {
		// given
		val String dslSnippet = '''
			Stakeholders {
				Stakeholder TestUser
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.stakeholders.get(0).stakeholders.size);
		assertEquals("TestUser", result.stakeholders.get(0).stakeholders.get(0).name);
	}

	@Test
	def void canDefineStakeholderContainerForBoundedContext() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext
			
			Stakeholders of testContext
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.stakeholders.size);
		assertEquals("testContext", result.stakeholders.get(0).contexts.get(0).name);
	}
	
	@Test
	def void canDefineStakeholder() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext
			
			Stakeholders of testContext {
				Stakeholder TestUser
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.stakeholders.get(0).stakeholders.size);
		assertEquals("TestUser", result.stakeholders.get(0).stakeholders.get(0).name);
	}
	
	@Test
	def void canDefineStakeholderGroup() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext
			
			Stakeholders of testContext {
				StakeholderGroup Users {
					Stakeholder TestUser1
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.stakeholders.get(0).stakeholders.size);
		assertEquals("Users", result.stakeholders.get(0).stakeholders.get(0).name);
		assertEquals(1, (result.stakeholders.get(0).stakeholders.get(0) as StakeholderGroup).stakeholders.size);
		assertEquals("TestUser1", (result.stakeholders.get(0).stakeholders.get(0) as StakeholderGroup).stakeholders.get(0).name);
	}

	@Test
	def void canDefineMultipleStakeholdersInGroup() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext
			
			Stakeholders of testContext {
				StakeholderGroup Users {
					Stakeholder TestUser1
					Stakeholder TestUser2
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.stakeholders.get(0).stakeholders.size);
		assertEquals("Users", result.stakeholders.get(0).stakeholders.get(0).name);
		assertEquals(2, (result.stakeholders.get(0).stakeholders.get(0) as StakeholderGroup).stakeholders.size);
		assertEquals("TestUser1", (result.stakeholders.get(0).stakeholders.get(0) as StakeholderGroup).stakeholders.get(0).name);
		assertEquals("TestUser2", (result.stakeholders.get(0).stakeholders.get(0) as StakeholderGroup).stakeholders.get(1).name);
	}
	
	@Test
	def void canDefineStakeholdersBesidesStakeholderGroups1() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext
			
			Stakeholders of testContext {
				StakeholderGroup Users {
					Stakeholder TestUser1
					
				}
				Stakeholder TestUser2
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(2, result.stakeholders.get(0).stakeholders.size);
		assertEquals("Users", (result.stakeholders.get(0).stakeholders.get(0) as StakeholderGroup).name);
		assertEquals("TestUser2", (result.stakeholders.get(0).stakeholders.get(1) as Stakeholder).name);
	}
	
	@Test
	def void canDefineStakeholdersBesidesStakeholderGroups2() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext
			
			Stakeholders of testContext {
				Stakeholder TestUser2
				StakeholderGroup Users {
					Stakeholder TestUser1
					
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(2, result.stakeholders.get(0).stakeholders.size);
		assertEquals("TestUser2", (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).name);
		assertEquals("Users", (result.stakeholders.get(0).stakeholders.get(1) as StakeholderGroup).name);
	}
	
	@Test
	def void canDefineStakeholdersInfluence1() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext
			
			Stakeholders of testContext {
				Stakeholder TestUser {
					influence = HIGH
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.stakeholders.get(0).stakeholders.size);
		assertEquals("TestUser", (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).name);
		assertEquals(INFLUENCE.HIGH, (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).influence);
	}
	
	@Test
	def void canDefineStakeholdersInfluence2() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext
			
			Stakeholders of testContext {
				Stakeholder TestUser {
					influence HIGH
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.stakeholders.get(0).stakeholders.size);
		assertEquals("TestUser", (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).name);
		assertEquals(INFLUENCE.HIGH, (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).influence);
	}
	
	@Test
	def void canDefineStakeholdersInterest1() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext
			
			Stakeholders of testContext {
				Stakeholder TestUser {
					interest = HIGH
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.stakeholders.get(0).stakeholders.size);
		assertEquals("TestUser", (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).name);
		assertEquals(INTEREST.HIGH, (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).interest);
	}

	@Test
	def void canDefineStakeholdersInterest2() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext
			
			Stakeholders of testContext {
				Stakeholder TestUser {
					interest HIGH
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.stakeholders.get(0).stakeholders.size);
		assertEquals("TestUser", (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).name);
		assertEquals(INTEREST.HIGH, (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).interest);
	}
	
	@Test
	def void canDefineStakeholdersInfluenceAndInterest1() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext
			
			Stakeholders of testContext {
				Stakeholder TestUser {
					influence HIGH
					interest LOW
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.stakeholders.get(0).stakeholders.size);
		assertEquals("TestUser", (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).name);
		assertEquals(INFLUENCE.HIGH, (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).influence);
		assertEquals(INTEREST.LOW, (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).interest);
	}
	
	@Test
	def void canDefineStakeholdersInfluenceAndInterest2() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext
			
			Stakeholders of testContext {
				Stakeholder TestUser {
					interest LOW
					influence HIGH
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.stakeholders.get(0).stakeholders.size);
		assertEquals("TestUser", (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).name);
		assertEquals(INTEREST.LOW, (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).interest);
		assertEquals(INFLUENCE.HIGH, (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).influence);
	}
	
	@Test
	def void canDefineStakeholderDescription1() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext
			
			Stakeholders of testContext {
				Stakeholder TestUser {
					description = "User that tests something"
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.stakeholders.get(0).stakeholders.size);
		assertEquals("TestUser", (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).name);
		assertEquals("User that tests something", (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).description);
	}
	
	@Test
	def void canDefineStakeholderDescription2() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext
			
			Stakeholders of testContext {
				Stakeholder TestUser {
					description "User that tests something"
				}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.stakeholders.get(0).stakeholders.size);
		assertEquals("TestUser", (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).name);
		assertEquals("User that tests something", (result.stakeholders.get(0).stakeholders.get(0) as Stakeholder).description);
	}
	
}

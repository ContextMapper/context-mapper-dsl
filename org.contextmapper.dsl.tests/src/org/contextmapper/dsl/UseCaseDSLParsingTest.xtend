/*
 * Copyright 2019 The Context Mapper Project Team
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
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.util.ParsingErrorAssertions.*
import static org.junit.jupiter.api.Assertions.*

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class UseCaseDSLParsingTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

	@Test
	def void canDefineUseCase() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		
		assertEquals(1, result.userRequirements.size)
		assertEquals("testUsecase", result.userRequirements.get(0).name)
	}
	
	@Test
	def void canDefineUserStory() {
		// given
		val String dslSnippet = '''
			UserStory testUserStory
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		
		assertEquals(1, result.userRequirements.size)
		assertEquals("testUserStory", result.userRequirements.get(0).name)
	}

	@Test
	def void canDefineReadAttributes() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				reads "Obj.attr1", "Obj.attr2"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		
		assertEquals(2, result.userRequirements.get(0).nanoentitiesRead.size)
		assertEquals("Obj.attr1", result.userRequirements.get(0).nanoentitiesRead.get(0))
		assertEquals("Obj.attr2", result.userRequirements.get(0).nanoentitiesRead.get(1))
	}
	
	@Test
	def void canDefineWriteAttributes() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				writes "Obj.attr1", "Obj.attr2"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		
		assertEquals(2, result.userRequirements.get(0).nanoentitiesWritten.size)
		assertEquals("Obj.attr1", result.userRequirements.get(0).nanoentitiesWritten.get(0))
		assertEquals("Obj.attr2", result.userRequirements.get(0).nanoentitiesWritten.get(1))
	}
	
	@Test
	def void canDefineLatencyCriticalityTrue() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				isLatencyCritical = true
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		
		assertEquals(true, result.userRequirements.get(0).isIsLatencyCritical)
	}
	
	@Test
	def void canDefineLatencyCriticalityFalse() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		
		assertEquals(false, result.userRequirements.get(0).isIsLatencyCritical)
	}
	
	@Test
	def void canDefineLatencyCriticalityWithoutEqualSign() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				isLatencyCritical true
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		
		assertEquals(true, result.userRequirements.get(0).isIsLatencyCritical)
	}
	
	@Test
	def void canDefineActor() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor "tester"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		
		assertEquals("tester", result.userRequirements.get(0).role)
	}
	
	@Test
	def void canDefineActivity() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				activity = create a "Customer"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		
		assertEquals("Insurance Employee", result.userRequirements.get(0).role)
		assertEquals("create", result.userRequirements.get(0).feature.verb)
		assertEquals("Customer", result.userRequirements.get(0).feature.entity)
	}
	
	@Test
	def void canDefineBenefit() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				activity = create a "Customer"
				benefit = "I can manage the customers data and ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		
		assertEquals("Insurance Employee", result.userRequirements.get(0).role)
		assertEquals("create", result.userRequirements.get(0).feature.verb)
		assertEquals("Customer", result.userRequirements.get(0).feature.entity)
		assertEquals("I can manage the customers data and ...", result.userRequirements.get(0).benefit)
	}
	
}

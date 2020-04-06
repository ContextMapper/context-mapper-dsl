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
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage
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
import static org.contextmapper.dsl.validation.ValidationMessages.*

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class UserStoryDSLParsingTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

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
	def void canDefineStory() {
		// given
		val String dslSnippet = '''
			UserStory testUsecase {
				As an "Insurance Employee" I want to create a "Customer" so that "I can manage the customers data and ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		assertEquals("Insurance Employee", result.userRequirements.get(0).role)
		assertEquals("create", result.userRequirements.get(0).features.get(0).verb)
		assertEquals("Customer", result.userRequirements.get(0).features.get(0).entity)
		assertEquals("I can manage the customers data and ...", result.userRequirements.get(0).benefit)
	}
	
	@Test
	def void canDefineStoryWithoutA() {
		// given
		val String dslSnippet = '''
			UserStory testUsecase {
				As an "Insurance Employee" I want to create "Customer" so that "I can manage the customers data and ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		assertEquals("Insurance Employee", result.userRequirements.get(0).role)
		assertEquals("create", result.userRequirements.get(0).features.get(0).verb)
		assertEquals("Customer", result.userRequirements.get(0).features.get(0).entity)
		assertEquals("I can manage the customers data and ...", result.userRequirements.get(0).benefit)
	}

	@Test
	def void verbWithWhitespacesProducesWarning() {
		// given
		val String dslSnippet = '''
			UserStory testUsecase {
				As an "Insurance Employee" I want to "create and delete" a "Customer" so that "I can manage the customers data and ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertWarning(result, ContextMappingDSLPackage.Literals.FEATURE, "",
			VERB_CONTAINS_INVALID_CHARACTERS);
	}
	
	@Test
	def void entityWithWhitespacesProducesWarning() {
		// given
		val String dslSnippet = '''
			UserStory testUsecase {
				As an "Insurance Employee" I want to create a "Customer Entity" so that "I can manage the customers data and ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertWarning(result, ContextMappingDSLPackage.Literals.FEATURE, "",
			ENTITY_NAME_CONTAINS_INVALID_CHARACTERS);
	}
	
	@Test
	def void canDefineStoryWithMultipleFeatures() {
		// given
		val String dslSnippet = '''
			UserStory testUsecase {
				As an "Insurance Employee" 
					I want to create a "Customer"
					I want to update a "Customer"
					I want to create an "Address"
				so that "I can manage the customers data and ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		
		assertEquals(1, result.userRequirements.size)
		assertEquals(3, result.userRequirements.get(0).features.size)
	}

}

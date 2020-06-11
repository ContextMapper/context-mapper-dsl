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
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.tests.ContextMappingDSLInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.validation.ValidationMessages.*
import static org.contextmapper.dsl.util.ParsingErrorAssertions.*

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class UserRequirementsValidatorTest {

	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

	@Test
	def void canCreateErrorIfReadNanoentityStringNotCorrect() {
		// given
		val String dslSnippet = '''
			UseCase TestUsecase {
				reads "just a string"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.USER_REQUIREMENT, "",
			String.format(STRING_IS_NOT_NANOENTITY, "just a string"));
	}

	@Test
	def void canCreateErrorIfWrittenNanoentityStringNotCorrect() {
		// given
		val String dslSnippet = '''
			UseCase TestUsecase {
				writes "just a string"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.USER_REQUIREMENT, "",
			String.format(STRING_IS_NOT_NANOENTITY, "just a string"));
	}

	@Test
	def void canIgnoreCorrectNanoentityReadStrings() {
		// given
		val String dslSnippet = '''
			UseCase TestUsecase {
				reads "Entity.attribute"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
	}

	@Test
	def void canIgnoreCorrectNanoentityWriteStrings() {
		// given
		val String dslSnippet = '''
			UseCase TestUsecase {
				writes "Entity.attribute"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
	}

}

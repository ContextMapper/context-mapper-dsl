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
import org.contextmapper.dsl.contextMappingDSL.UseCase
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
	def void canDefineOneSecondaryActor() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor "tester"
				secondaryActors "tester2"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val uc = result.userRequirements.get(0) as UseCase;
		assertEquals(1, uc.secondaryActors.size)
		assertEquals("tester2", uc.secondaryActors.get(0))
	}
	
	@Test
	def void canDefineMultipleSecondaryActors() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor "tester"
				secondaryActors "tester2", "tester3", "tester4"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val uc = result.userRequirements.get(0) as UseCase;
		assertEquals(3, uc.secondaryActors.size)
		assertEquals("tester2", uc.secondaryActors.get(0))
		assertEquals("tester3", uc.secondaryActors.get(1))
		assertEquals("tester4", uc.secondaryActors.get(2))
	}

	@Test
	def void canDefineActivity() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				interactions = create a "Customer"
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
	}

	@Test
	def void canDefineBenefit() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				interactions = create a "Customer"
				benefit = "I can manage the customers data and ..."
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
	def void canDefineMultipleInteractions() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				interactions = create a "Customer", "update" an "Address"
				benefit = "I can manage the customers data and ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		assertEquals(2, result.userRequirements.get(0).features.size)
	}

	@Test
	def void canDefineEntityAttributes() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				interactions = create a "Customer" with its "firstname", "lastname"
				benefit = "I can manage the customers data and ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val feature = result.userRequirements.get(0).features.get(0);
		assertTrue(feature.entityAttributes.contains("firstname"));
		assertTrue(feature.entityAttributes.contains("lastname"));
	}
	
	@Test
	def void canDefineEntityAttributesInPluralSentence() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				interactions = create "Customers" with their "firstname", "lastname"
				benefit = "I can manage the customers data and ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val feature = result.userRequirements.get(0).features.get(0);
		assertTrue(feature.entityAttributes.contains("firstname"));
		assertTrue(feature.entityAttributes.contains("lastname"));
	}

	@Test
	def void canDefineContainmentRelationshipSyntaxVariant1() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				interactions = create an "Address" with its "firstname", "lastname" for a "Customer"
				benefit = "I can manage the customers data and ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val feature = result.userRequirements.get(0).features.get(0);
		assertEquals("Customer", feature.containerEntity);
	}

	@Test
	def void canDefineContainmentRelationshipSyntaxVariant2() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				interactions = create an "Address" with its "firstname", "lastname" in a "Customer"
				benefit = "I can manage the customers data and ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val feature = result.userRequirements.get(0).features.get(0);
		assertEquals("Customer", feature.containerEntity);
	}

	@Test
	def void canDefineContainmentRelationshipSyntaxVariant3() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				interactions = create an "Address" with its "firstname", "lastname" for an "Customer"
				benefit = "I can manage the customers data and ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val feature = result.userRequirements.get(0).features.get(0);
		assertEquals("Customer", feature.containerEntity);
	}

	@Test
	def void canDefineContainmentRelationshipSyntaxVariant4() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				interactions = create an "Address" with its "firstname", "lastname" in an "Customer"
				benefit = "I can manage the customers data and ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val feature = result.userRequirements.get(0).features.get(0);
		assertEquals("Customer", feature.containerEntity);
	}

	@Test
	def void canDefineContainmentRelationshipSyntaxVariant5() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				interactions = create an "Address" with its "firstname", "lastname" for "Customer"
				benefit = "I can manage the customers data and ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val feature = result.userRequirements.get(0).features.get(0);
		assertEquals("Customer", feature.containerEntity);
	}

	@Test
	def void canDefineContainmentRelationshipSyntaxVariant6() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				interactions = create an "Address" with its "firstname", "lastname" in "Customer"
				benefit = "I can manage the customers data and ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val feature = result.userRequirements.get(0).features.get(0);
		assertEquals("Customer", feature.containerEntity);
	}
	
	@Test
	def void canDefineContainmentRelationshipSyntaxVariant7() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				interactions = "add" an "Address" with its "firstname", "lastname" to a "Customer"
				benefit = "I can manage the customers data and ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val feature = result.userRequirements.get(0).features.get(0);
		assertEquals("Customer", feature.containerEntity);
	}
	
	@Test
	def void canDefineContainmentRelationshipSyntaxVariant8() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				interactions = "add" an "Address" with its "firstname", "lastname" to an "Customer"
				benefit = "I can manage the customers data and ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val feature = result.userRequirements.get(0).features.get(0);
		assertEquals("Customer", feature.containerEntity);
	}
	
	@Test
	def void canDefineContainmentRelationshipSyntaxVariant9() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				interactions = 
					"add" an "Address" with its "firstname", "lastname" to "Customer",
					"add" an "Address" with its "firstname", "lastname" to a "Customer"
				benefit = "I can manage the customers data and ..."
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val feature1 = result.userRequirements.get(0).features.get(0);
		val feature2 = result.userRequirements.get(0).features.get(1);
		assertEquals("Customer", feature1.containerEntity);
		assertEquals("Customer", feature2.containerEntity);
	}

	@Test
	def void canDefineScope() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				interactions = create an "Address"
				benefit = "I can manage the customers addresses."
				scope = "Insurance ERP"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val ur = result.userRequirements.get(0) as UseCase;
		assertEquals("Insurance ERP", ur.scope);
	}

	@Test
	def void canDefineLevel() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				interactions = create an "Address"
				benefit = "I can manage the customers addresses."
				scope = "Insurance ERP"
				level = "Sea Level"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val ur = result.userRequirements.get(0) as UseCase;
		assertEquals("Sea Level", ur.level);
	}

	@Test
	def void canDefineUseCaseWithMultipleFeatures() {
		// given
		val String dslSnippet = '''
			UseCase testUsecase {
				actor = "Insurance Employee"
				interactions = 
					create a "Customer",
					create an "Address",
					create the "Test"
				benefit = "I can manage the customers addresses."
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

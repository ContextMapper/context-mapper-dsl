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
import org.contextmapper.dsl.contextMappingDSL.BoundedContext
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType
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

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class BoundedContextDSLParsingTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

	@Test
	def void canDefineBoundedContext() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.boundedContexts.size);
		assertEquals("testContext", result.boundedContexts.get(0).name);
	}

	@Test
	def void canDefineContextType() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				type = FEATURE
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(BoundedContextType.FEATURE, result.boundedContexts.get(0).type);
	}

	@Test
	def void canDefineContextResponsibility() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				responsibilities = "resp1"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.boundedContexts.get(0).responsibilities.size)
		assertEquals("resp1", result.boundedContexts.get(0).responsibilities.get(0))
	}

	@Test
	def void canDefineContextResponsibilities() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				responsibilities = "a responsibility description...", "another responsibility"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(2, result.boundedContexts.get(0).responsibilities.size)

		val responsibilities = result.boundedContexts.get(0).responsibilities;
		assertTrue(responsibilities.contains("a responsibility description..."));
		assertTrue(responsibilities.contains("another responsibility"));
	}

	@Test
	def void canAddVisionStatement() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				domainVisionStatement = "this is my vision"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals("this is my vision", result.boundedContexts.get(0).domainVisionStatement);
	}

	@Test
	def void canDefineKnowledgeLevel() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				knowledgeLevel = META
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(KnowledgeLevel.META, result.boundedContexts.get(0).knowledgeLevel);
	}

	@Test
	def void canDefineImplementationTechnology() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				implementationTechnology = "Java / Spring Boot App"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals("Java / Spring Boot App", result.boundedContexts.get(0).implementationTechnology);
	}

	@Test
	def void canDefineTeamRealizesContext() {
		// given
		val String dslSnippet = '''
			BoundedContext TeamA realizes ContextA {
				type = TEAM
			}
			BoundedContext ContextA {
				type = FEATURE
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		
		val BoundedContext team = result.boundedContexts.findFirst[it.type == BoundedContextType.TEAM];
		assertEquals("ContextA", team.realizedBoundedContexts.get(0).name);
	}

	@Test
	def void teamCanRealizeMultipleBCs() {
		// given
		val String dslSnippet = '''
			BoundedContext TeamA realizes ContextA, ContextB {
				type = TEAM
			}
			BoundedContext ContextA {
				type = FEATURE
			}

			BoundedContext ContextB {
				type = FEATURE
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		
		val BoundedContext team = result.boundedContexts.findFirst[it.type == BoundedContextType.TEAM];
		val realizedBCNames = team.realizedBoundedContexts.stream.map[name].collect(Collectors.toList);
		assertTrue(realizedBCNames.contains("ContextA"));
		assertTrue(realizedBCNames.contains("ContextB"));
	}

	@Test
	def void cannotDefineRealizationIfBoundedContextIsNotTeam() {
		// given
		val String dslSnippet = '''
			BoundedContext NotATeam realizes ContextA {
				type = SYSTEM
			}
			BoundedContext ContextA {
				type = FEATURE
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.BOUNDED_CONTEXT, "",
			String.format(ONLY_TEAMS_CAN_REALIZE_OTHER_BOUNDED_CONTEXT, "NotATeam"));
	} 
	
	@Test
	def void canDefineAttributesWithoutEqualSign() {
		// given
		val String dslSnippet = '''
			BoundedContext testContext {
				type FEATURE
				implementationTechnology "Java / Spring Boot App"
				knowledgeLevel META
				domainVisionStatement "this is my vision"
				responsibilities "a responsibility description...", "another responsibility"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals("Java / Spring Boot App", result.boundedContexts.get(0).implementationTechnology);
		assertEquals(KnowledgeLevel.META, result.boundedContexts.get(0).knowledgeLevel);
		assertEquals("this is my vision", result.boundedContexts.get(0).domainVisionStatement);
		
		val responsibilities = result.boundedContexts.get(0).responsibilities;
		assertTrue(responsibilities.contains("a responsibility description..."));
		assertTrue(responsibilities.contains("another responsibility"));
		assertEquals(BoundedContextType.FEATURE, result.boundedContexts.get(0).type);
	}
}

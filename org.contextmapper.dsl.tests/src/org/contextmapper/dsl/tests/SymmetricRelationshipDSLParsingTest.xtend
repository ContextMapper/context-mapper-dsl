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
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.contextMappingDSL.Partnership
import org.contextmapper.dsl.contextMappingDSL.Relationship
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.tests.util.ParsingErrorAssertions.*
import static org.junit.jupiter.api.Assertions.*
import org.contextmapper.dsl.contextMappingDSL.SharedKernel

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class SymmetricRelationshipDSLParsingTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	@Test
	def void canDefinePartnership() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 contains testContext
				 contains anotherTestContext

				 testContext Partnership anotherTestContext : testrel
			}

			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.map.relationships.size);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(Partnership))

		val Partnership partnership = relationship as Partnership;
		assertEquals("testrel", partnership.name);
		assertEquals("testContext", partnership.participant1.name);
		assertEquals("anotherTestContext", partnership.participant2.name);
	}

	@Test
	def void canDefinePartnershipInShortSyntaxVariantOne() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 contains testContext
				 contains anotherTestContext

				 testContext [P]<->[P] anotherTestContext
			}

			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.map.relationships.size);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(Partnership))

		val Partnership partnership = relationship as Partnership;
		assertEquals("testContext", partnership.participant1.name);
		assertEquals("anotherTestContext", partnership.participant2.name);
	}
	
	@Test
	def void canDefinePartnershipInShortSyntaxVariantTwo() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 contains testContext
				 contains anotherTestContext

				 [P]testContext <-> [P]anotherTestContext
			}

			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.map.relationships.size);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(Partnership))

		val Partnership partnership = relationship as Partnership;
		assertEquals("testContext", partnership.participant1.name);
		assertEquals("anotherTestContext", partnership.participant2.name);
	}
	
	@Test
	def void canDefinePartnershipInShortSyntaxVariantThree() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 contains testContext
				 contains anotherTestContext

				 testContext[P] <-> anotherTestContext[P]
			}

			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.map.relationships.size);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(Partnership))

		val Partnership partnership = relationship as Partnership;
		assertEquals("testContext", partnership.participant1.name);
		assertEquals("anotherTestContext", partnership.participant2.name);
	}
	
	@Test
	def void canDefinePartnershipInShortSyntaxVariantFour() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 contains testContext
				 contains anotherTestContext

				 [P]testContext <-> anotherTestContext[P]
			}

			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.map.relationships.size);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(Partnership))

		val Partnership partnership = relationship as Partnership;
		assertEquals("testContext", partnership.participant1.name);
		assertEquals("anotherTestContext", partnership.participant2.name);
	}

	@Test
	def void canDefineSharedKernel() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 contains testContext
				 contains anotherTestContext

				 testContext Shared-Kernel anotherTestContext : testrel
			}

			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.map.relationships.size);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(SharedKernel))

		val SharedKernel sharedKernel = relationship as SharedKernel;
		assertEquals("testContext", sharedKernel.participant1.name);
		assertEquals("anotherTestContext", sharedKernel.participant2.name);
		assertEquals("testrel", sharedKernel.name);
	}

	@Test
	def void canDefineSharedKernelInShortSyntaxVariantOne() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 contains testContext
				 contains anotherTestContext

				 testContext [SK]<->[SK] anotherTestContext
			}

			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.map.relationships.size);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(SharedKernel))

		val SharedKernel sharedKernel = relationship as SharedKernel;
		assertEquals("testContext", sharedKernel.participant1.name);
		assertEquals("anotherTestContext", sharedKernel.participant2.name);
	}
	
	@Test
	def void canDefineSharedKernelInShortSyntaxVariantTwo() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 contains testContext
				 contains anotherTestContext

				 [SK]testContext <-> [SK]anotherTestContext
			}

			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.map.relationships.size);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(SharedKernel))

		val SharedKernel sharedKernel = relationship as SharedKernel;
		assertEquals("testContext", sharedKernel.participant1.name);
		assertEquals("anotherTestContext", sharedKernel.participant2.name);
	}
	
	@Test
	def void canDefineSharedKernelInShortSyntaxVariantThree() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 contains testContext
				 contains anotherTestContext

				 testContext[SK] <-> anotherTestContext[SK]
			}

			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.map.relationships.size);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(SharedKernel))

		val SharedKernel sharedKernel = relationship as SharedKernel;
		assertEquals("testContext", sharedKernel.participant1.name);
		assertEquals("anotherTestContext", sharedKernel.participant2.name);
	}
	
	@Test
	def void canDefineSharedKernelInShortSyntaxVariantFour() {
		// given
		val String dslSnippet = '''
			ContextMap {
				 contains testContext
				 contains anotherTestContext

				 [SK]testContext <-> anotherTestContext[SK]
			}

			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals(1, result.map.relationships.size);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(SharedKernel))

		val SharedKernel sharedKernel = relationship as SharedKernel;
		assertEquals("testContext", sharedKernel.participant1.name);
		assertEquals("anotherTestContext", sharedKernel.participant2.name);
	}
	
	@Test
	def void canDefineSharedKernelTechnology() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains testContext
				contains anotherTestContext

				testContext [SK]<->[SK] anotherTestContext {
				 	implementationTechnology = "RPC"
			 	}
			}

			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals("RPC", result.map.relationships.get(0).implementationTechnology);
	}
	
	@Test
	def void canDefinePartnershipTechnology() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains testContext
				contains anotherTestContext

				testContext Partnership anotherTestContext : testrel {
				 	implementationTechnology = "Messaging"
			 	}
			}

			BoundedContext testContext
			BoundedContext anotherTestContext
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals("Messaging", result.map.relationships.get(0).implementationTechnology);
	}
}

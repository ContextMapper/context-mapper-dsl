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
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.contextMappingDSL.Relationship
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.tests.util.ParsingErrorAssertions.*
import static org.junit.jupiter.api.Assertions.*
import static org.contextmapper.dsl.validation.ValidationMessages.*
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class UpstreamDownstreamExposedAggregatesTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

	@Test
	def void canDefineSingleExposedAggregate() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains testContext
				contains anotherTestContext
			
				testContext [U,OHS,PL]->[D,CF] anotherTestContext {
					exposedAggregates = myAggregate
				}
			}
			
			BoundedContext testContext {
				Aggregate myAggregate {}
			}
			BoundedContext anotherTestContext
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(UpstreamDownstreamRelationship))

		val UpstreamDownstreamRelationship upstreamDownstreamRelationship = relationship as UpstreamDownstreamRelationship
		assertEquals("testContext", upstreamDownstreamRelationship.upstream.name)
		assertEquals("anotherTestContext", upstreamDownstreamRelationship.downstream.name)

		assertEquals(1, upstreamDownstreamRelationship.upstreamExposedAggregates.size)
		assertEquals("myAggregate", upstreamDownstreamRelationship.upstreamExposedAggregates.get(0).name)
	}
	
	@Test
	def void canDefineMultipleExposedAggregate() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains testContext
				contains anotherTestContext
			
				testContext [U,OHS,PL]->[D,CF] anotherTestContext {
					exposedAggregates = myAggregate, anotherAggregate
				}
			}
			
			BoundedContext testContext {
				Aggregate myAggregate {}
				Aggregate anotherAggregate {}
			}
			BoundedContext anotherTestContext
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		
		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(UpstreamDownstreamRelationship))

		val UpstreamDownstreamRelationship upstreamDownstreamRelationship = relationship as UpstreamDownstreamRelationship
		assertEquals("testContext", upstreamDownstreamRelationship.upstream.name)
		assertEquals("anotherTestContext", upstreamDownstreamRelationship.downstream.name)

		assertEquals(2, upstreamDownstreamRelationship.upstreamExposedAggregates.size)
		
		val aggregateNames = upstreamDownstreamRelationship.upstreamExposedAggregates.stream.map[name].collect(Collectors.toList);
		assertTrue(aggregateNames.contains("myAggregate"))
		assertTrue(aggregateNames.contains("anotherAggregate"))
	}
	
	@Test
	def void throwErrorIfAggregateIsNotPartOfUpstreamContext() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains testContext
				contains anotherTestContext
			
				testContext [U,OHS,PL]->[D,CF] anotherTestContext {
					exposedAggregates = anotherAggregate
				}
			}
			
			BoundedContext testContext {
				Aggregate myAggregate {}
			}
			BoundedContext anotherTestContext {
				Aggregate anotherAggregate {}
			}
		''';
		
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);

		// then
		assertThatNoParsingErrorsOccurred(result);
		validationTestHelper.assertError(result, ContextMappingDSLPackage.Literals.UPSTREAM_DOWNSTREAM_RELATIONSHIP, "",
			String.format(EXPOSED_AGGREGATE_NOT_PART_OF_UPSTREAM_CONTEXT, "anotherAggregate", "testContext"));
	}
	
}

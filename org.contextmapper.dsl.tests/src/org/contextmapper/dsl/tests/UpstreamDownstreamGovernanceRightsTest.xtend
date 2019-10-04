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
import java.util.ArrayList
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.contextMappingDSL.CustomerSupplierRelationship
import org.contextmapper.dsl.contextMappingDSL.DownstreamGovernanceRights
import org.contextmapper.dsl.contextMappingDSL.Relationship
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship
import org.contextmapper.dsl.contextMappingDSL.UpstreamRole
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.tests.util.ParsingErrorAssertions.*
import static org.junit.jupiter.api.Assertions.*

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class UpstreamDownstreamGovernanceRightsTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

	@Test
	def void canDefineGovernanceRights() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains testContext
				contains anotherTestContext
			
				testContext [U,OHS,PL]->[D] anotherTestContext {
					downstreamRights = INFLUENCER
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

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(UpstreamDownstreamRelationship))

		val UpstreamDownstreamRelationship upstreamDownstreamRelationship = relationship as UpstreamDownstreamRelationship
		assertEquals("testContext", upstreamDownstreamRelationship.upstream.name)
		assertEquals("anotherTestContext", upstreamDownstreamRelationship.downstream.name)

		assertTrue(upstreamDownstreamRelationship.upstreamRoles.contains(UpstreamRole.OPEN_HOST_SERVICE))
		assertTrue(upstreamDownstreamRelationship.upstreamRoles.contains(UpstreamRole.PUBLISHED_LANGUAGE))

		assertEquals(DownstreamGovernanceRights.INFLUENCER, upstreamDownstreamRelationship.downstreamGovernanceRights);
	}
	
	@Test
	def void canDefineGovernanceRightsOnCustomerSupplier() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains testContext
				contains anotherTestContext
			
				testContext [U,S]->[D,C] anotherTestContext {
					downstreamRights = VETO_RIGHT
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

		val Relationship relationship = result.map.relationships.get(0)
		assertTrue(relationship.class.interfaces.contains(CustomerSupplierRelationship))

		val CustomerSupplierRelationship customerSupplierRelationship = relationship as CustomerSupplierRelationship
		assertEquals("testContext", customerSupplierRelationship.upstream.name)
		assertEquals("anotherTestContext", customerSupplierRelationship.downstream.name)

		assertEquals(DownstreamGovernanceRights.VETO_RIGHT, customerSupplierRelationship.downstreamGovernanceRights);
	}
	
	@Test
	def void canDefineAttributesWithoutEqualSign() {
		// given
		val String dslSnippet = '''
			ContextMap {
				contains testContext
				contains anotherTestContext
			
				anotherTestContext [S]->[C] testContext {
					downstreamRights INFLUENCER
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
		
		val CustomerSupplierRelationship customerSupplierRelationship = result.map.relationships.get(0) as CustomerSupplierRelationship
		assertEquals(DownstreamGovernanceRights.INFLUENCER, customerSupplierRelationship.downstreamGovernanceRights);
	}
	
}

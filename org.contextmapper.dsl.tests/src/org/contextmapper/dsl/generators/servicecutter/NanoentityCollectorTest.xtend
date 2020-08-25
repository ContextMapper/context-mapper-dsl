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
package org.contextmapper.dsl.generators.servicecutter

import com.google.inject.Inject
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.generator.servicecutter.input.userrepresentations.NanoentityCollector
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
class NanoentityCollectorTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	@Test
	def void canCollectNanoentities4DefaultVerbs() {
		// given
		val String dslSnippet = '''
			UseCase TestCase {
				interactions
					create a "Claim" with its "date", // write
					update a "Claim" with its "number", // write
					delete a "Claim" with its "number", // write
					read a "Claim" with its "date", "description" // read
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val req = result.userRequirements.get(0);
		val reads = new NanoentityCollector().getNanoentitiesRead(req);
		val writes = new NanoentityCollector().getNanoentitiesWritten(req);

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		assertEquals(2, reads.size);
		assertEquals(2, writes.size);
		assertTrue(reads.contains("Claim.date"));
		assertTrue(reads.contains("Claim.description"));
		assertTrue(writes.contains("Claim.number"));
		assertTrue(writes.contains("Claim.date"));
	}

	@Test
	def void canCollectNanoEntities4CustomVerbs() {
		// given
		val String dslSnippet = '''
			UseCase TestCase {
				interactions
					"submit" a "Claim" with its "date",
					"reject" a "Claim" with its "number"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val req = result.userRequirements.get(0);
		val reads = new NanoentityCollector().getNanoentitiesRead(req);
		val writes = new NanoentityCollector().getNanoentitiesWritten(req);

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		assertEquals(2, reads.size);
		assertEquals(2, writes.size);
		assertTrue(reads.contains("Claim.date"));
		assertTrue(reads.contains("Claim.number"));
		assertTrue(writes.contains("Claim.date"));
		assertTrue(writes.contains("Claim.number"));
	}

	@Test
	def void canIgnoreEmptyStrings() {
		// given
		val String dslSnippet = '''
			UseCase TestCase {
				interactions
					"submit" a "Claim" with its "date", ""
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val req = result.userRequirements.get(0);
		val reads = new NanoentityCollector().getNanoentitiesRead(req);
		val writes = new NanoentityCollector().getNanoentitiesWritten(req);

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		assertEquals(1, reads.size);
		assertEquals(1, writes.size);
		assertTrue(reads.contains("Claim.date"));
		assertTrue(writes.contains("Claim.date"));
	}

	@Test
	def void canFormatAttributeStrings() {
		// given
		val String dslSnippet = '''
			UseCase TestCase {
				interactions
					create a "Claim" with its "a", "B", "Cee", "dee"
			}
		''';
		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val req = result.userRequirements.get(0);
		val writes = new NanoentityCollector().getNanoentitiesWritten(req);

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);

		assertEquals(4, writes.size);
		assertTrue(writes.contains("Claim.a"));
		assertTrue(writes.contains("Claim.b"));
		assertTrue(writes.contains("Claim.cee"));
		assertTrue(writes.contains("Claim.dee"));
	}

}

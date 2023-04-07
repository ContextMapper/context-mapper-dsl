/*
 * Copyright 2023 The Context Mapper Project Team
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
import org.contextmapper.tactic.dsl.tacticdsl.CollectionType
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject
import org.contextmapper.tactic.dsl.tacticdsl.Reference
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.util.ParsingErrorAssertions.*
import static org.junit.jupiter.api.Assertions.*

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class TacticDDDListOfEnumsTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	@Test
	def void canDefineListOfEnums() {
		// given
		val String dslSnippet = '''
			BoundedContext TestContext {
			    Aggregate TestAggregate {
			        Entity TestEntity {
			            - List<UserRoles> roles;
			        }
			        enum UserRoles {
			            ADMIN, MANAGER, USER
			        }
			    }
			}
		''';

		// when
		val ContextMappingModel result = parseHelper.parse(dslSnippet);
		val DomainObject entity = result.boundedContexts.get(0).aggregates.get(0).domainObjects.get(0) as DomainObject;
		val Reference rolesReference = entity.references.get(0)

		// then
		assertThatNoParsingErrorsOccurred(result);
		assertThatNoValidationErrorsOccurred(result);
		assertEquals("TestEntity", entity.name);
		assertEquals(1, entity.references.size);
		assertEquals("roles", rolesReference.name);
		assertEquals(CollectionType.LIST, rolesReference.collectionType)
		assertEquals("UserRoles", rolesReference.domainObjectType.name)
	}
}

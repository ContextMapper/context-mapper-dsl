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
package org.contextmapper.dsl.ide.tests.actions

import org.junit.jupiter.api.Test

class SplitAggregateByEntitiesActionTest extends AbstractBoundedContextCodeActionTest {

	@Test
	def void canCreateCodeAction4SplitByEntities() {
		testCodeAction [
			model = '''
				BoundedContext TestContext {
					Aggregate TestAggregate {
						Entity Entity1
						Entity Entity2
					}
				}
			'''
			line = 1
			expectedCodeActions = '''
				command : cml.ar.splitAggregateByEntities
				title : Split Aggregate By Entities
				args : 
				    file://«this.root»/MyModel.cml,TestAggregate
			'''
		]
	}

	@Test
	def void canIgnoreAggregateIfOnlyOneEntityInvolved() {
		testCodeAction [
			model = '''
				BoundedContext TestContext {
					Aggregate TestAggregate {
						Entity Entity1
					}
				}
			'''
			line = 1
			expectedCodeActions = '''
			'''
		]
	}
	
	@Test
	def void canIgnoreMultipleAggregatesSelected() {
		testCodeAction [
			model = '''
				BoundedContext TestContext {
					Aggregate TestAggregate1 {} Aggregate TestAggregate2 {}
				}
			'''
			line = 1
			expectedCodeActions = '''
			'''
		]
	}

}

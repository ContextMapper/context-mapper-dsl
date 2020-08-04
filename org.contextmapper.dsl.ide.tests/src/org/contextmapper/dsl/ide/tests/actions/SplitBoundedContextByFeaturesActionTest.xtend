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

class SplitBoundedContextByFeaturesActionTest extends AbstractBoundedContextCodeActionTest {

	@Test
	def void canCreateCodeAction4SplitByFeature() {
		testCodeAction [
			model = '''
				BoundedContext TestContext {
					Aggregate TestAggregate1 {
						userRequirements Story1
					}
					Aggregate TestAggregate2 {
						userRequirements Story2
					}
				}
				
				UserStory Story1
				UserStory Story2
			'''
			expectedCodeActions = '''
				command : cml.ar.splitBCByFeature
				title : Split Bounded Context by Features
				args : 
				    file://«this.root»/MyModel.cml,TestContext
				command : cml.ar.extractAggregatesByCohesion.proxy
				title : Extract Aggregates By Cohesion
				args : 
				    file://«this.root»/MyModel.cml,TestContext,TestAggregate1,TestAggregate2
			'''
		]
	}

	@Test
	def void canIgnoreContextIfOnlyOneFeatureInvolved() {
		testCodeAction [
			model = '''
				BoundedContext TestContext {
					Aggregate TestAggregate1 {
						userRequirements Story1
					}
					Aggregate TestAggregate2 {
						userRequirements Story1
					}
				}
				
				UserStory Story1
			'''
			expectedCodeActions = '''
				command : cml.ar.extractAggregatesByCohesion.proxy
				title : Extract Aggregates By Cohesion
				args : 
				    file://«this.root»/MyModel.cml,TestContext,TestAggregate1,TestAggregate2
			'''
		]
	}

}

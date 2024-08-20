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

class SplitBoundedContextByOwnerActionTest extends AbstractBoundedContextCodeActionTest {

	@Test
	def void canCreateCodeAction4SplitByTeam() {
		testCodeAction [
			model = '''
				BoundedContext TestContext {
					Aggregate TestAggregate1 {
						owner Team1
					}
					Aggregate TestAggregate2 {
						owner Team2
					}
				}
				
				BoundedContext Team1 { type TEAM }
				BoundedContext Team2 { type TEAM }
			'''
			expectedCodeActions = '''
				command : cml.ar.splitBCByOwner
				title : Split Bounded Context by Owner
				args : 
				    file://«this.root»/MyModel.cml,TestContext
				command : cml.ar.extractAggregatesByCohesion.proxy
				title : Extract Aggregates By Cohesion
				args : 
					file://«this.root»/MyModel.cml,TestContext,TestAggregate1,TestAggregate2
				command : cml.ar.mergeBoundedContexts.proxy
				title : Merge Bounded Contexts
				args : 
				    file://«this.root»/MyModel.cml,TestContext,Team2,Team1
				command : cml.ar.createValueRegisterForBoundedContext.proxy
				title : Create Value Register For Bounded Context
				args : 
				    file://«this.root»/MyModel.cml,TestContext
			'''
		]
	}

	@Test
	def void canIgnoreContextIfOnlyOneTeamInvolved() {
		testCodeAction [
			model = '''
				BoundedContext TestContext {
					Aggregate TestAggregate1 {
						owner Team1
					}
					Aggregate TestAggregate2 {
						owner Team1
					}
				}
				
				BoundedContext Team1 { type TEAM }
			'''
			expectedCodeActions = '''
				command : cml.ar.extractAggregatesByCohesion.proxy
				title : Extract Aggregates By Cohesion
				args : 
				    file://«this.root»/MyModel.cml,TestContext,TestAggregate1,TestAggregate2
				command : cml.ar.mergeBoundedContexts.proxy
				title : Merge Bounded Contexts
				args : 
				    file://«this.root»/MyModel.cml,TestContext,Team1
				command : cml.ar.createValueRegisterForBoundedContext.proxy
				title : Create Value Register For Bounded Context
				args : 
				    file://«this.root»/MyModel.cml,TestContext
			'''
		]
	}

}

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
package org.contextmapper.dsl.ide.tests.quickfixes

import org.contextmapper.dsl.ide.tests.commands.AbstractCMLCommandTest
import org.junit.jupiter.api.Test

class OpenCoordinationInBPMNSketchMinerActionTest extends AbstractCMLCommandTest {

	@Test
	def void canOfferCodeAction() {
		testCodeAction [
			model = '''
				BoundedContext ContextA {
					Application {
						Coordination TestCoordination {
							ContextA::TestService::testOperation;
						}
						Service TestService {
							testOperation;
						}
					}
				}
			'''
			expectedCodeActions = '''
				command : cml.ar.createValueRegisterForBoundedContext.proxy
				title : Create Value Register For Bounded Context
				args : 
				    file://«this.root»/MyModel.cml,ContextA
				title : Open coordination in BPMN Sketch Miner
				kind : quickfix
				command : Command [
				  title = "Open coordination in BPMN Sketch Miner"
				  command = "cml.coordination.open.sketch.miner"
				  arguments = LinkedList (
				    "https://www.bpmn-sketch-miner.ai/index.html#EYBwNgdgXAbgjAKALRIQYQPYQC4FMAe2AglAggM64BOMAlgMa4AEAKrudgMrV2MB0eDgHkQ1AIbZaWMkA"
				  )
				]
				codes : open-coordination-in-sketch-miner
				edit : 
			'''
		]
	}

}

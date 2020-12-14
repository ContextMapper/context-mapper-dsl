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
package org.contextmapper.dsl.ide.tests.quickfixes

import org.contextmapper.dsl.ide.tests.commands.AbstractCMLCommandTest
import org.junit.jupiter.api.Test

class OpenFlowInBPMNSketchMinerActionTest extends AbstractCMLCommandTest {

	@Test
	def void canOfferCodeAction() {
		testCodeAction [
			model = '''
				BoundedContext ContextA {
					Application {
						Flow TestFlow {
							command CommandB emits event EventA
						}
					}
					
					Aggregate AggA {
						DomainEvent EventA
						Command CommandB
					}
				}
			'''
			expectedCodeActions = '''
				title : Open flow in BPMN Sketch Miner
				kind : quickfix
				command : Command [
				  title = "Open flow in BPMN Sketch Miner"
				  command = "cml.flow.open.sketch.miner"
				  arguments = LinkedList (
				    "https://www.bpmn-sketch-miner.ai/index.html#EYBwNgdgXAbgjAKALRIQYQPYQC4FMAe2AggAREjgCWAxgIbaVZQIIDOuATjDbiZgLb9aEACYAhBAFEYuHERZA"
				  )
				]
				codes : open-flow-in-sketch-miner
				edit : 
			'''
		]
	}

}

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
package org.contextmapper.dsl.ide.tests.commands.generation

import com.google.gson.JsonPrimitive
import java.io.File
import org.contextmapper.dsl.ide.commands.CMLCommandService
import org.contextmapper.dsl.ide.tests.commands.AbstractCMLCommandTest
import org.eclipse.lsp4j.ExecuteCommandParams
import org.junit.jupiter.api.Test

import static extension org.junit.jupiter.api.Assertions.assertTrue

class SketchMinerGeneratorCommandTest extends AbstractCMLCommandTest {

	@Test
	def void testSketchMinerGeneratorCommandExecution() {
		// given
		initializeCommandsDynamically()
		val model = '''
			BoundedContext TestContext {
				Application {
					Service AppService {
						void Command1();
					}
					Flow TestFlow {
						event StartEvent triggers operation Command1
						operation Command1 [ initiated by "Test User" ] delegates to TestAggregate[STATE1, STATE2 -> STATE3 X STATE4] emits event EndEvent
					}
				}
				Aggregate TestAggregate {
					DomainEvent StartEvent
					DomainEvent EndEvent
					
					enum States {
						aggregateLifecycle
						STATE1, STATE2, STATE3, STATE4
					}
				}
			}
		'''
		val fileURI = 'test.cml'.writeFile(model)

		// when
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.generate.sketchminer", #[new JsonPrimitive(fileURI)]))
		val resultVal = result.get as String

		// then
		CMLCommandService.COMMAND_EXECUTED_RETURN_VALUE.assertEquals(resultVal)
		srcGenFolder.exists.assertTrue
		new File(srcGenFolder, "test_BC_TestContext_TestFlow.sketch_miner").exists.assertTrue
	}
	
	@Test
	def void testSketchMinerGeneratorCommandExecutionOnCoordination() {
		// given
		initializeCommandsDynamically()
		val model = '''
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
		val fileURI = 'test.cml'.writeFile(model)

		// when
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.generate.sketchminer", #[new JsonPrimitive(fileURI)]))
		val resultVal = result.get as String

		// then
		CMLCommandService.COMMAND_EXECUTED_RETURN_VALUE.assertEquals(resultVal)
		srcGenFolder.exists.assertTrue
		new File(srcGenFolder, "coordinations/test_BC_ContextA_TestCoordination.sketch_miner").exists.assertTrue
	}

}

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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static extension org.junit.jupiter.api.Assertions.assertTrue

class ServiceCutterInputGeneratorCommandTest extends AbstractCMLCommandTest {

	@BeforeEach
	def void prepare() {
		deleteGeneratedFiles("json")
	}

	@Test
	def void testServiceCutterInputGeneratorCommandExecution() {
		// given
		initializeCommandsDynamically()
		val model = '''
			ContextMap {
				contains context1, context2
				
				context1 -> context2
			}
			BoundedContext context1 {
				Aggregate agg1 {
					Entity entity1 {
						String attr1
					}
				}
			}
			BoundedContext context2 {
				Aggregate agg2 {
					Entity entity2 {
						String attr2
					}
				}
			}
		'''
		val fileURI = 'test.cml'.writeFile(model)

		// when
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.generate.servicecutter.input", #[new JsonPrimitive(fileURI)]))
		val resultVal = result.get as String

		// then
		CMLCommandService.COMMAND_EXECUTED_RETURN_VALUE.assertEquals(resultVal)
		srcGenFolder.exists.assertTrue
		new File(srcGenFolder, "test.json").exists.assertTrue
	}

}

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
package org.contextmapper.dsl.ide.tests.commands.refactoring

import com.google.gson.JsonPrimitive
import org.contextmapper.dsl.ide.commands.CMLCommandService
import org.contextmapper.dsl.ide.tests.commands.AbstractCMLCommandTest
import org.eclipse.lsp4j.ExecuteCommandParams
import org.junit.jupiter.api.Test

class SwitchFromSharedKernelToPartnershipCommandTest extends AbstractCMLCommandTest {

	@Test
	def void canExecuteAR() {
		// given
		initializeCommandsDynamically()
		val model = '''
			ContextMap {
				contains TestContext1, TestContext2
				
				TestContext1 [SK]<->[SK] TestContext2
			}
			BoundedContext TestContext1
			BoundedContext TestContext2
		'''
		val fileURI = 'test.cml'.writeFile(model)

		// when
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.ar.switchSharedKernelToPartnership",
				#[new JsonPrimitive(fileURI), new JsonPrimitive("TestContext1"), new JsonPrimitive("TestContext2")]))
		val resultVal = result.get as String

		// then
		CMLCommandService.COMMAND_EXECUTED_RETURN_VALUE.assertEquals(resultVal)
	}

}

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
package org.contextmapper.servicecutter.dsl.ide.commands.impl.generation

import com.google.gson.JsonPrimitive
import java.io.File
import org.contextmapper.servicecutter.dsl.ide.commands.AbstractSCLCommandTest
import org.contextmapper.servicecutter.dsl.ide.commands.SCLCommandService
import org.eclipse.lsp4j.ExecuteCommandParams
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static extension org.junit.jupiter.api.Assertions.assertTrue

class ServiceCutterUserRepresentationsJSONGeneratorCommandTest extends AbstractSCLCommandTest {

	@BeforeEach
	def void prepare() {
		deleteGeneratedFiles("json")
	}

	@Test
	def void testServiceCutterUserRepresentationJSONGeneratorCommandExecution() {
		// given
		initializeCommandsDynamically()
		val model = '''
			Aggregate Customers {
				"SocialInsuranceNumber.sin" , "Address.street" , "Address.postalCode" , "Address.city" , "Customer.firstname" , "Customer.lastname"
			}
		'''
		val fileURI = 'testSCL-JSON.scl'.writeFile(model)

		// when
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("scl.generate.user.representations.json.file", #[new JsonPrimitive(fileURI)]))
		val resultVal = result.get as String

		// then
		SCLCommandService.COMMAND_EXECUTED_RETURN_VALUE.assertEquals(resultVal)
		srcGenFolder.exists.assertTrue
		new File(srcGenFolder, "testSCL-JSON_user-representations.json").exists.assertTrue
	}

}

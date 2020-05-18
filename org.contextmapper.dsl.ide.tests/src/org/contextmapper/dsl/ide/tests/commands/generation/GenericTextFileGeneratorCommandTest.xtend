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

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.io.File
import org.contextmapper.dsl.ide.commands.CMLCommandService
import org.contextmapper.dsl.ide.tests.commands.AbstractCMLCommandTest
import org.eclipse.lsp4j.ExecuteCommandParams
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static extension org.junit.jupiter.api.Assertions.assertTrue

class GenericTextFileGeneratorCommandTest extends AbstractCMLCommandTest {

	@BeforeEach
	def void prepare() {
		deleteGeneratedFiles("jdl")
	}

	@Test
	def void testJDLGeneratorCommandExecution() {
		// given
		initializeCommandsDynamically()
		val model = '''
			BoundedContext context1 {
				Aggregate TestAggregate {
					Entity Entity1
					Entity Entity2
				}
			}
		'''
		val fileURI = 'test.cml'.writeFile(model)

		// when
		val paramObject = new JsonObject
		paramObject.add("templateUri",
			new JsonPrimitive(
				new File("./../org.contextmapper.dsl.ui/samples/freemarker/jhipster/JDL.ftl").toURI.toString))
		paramObject.add("outputFileName", new JsonPrimitive("entities.jdl"))
		val paramArray = new JsonArray
		paramArray.add(paramObject)
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.generate.generic.text.file", #[new JsonPrimitive(fileURI), paramArray]))
		val resultVal = result.get as String

		// then
		CMLCommandService.COMMAND_EXECUTED_RETURN_VALUE.assertEquals(resultVal)
		srcGenFolder.exists.assertTrue
		new File(srcGenFolder, "entities.jdl").exists.assertTrue
	}

	@Test
	def void canThrowExceptionIfParamsMissing() {
		// given
		initializeCommandsDynamically()
		val model = '''
			BoundedContext context1 {
				Aggregate TestAggregate {
					Entity Entity1
					Entity Entity2
				}
			}
		'''

		// when
		val fileURI = 'test.cml'.writeFile(model)
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.generate.generic.text.file", #[new JsonPrimitive(fileURI)]))
		val resultVal = result.get as String

		// then
		resultVal.startsWith(CMLCommandService.COMMAND_EXECUTION_ERROR_PREFIX).assertTrue
	}

	@Test
	def void canThrowExceptionIfURIHasWrongFormat() {
		// given
		initializeCommandsDynamically()
		val model = '''
			BoundedContext context1 {
				Aggregate TestAggregate {
					Entity Entity1
					Entity Entity2
				}
			}
		'''
		val fileURI = 'test.cml'.writeFile(model)

		// when
		val paramObject = new JsonObject
		paramObject.add("templateUri",
			new JsonPrimitive(new File("./../org.contextmapper.dsl.ui/samples/freemarker/jhipster/JDL.ftl").toString)) // no correct URI (does not start with file:)
		paramObject.add("outputFileName", new JsonPrimitive("entities.jdl"))
		val paramArray = new JsonArray
		paramArray.add(paramObject)
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.generate.generic.text.file", #[new JsonPrimitive(fileURI), paramArray]))
		val resultVal = result.get as String

		// then
		resultVal.startsWith(CMLCommandService.COMMAND_EXECUTION_ERROR_PREFIX).assertTrue
	}

	@Test
	def void canThrowExceptionIfURISyntaxWrong() {
		// given
		initializeCommandsDynamically()
		val model = '''
			BoundedContext context1 {
				Aggregate TestAggregate {
					Entity Entity1
					Entity Entity2
				}
			}
		'''
		val fileURI = 'test.cml'.writeFile(model)

		// when
		val paramObject = new JsonObject
		paramObject.add("templateUri", new JsonPrimitive("just some string"))
		paramObject.add("outputFileName", new JsonPrimitive("entities.jdl"))
		val paramArray = new JsonArray
		paramArray.add(paramObject)
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.generate.generic.text.file", #[new JsonPrimitive(fileURI), paramArray]))
		val resultVal = result.get as String

		// then
		resultVal.startsWith(CMLCommandService.COMMAND_EXECUTION_ERROR_PREFIX).assertTrue
	}

}

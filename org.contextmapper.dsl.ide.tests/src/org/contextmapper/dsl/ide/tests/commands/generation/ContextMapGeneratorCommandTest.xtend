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
import javax.imageio.ImageIO
import org.contextmapper.dsl.ide.commands.CMLCommandService
import org.contextmapper.dsl.ide.tests.commands.AbstractCMLCommandTest
import org.eclipse.lsp4j.ExecuteCommandParams
import org.junit.jupiter.api.Test

import static extension org.junit.jupiter.api.Assertions.assertEquals
import static extension org.junit.jupiter.api.Assertions.assertTrue

class ContextMapGeneratorCommandTest extends AbstractCMLCommandTest {

	@Test
	def void canGenerateContextMap() {
		// given
		initializeCommandsDynamically()
		val model = '''
			ContextMap {
				contains Context1, Context2
				
				Context1 -> Context2
			}
			BoundedContext Context1
			BoundedContext Context2
		'''
		val fileURI = 'test.cml'.writeFile(model)

		// when
		val paramObject = new JsonObject
		var formats = new JsonArray
		formats.add("png")
		formats.add("svg")
		paramObject.add("formats", formats)
		paramObject.add("fixWidth", new JsonPrimitive(false))
		paramObject.add("fixHeight", new JsonPrimitive(false))
		paramObject.add("generateLabels", new JsonPrimitive(false))
		paramObject.add("labelSpacingFactor", new JsonPrimitive(5))
		paramObject.add("clusterTeams", new JsonPrimitive(true))
		val paramArray = new JsonArray
		paramArray.add(paramObject)
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.generate.contextmap", #[new JsonPrimitive(fileURI), paramArray]))
		val resultVal = result.get as String

		// then
		CMLCommandService.COMMAND_EXECUTED_RETURN_VALUE.assertEquals(resultVal)
		srcGenFolder.exists.assertTrue
		new File(srcGenFolder, "test_ContextMap.png").exists.assertTrue
		new File(srcGenFolder, "test_ContextMap.svg").exists.assertTrue
	}
	
	@Test
	def void canFixImageWidth() {
		// given
		initializeCommandsDynamically()
		val model = '''
			ContextMap {
				contains Context1, Context2
				
				Context1 -> Context2
			}
			BoundedContext Context1
			BoundedContext Context2
		'''
		val fileURI = 'test.cml'.writeFile(model)

		// when
		val paramObject = new JsonObject
		var formats = new JsonArray
		formats.add("png")
		paramObject.add("formats", formats)
		paramObject.add("fixWidth", new JsonPrimitive(true))
		paramObject.add("width", new JsonPrimitive(500))
		paramObject.add("fixHeight", new JsonPrimitive(false))
		paramObject.add("generateLabels", new JsonPrimitive(false))
		paramObject.add("labelSpacingFactor", new JsonPrimitive(5))
		paramObject.add("clusterTeams", new JsonPrimitive(true))
		val paramArray = new JsonArray
		paramArray.add(paramObject)
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.generate.contextmap", #[new JsonPrimitive(fileURI), paramArray]))
		val resultVal = result.get as String

		// then
		CMLCommandService.COMMAND_EXECUTED_RETURN_VALUE.assertEquals(resultVal)
		srcGenFolder.exists.assertTrue
		val generatedFile = new File(srcGenFolder, "test_ContextMap.png")
		generatedFile.exists.assertTrue
		val image = ImageIO.read(generatedFile)
		500.assertEquals(image.width)
	}
	
	@Test
	def void canFixImageHeight() {
		// given
		initializeCommandsDynamically()
		val model = '''
			ContextMap {
				contains Context1, Context2
				
				Context1 -> Context2
			}
			BoundedContext Context1
			BoundedContext Context2
		'''
		val fileURI = 'test.cml'.writeFile(model)

		// when
		val paramObject = new JsonObject
		var formats = new JsonArray
		formats.add("png")
		paramObject.add("formats", formats)
		paramObject.add("fixHeight", new JsonPrimitive(true))
		paramObject.add("height", new JsonPrimitive(600))
		paramObject.add("fixWidth", new JsonPrimitive(false))
		paramObject.add("generateLabels", new JsonPrimitive(false))
		paramObject.add("labelSpacingFactor", new JsonPrimitive(5))
		paramObject.add("clusterTeams", new JsonPrimitive(true))
		val paramArray = new JsonArray
		paramArray.add(paramObject)
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.generate.contextmap", #[new JsonPrimitive(fileURI), paramArray]))
		val resultVal = result.get as String

		// then
		CMLCommandService.COMMAND_EXECUTED_RETURN_VALUE.assertEquals(resultVal)
		srcGenFolder.exists.assertTrue
		val generatedFile = new File(srcGenFolder, "test_ContextMap.png")
		generatedFile.exists.assertTrue
		val image = ImageIO.read(generatedFile)
		600.assertEquals(image.height)
	}

	@Test
	def void canThrowExceptionIfParamsMissing() {
		// given
		initializeCommandsDynamically()
		val model = '''
			ContextMap {
				contains Context1, Context2
				
				Context1 -> Context2
			}
			BoundedContext Context1
			BoundedContext Context2
		'''

		// when
		val fileURI = 'test.cml'.writeFile(model)
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.generate.contextmap", #[new JsonPrimitive(fileURI)]))
		val resultVal = result.get as String

		// then
		resultVal.startsWith(CMLCommandService.COMMAND_EXECUTION_ERROR_PREFIX).assertTrue
	}

}

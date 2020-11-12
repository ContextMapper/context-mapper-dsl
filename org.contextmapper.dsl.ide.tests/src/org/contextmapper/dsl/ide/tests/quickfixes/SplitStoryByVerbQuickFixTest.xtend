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

import com.google.gson.JsonArray
import com.google.gson.JsonPrimitive
import org.contextmapper.dsl.ide.commands.CMLCommandService
import org.contextmapper.dsl.ide.tests.commands.AbstractCMLCommandTest
import org.eclipse.lsp4j.ExecuteCommandParams
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

class SplitStoryByVerbQuickFixTest extends AbstractCMLCommandTest {

	@Test
	def void canOfferCodeAction() {
		testCodeAction [
			model = '''
				UserStory TestStory {
					As a "Tester"
					I want to create a "UnitTest"
					so that "I can ensure that my quickfix is suggested in the CML editor."
				}
			'''
			expectedCodeActions = '''
				command : cml.ar.deriveSubdomainFromURs.proxy
				title : Derive Subdomain From User Requirements
				args : 
				    file://«this.root»/MyModel.cml,TestStory
				title : Split Story by Verb/Operation
				kind : quickfix
				command : Command [
				  title = "Split Story by Verb/Operation"
				  command = "cml.quickfix.command.splitStoryByVerb.proxy"
				  arguments = LinkedList (
				    "file://«this.root»/MyModel.cml",
				    "TestStory"
				  )
				]
				codes : split-feature-by-verb-suggestion
				edit : 
			'''
		]
	}

	@Test
	def void testExecuteQuickfix() {
		// given
		initializeCommandsDynamically()
		val model = '''
			UserStory TestStory {
			  As an "Insurance Employee" I want to "create" a "Customer" so that "I am able to manage customer data ..."
			}
		'''
		val fileURI = 'test.cml'.writeFile(model)

		// when
		val selectedVerbs = new JsonArray
		selectedVerbs.add("search")
		selectedVerbs.add("update")
		selectedVerbs.add("delete")

		val quickFixParams = new JsonArray
		quickFixParams.add("TestStory")
		quickFixParams.add(selectedVerbs)
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.quickfix.command.splitStoryByVerb",
				#[new JsonPrimitive(fileURI), quickFixParams]))
		val resultVal = result.get as String

		// then
		CMLCommandService.COMMAND_EXECUTED_RETURN_VALUE.assertEquals(resultVal)
	}

	@Test
	def void cannotExecuteQuickfix4MultipleFeatures() {
		// given
		initializeCommandsDynamically()
		val model = '''
			UserStory TestStory {
			  As an "Insurance Employee" 
			  	I want to "create" a "Customer"
			  	I want to "update" a "Customer" 
			  so that "I am able to manage customer data ..."
			}
		'''
		val fileURI = 'test.cml'.writeFile(model)

		// when
		val selectedVerbs = new JsonArray
		selectedVerbs.add("search")
		selectedVerbs.add("delete")

		val quickFixParams = new JsonArray
		quickFixParams.add("TestStory")
		quickFixParams.add(selectedVerbs)
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.quickfix.command.splitStoryByVerb",
				#[new JsonPrimitive(fileURI), quickFixParams]))
		val resultVal = result.get as String

		// then
		assertTrue(resultVal.startsWith(CMLCommandService.COMMAND_EXECUTION_ERROR_PREFIX));
	}

}

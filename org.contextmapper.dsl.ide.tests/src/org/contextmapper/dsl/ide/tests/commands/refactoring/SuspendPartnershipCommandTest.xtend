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

import com.google.gson.JsonArray
import com.google.gson.JsonPrimitive
import org.contextmapper.dsl.ide.commands.CMLCommandService
import org.contextmapper.dsl.ide.tests.commands.AbstractCMLCommandTest
import org.eclipse.lsp4j.ExecuteCommandParams
import org.junit.jupiter.api.Test
import org.contextmapper.dsl.refactoring.SuspendPartnershipMode

class SuspendPartnershipCommandTest extends AbstractCMLCommandTest {

	// Unfortunately MERGE mode does currently not work because of this Xtext bug: 
	// https://github.com/eclipse/xtext-core/issues/1494
	// @Test
	def void canSuspendInMergeMode() {
		// given
		initializeCommandsDynamically()
		val model = '''
			ContextMap {
				contains TestContext1, TestContext2
				
				TestContext1 [P]<->[P] TestContext2
			}
			BoundedContext TestContext1
			BoundedContext TestContext2
		'''
		val fileURI = 'test.cml'.writeFile(model)

		// when
		val refactoringParams = new JsonArray
		refactoringParams.add("TestContext1")
		refactoringParams.add("TestContext2")
		refactoringParams.add(SuspendPartnershipMode.MERGE_BOUNDED_CONTEXTS.toString)
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.ar.suspendPartnership", #[new JsonPrimitive(fileURI), refactoringParams]))
		val resultVal = result.get as String

		// then
		CMLCommandService.COMMAND_EXECUTED_RETURN_VALUE.assertEquals(resultVal)
	}

	@Test
	def void canSuspendInExtractMode() {
		// given
		initializeCommandsDynamically()
		val model = '''
			ContextMap {
				contains TestContext1, TestContext2
				
				TestContext1 [P]<->[P] TestContext2
			}
			BoundedContext TestContext1
			BoundedContext TestContext2
		'''
		val fileURI = 'test.cml'.writeFile(model)

		// when
		val refactoringParams = new JsonArray
		refactoringParams.add("TestContext1")
		refactoringParams.add("TestContext2")
		refactoringParams.add(SuspendPartnershipMode.EXTRACT_NEW_BOUNDED_CONTEXT.toString)
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.ar.suspendPartnership", #[new JsonPrimitive(fileURI), refactoringParams]))
		val resultVal = result.get as String

		// then
		CMLCommandService.COMMAND_EXECUTED_RETURN_VALUE.assertEquals(resultVal)
	}

	@Test
	def void canSuspendInChangeRelationshipMode1() {
		// given
		initializeCommandsDynamically()
		val model = '''
			ContextMap {
				contains TestContext1, TestContext2
				
				TestContext1 [P]<->[P] TestContext2
			}
			BoundedContext TestContext1
			BoundedContext TestContext2
		'''
		val fileURI = 'test.cml'.writeFile(model)

		// when
		val refactoringParams = new JsonArray
		refactoringParams.add("TestContext1")
		refactoringParams.add("TestContext2")
		refactoringParams.add(SuspendPartnershipMode.REPLACE_RELATIONSHIP_WITH_UPSTREAM_DOWNSTREAM.toString)
		refactoringParams.add("TestContext2")
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.ar.suspendPartnership", #[new JsonPrimitive(fileURI), refactoringParams]))
		val resultVal = result.get as String

		// then
		CMLCommandService.COMMAND_EXECUTED_RETURN_VALUE.assertEquals(resultVal)
	}

	@Test
	def void canSuspendInChangeRelationshipMode2() {
		// given
		initializeCommandsDynamically()
		val model = '''
			ContextMap {
				contains TestContext1, TestContext2
				
				TestContext1 [P]<->[P] TestContext2
			}
			BoundedContext TestContext1
			BoundedContext TestContext2
		'''
		val fileURI = 'test.cml'.writeFile(model)

		// when
		val refactoringParams = new JsonArray
		refactoringParams.add("TestContext1")
		refactoringParams.add("TestContext2")
		refactoringParams.add(SuspendPartnershipMode.REPLACE_RELATIONSHIP_WITH_UPSTREAM_DOWNSTREAM.toString)
		refactoringParams.add("TestContext1")
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.ar.suspendPartnership", #[new JsonPrimitive(fileURI), refactoringParams]))
		val resultVal = result.get as String

		// then
		CMLCommandService.COMMAND_EXECUTED_RETURN_VALUE.assertEquals(resultVal)
	}

}

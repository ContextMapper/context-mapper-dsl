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

class ServiceCutterUserRepresentationExampleFileGeneratorCommandTest extends AbstractCMLCommandTest {

	@BeforeEach
	def void prepare() {
		deleteGeneratedFiles("scl")
	}

	@Test
	def void testServiceCutterUserRepresentationExampleFileGeneratorCommandExecution() {
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
			UseCase Get_paid_for_car_accident { // title
				actor "Claimant" // primary actor
				interactions
				   	"submit" a "Claim" with its "date", "amountClaimed", "description" for a "Policy", // step 1: claimant submits claim
				   	"verifyExistanceOf" "Policy" with its "startDate", "endDate" for a "Contract",     // step 2: insurance company verifies that valid policy exists
				   	"assign" an "Agent" with its "personalID", "firstName", "lastName" for "Claim",    // step 3: agent is assigned to claim
				   	"verify" "Policy" for a "Contract",                                                // step 4: agent verifies all details are within policy guidelines
				   	"pay" "Claimant" with its "firstName", "lastName",                                 // step 5 (1): claimant gets paid
				   	"close" "Claim" for "Claimant"                                                     // step 5 (2): file/claim gets closed
				benefit "A claimant submits a claim and and gets paid from the insurance company." // story (brief summary)
				scope "Insurance company" // scope
				level "Summary" // level
				reads "Claimant.firstName", "Claimant.lastName"
				writes "Claim.date", "Claim.amountClaimed"
			}
		'''
		val fileURI = 'test.cml'.writeFile(model)

		// when
		val result = languageServer.executeCommand(
			new ExecuteCommandParams("cml.generate.servicecutter.user.representation.example.file", #[new JsonPrimitive(fileURI)]))
		val resultVal = result.get as String

		// then
		CMLCommandService.COMMAND_EXECUTED_RETURN_VALUE.assertEquals(resultVal)
		new File(root, "test_user-representations.scl").exists.assertTrue
	}

}

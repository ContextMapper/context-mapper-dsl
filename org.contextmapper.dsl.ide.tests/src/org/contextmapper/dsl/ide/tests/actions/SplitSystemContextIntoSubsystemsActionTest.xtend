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
package org.contextmapper.dsl.ide.tests.actions

import org.junit.jupiter.api.Test

class SplitSystemContextIntoSubsystemsActionTest extends AbstractBoundedContextCodeActionTest {

	@Test
	def void canOfferAction4Subdomain() {
		testCodeAction [
			model = '''
				BoundedContext TestSystemContext {
					type SYSTEM
				}
			'''
			line = 1
			expectedCodeActions = '''
				command : cml.ar.splitSystemContextIntoSubsystems.proxy
				title : Split System Context Into Subsystems
				args : 
				    file://«this.root»/MyModel.cml,TestSystemContext
				command : cml.ar.createValueRegisterForBoundedContext.proxy
				title : Create Value Register For Bounded Context
				args : 
				    file://«this.root»/MyModel.cml,TestSystemContext
			'''
		]
	}
	
}

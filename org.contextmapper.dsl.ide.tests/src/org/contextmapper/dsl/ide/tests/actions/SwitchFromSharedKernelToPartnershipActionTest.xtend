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

class SwitchFromSharedKernelToPartnershipActionTest extends AbstractBoundedContextCodeActionTest {

	@Test
	def void canOfferAction4Partnership() {
		testCodeAction [
			model = '''
				ContextMap {
					contains TestContext1, TestContext2
					
					TestContext1 [SK]<->[SK] TestContext2
				}
				BoundedContext TestContext1
				BoundedContext TestContext2
			'''
			line = 3
			expectedCodeActions = '''
				command : cml.ar.extractSharedKernel
				title : Extract Shared Kernel
				args : 
				    file://«this.root»/MyModel.cml,TestContext1,TestContext2
				command : cml.ar.switchSharedKernelToPartnership
				title : Change to Partnership
				args : 
				    file://«this.root»/MyModel.cml,TestContext1,TestContext2
			'''
		]
	}

}

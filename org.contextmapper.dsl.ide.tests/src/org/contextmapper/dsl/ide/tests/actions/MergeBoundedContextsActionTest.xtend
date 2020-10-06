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

class MergeBoundedContextsActionTest extends AbstractBoundedContextCodeActionTest {

	@Test
	def void canOfferAction4BoundedContext() {
		testCodeAction [
			model = '''
				BoundedContext TestContext1
				BoundedContext TestContext2
			'''
			line = 0
			expectedCodeActions = '''
				command : cml.ar.mergeBoundedContexts.proxy
				title : Merge Bounded Contexts
				args : 
				    file://«this.root»/MyModel.cml,TestContext1,TestContext2
			'''
		]
	}
	
	@Test
	def void dontOfferAction4NotExistingBoundedContext() {
		testCodeAction [
			model = '''
				ContextMap {
					contains SomeContext
				}
				BoundedContext TestContext1
				BoundedContext TestContext2
			'''
			line = 1
			column = 12 
			expectedCodeActions = '''
				title : Create a Bounded Context named 'SomeContext'.
				kind : quickfix
				command : 
				codes : org.eclipse.xtext.diagnostics.Diagnostic.Linking
				edit : changes :
				    MyModel.cml : ContextMap {
				        contains SomeContext
				    }
				    
				    BoundedContext TestContext1
				    
				    BoundedContext TestContext2
				    
				    BoundedContext SomeContext [[0, 0] .. [5, 0]]
				documentChanges : 
			'''
		]
	}
	
}

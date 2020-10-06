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

import org.contextmapper.dsl.ide.tests.AbstractCMLLanguageServerTest
import org.junit.jupiter.api.Test

class CreateMissingBoundedContextQuickFixTest extends AbstractCMLLanguageServerTest {

	@Test
	def void canOfferToCreateMissingContext() {
		testCodeAction [
			model = '''
				ContextMap {
					contains MissingContext
				}
			'''
			expectedCodeActions = '''
				title : Create a Bounded Context named 'MissingContext'.
				kind : quickfix
				command : 
				codes : org.eclipse.xtext.diagnostics.Diagnostic.Linking
				edit : changes :
				    MyModel.cml : ContextMap {
				        contains MissingContext
				    }
				    
				    BoundedContext MissingContext [[0, 0] .. [3, 0]]
				documentChanges : 
			'''
		]
	}

}

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

class SplitStoryByVerbQuickFixTest extends AbstractCMLLanguageServerTest {

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
				command : 
				codes : split-feature-by-verb-suggestion
				edit : changes :
				    MyModel.cml :  I want to "{verb}" a "UnitTest" [[2, 30] .. [2, 30]]
				documentChanges : 
			'''
		]
	}

}

/*
 * Copyright 2020-2024 The Context Mapper Project Team
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

class UserStoryActionsTest extends AbstractBoundedContextCodeActionTest {

	@Test
	def void canOfferActions4UserStories() {
		testCodeAction [
			model = '''
				UserStory TestStory
			'''
			expectedCodeActions = '''
				command : cml.ar.deriveSubdomainFromURs.proxy
				title : Derive Subdomain From User Requirements
				args : 
				    file://«this.root»/MyModel.cml,TestStory
				command : cml.ar.addEthicalValueAssessment.proxy
				title : Add Ethical Value Assessment
				args : 
				    file://«this.root»/MyModel.cml,TestStory
			'''
		]
	}
	
}

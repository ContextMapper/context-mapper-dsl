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
	
	@Test
	def void canOfferAction2CreateStakeholderIfRoleIsDefined() {
		testCodeAction [
			model = '''
				UserStory SampleStory1 {
					As a "SampleUserOfFutureSystem" 
					I want to "manipulate" a "BusinessObject" with its "property1", "property2" 
					so that "I am more efficient"
				}
			'''
			expectedCodeActions = '''
				command : cml.ar.deriveSubdomainFromURs.proxy
				title : Derive Subdomain From User Requirements
				args : 
				    file://«this.root»/MyModel.cml,SampleStory1
				command : cml.ar.addEthicalValueAssessment.proxy
				title : Add Ethical Value Assessment
				args : 
				    file://«this.root»/MyModel.cml,SampleStory1
				command : cml.ar.createStakeholderForUserStoryRole.proxy
				title : Create Stakeholder For User Story Role
				args : 
				    file://«this.root»/MyModel.cml,SampleStory1
				title : Split Story by Verb/Operation
				kind : quickfix
				command : Command [
				  title = "Split Story by Verb/Operation"
				  command = "cml.quickfix.command.splitStoryByVerb.proxy"
				  arguments = LinkedList (
				    "file://«this.root»/MyModel.cml",
				    "SampleStory1"
				  )
				]
				codes : split-feature-by-verb-suggestion
				edit : 
			'''
		]
	}
	
}

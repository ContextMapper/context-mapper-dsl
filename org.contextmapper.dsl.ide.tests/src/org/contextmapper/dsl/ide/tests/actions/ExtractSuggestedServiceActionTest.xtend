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

class ExtractSuggestedServiceActionTest extends AbstractBoundedContextCodeActionTest {

	@Test
	def void canOfferAction4BoundedContext() {
		testCodeAction [
			filePath = 'MyModel_Markov_Clustering_Cut_1.cml'
			model = '''
				BoundedContext TestContext {
				}
			'''
			line = 0
			expectedCodeActions = '''
				command : cml.ar.extractSuggestedService.proxy
				title : Extract Suggested Service in Original Model
				args : 
				    file://«this.root»/MyModel.cml,file://«this.root»/MyModel_Markov_Clustering_Cut_1.cml,TestContext
			'''
		]
	}

}

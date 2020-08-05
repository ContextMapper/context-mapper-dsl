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
package org.contextmapper.dsl.ide.tests.hover

import org.junit.jupiter.api.Test
import org.contextmapper.dsl.ide.tests.AbstractCMLLanguageServerTest

class CMLHoverTextsTest extends AbstractCMLLanguageServerTest {

	@Test
	def void canProvideHoverText4Keyword() {
		testHover [
			model = '''
				ContextMap {
				}
			'''
			line = 0
			column = 0
			expectedHover = '''
				[[0, 0] .. [0, 10]]
				kind: markdown
				value: **Context Map**: A model describing bounded contexts 
				and especially their relationships. Brandolini provides a very good introduction into context mapping here:
				[https://www.infoq.com/articles/ddd-contextmapping/](https://www.infoq.com/articles/ddd-contextmapping/)
				
				Find all DDD pattern descriptions in the DDD reference under 
				[http://domainlanguage.com/ddd/reference/](http://domainlanguage.com/ddd/reference/)
			'''
		]
	}

	@Test
	def void canProvideHoverText4EnumLiteral() {
		testHover [
			model = '''
				ContextMap {
					contains TestContext1, TestContext2
					
					TestContext1 [OHS]-> TestContext2
				}
				BoundedContext TestContext1
				BoundedContext TestContext2
			'''
			line = 3
			column = 15
			expectedHover = '''
				[[3, 15] .. [3, 18]]
				kind: markdown
				value: **Open Host Service (OHS)**: Describes a role of a 
				bounded context which is providing certain functionality needed by many other contexts. 
				Because of the broad usage, a public API is provided.
				
				Find all DDD pattern descriptions in the DDD reference under 
				[http://domainlanguage.com/ddd/reference/](http://domainlanguage.com/ddd/reference/)
			'''
		]
	}

	@Test
	def void canIgnoreKeywordsWithoutHoverText() {
		testHover [
			model = '''
				ContextMap {
					contains TestContext1, TestContext2
				}
				BoundedContext TestContext1
				BoundedContext TestContext2
			'''
			line = 1
			column = 3
			expectedHover = '''
				[[1, 1] .. [1, 9]]
				kind: markdown
				value: 
			'''
		]
	}

	@Test
	def void canReturnEmptyText4NonKeywordToken() {
		testHover [
			model = '''
				ContextMap {
					contains TestContext1, TestContext2
				}
				BoundedContext TestContext1
				BoundedContext TestContext2
			'''
			line = 1
			column = 12
			expectedHover = '''
				[[1, 10] .. [1, 22]]
				kind: markdown
				value: 
			'''
		]
	}

}

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

class ExtractIDValueObjectQuickFixTest extends AbstractCMLLanguageServerTest {

	@Test
	def void canOfferCodeAction() {
		testCodeAction [
			model = '''
				BoundedContext TestContext {
					Aggregate TestAggregate {
						Entity Customer { 
							String customerId
							String firstname
							String lastname
						}
					}
				}
			'''
			expectedCodeActions = '''
				title : Extract Value Object
				kind : quickfix
				command : 
				codes : primitive-id-detected
				edit : changes :
				    MyModel.cml : 
				        Aggregate TestAggregate {
				            Entity Customer {
				                String firstname
				                String lastname
				                - CustomerId customerId
				            }
				            ValueObject CustomerId {
				                String id
				            }
				        }
				     [[0, 28] .. [8, 0]]
				documentChanges : 
			'''
		]
	}

}

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
package org.contextmapper.dsl

import com.google.inject.Inject
import java.util.ArrayList
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.tests.ContextMappingDSLInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.contextmapper.dsl.util.ParsingErrorAssertions.*
import static org.contextmapper.dsl.validation.ValidationMessages.*

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class TacticDDDModuleValidationTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

	@Test
	def void canWarnUserAboutPotentiallyIgnoredObjectsInModule() {
		// given
		val dslSnippets = new ArrayList<String>;

		dslSnippets.add('''
			BoundedContext TestBC {
				Module TestModule {
					Entity Entity1
				}
			}
		''');
		dslSnippets.add('''
			BoundedContext TestBC {
				Module TestModule {
					Service Service1
				}
			}
		''');
		dslSnippets.add('''
			BoundedContext TestBC {
				Module TestModule {
					Service Service1
					Entity Entity1
				}
			}
		''');

		for (String dslSnippet : dslSnippets) {
			// when
			val ContextMappingModel result = parseHelper.parse(dslSnippet);
			// then
			assertThatNoParsingErrorsOccurred(result);
			validationTestHelper.assertWarning(result, ContextMappingDSLPackage.Literals.SCULPTOR_MODULE, "",
				String.format(MODULE_CONTAINS_POTENTIALLY_IGNORED_OBJECTS, "TestModule"));
		}
	}

}

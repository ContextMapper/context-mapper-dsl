/*
 * Copyright 2018 The Context Mapper Project Team
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
package org.contextmapper.tactic.dsl

import org.contextmapper.tactic.dsl.conversion.TacticDslValueConverters
import org.contextmapper.tactic.dsl.validation.TacticDslSyntaxErrorMessageProvider
import org.eclipse.xtext.conversion.IValueConverterService
import org.eclipse.xtext.parser.antlr.ISyntaxErrorMessageProvider

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
class TacticDDDLanguageRuntimeModule extends AbstractTacticDDDLanguageRuntimeModule {

	def Class<? extends ISyntaxErrorMessageProvider> bindISyntaxErrorMessageProvider() {
		typeof(TacticDslSyntaxErrorMessageProvider)
	}

	override Class<? extends IValueConverterService> bindIValueConverterService() {
		typeof(TacticDslValueConverters)
	}

}

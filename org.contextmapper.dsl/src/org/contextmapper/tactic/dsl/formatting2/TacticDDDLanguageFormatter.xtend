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
package org.contextmapper.tactic.dsl.formatting2

import com.google.inject.Inject
import org.contextmapper.tactic.dsl.services.TacticDDDLanguageGrammarAccess
import org.contextmapper.tactic.dsl.tacticdsl.Entity
import org.contextmapper.tactic.dsl.tacticdsl.TacticDDDModel
import org.eclipse.xtext.formatting2.AbstractFormatter2
import org.eclipse.xtext.formatting2.IFormattableDocument

class TacticDDDLanguageFormatter extends AbstractFormatter2 {

	@Inject extension TacticDDDLanguageGrammarAccess

	def dispatch void format(TacticDDDModel tacticDDDModel, extension IFormattableDocument document) {
		for (_import : tacticDDDModel.imports) {
			_import.format
		}
		tacticDDDModel.app.format
	}
	
	def dispatch void format(Entity entity, extension IFormattableDocument document) {
		interior(
			entity.regionFor.keyword('{').append[newLine],
			entity.regionFor.keyword('}').prepend[newLine].append[newLine]
		)[indent]

		for (attribute : entity.attributes) {
			attribute.format
			attribute.append[newLine]
		}
		for (reference : entity.references) {
			reference.format
		}
	}

}

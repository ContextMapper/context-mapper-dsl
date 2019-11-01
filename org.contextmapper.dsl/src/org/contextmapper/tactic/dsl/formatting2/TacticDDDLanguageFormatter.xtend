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
import org.contextmapper.tactic.dsl.tacticdsl.Attribute
import org.contextmapper.tactic.dsl.tacticdsl.DomainEvent
import org.contextmapper.tactic.dsl.tacticdsl.Entity
import org.contextmapper.tactic.dsl.tacticdsl.Reference
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

		entity.regionFor.keyword('aggregateRoot').append[newLine]
		entity.prepend[newLines = 1]
		entity.regionFor.keyword('Entity').prepend[newLine]

		for (attribute : entity.attributes) {
			attribute.format
			attribute.append[newLine]
		}
		for (reference : entity.references) {
			reference.format
		}
	}
	
	def dispatch void format(DomainEvent domainEvent, extension IFormattableDocument document) {
		interior(
			domainEvent.regionFor.keyword('{').append[newLine],
			domainEvent.regionFor.keyword('}').prepend[newLine].append[newLine]
		)[indent]

		domainEvent.regionFor.keyword('aggregateRoot').append[newLine]

		for (attribute : domainEvent.attributes) {
			attribute.format
			attribute.append[newLine]
		}
		for (reference : domainEvent.references) {
			reference.format
		}
	}

	def dispatch void format(Attribute attribute, extension IFormattableDocument document) {
		attribute.regionFor.keyword('<').surround[noSpace]
		attribute.regionFor.keyword('>').prepend[noSpace]
	}
	
	def dispatch void format(Reference reference, extension IFormattableDocument document) {
		reference.regionFor.keyword('<').surround[noSpace]
		reference.regionFor.keyword('>').prepend[noSpace]
	}

}

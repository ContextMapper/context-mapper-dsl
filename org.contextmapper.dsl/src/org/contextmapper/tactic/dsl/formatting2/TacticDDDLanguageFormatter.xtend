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

import org.contextmapper.tactic.dsl.tacticdsl.Attribute
import org.contextmapper.tactic.dsl.tacticdsl.CommandEvent
import org.contextmapper.tactic.dsl.tacticdsl.ComplexType
import org.contextmapper.tactic.dsl.tacticdsl.DomainEvent
import org.contextmapper.tactic.dsl.tacticdsl.DomainObjectOperation
import org.contextmapper.tactic.dsl.tacticdsl.Entity
import org.contextmapper.tactic.dsl.tacticdsl.Parameter
import org.contextmapper.tactic.dsl.tacticdsl.Reference
import org.contextmapper.tactic.dsl.tacticdsl.Service
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject
import org.contextmapper.tactic.dsl.tacticdsl.Enum
import org.eclipse.xtext.formatting2.AbstractFormatter2
import org.eclipse.xtext.formatting2.IFormattableDocument
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation

class TacticDDDLanguageFormatter extends AbstractFormatter2 {

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
			reference.append[newLine]
		}
		for (operation : entity.operations) {
			operation.format
			operation.append[newLine]
		}
	}
	
	def dispatch void format(DomainEvent domainEvent, extension IFormattableDocument document) {
		interior(
			domainEvent.regionFor.keyword('{').append[newLine],
			domainEvent.regionFor.keyword('}').prepend[newLine].append[newLine]
		)[indent]

		domainEvent.regionFor.keyword('aggregateRoot').append[newLine]
		domainEvent.prepend[newLines = 1]
		domainEvent.regionFor.keyword('DomainEvent').prepend[newLine]

		for (attribute : domainEvent.attributes) {
			attribute.format
			attribute.append[newLine]
		}
		for (reference : domainEvent.references) {
			reference.format
			reference.append[newLine]
		}
		for (operation : domainEvent.operations) {
			operation.format
			operation.append[newLine]
		}
	}
	
	def dispatch void format(CommandEvent commandEvent, extension IFormattableDocument document) {
		interior(
			commandEvent.regionFor.keyword('{').append[newLine],
			commandEvent.regionFor.keyword('}').prepend[newLine].append[newLine]
		)[indent]

		commandEvent.regionFor.keyword('aggregateRoot').append[newLine]
		commandEvent.prepend[newLines = 1]
		commandEvent.regionFor.keyword('CommandEvent').prepend[newLine]

		for (attribute : commandEvent.attributes) {
			attribute.format
			attribute.append[newLine]
		}
		for (reference : commandEvent.references) {
			reference.format
			reference.append[newLine]
		}
		for (operation : commandEvent.operations) {
			operation.format
			operation.append[newLine]
		}
	}
	
	def dispatch void format(ValueObject valueObject, extension IFormattableDocument document) {
		interior(
			valueObject.regionFor.keyword('{').append[newLine],
			valueObject.regionFor.keyword('}').prepend[newLine].append[newLine]
		)[indent]

		valueObject.prepend[newLines = 1]
		valueObject.regionFor.keyword('ValueObject').prepend[newLine]

		for (attribute : valueObject.attributes) {
			attribute.format
			attribute.append[newLine]
		}
		for (reference : valueObject.references) {
			reference.format
			reference.append[newLine]
		}
		for (operation : valueObject.operations) {
			operation.format
			operation.append[newLine]
		}
	}
	
	def dispatch void format(Enum enumm, extension IFormattableDocument document) {
		interior(
			enumm.regionFor.keyword('{').append[newLine],
			enumm.regionFor.keyword('}').prepend[newLine].append[newLine]
		)[indent]

		enumm.prepend[newLines = 1]
		enumm.regionFor.keyword('enum').prepend[newLine]

		for (attribute : enumm.attributes) {
			attribute.format
			attribute.append[newLine]
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

	def dispatch void format(DomainObjectOperation operation, extension IFormattableDocument document) {
		operation.prepend[newLines = 1]
		
		operation.regionFor.keyword(';').prepend[noSpace]
		operation.regionFor.keyword('(').append[noSpace]
		operation.regionFor.keyword(')').prepend[noSpace]
		operation.regionFor.keywords(',').forEach[
			prepend[noSpace]
		]
		
		operation.returnType.format
		
		for(parameter : operation.parameters) {
			parameter.format
		}
	}
	
	def dispatch void format(ComplexType complexType, extension IFormattableDocument document) {
		complexType.regionFor.keywords('<').forEach[
			surround[noSpace]
		]
		complexType.regionFor.keywords('>').forEach[
			prepend[noSpace]
		]
		complexType.regionFor.keywords('@').forEach[
			append[noSpace]
		]
	}
	
	def dispatch void format(Parameter parameter, extension IFormattableDocument document) {
		parameter.parameterType.format
	}
	
	def dispatch void format(Service service, extension IFormattableDocument document) {
		interior(
			service.regionFor.keyword('{').append[newLine],
			service.regionFor.keyword('}').prepend[newLine].append[newLine]
		)[indent]

		service.prepend[newLines = 1]
		service.regionFor.keyword('Service').prepend[newLine]

		for (operation : service.operations) {
			operation.format
		}
	}
	
	def dispatch void format(ServiceOperation operation, extension IFormattableDocument document) {
		operation.prepend[newLines = 1]
		
		operation.regionFor.keyword(';').prepend[noSpace]
		operation.regionFor.keyword('(').append[noSpace]
		operation.regionFor.keyword(')').prepend[noSpace]
		operation.regionFor.keywords(',').forEach[
			prepend[noSpace]
		]
		
		operation.returnType.format
		
		for(parameter : operation.parameters) {
			parameter.format
		}
	}
}

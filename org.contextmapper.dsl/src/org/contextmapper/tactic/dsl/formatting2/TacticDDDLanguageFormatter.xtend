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
import org.contextmapper.tactic.dsl.tacticdsl.ComplexType
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject
import org.contextmapper.tactic.dsl.tacticdsl.DomainObjectOperation
import org.contextmapper.tactic.dsl.tacticdsl.Trait
import org.contextmapper.tactic.dsl.tacticdsl.BasicType
import org.contextmapper.tactic.dsl.tacticdsl.Entity
import org.contextmapper.tactic.dsl.tacticdsl.Parameter
import org.contextmapper.tactic.dsl.tacticdsl.Reference
import org.contextmapper.tactic.dsl.tacticdsl.Service
import org.contextmapper.tactic.dsl.tacticdsl.Enum
import org.eclipse.xtext.formatting2.AbstractFormatter2
import org.eclipse.xtext.formatting2.IFormattableDocument
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EStructuralFeature

class TacticDDDLanguageFormatter extends AbstractFormatter2 {

	def dispatch void format(Entity entity, extension IFormattableDocument document) {
		val EClass clazz = TacticdslPackage.eINSTANCE.getEntity()
        val EStructuralFeature docFeature = clazz.getEStructuralFeature(TacticdslPackage.ENTITY__DOC)
		entity.regionFor.feature(docFeature).append[newLine]

		interior(
			entity.regionFor.keyword('{').prepend[oneSpace].append[newLine],
			entity.regionFor.keyword('}').prepend[newLine]
		)[indent]

		entity.regionFor.keyword('aggregateRoot').prepend[newLine]
		entity.regionFor.keywords('hint', 'validate', 'belongsTo').forEach[
			prepend[newLine]
		]
        entity.regionFor.keywords('with').forEach[
            append[noSpace].prepend[oneSpace]
        ]
        entity.regionFor.keywords('@').forEach[
            append[noSpace]
        ]

		for (attribute : entity.attributes) {
			attribute.format
	        attribute.prepend[newLine]
		}
		for (reference : entity.references) {
			reference.format
		    reference.prepend[newLine]
		}
		for (operation : entity.operations) {
			operation.format
			operation.prepend[newLine]
		}
	}

	def dispatch void format(DomainObject domainObject, extension IFormattableDocument document) {
        val EClass clazz = TacticdslPackage.eINSTANCE.getDomainObject()
        val EStructuralFeature docFeature = clazz.getEStructuralFeature(TacticdslPackage.DOMAIN_OBJECT__DOC)
        domainObject.regionFor.feature(docFeature).append[newLine]

        interior(
			domainObject.regionFor.keyword('{').prepend[oneSpace].append[newLine],
			domainObject.regionFor.keyword('}').prepend[newLine]
		)[indent]

		domainObject.regionFor.keyword('aggregateRoot').append[newLine]
		domainObject.regionFor.keyword('hint').prepend[newLine]
        domainObject.regionFor.keywords('with').forEach[
            append[noSpace].prepend[oneSpace]
        ]
        domainObject.regionFor.keywords('@').forEach[
            append[noSpace]
        ]        

		for (attribute : domainObject.attributes) {
			attribute.format
			attribute.prepend[newLine]
		}
		for (reference : domainObject.references) {
			reference.format
			reference.prepend[newLine]
		}
		for (operation : domainObject.operations) {
			operation.format
			operation.prepend[newLine]
		}
	}

	def dispatch void format(Trait trait, extension IFormattableDocument document) {
		val EClass clazz = TacticdslPackage.eINSTANCE.getTrait()
        val EStructuralFeature docFeature = clazz.getEStructuralFeature(TacticdslPackage.TRAIT__DOC)
		trait.regionFor.feature(docFeature).append[newLine]

		interior(
			trait.regionFor.keyword('{').prepend[oneSpace].append[newLine],
			trait.regionFor.keyword('}').prepend[newLine]
		)[indent]

		trait.regionFor.keyword('hint').prepend[newLine]

		for (attribute : trait.attributes) {
			attribute.format
			attribute.prepend[newLine]
		}
		for (reference : trait.references) {
			reference.format
			reference.prepend[newLine]
		}
		for (operation : trait.operations) {
			operation.format
			operation.prepend[newLine]
		}
	}

    def dispatch void format(BasicType basicType, extension IFormattableDocument document) {
        val EClass clazz = TacticdslPackage.eINSTANCE.getBasicType()
        val EStructuralFeature docFeature = clazz.getEStructuralFeature(TacticdslPackage.BASIC_TYPE__DOC)
        basicType.regionFor.feature(docFeature).append[newLine]

        interior(
            basicType.regionFor.keyword('{').prepend[oneSpace].append[newLine],
            basicType.regionFor.keyword('}').prepend[newLine]
        )[indent]

        basicType.regionFor.keyword('hint').prepend[newLine]
        basicType.regionFor.keywords('with').forEach[
            append[noSpace].prepend[oneSpace]
        ]
        basicType.regionFor.keywords('@').forEach[
            append[noSpace]
        ] 
        
        for (attribute : basicType.attributes) {
            attribute.format
            attribute.prepend[newLine]
        }
        for (reference : basicType.references) {
            reference.format
            reference.prepend[newLine]
        }
        for (operation : basicType.operations) {
            operation.format
            operation.prepend[newLine]
        }
    }

	def dispatch void format(Enum enumm, extension IFormattableDocument document) {
		val EClass clazz = TacticdslPackage.eINSTANCE.getEnum()
        val EStructuralFeature docFeature = clazz.getEStructuralFeature(TacticdslPackage.ENUM__DOC)
		enumm.regionFor.feature(docFeature).append[newLine]

		interior(
			enumm.regionFor.keyword('{').prepend[oneSpace].append[newLine],
			enumm.regionFor.keyword('}').prepend[newLine]
		)[indent]

        enumm.regionFor.keywords(',').forEach [
            prepend[noSpace]
            append[oneSpace]
        ]

		for (attribute : enumm.attributes) {
			attribute.format
			attribute.prepend[newLine]
		}
	}

	def dispatch void format(Attribute attribute, extension IFormattableDocument document) {
		val EClass attributeClass = TacticdslPackage.eINSTANCE.getAttribute()
        val EStructuralFeature docFeature = attributeClass.getEStructuralFeature(TacticdslPackage.ATTRIBUTE__DOC)

		attribute.regionFor.feature(docFeature).append[newLine]
		attribute.regionFor.keyword('<').surround[noSpace]
		attribute.regionFor.keyword('>').prepend[noSpace]
	}
	
	def dispatch void format(Reference reference, extension IFormattableDocument document) {
		val EClass referenceClass = TacticdslPackage.eINSTANCE.getReference()
        val EStructuralFeature docFeature = referenceClass.getEStructuralFeature(TacticdslPackage.REFERENCE__DOC)

		reference.regionFor.feature(docFeature).append[newLine]
		reference.regionFor.keyword('<').surround[noSpace]
		reference.regionFor.keyword('>').prepend[noSpace]
	}

	def dispatch void format(DomainObjectOperation operation, extension IFormattableDocument document) {
		operation.prepend[newLine]
		
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
			service.regionFor.keyword('{').prepend[oneSpace].append[newLine],
			service.regionFor.keyword('}').prepend[newLine].append[newLine]
		)[indent]

		service.prepend[newLine]
		service.regionFor.keyword('Service').prepend[newLine]

		for (operation : service.operations) {
			operation.format
		}
	}
	
	def dispatch void format(ServiceOperation operation, extension IFormattableDocument document) {
		operation.prepend[newLine]
		
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

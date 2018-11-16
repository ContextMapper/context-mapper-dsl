/*
 * Copyright 2013 The Sculptor Project Team, including the original 
 * author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.contextmapper.tactic.dsl

import java.util.Iterator
import org.contextmapper.tactic.dsl.tacticdsl.Attribute
import org.contextmapper.tactic.dsl.tacticdsl.ComplexType
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject
import org.eclipse.emf.ecore.EObject

import static extension org.eclipse.xtext.EcoreUtil2.*

/**
 * Extensions for model elements of the domain model.
 * Usage in Xtend files:
 * <pre>
 *   @Inject extension TacticDslExtensions
 * 
 *   // ...
 * 
 *     element.rootContainer.eAllOfClass(typeof(DslService))
 * </pre>
 */
class TacticDslExtensions {
	/**
	 * Extensions for model elements of the domain model.
	 * Usage in Xtend files:
	 * <pre>
	 *   @Inject extension SculptordslExtensions
	 * 
	 *   // ...
	 * 
	 *     element.rootContainer.eAllOfClass(typeof(DslService))
	 * </pre>
	 */
	def static <T extends EObject> Iterator<T> eAllOfClass(EObject obj, Class<T> clazz) {
		obj?.eAll.filter(clazz)
	}

	/**
	 * @return DslSimpleDomainObjects whose type matches attr.type
	 */
	def static Iterable<SimpleDomainObject> domainObjectsForAttributeType(Attribute attr) {
		attr.rootContainer.eAllOfType(typeof(SimpleDomainObject)).filter[it.name == attr.type]
	}

	/**
	 * @return the first DslSimpleDomainObject whose type matches complexType, or null
	 */
	def static firstDomainObjectForType(ComplexType complexType) {
		val res = complexType.rootContainer.eAllOfType(typeof(SimpleDomainObject)).findFirst[name == complexType.type]
		res
	}

}

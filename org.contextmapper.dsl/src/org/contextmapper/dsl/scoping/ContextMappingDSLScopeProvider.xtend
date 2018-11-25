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
package org.contextmapper.dsl.scoping

import java.util.ArrayList
import org.contextmapper.tactic.dsl.scoping.Scope
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject
import org.contextmapper.tactic.dsl.tacticdsl.OppositeHolder
import org.contextmapper.tactic.dsl.tacticdsl.Reference
import org.contextmapper.tactic.dsl.tacticdsl.Repository
import org.contextmapper.tactic.dsl.tacticdsl.ResourceOperationDelegate
import org.contextmapper.tactic.dsl.tacticdsl.Service
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperationDelegate
import org.eclipse.emf.ecore.EReference
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.resource.EObjectDescription
import org.eclipse.xtext.resource.IEObjectDescription
import org.eclipse.xtext.scoping.IScope
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider

/**
 * This class contains custom scoping description.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#scoping
 * on how and when to use it.
 */
class ContextMappingDSLScopeProvider extends AbstractDeclarativeScopeProvider {
	def IScope scope_DslOppositeHolder_opposite(OppositeHolder ctx, EReference ref) {
		val Scope scope = new Scope()
		val elements = new ArrayList<IEObjectDescription>()
		val DomainObject domainObject = (ctx.eContainer as Reference).domainObjectType as DomainObject
		domainObject.references.forEach [
			if (it.eContainer !== null) {
				elements.add(new EObjectDescription(QualifiedName.create(it.name), it, null))
			}
		]
		scope.elements = elements
		return scope
	}

	def IScope scope_DslServiceOperationDelegate_delegateOperation(ServiceOperationDelegate ctx, EReference ref) {
		val Scope scope = new Scope();
		val elements = new ArrayList<IEObjectDescription>()
		val option = ctx.delegate
		if (option !== null) {
			if (option instanceof Repository) {
				option.operations.forEach [
					elements.add(new EObjectDescription(QualifiedName.create(it.name), it, null))
				]
			} else {
				(option as Service).operations.forEach [
					elements.add(new EObjectDescription(QualifiedName.create(it.name), it, null))
				]
			}
		}
		scope.elements = elements
		return scope
	}

	def IScope scope_DslResourceOperationDelegate_delegateOperation(ResourceOperationDelegate ctx, EReference ref) {
		val Scope scope = new Scope()
		val elements = new ArrayList<IEObjectDescription>()
		val option = ctx.delegate
		if (option !== null) {
			option.operations.forEach [
				elements.add(new EObjectDescription(QualifiedName.create(it.name), it, null))
			]
		}
		scope.elements = elements
		return scope;
	}
}

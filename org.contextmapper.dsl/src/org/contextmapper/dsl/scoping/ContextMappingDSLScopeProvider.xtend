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

import org.contextmapper.tactic.dsl.tacticdsl.Reference
import org.contextmapper.tactic.dsl.tacticdsl.Association
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider
import com.google.inject.Inject

/**
 * This class contains custom scoping description.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#scoping
 * on how and when to use it.
 */
class ContextMappingDSLScopeProvider extends AbstractDeclarativeScopeProvider {

	@Inject CMLScopingHelper cmlScopingHelper

	override getScope(EObject context, EReference reference) {
		if (context instanceof Reference) {
			return cmlScopingHelper.reduceReferenceScope(super.getScope(context, reference), context, reference);
		}
		if (context instanceof Association) {
			return cmlScopingHelper.reduceReferenceScope(super.getScope(context, reference), context, reference);
		}
		super.getScope(context, reference)
	}

}

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
package org.contextmapper.dsl.scoping;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.contextmapper.dsl.contextMappingDSL.impl.BoundedContextImpl;
import org.contextmapper.dsl.contextMappingDSL.impl.DomainImpl;
import org.contextmapper.tactic.dsl.tacticdsl.Association;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.SimpleScope;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Helper class to filter scope. For example: domain objects in domains shall
 * not refer domain objects in Bounded Contexts and vice versa.
 * 
 * @author Stefan Kapferer
 *
 */
public class CMLScopingHelper {

	public IScope reduceReferenceScope(IScope existingScope, Reference reference, EReference eReference) {
		// domain objects in Domains shall not refer to domain objects in Bounded
		// Contexts and vice versa:
		if (isPartOfBoundedContext(reference)) {
			return filterScope(existingScope, (ieoDesc) -> ieoDesc.getEObjectOrProxy() instanceof DomainObject && isPartOfDomain(ieoDesc.getEObjectOrProxy()));
		} else if (isPartOfDomain(reference)) {
			return filterScope(existingScope, (ieoDesc) -> ieoDesc.getEObjectOrProxy() instanceof DomainObject && isPartOfBoundedContext(ieoDesc.getEObjectOrProxy()));
		}
		return existingScope;
	}

	public IScope reduceReferenceScope(IScope existingScope, Association association, EReference eReference) {
		// domain objects in Domains shall not refer to domain objects in Bounded
		// Contexts and vice versa:
		if (isPartOfBoundedContext(association)) {
			return filterScope(existingScope, (ieoDesc) -> ieoDesc.getEObjectOrProxy() instanceof DomainObject && isPartOfDomain(ieoDesc.getEObjectOrProxy()));
		} else if (isPartOfDomain(association)) {
			return filterScope(existingScope, (ieoDesc) -> ieoDesc.getEObjectOrProxy() instanceof DomainObject && isPartOfBoundedContext(ieoDesc.getEObjectOrProxy()));
		}
		return existingScope;
	}

	private IScope filterScope(IScope scope, Predicate<IEObjectDescription> descriptionsToRemovePredicate) {
		List<IEObjectDescription> descriptions = Lists.newLinkedList();
		for (IEObjectDescription ieoDesc : scope.getAllElements()) {
			if (!(descriptionsToRemovePredicate.test(ieoDesc)))
				descriptions.add(ieoDesc);
		}
		return new SimpleScope(descriptions);
	}

	private boolean isPartOfBoundedContext(EObject object) {
		return getParentTypes(object).contains(BoundedContextImpl.class);
	}

	private boolean isPartOfDomain(EObject object) {
		return getParentTypes(object).contains(DomainImpl.class);
	}

	private Set<Class<? extends EObject>> getParentTypes(EObject object) {
		Set<Class<? extends EObject>> parentTypes = Sets.newHashSet();
		EObject parent = object.eContainer();
		while (parent != null) {
			parentTypes.add(parent.getClass());
			parent = parent.eContainer();
		}
		return parentTypes;
	}

}

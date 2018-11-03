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

package org.contextmapper.tactic.dsl.validation;

import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import com.google.common.collect.Iterators;

/**
 * Extensions for model elements of the domain model. Usage in Xtend files:
 * 
 * <pre>
 *   &#64;Inject extension TacticDslExtensions
 * 
 *   // ...
 * 
 *     element.rootContainer.eAllOfClass(typeof(DslService))
 * </pre>
 */
public class TacticDslExtensions {
	public static <T extends EObject> List<T> eAllOfClass(final EObject obj, final Class<T> clazz) {
		TreeIterator<EObject> eAll = null;
		if (obj != null) {
			eAll = EcoreUtil2.eAll(obj);
		}
		return IteratorExtensions.<T>toList(Iterators.<T>filter(eAll, clazz));
	}
}

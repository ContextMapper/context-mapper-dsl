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
package org.contextmapper.dsl.quickfixes;

import org.eclipse.emf.ecore.EObject;

/**
 * Interface for all CML quickfixes (semantic changes to the corresponding CML
 * model).
 * 
 * @author ska
 *
 */
public interface CMLQuickFix<T extends EObject> {

	/**
	 * Applies a quick fix (semantic change to the ContextMappingModel).
	 * 
	 * @param contextObject the object on which the quickfix is triggered (must be
	 *                      part of the model)
	 */
	public void applyQuickfix(T contextObject);

	/**
	 * Applies a quick fix (semantic change to the ContextMappingModel). IN CASE THE
	 * TYPE OF THE TRIGGER OBJECT IS NOT KNOWN!
	 * 
	 * @param contextObject the object on which the quickfix is triggered (must be
	 *                      part of the model)
	 */
	@SuppressWarnings("unchecked")
	default public void applyQuickfix2EObject(EObject contextObject) {
		applyQuickfix((T) contextObject);
	}

	/**
	 * Defines the name of the quick fix.
	 * 
	 * @return name of the quick fix as string
	 */
	public String getName();

	/**
	 * Defines the description text of the quick fix.
	 * 
	 * @return the description text of the quick fix as string
	 */
	public String getDescription();

}

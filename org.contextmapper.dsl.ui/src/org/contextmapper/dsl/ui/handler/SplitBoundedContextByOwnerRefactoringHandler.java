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
package org.contextmapper.dsl.ui.handler;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.refactoring.SplitBoundedContextByOwner;
import org.contextmapper.dsl.refactoring.henshin.Refactoring;
import org.eclipse.emf.ecore.EObject;

public class SplitBoundedContextByOwnerRefactoringHandler extends AbstractRefactoringHandler {

	@Override
	protected Refactoring getRefactoring() {
		BoundedContext bc = (BoundedContext) getSelectedElement();
		return new SplitBoundedContextByOwner(bc.getName());
	}

	@Override
	public boolean isEnabled() {
		EObject obj = getSelectedElement();

		if (obj == null || !super.isEnabled())
			return false;

		// only allowed on aggregates
		if (!(obj instanceof BoundedContext))
			return false;

		return true;
	}

}

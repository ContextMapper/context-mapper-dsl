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
import org.contextmapper.dsl.refactoring.henshin.SplitBoundedContextByDuplicateEntityInAggregatesRefactoring;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

public class SplitBoundedContextRefactoringHandler extends AbstractRefactoringHandler {

	@Override
	protected void executeRefactoring(Resource resource, ExecutionEvent event) {
		new SplitBoundedContextByDuplicateEntityInAggregatesRefactoring().doRefactor(resource);
	}

	@Override
	public boolean isEnabled() {
		EObject obj = getSelectedElement();
		return obj != null && obj instanceof BoundedContext && super.isEnabled();
	}

}

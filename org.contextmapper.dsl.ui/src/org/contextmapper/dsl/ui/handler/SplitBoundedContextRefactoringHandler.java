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

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.refactoring.henshin.SplitBoundedContextByDuplicateEntityInAggregatesRefactoring;
import org.eclipse.core.commands.ExecutionEvent;

public class SplitBoundedContextRefactoringHandler extends AbstractRefactoringHandler {

	@Override
	protected void executeRefactoring(CMLResourceContainer resource, ExecutionEvent event) {
		BoundedContext bc = (BoundedContext) getSelectedElement();
		new SplitBoundedContextByDuplicateEntityInAggregatesRefactoring(bc.getName()).doRefactor(resource);
	}

	/**
	 * 07.05.2019: Disable this AR for now.
	 */
	@Override
	public boolean isEnabled() {
//		EObject obj = getSelectedElement();
//
//		if (obj == null || !(obj instanceof BoundedContext))
//			return false;
//
//		BoundedContext bc = (BoundedContext) obj;
//		return new ContextMappingModelHelper(getCurrentContextMappingModel()).findDuplicateEntities(bc.getName()).size() > 0 && super.isEnabled();
		return false;
	}

}

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

import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.refactoring.SemanticCMLRefactoring;
import org.contextmapper.dsl.refactoring.SplitBoundedContextByOwner;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.ecore.EObject;

public class SplitBoundedContextByOwnerRefactoringHandler extends AbstractRefactoringHandler {

	@Override
	protected void executeRefactoring(CMLResource resource, ExecutionEvent event) {
		BoundedContext bc = (BoundedContext) getSelectedElement();
		SemanticCMLRefactoring ar = new SplitBoundedContextByOwner(bc.getName());
		ar.refactor(resource, getAllResources());
		ar.persistChanges();
	}

	@Override
	public boolean isEnabled() {
		EObject obj = getSelectedElement();

		if (obj == null || !super.isEnabled())
			return false;

		// only allowed on aggregates
		if (!(obj instanceof BoundedContext))
			return false;

		BoundedContext bc = (BoundedContext) obj;
		Set<String> owners = bc.getAggregates().stream().map(agg -> agg.getOwner() == null ? "" : agg.getOwner().getName()).collect(Collectors.toSet());
		return owners.size() > 1;
	}

}

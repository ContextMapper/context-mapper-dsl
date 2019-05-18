/*
 * Copyright 2019 The Context Mapper Project Team
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

import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.Module;
import org.contextmapper.dsl.refactoring.MergeAggregatesRefactoring;
import org.contextmapper.dsl.ui.handler.wizard.MergeAggregatesContext;
import org.contextmapper.dsl.ui.handler.wizard.MergeAggregatesRefactoringWizard;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class MergeAggregatesRefactoringHandler extends AbstractRefactoringWithUserInputHandler {

	@Override
	public boolean isEnabled() {
		EObject obj = getSelectedElement();

		if (obj == null || !super.isEnabled())
			return false;

		// only allowed on aggregates
		if (!(obj instanceof Aggregate))
			return false;

		return true;
	}

	@Override
	protected void executeRefactoring(Resource resource, ExecutionEvent event) {
		Aggregate aggregate = (Aggregate) getSelectedElement();
		BoundedContext parentBC = null;
		if (aggregate.eContainer() instanceof BoundedContext) {
			parentBC = (BoundedContext) aggregate.eContainer();
		} else if (aggregate.eContainer() instanceof Module) {
			Module module = (Module) aggregate.eContainer();
			parentBC = (BoundedContext) module.eContainer();
		} else {
			throw new RuntimeException("Unexpected change in model: Aggregate must be part of a Bounded Context or a Module!");
		}

		List<String> allAggregates = parentBC.getAggregates().stream().map(agg -> agg.getName()).collect(Collectors.toList());
		for (Module module : parentBC.getModules()) {
			allAggregates.addAll(module.getAggregates().stream().map(agg -> agg.getName()).collect(Collectors.toList()));
		}

		MergeAggregatesContext refactoringContext = new MergeAggregatesContext(aggregate.getName(), allAggregates);

		new WizardDialog(HandlerUtil.getActiveShell(event), new MergeAggregatesRefactoringWizard(refactoringContext, executionContext -> {
			return finishRefactoring(new MergeAggregatesRefactoring(executionContext.getSelectedAggregate1(), executionContext.getSelectedAggregate2()), resource, event);
		})).open();
	}

}

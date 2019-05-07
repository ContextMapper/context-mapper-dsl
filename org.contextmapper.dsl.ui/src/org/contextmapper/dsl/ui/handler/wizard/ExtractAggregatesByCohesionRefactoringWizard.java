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
package org.contextmapper.dsl.ui.handler.wizard;

import java.util.function.Function;

import org.contextmapper.dsl.ui.handler.wizard.pages.AggregateSelectionWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class ExtractAggregatesByCohesionRefactoringWizard extends Wizard {

	private AggregateSelectionWizardPage page;
	private Function<ExtractAggregatesByCohesionContext, Boolean> finishFunction;
	private ExtractAggregatesByCohesionContext context;

	public ExtractAggregatesByCohesionRefactoringWizard(ExtractAggregatesByCohesionContext context, Function<ExtractAggregatesByCohesionContext, Boolean> finishFunction) {
		super();
		setNeedsProgressMonitor(true);
		this.context = context;
		this.finishFunction = finishFunction;
	}

	@Override
	public String getWindowTitle() {
		return "Extract Aggregates by Cohesion (Manual Selection)";
	}

	@Override
	public void addPages() {
		page = new AggregateSelectionWizardPage(context.getNewBoundedContextName(), context.getAllAggregates());
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		context.setNewBoundedContextName(page.getBoundedContextName());
		context.setSelectedAggregates(page.getSelectedAggregates());
		return finishFunction.apply(context);
	}

}

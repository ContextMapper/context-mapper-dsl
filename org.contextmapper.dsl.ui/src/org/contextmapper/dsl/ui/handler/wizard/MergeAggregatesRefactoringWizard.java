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
package org.contextmapper.dsl.ui.handler.wizard;

import java.util.function.Function;

import org.contextmapper.dsl.ui.handler.wizard.pages.TwoAggregatesSelectionWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class MergeAggregatesRefactoringWizard extends Wizard {

	private TwoAggregatesSelectionWizardPage page;
	private Function<MergeAggregatesContext, Boolean> finishFunction;
	private MergeAggregatesContext context;

	public MergeAggregatesRefactoringWizard(MergeAggregatesContext context, Function<MergeAggregatesContext, Boolean> finishFunction) {
		super();
		setNeedsProgressMonitor(true);
		this.context = context;
		this.finishFunction = finishFunction;
	}

	@Override
	public String getWindowTitle() {
		return "Merge Aggregates";
	}

	@Override
	public void addPages() {
		page = new TwoAggregatesSelectionWizardPage(context.getInitialAggregate1(), context.getAllAggregates());
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		context.setSelectedAggregate1(page.getAggregate1());
		context.setSelectedAggregate2(page.getAggregate2());
		context.setTakeAttributesFromSecondAggregate(page.takeAttributesFromSecondAggregateCheckBox());
		return finishFunction.apply(context);
	}

}

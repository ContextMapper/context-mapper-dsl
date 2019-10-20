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

import java.util.Arrays;
import java.util.function.Function;

import org.contextmapper.dsl.ui.handler.wizard.pages.SuspendPartnershipWizardPage;
import org.contextmapper.dsl.ui.handler.wizard.pages.TwoBoundedContextSelectionWizardPage;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class SuspendPartnershipRefactoringWizard extends Wizard {

	private SuspendPartnershipWizardPage suspendPartnershipPage;
	private TwoBoundedContextSelectionWizardPage mergeBoundedContextsPage;
	private Function<SuspendPartnershipContext, Boolean> finishFunction;
	private SuspendPartnershipContext context;

	public SuspendPartnershipRefactoringWizard(SuspendPartnershipContext context, Function<SuspendPartnershipContext, Boolean> finishFunction) {
		super();
		setNeedsProgressMonitor(true);
		this.context = context;
		this.finishFunction = finishFunction;
		setForcePreviousAndNextButtons(true);
	}

	@Override
	public String getWindowTitle() {
		return "Suspend Partnership Relationship";
	}

	@Override
	public void addPages() {
		suspendPartnershipPage = new SuspendPartnershipWizardPage(context.getBoundedContext1(), context.getBoundedContext2());
		mergeBoundedContextsPage = new TwoBoundedContextSelectionWizardPage(context.getBoundedContext1(),
				Arrays.asList(new String[] { context.getBoundedContext1(), context.getBoundedContext2() }));
		mergeBoundedContextsPage.setInitialBoundedContext2(context.getBoundedContext2());
		mergeBoundedContextsPage.disableTextFields();
		addPage(suspendPartnershipPage);
		addPage(mergeBoundedContextsPage);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof SuspendPartnershipWizardPage && suspendPartnershipPage.getSuspendMode() == SuspendPartnershipMode.MERGE_BOUNDED_CONTEXTS) {
			addPage(mergeBoundedContextsPage);
			return mergeBoundedContextsPage;
		} else {
			return null;
		}
	}

	@Override
	public boolean canFinish() {
		if (suspendPartnershipPage.getSuspendMode() == SuspendPartnershipMode.MERGE_BOUNDED_CONTEXTS) {
			return suspendPartnershipPage.isPageComplete() && mergeBoundedContextsPage.isPageComplete();
		} else {
			return suspendPartnershipPage.isPageComplete();
		}
	}

	@Override
	public boolean performFinish() {
		context.setMode(suspendPartnershipPage.getSuspendMode());
		context.setReplaceModeUpstreamBoundedContext(suspendPartnershipPage.getReplaceModeUpstreamBoundedContext());
		context.setMergeModeSelectedBoundedContext1(mergeBoundedContextsPage.getBoundedContext1());
		context.setMergeModeSelectedBoundedContext2(mergeBoundedContextsPage.getBoundedContext2());
		context.setMergeModeTakeAttributesFromSecondBoundedContext(mergeBoundedContextsPage.takeAttributesFromSecondBoundedContext());
		return finishFunction.apply(context);
	}

}

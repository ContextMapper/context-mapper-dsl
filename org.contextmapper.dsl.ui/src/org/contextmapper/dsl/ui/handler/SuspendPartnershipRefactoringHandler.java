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

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.Partnership;
import org.contextmapper.dsl.refactoring.ChangePartnershipToUpstreamDownstreamRefactoring;
import org.contextmapper.dsl.refactoring.ExtractPartnershipRefactoring;
import org.contextmapper.dsl.refactoring.MergeBoundedContextsRefactoring;
import org.contextmapper.dsl.ui.handler.wizard.SuspendPartnershipContext;
import org.contextmapper.dsl.ui.handler.wizard.SuspendPartnershipMode;
import org.contextmapper.dsl.ui.handler.wizard.SuspendPartnershipRefactoringWizard;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class SuspendPartnershipRefactoringHandler extends AbstractRefactoringWithUserInputHandler {

	@Override
	public boolean isEnabled() {
		EObject obj = getSelectedElement();

		if (obj == null || !super.isEnabled())
			return false;

		// only allowed on partnerships
		if (!(obj instanceof Partnership))
			return false;

		return true;
	}

	@Override
	protected void executeRefactoring(CMLResourceContainer resource, ExecutionEvent event) {
		Partnership partnership = (Partnership) getSelectedElement();

		SuspendPartnershipContext refactoringContext = new SuspendPartnershipContext(partnership.getParticipant1().getName(), partnership.getParticipant2().getName());

		new WizardDialog(HandlerUtil.getActiveShell(event), new SuspendPartnershipRefactoringWizard(refactoringContext, executionContext -> {
			if (refactoringContext.getMode() == SuspendPartnershipMode.MERGE_BOUNDED_CONTEXTS) {
				// just use the 'Merge Bounded Contexts' AR in this case ...
				return finishRefactoring(new MergeBoundedContextsRefactoring(executionContext.getMergeModeSelectedBoundedContext1(),
						executionContext.getMergeModeSelectedBoundedContext2(), executionContext.isMergeModeTakeAttributesFromSecondBoundedContext()), resource, event);
			} else if (refactoringContext.getMode() == SuspendPartnershipMode.EXTRACT_NEW_BOUNDED_CONTEXT) {
				return finishRefactoring(new ExtractPartnershipRefactoring(refactoringContext.getBoundedContext1(), refactoringContext.getBoundedContext2()), resource, event);
			} else {
				String upstream = refactoringContext.getBoundedContext1();
				String downstream = refactoringContext.getBoundedContext2();
				if (refactoringContext.getReplaceModeUpstreamBoundedContext().equals(refactoringContext.getBoundedContext2())) {
					upstream = refactoringContext.getBoundedContext2();
					downstream = refactoringContext.getBoundedContext1();
				}
				return finishRefactoring(new ChangePartnershipToUpstreamDownstreamRefactoring(upstream, downstream), resource, event);
			}
		})).open();
	}

}

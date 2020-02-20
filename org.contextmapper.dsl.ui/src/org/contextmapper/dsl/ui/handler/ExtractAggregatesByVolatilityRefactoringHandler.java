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

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.LikelihoodForChange;
import org.contextmapper.dsl.refactoring.ExtractAggregatesByVolatility;
import org.contextmapper.dsl.ui.handler.wizard.ExtractAggregatesByVolatilityContext;
import org.contextmapper.dsl.ui.handler.wizard.ExtractAggregatesByVolatilityRefactoringWizard;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class ExtractAggregatesByVolatilityRefactoringHandler extends AbstractRefactoringWithUserInputHandler {

	@Override
	protected void executeRefactoring(CMLResourceContainer resource, ExecutionEvent event) {
		BoundedContext bc = (BoundedContext) getSelectedElement();

		ExtractAggregatesByVolatilityContext refactoringContext = new ExtractAggregatesByVolatilityContext(
				bc.getAggregates().stream().map(agg -> agg.getLikelihoodForChange()).collect(Collectors.toList()));

		new WizardDialog(HandlerUtil.getActiveShell(event), new ExtractAggregatesByVolatilityRefactoringWizard(refactoringContext, executionContext -> {
			ExtractAggregatesByVolatility ar = new ExtractAggregatesByVolatility(bc.getName(), executionContext.getVolatilityToExtract());
			return finishRefactoring(ar, resource, event);
		})).open();
	}

	@Override
	public boolean isEnabled() {
		EObject obj = getSelectedElement();

		if (obj == null || !super.isEnabled())
			return false;

		// only allowed on bounded contexts
		if (!(obj instanceof BoundedContext))
			return false;

		BoundedContext bc = (BoundedContext) obj;
		List<LikelihoodForChange> likelihoods = bc.getAggregates().stream().map(agg -> agg.getLikelihoodForChange()).collect(Collectors.toList());
		return likelihoods.size() > 1;
	}

}

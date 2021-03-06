/*
 * Copyright 2020 The Context Mapper Project Team
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

import org.contextmapper.dsl.ui.handler.wizard.pages.SplitSystemIntoSubsystemsWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class SplitSystemIntoSubsystemsWizard extends Wizard {

	private SplitSystemIntoSubsystemsWizardPage page;
	private Function<SplitSystemIntoSubsystemsContext, Boolean> finishFunction;
	private SplitSystemIntoSubsystemsContext context;

	public SplitSystemIntoSubsystemsWizard(SplitSystemIntoSubsystemsContext context, Function<SplitSystemIntoSubsystemsContext, Boolean> finishFunction) {
		super();
		setNeedsProgressMonitor(true);
		this.context = context;
		this.finishFunction = finishFunction;
	}

	@Override
	public String getWindowTitle() {
		return "Split System Context Into Two Subsystems";
	}

	@Override
	public void addPages() {
		page = new SplitSystemIntoSubsystemsWizardPage(context);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		context.setExistingContextSubsystemName(page.getExistingSubsystemName());
		context.setNewSubsystemName(page.getNewSubsystemName());
		context.copyDomainModel(page.copyDomainModel());
		context.setRelationshipType(page.getRelationshipType());
		context.setIntegrationType(page.getIntegrationType());
		context.setNewSubsystemImplementationTechnology(page.getNewSubsystemImplementationTechnology());
		context.setNewRelationshipImplementationTechnology(page.getNewRelationshipImplementationTechnology());
		return finishFunction.apply(context);
	}

}

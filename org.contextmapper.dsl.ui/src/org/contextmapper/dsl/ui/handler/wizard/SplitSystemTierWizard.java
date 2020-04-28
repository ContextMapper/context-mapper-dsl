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

import org.contextmapper.dsl.ui.handler.wizard.pages.SplitSystemTierWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class SplitSystemTierWizard extends Wizard {

	private SplitSystemTierWizardPage page;
	private Function<SplitSystemTierContext, Boolean> finishFunction;
	private SplitSystemTierContext context;

	public SplitSystemTierWizard(SplitSystemTierContext context, Function<SplitSystemTierContext, Boolean> finishFunction) {
		super();
		setNeedsProgressMonitor(true);
		this.context = context;
		this.finishFunction = finishFunction;
	}

	@Override
	public String getWindowTitle() {
		return "Split System Into Two Tiers";
	}

	@Override
	public void addPages() {
		page = new SplitSystemTierWizardPage(context);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		context.setExistingContextTierName(page.getExistingContextTierName());
		context.setNewTierName(page.getNewContextTierName());
		context.copyDomainModel(page.copyDomainModel());
		context.setRelationshipType(page.getRelationshipType());
		context.setIntegrationType(page.getIntegrationType());
		context.setNewTierImplementationTechnology(page.getNewTierImplementationTechnology());
		context.setNewRelationshipImplementationTechnology(page.getNewRelationshipImplementationTechnology());
		return finishFunction.apply(context);
	}

}

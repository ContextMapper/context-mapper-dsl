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

import org.contextmapper.dsl.ui.handler.wizard.pages.DeriveBackendFrontendSystemsFromFeatureWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class DeriveBackendFrontendSystemsFromFeatureWizard extends Wizard {

	private DeriveBackendFrontendSystemsFromFeatureWizardPage page;
	private Function<DeriveBackendFrontendFromFeatureContext, Boolean> finishFunction;
	private DeriveBackendFrontendFromFeatureContext context;

	public DeriveBackendFrontendSystemsFromFeatureWizard(DeriveBackendFrontendFromFeatureContext context,
			Function<DeriveBackendFrontendFromFeatureContext, Boolean> finishFunction) {
		super();
		setNeedsProgressMonitor(true);
		this.context = context;
		this.finishFunction = finishFunction;
	}

	@Override
	public String getWindowTitle() {
		return "Derive Frontend/Backend Systems and Integration Relationship";
	}

	@Override
	public void addPages() {
		page = new DeriveBackendFrontendSystemsFromFeatureWizardPage(context);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		context.setFrontendName(page.getFrontendName());
		context.setBackendName(page.getBackendName());
		context.deriveViewModelInFrontend(page.deriveViewModelInFrontend());
		context.setRelationshipType(page.getRelationshipType());
		context.setFrontendImplementationTechnology(page.getFrontendImplementationTechnology());
		context.setBackendImplementationTechnology(page.getBackendImplementationTechnology());
		context.setRelationshipImplementationTechnology(page.getRelationshipImplementationTechnology());
		return finishFunction.apply(context);
	}

}

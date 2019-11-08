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

import org.contextmapper.dsl.ui.handler.wizard.pages.GenerateContextMapWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class GenerateContextMapWizard extends Wizard {

	private GenerateContextMapWizardPage page;
	private Function<GenerateContextMapContext, Boolean> finishFunction;
	private GenerateContextMapContext context;

	public GenerateContextMapWizard(GenerateContextMapContext context, Function<GenerateContextMapContext, Boolean> finishFunction) {
		super();
		setNeedsProgressMonitor(true);
		this.context = context;
		this.finishFunction = finishFunction;
	}

	@Override
	public String getWindowTitle() {
		return "Generate Context Map";
	}

	@Override
	public void addPages() {
		page = new GenerateContextMapWizardPage();
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		context.setFormat(page.getSelectedFormat());
		context.setLabelSpacingFactor(page.getLabelSpacingFactor());
		context.setFixHeight(page.takeHeight());
		context.setFixWidth(page.takeWidth());
		context.setHeight(page.getHeight());
		context.setWidth(page.getWidth());
		return finishFunction.apply(context);
	}

}

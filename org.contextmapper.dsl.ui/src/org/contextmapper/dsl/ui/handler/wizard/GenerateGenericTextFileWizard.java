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

import org.contextmapper.dsl.ui.handler.wizard.pages.GenerateGenericTextFileWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class GenerateGenericTextFileWizard extends Wizard {

	private GenerateGenericTextFileWizardPage page;
	private Function<GenerateGenericTextFileContext, Boolean> finishFunction;
	private GenerateGenericTextFileContext context;

	public GenerateGenericTextFileWizard(GenerateGenericTextFileContext context, Function<GenerateGenericTextFileContext, Boolean> finishFunction) {
		super();
		setNeedsProgressMonitor(true);
		this.context = context;
		this.finishFunction = finishFunction;
	}

	@Override
	public String getWindowTitle() {
		return "Generate Generic Textual File with Freemarker Template";
	}

	@Override
	public void addPages() {
		page = new GenerateGenericTextFileWizardPage();
		page.setInitialTemplateFile(context.getFreemarkerTemplateFile());
		page.setInitialFilename(context.getTargetFileName());
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		context.setFreemarkerTemplateFile(page.getFile());
		context.setTargetFileName(page.getTargetFileName());
		return finishFunction.apply(context);
	}

}

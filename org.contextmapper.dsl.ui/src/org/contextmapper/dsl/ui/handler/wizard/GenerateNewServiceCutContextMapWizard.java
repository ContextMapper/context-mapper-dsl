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

import org.contextmapper.dsl.ui.handler.wizard.pages.GenerateNewServiceCutContextMapWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class GenerateNewServiceCutContextMapWizard extends Wizard {

	private GenerateNewServiceCutContextMapWizardPage page;
	private Function<GenerateNewServiceCutContextMapContext, Boolean> finishFunction;
	private GenerateNewServiceCutContextMapContext context;

	public GenerateNewServiceCutContextMapWizard(GenerateNewServiceCutContextMapContext solverConfiguration,
			Function<GenerateNewServiceCutContextMapContext, Boolean> finishFunction) {
		super();
		setNeedsProgressMonitor(true);
		this.context = solverConfiguration;
		this.finishFunction = finishFunction;
	}

	@Override
	public String getWindowTitle() {
		return "Generate New Service Cut (Context Map)";
	}

	@Override
	public void addPages() {
		page = new GenerateNewServiceCutContextMapWizardPage(this.context.getSolverConfiguration(), this.context.getSclFile());
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		context.setSolverConfiguration(page.getSolverConfiguration());
		context.setSclFile(page.getSCLFile());
		return finishFunction.apply(context);
	}

}

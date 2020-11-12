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
package org.contextmapper.dsl.ui.quickfix.context;

import org.contextmapper.dsl.ui.quickfix.wizard.SelectStoryVerbsWizard;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class SelectStoryVerbsContextCreator {

	public SelectStoryVerbsContext getContext() {
		Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		SelectStoryVerbsContext context = new SelectStoryVerbsContext();
		WizardDialog wizard = new WizardDialog(activeShell, new SelectStoryVerbsWizard(context));
		wizard.setBlockOnOpen(true);
		int returnCode = wizard.open();
		context.setFinishedWizard(returnCode == Window.OK);
		return context;
	}

}

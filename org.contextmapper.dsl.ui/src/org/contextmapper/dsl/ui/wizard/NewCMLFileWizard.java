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
package org.contextmapper.dsl.ui.wizard;

import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.ui.images.CMLImageDescriptionFactory;
import org.contextmapper.dsl.ui.wizard.page.WizardNewCMLFileCreationPage;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.DialogUtil;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

/**
 * 
 * Wizard to create new Context Mapper DSL (CML) file.
 * 
 * @author Stefan Kapferer
 *
 */
public class NewCMLFileWizard extends BasicNewResourceWizard {

	private WizardNewCMLFileCreationPage mainPage;

	@Override
	public void addPages() {
		mainPage = new WizardNewCMLFileCreationPage("New CML File Page", getSelection());
		mainPage.setAllowExistingResources(false);
		mainPage.setFileExtension("cml");
		mainPage.setFileName("model.cml");
		mainPage.setDescription("Create a new Context Mapper DSL (CML) file");
		mainPage.setTitle("New CML File");
		addPage(mainPage);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		super.init(workbench, currentSelection);
		setWindowTitle("New CML File Wizard");
		setNeedsProgressMonitor(true);
	}

	@Override
	protected void initializeDefaultPageImageDescriptor() {
		setDefaultPageImageDescriptor(CMLImageDescriptionFactory.createContextMapperLogo4DialogDescriptor());
	}

	@Override
	public boolean performFinish() {
		IFile file = mainPage.createNewFile();
		if (file == null) {
			return false;
		}

		selectAndReveal(file);

		// Open editor on new file.
		IWorkbenchWindow dw = getWorkbench().getActiveWorkbenchWindow();
		try {
			if (dw != null) {
				IWorkbenchPage page = dw.getActivePage();
				if (page != null) {
					IDE.openEditor(page, file, true);
				}
			}
		} catch (PartInitException e) {
			throw new ContextMapperApplicationException(e.getMessage(), e);
		}
		return true;
	}

}

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
package org.contextmapper.dsl.ui.handler;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.refactoring.ExtractSuggestedService;
import org.contextmapper.dsl.refactoring.SemanticCMLRefactoring;
import org.contextmapper.dsl.ui.handler.wizard.ChooseName4NewBoundedContextContext;
import org.contextmapper.dsl.ui.handler.wizard.ChooseName4NewBoundedContextWizard;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

public class ExtractSuggestedServiceRefactoringHandler extends AbstractRefactoringWithUserInputHandler {

	private Resource originalCMLResource;

	@Override
	public boolean isEnabled() {
		EObject obj = getSelectedElement();

		if (obj == null || !super.isEnabled())
			return false;

		Resource resource = obj.eResource();
		if (resource == null)
			return false;

		if (!resource.getURI().lastSegment().matches(ExtractSuggestedService.SERVICE_CUTTER_SUGGESTION_FILE_NAME_PATTERN))
			return false;

		return obj instanceof BoundedContext;
	}

	@Override
	protected void executeRefactoring(CMLResourceContainer resource, ExecutionEvent event) {
		BoundedContext bc = (BoundedContext) getSelectedElement();

		ChooseName4NewBoundedContextContext refactoringContext = new ChooseName4NewBoundedContextContext("NewBoundedContext");
		originalCMLResource = getAllResources().getResource(new ExtractSuggestedService(bc, "TempBC").constructOriginalModelUri(), false);
		if (originalCMLResource == null)
			throw new ContextMapperApplicationException("We were not able to find the original CML model. Please do not rename the models after generating service cut suggestions.");

		new WizardDialog(HandlerUtil.getActiveShell(event), new ChooseName4NewBoundedContextWizard(refactoringContext, executionContext -> {
			return finishRefactoring(new ExtractSuggestedService(bc, executionContext.getNewBoundedContextName()), new CMLResourceContainer(originalCMLResource), event);
		})).open();
	}

	@Override
	protected boolean finishRefactoring(SemanticCMLRefactoring ar, CMLResourceContainer resource, ExecutionEvent event) {
		IWorkbenchPage workbenchPage = getCurrentWorkbenchPage();
		if (workbenchPage != null && originalCMLResource != null) {
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(originalCMLResource.getURI().toPlatformString(false)));
			if (file != null) {
				try {
					IDE.openEditor(workbenchPage, file);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		}
		return super.finishRefactoring(ar, resource, event);
	}

	private IWorkbenchPage getCurrentWorkbenchPage() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return null;

		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window == null)
			return null;

		return window.getActivePage();
	}

}

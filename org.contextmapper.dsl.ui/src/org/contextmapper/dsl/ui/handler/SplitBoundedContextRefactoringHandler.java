/*
 * Copyright 2018 The Context Mapper Project Team
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

import java.net.URL;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.refactoring.HenshinSplitBoundedContextRefactoring;
import org.contextmapper.dsl.ui.internal.DslActivator;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.osgi.framework.Bundle;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class SplitBoundedContextRefactoringHandler extends AbstractHandler implements IHandler {

	@Inject
	private EObjectAtOffsetHelper eObjectAtOffsetHelper;

	@Inject
	private Provider<EclipseResourceFileSystemAccess2> fileAccessProvider;

	@Inject
	IResourceDescriptions resourceDescriptions;

	@Inject
	IResourceSetProvider resourceSetProvider;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			XtextEditor xEditor = EditorUtils.getActiveXtextEditor();
			IResource xResource = xEditor.getResource();

			URI uri = URI.createPlatformResourceURI(xResource.getFullPath().toString(), true);

			ResourceSet rs = resourceSetProvider.get(xResource.getProject());
			Resource resource = rs.getResource(uri, true);

			HenshinSplitBoundedContextRefactoring refactoring = new HenshinSplitBoundedContextRefactoring();
			refactoring.doRefactor(resource);
		} catch (Exception e) {
			String message = e.getMessage() != null && !"".equals(e.getMessage()) ? e.getMessage() : e.getClass().getName() + " occurred in " + this.getClass().getName();
			Status status = new Status(IStatus.ERROR, DslActivator.PLUGIN_ID, message, e);
			StatusManager.getManager().handle(status);
			ErrorDialog.openError(HandlerUtil.getActiveShell(event), "Error", "Exception occured during execution of command!", status);
		}
		return null;
	}

	private EObject getSelectedElement() {
		final XtextEditor editor = EditorUtils.getActiveXtextEditor();
		if (editor != null) {
			final ITextSelection selection = (ITextSelection) editor.getSelectionProvider().getSelection();
			ContextMapperRefactoringContext context = editor.getDocument().priorityReadOnly(new IUnitOfWork<ContextMapperRefactoringContext, XtextResource>() {
				@Override
				public ContextMapperRefactoringContext exec(XtextResource resource) throws Exception {
					EObject selectedElement = eObjectAtOffsetHelper.resolveElementAt(resource, selection.getOffset());
					if (selectedElement != null) {
						return new ContextMapperRefactoringContext(selectedElement);
					}
					return null;
				}

			});
			if (context != null) {
				return context.selectedObject;
			}
		}
		return null;
	}

	private boolean editorHasChanges() {
		final XtextEditor editor = EditorUtils.getActiveXtextEditor();
		if (editor != null) {
			return editor.isDirty();
		}
		return false;
	}

	@Override
	public boolean isEnabled() {
		EObject obj = getSelectedElement();
		return obj != null && obj instanceof BoundedContext && !editorHasChanges();
	}

	private class ContextMapperRefactoringContext {
		private EObject selectedObject;

		public ContextMapperRefactoringContext(EObject selectedObject) {
			this.selectedObject = selectedObject;
		}
	}

}

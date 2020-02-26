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

import java.util.ArrayList;
import java.util.List;

import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.ui.internal.DslActivator;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGenerator2;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;
import com.google.inject.Provider;

public abstract class AbstractGenerationHandler extends AbstractHandler implements IHandler {

	@Inject
	private Provider<EclipseResourceFileSystemAccess2> fileAccessProvider;

	@Inject
	IResourceDescriptions resourceDescriptions;

	@Inject
	IResourceSetProvider resourceSetProvider;

	protected abstract IGenerator2 getGenerator();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Resource resource = getResource(event);
		IFile file = getSelectedFile(event);
		if (resource != null && file != null) {
			try {
				final EclipseResourceFileSystemAccess2 fsa = fileAccessProvider.get();
				fsa.setProject(file.getProject());
				fsa.setOutputPath(getGenFolder(file).toString());
				fsa.setMonitor(new NullProgressMonitor());
				runGeneration(resource, event, fsa);
			} catch (ContextMapperApplicationException e) {
				MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "Model Input", e.getMessage());
			} catch (Exception e) {
				String message = e.getMessage() != null && !"".equals(e.getMessage()) ? e.getMessage() : e.getClass().getName() + " occurred in " + this.getClass().getName();
				Status status = new Status(IStatus.ERROR, DslActivator.PLUGIN_ID, message, e);
				StatusManager.getManager().handle(status);
				ErrorDialog.openError(HandlerUtil.getActiveShell(event), "Error", "Exception occured during execution of command!", createMultiStatus(e.getLocalizedMessage(), e));
			}
		}
		return null;
	}

	protected void runGeneration(Resource resource, ExecutionEvent event, IFileSystemAccess2 fsa) {
		try {
			getGenerator().doGenerate(resource, fsa, new GeneratorContext());
		} catch (ContextMapperApplicationException e) {
			MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "Model Input", e.getMessage());
		} catch (Exception e) {
			String message = e.getMessage() != null && !"".equals(e.getMessage()) ? e.getMessage() : e.getClass().getName() + " occurred in " + this.getClass().getName();
			Status status = new Status(IStatus.ERROR, DslActivator.PLUGIN_ID, message, e);
			StatusManager.getManager().handle(status);
			ErrorDialog.openError(HandlerUtil.getActiveShell(event), "Error", "Exception occured during execution of command!", createMultiStatus(e.getLocalizedMessage(), e));
		}
	}

	private Resource getResource(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object firstElement = structuredSelection.getFirstElement();
			if (firstElement instanceof IFile) {
				return getResource((IFile) firstElement);
			}
		} else if (selection instanceof TextSelection && EditorUtils.getActiveXtextEditor() != null) {
			XtextEditor xEditor = EditorUtils.getActiveXtextEditor();
			IResource xResource = xEditor.getResource();

			URI uri = URI.createPlatformResourceURI(xResource.getFullPath().toString(), true);

			ResourceSet rs = resourceSetProvider.get(xResource.getProject());
			return rs.getResource(uri, true);
		}
		return null;
	}

	protected IFile getSelectedFile(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object firstElement = structuredSelection.getFirstElement();
			if (firstElement instanceof IFile) {
				return (IFile) firstElement;
			}
		} else if (selection instanceof TextSelection && EditorUtils.getActiveXtextEditor() != null) {
			XtextEditor xEditor = EditorUtils.getActiveXtextEditor();
			IEditorInput input = xEditor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				return ((IFileEditorInput) input).getFile();
			}
		}
		return null;
	}

	protected Resource getResource(IFile file) {
		URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
		ResourceSet rs = resourceSetProvider.get(file.getProject());
		return rs.getResource(uri, true);
	}

	protected IPath getGenFolder(IFile file) {
		IFolder srcGenFolder = file.getProject().getFolder("src-gen");
		if (!srcGenFolder.exists()) {
			try {
				srcGenFolder.create(true, true, new NullProgressMonitor());
			} catch (CoreException e) {
				return null;
			}
		}
		return srcGenFolder.getProjectRelativePath();
	}

	protected MultiStatus createMultiStatus(String msg, Throwable t) {
		List<Status> childStatuses = new ArrayList<>();
		StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
		for (StackTraceElement stackTrace : stackTraces) {
			Status status = new Status(IStatus.ERROR, "org.contextmapper.dsl.ui", stackTrace.toString());
			childStatuses.add(status);
		}
		return new MultiStatus("org.contextmapper.dsl.ui", IStatus.ERROR, childStatuses.toArray(new Status[] {}), t.toString(), t);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}

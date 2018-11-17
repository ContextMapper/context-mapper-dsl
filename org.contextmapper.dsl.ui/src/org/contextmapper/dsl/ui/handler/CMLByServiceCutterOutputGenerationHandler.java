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

import org.contextmapper.dsl.generator.CMLByServiceCutterOutputGenerator;
import org.contextmapper.dsl.generator.servicecutter.output.factory.ServiceCutterOutputModelFactory;
import org.contextmapper.dsl.generator.servicecutter.output.factory.ServiceCutterOutputModelReadingException;
import org.contextmapper.dsl.generator.servicecutter.output.model.ServiceCutterOutputModel;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class CMLByServiceCutterOutputGenerationHandler extends AbstractHandler implements IHandler {

	@Inject
	private CMLByServiceCutterOutputGenerator generator;

	@Inject
	private Provider<EclipseResourceFileSystemAccess2> fileAccessProvider;

	@Inject
	IResourceDescriptions resourceDescriptions;

	@Inject
	IResourceSetProvider resourceSetProvider;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object firstElement = structuredSelection.getFirstElement();
			if (firstElement instanceof IFile) {
				IFile file = (IFile) firstElement;
				IProject project = file.getProject();

				final EclipseResourceFileSystemAccess2 fsa = fileAccessProvider.get();
				fsa.setProject(project);
				fsa.setMonitor(new NullProgressMonitor());
				URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);

				try {
					ServiceCutterOutputModelFactory modelFactory = new ServiceCutterOutputModelFactory();
					ServiceCutterOutputModel model = modelFactory.createFromJsonFile(uri);
					generator.doGenerate(uri, model);
				} catch (ServiceCutterOutputModelReadingException e) {
					MessageDialog.openError(HandlerUtil.getActiveShell(event), "Input Error", e.getMessage());
				}
			}
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}

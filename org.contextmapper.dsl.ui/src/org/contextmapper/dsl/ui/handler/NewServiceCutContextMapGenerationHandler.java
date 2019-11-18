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
package org.contextmapper.dsl.ui.handler;

import org.contextmapper.dsl.generator.NewServiceCutContextMapGenerator;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.contextmapper.dsl.generator.servicecutter.input.converter.SCLToUserRepresentationsConverter;
import org.contextmapper.dsl.ui.handler.wizard.GenerateNewServiceCutContextMapContext;
import org.contextmapper.dsl.ui.handler.wizard.GenerateNewServiceCutContextMapWizard;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterUserRepresentationsModel;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGenerator2;

import com.google.inject.Inject;

import ch.hsr.servicecutter.api.SolverConfigurationFactory;
import ch.hsr.servicecutter.api.model.UserRepresentationContainer;

public class NewServiceCutContextMapGenerationHandler extends AbstractGenerationHandler {

	@Inject
	private NewServiceCutContextMapGenerator generator;

	@Override
	protected IGenerator2 getGenerator() {
		return generator;
	}

	@Override
	protected void runGeneration(Resource resource, ExecutionEvent event, IFileSystemAccess2 fsa) {
		GenerateNewServiceCutContextMapContext context = new GenerateNewServiceCutContextMapContext();
		context.setSolverConfiguration(new SolverConfigurationFactory().createDefaultConfiguration());
		context.setSclFile(getDefaultSCLFile(event));
		new WizardDialog(HandlerUtil.getActiveShell(event), new GenerateNewServiceCutContextMapWizard(context, executionContext -> {
			generator.setSolverConfiguration(executionContext.getSolverConfiguration());
			generator.setUserRepresentationContainer(getUserRepresentations(executionContext.getSclFile()));
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					try {
						getGenerator().doGenerate(resource, fsa, new GeneratorContext());
					} catch (GeneratorInputException e) {
						MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "Model Input", e.getMessage());
					} catch (Exception e) {
						ErrorDialog.openError(HandlerUtil.getActiveShell(event), "Error", "Exception occured during execution of command!",
								createMultiStatus(e.getLocalizedMessage(), e));
					}
				}
			});
			return true;
		})).open();
	}

	@Override
	protected IPath getGenFolder(IFile file) {
		return file.getParent().getProjectRelativePath();
	}

	private IFile getDefaultSCLFile(ExecutionEvent event) {
		IFile cmlFile = getSelectedFile(event);
		String defaultSCLFileName = cmlFile.getName().replace(".cml", "") + "_User_Representations.scl";
		IContainer container = cmlFile.getParent();
		IResource resource = container.findMember(defaultSCLFileName);
		if (resource != null && resource instanceof IFile)
			return (IFile) resource;
		return null;
	}

	private UserRepresentationContainer getUserRepresentations(IFile sclFile) {
		if (sclFile == null)
			return null;
		Resource resource = getResource(sclFile);
		ServiceCutterUserRepresentationsModel model = (ServiceCutterUserRepresentationsModel) resource.getContents().get(0);
		return new SCLToUserRepresentationsConverter().convert(model);
	}

}

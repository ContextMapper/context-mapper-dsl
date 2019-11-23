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

import java.util.Map;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.NewServiceCutContextMapGenerator;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.contextmapper.dsl.generator.servicecutter.input.converter.SCLToUserRepresentationsConverter;
import org.contextmapper.dsl.ui.handler.wizard.GenerateNewServiceCutContextMapContext;
import org.contextmapper.dsl.ui.handler.wizard.GenerateNewServiceCutContextMapWizard;
import org.contextmapper.dsl.ui.internal.DslActivator;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterUserRepresentationsModel;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGenerator2;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.google.inject.Inject;

import ch.hsr.servicecutter.api.SolverConfigurationFactory;
import ch.hsr.servicecutter.api.model.UserRepresentationContainer;
import ch.hsr.servicecutter.solver.SolverAlgorithm;
import ch.hsr.servicecutter.solver.SolverConfiguration;
import ch.hsr.servicecutter.solver.SolverPriority;

public class NewServiceCutContextMapGenerationHandler extends AbstractGenerationHandler {

	private static final String PREFERENCES_QUALIFIER = "org.contextmapper.servicecutter.solverconfiguration";

	@Inject
	private NewServiceCutContextMapGenerator generator;

	private IFile cmlFile;

	@Override
	protected IGenerator2 getGenerator() {
		return generator;
	}

	@Override
	protected void runGeneration(Resource resource, ExecutionEvent event, IFileSystemAccess2 fsa) {
		checkPreconditions(resource);
		cmlFile = getSelectedFile(event);
		GenerateNewServiceCutContextMapContext context = new GenerateNewServiceCutContextMapContext();
		context.setSolverConfiguration(createSolverConfiguration());
		context.setSclFile(getLastSelectedOrDefaultSCLFile(event));
		new WizardDialog(HandlerUtil.getActiveShell(event), new GenerateNewServiceCutContextMapWizard(context, executionContext -> {
			generator.setSolverConfiguration(executionContext.getSolverConfiguration());
			generator.setUserRepresentationContainer(getUserRepresentations(executionContext.getSclFile()));
			persistSolverConfiguration(executionContext.getSolverConfiguration());
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

	private void checkPreconditions(Resource resource) {
		ContextMappingModel model = (ContextMappingModel) resource.getContents().get(0);
		generator.checkPreconditions(model);
	}

	@Override
	protected IPath getGenFolder(IFile file) {
		return file.getParent().getProjectRelativePath();
	}

	private IFile getLastSelectedOrDefaultSCLFile(ExecutionEvent event) {
		// check if last selection is stored
		try {
			Map<QualifiedName, String> properties = cmlFile.getPersistentProperties();
			if (properties.containsKey(getQualifiedName4File(cmlFile)) && findFileInContainer(cmlFile.getProject(), properties.get(getQualifiedName4File(cmlFile))) != null) {
				return findFileInContainer(cmlFile.getProject(), properties.get(getQualifiedName4File(cmlFile)));
			}
		} catch (CoreException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, DslActivator.PLUGIN_ID, "Could not read persisted SCL file location.", e));
		}

		// check if default file exists
		String defaultSCLFileName = cmlFile.getName().replace(".cml", "") + "_User_Representations.scl";
		return findFileInContainer(cmlFile.getParent(), defaultSCLFileName);
	}

	private IFile findFileInContainer(IContainer container, String path) {
		IResource resource = container.findMember(path);
		if (resource != null && resource instanceof IFile)
			return (IFile) resource;
		return null;
	}

	private UserRepresentationContainer getUserRepresentations(IFile sclFile) {
		if (sclFile == null)
			return null;
		Resource resource = getResource(sclFile);
		ServiceCutterUserRepresentationsModel model = (ServiceCutterUserRepresentationsModel) resource.getContents().get(0);
		persistSelectedUserRepresentationsFile(sclFile);
		return new SCLToUserRepresentationsConverter().convert(model);
	}

	private void persistSelectedUserRepresentationsFile(IFile sclFile) {
		try {
			cmlFile.setPersistentProperty(getQualifiedName4File(cmlFile), sclFile.getProjectRelativePath().toString());
		} catch (CoreException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, DslActivator.PLUGIN_ID, "Could not persist SCL file location.", e));
		}
	}

	private SolverConfiguration createSolverConfiguration() {
		SolverConfiguration solverConfiguration = new SolverConfigurationFactory().createDefaultConfiguration();
		IEclipsePreferences preferencesRoot = InstanceScope.INSTANCE.getNode(PREFERENCES_QUALIFIER);
		Preferences filePreferences = preferencesRoot.node(cmlFile.getProjectRelativePath().toString());
		solverConfiguration.setAlgorithm(SolverAlgorithm.valueOf(filePreferences.get("algorithm", solverConfiguration.getAlgorithm().name())));
		for (Map.Entry<String, SolverPriority> entry : solverConfiguration.getPriorities().entrySet()) {
			entry.setValue(SolverPriority.valueOf(filePreferences.get(entry.getKey(), entry.getValue().name())));
		}
		return solverConfiguration;
	}

	private void persistSolverConfiguration(SolverConfiguration solverConfiguration) {
		IEclipsePreferences preferencesRoot = InstanceScope.INSTANCE.getNode(PREFERENCES_QUALIFIER);
		Preferences filePreferences = preferencesRoot.node(cmlFile.getProjectRelativePath().toString());
		filePreferences.put("algorithm", solverConfiguration.getAlgorithm().name());
		for (Map.Entry<String, SolverPriority> entry : solverConfiguration.getPriorities().entrySet()) {
			filePreferences.put(entry.getKey(), entry.getValue().name());
		}
		try {
			preferencesRoot.flush();
		} catch (BackingStoreException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, DslActivator.PLUGIN_ID, "Could not persist solver priorities.", e));
		}
	}

	private QualifiedName getQualifiedName4File(IFile file) {
		return new QualifiedName("org.contextmapper.servicecutterconfig", file.getProjectRelativePath().toString());
	}

}

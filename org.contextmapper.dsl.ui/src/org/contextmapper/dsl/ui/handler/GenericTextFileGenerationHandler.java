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

import java.io.File;
import java.util.Map;

import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.generator.GenericContentGenerator;
import org.contextmapper.dsl.ui.handler.wizard.GenerateGenericTextFileContext;
import org.contextmapper.dsl.ui.handler.wizard.GenerateGenericTextFileWizard;
import org.contextmapper.dsl.ui.internal.DslActivator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGenerator2;

import com.google.inject.Inject;

public class GenericTextFileGenerationHandler extends AbstractGenerationHandler {

	private static final String LAST_SELECTED_TEMPLATE_PROPERTY = "lastSelectedTemplate";
	private static final String LAST_TARGET_FILE_NAME_PROPERTY = "lastTargetFileName";

	@Inject
	private GenericContentGenerator generator;

	private IFile selectedCmlFile;

	@Override
	protected IGenerator2 getGenerator() {
		return generator;
	}

	@Override
	protected void runGeneration(Resource resource, ExecutionEvent event, IFileSystemAccess2 fsa) {
		selectedCmlFile = getSelectedFile(event);
		GenerateGenericTextFileContext context = getLastStoredContext(event);
		new WizardDialog(HandlerUtil.getActiveShell(event), new GenerateGenericTextFileWizard(context, executionContext -> {
			generator.setFreemarkerTemplateFile(new File(context.getFreemarkerTemplateFile().getLocationURI()));
			generator.setTargetFileName(context.getTargetFileName());
			persistContext(context);
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					try {
						getGenerator().doGenerate(resource, fsa, new GeneratorContext());
					} catch (ContextMapperApplicationException e) {
						MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "Model Input", e.getMessage());
					}
				}
			});
			return true;
		})).open();
	}

	private GenerateGenericTextFileContext getLastStoredContext(ExecutionEvent event) {
		GenerateGenericTextFileContext context = new GenerateGenericTextFileContext();
		try {
			Map<QualifiedName, String> properties = selectedCmlFile.getPersistentProperties();
			if (!properties.containsKey(getQualifiedName4File(selectedCmlFile, LAST_SELECTED_TEMPLATE_PROPERTY)))
				return null;
			IFile templateFile = findFileInContainer(selectedCmlFile.getProject(), properties.get(getQualifiedName4File(selectedCmlFile, LAST_SELECTED_TEMPLATE_PROPERTY)));
			context.setFreemarkerTemplateFile(templateFile);
			if (properties.containsKey(getQualifiedName4File(selectedCmlFile, LAST_TARGET_FILE_NAME_PROPERTY)))
				context.setTargetFileName(properties.get(getQualifiedName4File(selectedCmlFile, LAST_TARGET_FILE_NAME_PROPERTY)));
		} catch (CoreException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, DslActivator.PLUGIN_ID, "Could not read persisted template file location.", e));
		}
		return context;
	}

	private void persistContext(GenerateGenericTextFileContext context) {
		try {
			selectedCmlFile.setPersistentProperty(getQualifiedName4File(selectedCmlFile, LAST_SELECTED_TEMPLATE_PROPERTY),
					context.getFreemarkerTemplateFile().getProjectRelativePath().toString());
			selectedCmlFile.setPersistentProperty(getQualifiedName4File(selectedCmlFile, LAST_TARGET_FILE_NAME_PROPERTY), context.getTargetFileName());
		} catch (CoreException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, DslActivator.PLUGIN_ID, "Could not persist template file location.", e));
		}
	}

	private QualifiedName getQualifiedName4File(IFile file, String property) {
		return new QualifiedName("org.contextmapper.genericGenerator." + property, file.getProjectRelativePath().toString());
	}

}

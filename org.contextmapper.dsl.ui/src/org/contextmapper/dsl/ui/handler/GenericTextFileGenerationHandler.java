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

import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.generator.GenericContentGenerator;
import org.contextmapper.dsl.ui.handler.wizard.GenerateGenericTextFileContext;
import org.contextmapper.dsl.ui.handler.wizard.GenerateGenericTextFileWizard;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGenerator2;

import com.google.inject.Inject;

public class GenericTextFileGenerationHandler extends AbstractGenerationHandler {

	@Inject
	private GenericContentGenerator generator;

	@Override
	protected IGenerator2 getGenerator() {
		return generator;
	}

	@Override
	protected void runGeneration(Resource resource, ExecutionEvent event, IFileSystemAccess2 fsa) {
		GenerateGenericTextFileContext context = new GenerateGenericTextFileContext();
		new WizardDialog(HandlerUtil.getActiveShell(event), new GenerateGenericTextFileWizard(context, executionContext -> {
			generator.setFreemarkerTemplateFile(new File(context.getFreemarkerTemplateFile().getLocationURI()));
			generator.setTargetFileName(context.getTargetFileName());
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

}

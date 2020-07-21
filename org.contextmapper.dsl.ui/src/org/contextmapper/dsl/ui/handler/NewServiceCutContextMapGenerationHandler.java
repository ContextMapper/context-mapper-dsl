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

import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.NewServiceCutContextMapGenerator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGenerator2;

import com.google.inject.Inject;

public class NewServiceCutContextMapGenerationHandler extends AbstractGenerationHandler {

	@Inject
	private NewServiceCutContextMapGenerator generator;

	@Override
	protected IGenerator2 getGenerator() {
		return generator;
	}

	@Override
	protected void runGeneration(Resource resource, ExecutionEvent event, IFileSystemAccess2 fsa) {
		String projectDir = getSelectedFile(event).getProject().getLocation().toOSString();
		this.generator.setProjectDirectory(new File(projectDir));
		checkPreconditions(resource);
		super.runGeneration(resource, event, fsa);
	}

	private void checkPreconditions(Resource resource) {
		ContextMappingModel model = (ContextMappingModel) resource.getContents().get(0);
		generator.checkPreconditions(model);
	}

	@Override
	protected IPath getGenFolder(IFile file) {
		return file.getParent().getProjectRelativePath();
	}

}

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
package org.contextmapper.dsl.ide.commands.impl.generation;

import java.nio.file.Paths;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.generator.NewServiceCutContextMapGenerator;
import org.contextmapper.dsl.standalone.FileSystemHelper;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.IGenerator2;
import org.eclipse.xtext.ide.server.Document;
import org.eclipse.xtext.ide.server.ILanguageServerAccess;

/**
 * This command calls the 'New Service Cut' generator that generates a new
 * Context Mapping Model using Service Cutter.
 * 
 * @author Stefan Kapferer
 *
 */
public class NewServiceCutGenerationCommand extends AbstractGenerationCommand {

	@Override
	public void executeCommand(CMLResource cmlResource, Document document, ILanguageServerAccess access, ExecuteCommandParams params) {
		NewServiceCutContextMapGenerator serviceCutGenerator = (NewServiceCutContextMapGenerator) getGenerator();
		serviceCutGenerator.setProjectDirectory(Paths.get(access.getInitializeParams().getWorkspaceFolders().get(0).getUri().replace("file:", "")).toFile());
		serviceCutGenerator.doGenerate(cmlResource, FileSystemHelper.getFileSystemAccess(cmlResource.getURI().trimFileExtension().trimSegments(1).toFileString()), new GeneratorContext());
	}

	@Override
	IGenerator2 getGenerator() {
		return new NewServiceCutContextMapGenerator();
	}

}

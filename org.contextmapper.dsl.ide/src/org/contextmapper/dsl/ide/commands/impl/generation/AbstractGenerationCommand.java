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

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.ide.commands.CMLResourceCommand;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.IGenerator2;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.ide.server.Document;
import org.eclipse.xtext.ide.server.ILanguageServerAccess;
import org.eclipse.xtext.parser.IEncodingProvider;
import org.eclipse.xtext.service.AbstractGenericModule;

import com.google.inject.Guice;

/**
 * Abstract command to call generators.
 * 
 * @author Stefan Kapferer
 *
 */
public abstract class AbstractGenerationCommand implements CMLResourceCommand {

	/**
	 * Override this method to define which generator shall be called.
	 */
	abstract IGenerator2 getGenerator();

	@Override
	public void executeCommand(CMLResourceContainer cmlResource, Document document, ILanguageServerAccess access, ExecuteCommandParams params) {
		JavaIoFileSystemAccess javaIoFileSystemAccess = getFileSystemAccess();
		javaIoFileSystemAccess.setOutputPath("./src-gen");
		getGenerator().doGenerate(cmlResource.getResource(), javaIoFileSystemAccess, new GeneratorContext());		
	}
	
	private JavaIoFileSystemAccess getFileSystemAccess() {
		JavaIoFileSystemAccess fsa = new JavaIoFileSystemAccess();
		Guice.createInjector(new AbstractGenericModule() {
			@SuppressWarnings("unused")
			public Class<? extends IEncodingProvider> bindIEncodingProvider() {
				return IEncodingProvider.Runtime.class;
			}
		}).injectMembers(fsa);
		return fsa;
	}
}

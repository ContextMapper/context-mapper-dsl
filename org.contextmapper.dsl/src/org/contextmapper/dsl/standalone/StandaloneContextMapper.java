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
package org.contextmapper.dsl.standalone;

import java.io.File;

import org.contextmapper.dsl.ContextMappingDSLStandaloneSetup;
import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.refactoring.SemanticCMLRefactoring;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.IGenerator2;
import org.eclipse.xtext.serializer.ISerializer;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class StandaloneContextMapper implements StandaloneContextMapperAPI {

	@Inject
	private ISerializer serializer;

	public StandaloneContextMapper() {
		Injector injector = new ContextMappingDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
		injector.injectMembers(this);
	}

	@Override
	public CMLResource loadCML(String filepath) {
		return new CMLResource(new ResourceSetImpl().getResource(URI.createURI(filepath), true));
	}

	@Override
	public CMLResource loadCML(File cmlFile) {
		return new CMLResource(new ResourceSetImpl().getResource(URI.createFileURI(cmlFile.getAbsolutePath()), true));
	}

	@Override
	public void callGenerator(CMLResource cml, IGenerator2 generator) {
		generator.doGenerate(cml, FileSystemHelper.getFileSystemAccess(), new GeneratorContext());
	}

	@Override
	public void callGenerator(CMLResource cml, IGenerator2 generator, String outputDir) {
		generator.doGenerate(cml, FileSystemHelper.getFileSystemAccess(outputDir), new GeneratorContext());
	}

	@Override
	public CMLResource createCML(String filepath) {
		return createNewCMLResource(new File(filepath));
	}

	@Override
	public CMLResource createCML(File cmlFile) {
		return createNewCMLResource(cmlFile);
	}

	private CMLResource createNewCMLResource(File file) {
		Resource resource = new ResourceSetImpl().createResource(URI.createFileURI(file.getAbsolutePath()));
		resource.getContents().add(ContextMappingDSLFactory.eINSTANCE.createContextMappingModel());
		return new CMLResource(resource);
	}

	@Override
	public void applyRefactoring(CMLResource cml, SemanticCMLRefactoring refactoring) {
		refactoring.refactor(cml);
		refactoring.persistChanges(serializer);
	}

}

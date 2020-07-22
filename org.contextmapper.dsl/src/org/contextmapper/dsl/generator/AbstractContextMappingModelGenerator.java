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
package org.contextmapper.dsl.generator;

import java.util.List;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.exception.NoContextMappingModelDefinedException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import com.google.common.collect.Iterators;

public abstract class AbstractContextMappingModelGenerator extends AbstractGenerator {

	protected ContextMappingModel contextMappingModel;
	protected ResourceSet resourceSet;
	protected IFileSystemAccess2 fsa;
	protected IGeneratorContext context;

	@Override
	public void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {
		this.resourceSet = resource.getResourceSet();
		this.fsa = fsa;
		this.context = context;
		List<ContextMappingModel> contextMappingModels = IteratorExtensions.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(resource.getAllContents(), ContextMappingModel.class));

		if (contextMappingModels.isEmpty())
			throw new NoContextMappingModelDefinedException();

		// cml file can only contain one model
		contextMappingModel = contextMappingModels.get(0);
		EcoreUtil.resolveAll(contextMappingModel);

		this.generateFromContextMappingModel(contextMappingModel, fsa, resource.getURI());
	}

	protected abstract void generateFromContextMappingModel(ContextMappingModel model, IFileSystemAccess2 fsa, URI inputFileURI);

}

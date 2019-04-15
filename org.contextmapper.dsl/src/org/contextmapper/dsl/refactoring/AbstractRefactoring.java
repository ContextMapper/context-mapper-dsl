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
package org.contextmapper.dsl.refactoring;

import java.io.IOException;
import java.util.List;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.refactoring.henshin.Refactoring;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import com.google.common.collect.Iterators;

public abstract class AbstractRefactoring implements Refactoring {

	protected ContextMappingModel model;
	protected Resource originalResource;

	@Override
	public void doRefactor(Resource resource) {
		this.originalResource = resource;
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(resource.getAllContents(), ContextMappingModel.class));

		if (contextMappingModels.size() > 0) {
			this.model = contextMappingModels.get(0);
			doRefactor();
		}
	}

	protected abstract void doRefactor();

	protected void saveResource(Resource resource) {
		try {
			resource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
		} catch (IOException e) {
			throw new RuntimeException("Document cannot be formatted.");
		}
	}

	protected void saveResource() {
		saveResource(originalResource);
	}

}

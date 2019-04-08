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

import java.util.List;
import java.util.Set;

import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.refactoring.henshin.HenshinTransformationFileProvider;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.UnitApplication;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.interpreter.impl.UnitApplicationImpl;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class HenshinSplitBoundedContextRefactoring {

	private final String transformationsFile;

	public HenshinSplitBoundedContextRefactoring() {
		this.transformationsFile = new HenshinTransformationFileProvider().getTransformationFilePath(HenshinTransformationFileProvider.FILE_CONTEXTMAP_REFACTORINGS);
	}

	public void doRefactor(final Resource resource) {
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(resource.getAllContents(), ContextMappingModel.class));

		if (contextMappingModels.size() > 0) {
			List<String> duplicateEntities = findDuplicateEntities(contextMappingModels.get(0).getMap());
			if (duplicateEntities.isEmpty())
				throw new NoDuplicateEntityFoundException();

			// this is a proof of concept! just take the first duplicate found...
			splitBoundedContextsBySameEntityName(resource, duplicateEntities.get(0));
		}
	}

	private void splitBoundedContextsBySameEntityName(Resource resource, String entityName) {
		// Create a resource set with a base directory:
		HenshinResourceSet resourceSet = new HenshinResourceSet();

		// Load the module:
		Module module = resourceSet.getModule(transformationsFile, false);

		// Load the example model into an EGraph:
		EGraph graph = new EGraphImpl(resource);

		// Create an engine and a rule application:
		Engine engine = new EngineImpl();
		UnitApplication splitUnit = new UnitApplicationImpl(engine);
		splitUnit.setEGraph(graph);

		// Split...
		splitUnit.setUnit(module.getUnit("splitBoundedContextsBySameEntityName"));
		splitUnit.setParameterValue("entityName", entityName);
		if (!splitUnit.execute(null))
			throw new RuntimeException("Error splitting by entity '" + entityName + "' ...");

		// Saving the result:
		resourceSet.saveEObject(graph.getRoots().get(0), resource.getURI());
	}

	private List<String> findDuplicateEntities(ContextMap contextMap) {
		List<String> duplicates = Lists.newArrayList();
		Set<String> uniqueNameCheckSet = Sets.newHashSet();
		List<Entity> entities = EcoreUtil2.<Entity>getAllContentsOfType(EcoreUtil.getRootContainer(contextMap), Entity.class);
		for (Entity entity : entities) {
			if (!uniqueNameCheckSet.contains(entity.getName())) {
				uniqueNameCheckSet.add(entity.getName());
			} else {
				duplicates.add(entity.getName());
			}
		}
		return duplicates;
	}

	private class NoDuplicateEntityFoundException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public NoDuplicateEntityFoundException() {
			super("No duplicate entity found on this context map!");
		}
	}

}

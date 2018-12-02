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
package org.contextmapper.dsl.generator;

import java.util.List;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.plantuml.PlantUMLClassDiagramCreator;
import org.contextmapper.dsl.generator.plantuml.PlantUMLComponentDiagramCreator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import com.google.common.collect.Iterators;

public class PlantUMLGenerator extends AbstractGenerator {

	private static final String PLANT_UML_FILE_EXT = "puml";

	@Override
	public void doGenerate(final Resource resource, final IFileSystemAccess2 fsa, final IGeneratorContext context) {
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(resource.getAllContents(), ContextMappingModel.class));

		if (contextMappingModels.size() > 0) {
			ContextMappingModel model = contextMappingModels.get(0);

			String fileName = resource.getURI().trimFileExtension().lastSegment();
			fsa.generateFile(fileName + "_ContextMap." + PLANT_UML_FILE_EXT, new PlantUMLComponentDiagramCreator().createDiagram(model.getMap()));
			for (BoundedContext boundedContext : model.getMap().getBoundedContexts()) {
				fsa.generateFile(fileName + "_BC_" + boundedContext.getName() + "." + PLANT_UML_FILE_EXT, new PlantUMLClassDiagramCreator().createDiagram(boundedContext));
			}
		}
	}

}

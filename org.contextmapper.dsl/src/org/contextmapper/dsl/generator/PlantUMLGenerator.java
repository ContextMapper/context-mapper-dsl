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

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.generator.plantuml.PlantUMLClassDiagramCreator;
import org.contextmapper.dsl.generator.plantuml.PlantUMLComponentDiagramCreator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.generator.IFileSystemAccess2;

public class PlantUMLGenerator extends AbstractContextMapGenerator {

	private static final String PLANT_UML_FILE_EXT = "puml";

	@Override
	protected void generateFromContextMap(ContextMap contextMap, IFileSystemAccess2 fsa, URI inputFileURI) {
		String fileName = inputFileURI.trimFileExtension().lastSegment();
		fsa.generateFile(fileName + "_ContextMap." + PLANT_UML_FILE_EXT, new PlantUMLComponentDiagramCreator().createDiagram(contextMap));
		for (BoundedContext boundedContext : contextMap.getBoundedContexts()) {
			fsa.generateFile(fileName + "_BC_" + boundedContext.getName() + "." + PLANT_UML_FILE_EXT, new PlantUMLClassDiagramCreator().createDiagram(boundedContext));
		}
	}

}

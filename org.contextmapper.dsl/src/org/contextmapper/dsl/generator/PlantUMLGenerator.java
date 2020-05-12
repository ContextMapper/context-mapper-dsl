/*
 * Copyright 2018-2020 The Context Mapper Project Team
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

import java.util.Optional;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.contextmapper.dsl.generator.plantuml.PlantUMLBoundedContextClassDiagramCreator;
import org.contextmapper.dsl.generator.plantuml.PlantUMLComponentDiagramCreator;
import org.contextmapper.dsl.generator.plantuml.PlantUMLSubdomainClassDiagramCreator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.generator.IFileSystemAccess2;

public class PlantUMLGenerator extends AbstractContextMappingModelGenerator {

	private static final String PLANT_UML_FILE_EXT = "puml";

	@Override
	protected void generateFromContextMappingModel(ContextMappingModel model, IFileSystemAccess2 fsa, URI inputFileURI) {
		checkPreconditions();
		String fileName = inputFileURI.trimFileExtension().lastSegment();

		// generate component diagram, if Context Map available
		if (model.getMap() != null)
			fsa.generateFile(fileName + "_ContextMap." + PLANT_UML_FILE_EXT, new PlantUMLComponentDiagramCreator().createDiagram(model.getMap()));

		// generate class diagrams for Bounded Contexts
		for (BoundedContext boundedContext : model.getBoundedContexts()) {
			fsa.generateFile(fileName + "_BC_" + boundedContext.getName() + "." + PLANT_UML_FILE_EXT,
					new PlantUMLBoundedContextClassDiagramCreator().createDiagram(boundedContext));
		}

		// generate class diagrams for subdomains (that have entities)
		for (Domain domain : model.getDomains()) {
			domain.getSubdomains().stream().filter(subdomain -> !subdomain.getEntities().isEmpty()).forEach(subdomain -> {
				fsa.generateFile(fileName + "_SD_" + subdomain.getName() + "." + PLANT_UML_FILE_EXT,
						new PlantUMLSubdomainClassDiagramCreator(domain.getName()).createDiagram(subdomain));
			});
		}
	}

	private void checkPreconditions() {
		if (this.contextMappingModel.getMap() == null && this.contextMappingModel.getBoundedContexts().isEmpty() && !modelHasSubdomainWithEntities())
			throw new GeneratorInputException(
					"Your model does not contain a Context Map, a Bounded Context, or a Subdomain. Therefore we have nothing to generate. Create at least one of the mentioned Objects.");
	}

	private boolean modelHasSubdomainWithEntities() {
		for (Domain domain : this.contextMappingModel.getDomains()) {
			Optional<Subdomain> optSubdomain = domain.getSubdomains().stream().filter(subdomain -> !subdomain.getEntities().isEmpty()).findAny();
			return optSubdomain.isPresent();
		}
		return false;
	}

}

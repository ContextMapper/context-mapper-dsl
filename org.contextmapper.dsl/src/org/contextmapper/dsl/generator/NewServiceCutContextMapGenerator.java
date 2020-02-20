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
package org.contextmapper.dsl.generator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.contextmapper.dsl.generator.exception.NoContextMapDefinedException;
import org.contextmapper.dsl.generator.servicecutter.input.converter.ContextMappingModelToServiceCutterERDConverter;
import org.contextmapper.dsl.generator.servicecutter.output.converter.ServiceCutterOutputToContextMappingModelConverter;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import com.google.common.collect.Lists;

import ch.hsr.servicecutter.api.ServiceCutter;
import ch.hsr.servicecutter.api.ServiceCutterContext;
import ch.hsr.servicecutter.api.ServiceCutterContextBuilder;
import ch.hsr.servicecutter.api.model.EntityRelationDiagram;
import ch.hsr.servicecutter.api.model.SolverResult;
import ch.hsr.servicecutter.api.model.UserRepresentationContainer;
import ch.hsr.servicecutter.solver.SolverConfiguration;

/**
 * Uses the Service Cutter engine to calculate a new Service Cut. The new cut is
 * translated back into a new Context Map. Use this class to generate new
 * Context Maps on the basis of Service Cutter.
 * 
 * @author Stefan Kapferer
 */
public class NewServiceCutContextMapGenerator extends AbstractContextMapGenerator {

	private SolverConfiguration solverConfiguration;
	private UserRepresentationContainer userRepresentationContainer;

	/**
	 * Sets a custom {@link SolverConfiguration}. If not called, a default
	 * configuration will be created.
	 * 
	 * @param solverConfiguration the {@link SolverConfiguration} to be used to
	 *                            create the service cut.
	 */
	public NewServiceCutContextMapGenerator setSolverConfiguration(SolverConfiguration solverConfiguration) {
		this.solverConfiguration = solverConfiguration;
		return this;
	}

	/**
	 * Sets the user representations for the Service Cutter solver.
	 * 
	 * @param userRepresentationContainer the container with the user
	 *                                    representations
	 */
	public NewServiceCutContextMapGenerator setUserRepresentationContainer(UserRepresentationContainer userRepresentationContainer) {
		this.userRepresentationContainer = userRepresentationContainer;
		return this;
	}

	@Override
	protected void generateFromContextMap(ContextMap contextMap, IFileSystemAccess2 fsa, URI inputFileURI) {
		checkPreconditions(contextMappingModel);
		String fileBaseName = inputFileURI.trimFileExtension().lastSegment();

		// prepare service cutter input
		EntityRelationDiagram erdInput = new ContextMappingModelToServiceCutterERDConverter().convert(fileBaseName, contextMap);
		ServiceCutterContextBuilder contextBuilder = new ServiceCutterContextBuilder(erdInput);
		if (solverConfiguration != null)
			contextBuilder.withCustomSolverConfiguration(solverConfiguration);
		if (userRepresentationContainer != null)
			contextBuilder.withUserRepresentations(userRepresentationContainer);
		ServiceCutterContext context = contextBuilder.build();

		// calculate new service cut
		SolverResult result = new ServiceCutter(context).generateDecomposition();
		ContextMappingModel newServiceCutModel = new ServiceCutterOutputToContextMappingModelConverter().convert(result);

		// save new CML file
		int counter = 1;
		String fileName = inputFileURI.trimFileExtension().lastSegment() + "_NewCut_" + counter + ".cml";

		if (fsa.isFile(fileName)) {
			while (fsa.isFile(fileName)) {
				counter++;
				fileName = inputFileURI.trimFileExtension().lastSegment() + "_NewCut_" + counter + ".cml";
			}
		}
		Resource resource = resourceSet.createResource(URI.createURI(fileName));
		resource.getContents().add(newServiceCutModel);
		try (ByteArrayOutputStream outputstream = new ByteArrayOutputStream()) {
			resource.save(outputstream, null);
			try (InputStream inputstream = new ByteArrayInputStream(outputstream.toByteArray())) {
				fsa.generateFile(fileName, inputstream);
			}
		} catch (IOException e) {
			throw new RuntimeException("Saving CML model was not possible.", e);
		}
	}

	public void checkPreconditions(ContextMappingModel model) {
		if(model.getMap() == null)
			throw new NoContextMapDefinedException();
		
		if (collectAttributes(model).isEmpty())
			throw new GeneratorInputException(
					"Your model should at least contain one Bounded Context with entities and some attributes. Without attributes (Service Cutter nanoentities) we cannot calculate service cuts.");
	}

	private List<Attribute> collectAttributes(ContextMappingModel model) {
		List<Attribute> attributes = Lists.newArrayList();
		for(BoundedContext bc : model.getMap().getBoundedContexts()) {
			attributes.addAll(EcoreUtil2.getAllContentsOfType(bc, Attribute.class));
		}
		return attributes;
	}

}

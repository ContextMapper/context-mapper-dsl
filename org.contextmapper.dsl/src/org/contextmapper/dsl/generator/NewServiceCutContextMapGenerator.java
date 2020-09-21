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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.contextmapper.dsl.config.ServiceCutterConfigHandler;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.contextmapper.dsl.generator.servicecutter.input.converter.ContextMappingModelToServiceCutterERDConverter;
import org.contextmapper.dsl.generator.servicecutter.input.converter.SCLToUserRepresentationsConverter;
import org.contextmapper.dsl.generator.servicecutter.output.converter.ServiceCutterOutputToContextMappingModelConverter;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterUserRepresentationsModel;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import com.google.common.collect.Lists;

import ch.hsr.servicecutter.api.ServiceCutter;
import ch.hsr.servicecutter.api.ServiceCutterContext;
import ch.hsr.servicecutter.api.ServiceCutterContextBuilder;
import ch.hsr.servicecutter.api.SolverConfigurationFactory;
import ch.hsr.servicecutter.api.model.EntityRelationDiagram;
import ch.hsr.servicecutter.api.model.SolverResult;
import ch.hsr.servicecutter.api.model.UserRepresentationContainer;
import ch.hsr.servicecutter.model.solver.EntityPair;
import ch.hsr.servicecutter.scorer.Score;
import ch.hsr.servicecutter.scorer.Scorer;
import ch.hsr.servicecutter.solver.SolverConfiguration;

/**
 * Uses the Service Cutter engine to calculate a new Service Cut. The new cut is
 * translated back into a new Context Map. Use this class to generate new
 * Context Maps on the basis of Service Cutter.
 * 
 * @author Stefan Kapferer
 */
public class NewServiceCutContextMapGenerator extends AbstractContextMappingModelGenerator {

	private File projectDir;

	@Override
	protected void generateFromContextMappingModel(ContextMappingModel model, IFileSystemAccess2 fsa, URI inputFileURI) {
		checkPreconditions(model);
		String fileBaseName = inputFileURI.trimFileExtension().lastSegment();

		// prepare service cutter input
		EntityRelationDiagram erdInput = new ContextMappingModelToServiceCutterERDConverter().convert(fileBaseName, model);
		ServiceCutterContextBuilder contextBuilder = new ServiceCutterContextBuilder(erdInput);
		SolverConfiguration solverConfig = getSolverConfiguration();
		contextBuilder.withCustomSolverConfiguration(solverConfig);
		contextBuilder.withUserRepresentations(getUserRepresentations(inputFileURI));
		ServiceCutterContext context = contextBuilder.build();

		// calculate new service cut
		SolverResult result = new ServiceCutter(context).generateDecomposition();
		ContextMappingModel newServiceCutModel = new ServiceCutterOutputToContextMappingModelConverter(contextMappingModel, context, getSCLModel(inputFileURI).eResource().getURI()).convert(result);

		// save new CML file
		int counter = 1;
		String baseFileName = inputFileURI.trimFileExtension().lastSegment() + "_" + solverConfig.getAlgorithm().toString().replace(" ", "_") + "_Cut_";
		URI fileName = inputFileURI.trimFileExtension().trimSegments(1).appendSegment(baseFileName + counter).appendFileExtension("cml");

		while (resourceSet.getURIConverter().exists(fileName, null)) {
			counter++;
			fileName = inputFileURI.trimFileExtension().trimSegments(1).appendSegment(baseFileName + counter).appendFileExtension("cml");
		}
		Resource resource = resourceSet.createResource(fileName);
		resource.getContents().add(newServiceCutModel);
		try {
			resource.save(null);
		} catch (IOException e) {
			throw new RuntimeException("Saving CML model was not possible.", e);
		}

		// save scoring as graphviz DOT file
		fsa.generateFile(fileName.trimFileExtension().lastSegment() + ".gv", generateGraphvizScoringRepresentation(context));
	}

	private String generateGraphvizScoringRepresentation(ServiceCutterContext context) {
		StringBuilder sb = new StringBuilder();
		Scorer scorer = new Scorer(context.getCouplingInstances(), context.getNanoEntities());
		Map<EntityPair, Map<String, Score>> scores = scorer.getScores((final String key) -> {
			return context.getSolverConfiguration().getPriorityForCouplingCriterion(key).toValue();
		});
		sb.append("graph G {" + System.lineSeparator());
		for (Entry<EntityPair, Map<String, Score>> entry : scores.entrySet()) {
			if (entry.getKey().nanoentityA == null || entry.getKey().nanoentityB == null)
				continue;

			double score = entry.getValue().values().stream().mapToDouble(Score::getPrioritizedScore).sum();
			if (score > 0) {
				String nameA = entry.getKey().nanoentityA.getContextName();
				String nameB = entry.getKey().nanoentityB.getContextName();
				sb.append("  \"" + nameA + "\" -- \"" + nameB + "\" [weight=" + score + ",label=" + score + "]; // { ");

				List<String> scoreEntries = entry.getValue().entrySet().stream().map(
						scoreEntry -> scoreEntry.getKey() + ": " + scoreEntry.getValue().getPriority() + " * " + scoreEntry.getValue().getScore() + " = " + scoreEntry.getValue().getPrioritizedScore())
						.collect(Collectors.toList());
				sb.append(String.join(", ", scoreEntries));
				sb.append(" }");
				sb.append(System.lineSeparator());
			}
		}
		sb.append("}").append(System.lineSeparator());
		return sb.toString();
	}

	/**
	 * Sets the root directory of the project. Must be set if a .servicecutter.yml
	 * file shall be created. Otherwise the user is not able to change the Service
	 * Cutter input parameters.
	 */
	public void setProjectDirectory(File projectDir) {
		if (!projectDir.exists())
			throw new ContextMapperApplicationException("The project directory '" + projectDir.getAbsolutePath() + "' does not exist!");
		this.projectDir = projectDir;
	}

	private SolverConfiguration getSolverConfiguration() {
		if (this.projectDir != null) {
			ServiceCutterConfigHandler configHandler = new ServiceCutterConfigHandler(projectDir);
			return configHandler.getServiceCutterSolverConfiguration();
		} else {
			return new SolverConfigurationFactory().createDefaultConfiguration();
		}
	}

	private UserRepresentationContainer getUserRepresentations(URI inputFileURI) {
		updateUserRepresentations();
		return new SCLToUserRepresentationsConverter().convert(getSCLModel(inputFileURI));
	}

	private ServiceCutterUserRepresentationsModel getSCLModel(URI inputFileURI) {
		URI sclURI = inputFileURI.trimFileExtension().appendFileExtension("scl");
		Resource sclResource = resourceSet.getResource(sclURI, true);
		return (ServiceCutterUserRepresentationsModel) sclResource.getContents().get(0);
	}

	private void updateUserRepresentations() {
		new ServiceCutterUserRepresentationsGenerator().doGenerate(contextMappingModel.eResource(), fsa, context);
	}

	public void checkPreconditions(ContextMappingModel model) {
		if (collectAttributes(model).isEmpty())
			throw new GeneratorInputException(
					"Your model should at least contain one Bounded Context with entities and some attributes. Without attributes (Service Cutter nanoentities) we cannot calculate service cuts.");
	}

	private List<Attribute> collectAttributes(ContextMappingModel model) {
		List<Attribute> attributes = Lists.newArrayList();
		for (BoundedContext bc : model.getBoundedContexts()) {
			attributes.addAll(EcoreUtil2.getAllContentsOfType(bc, Attribute.class));
		}
		return attributes;
	}

}

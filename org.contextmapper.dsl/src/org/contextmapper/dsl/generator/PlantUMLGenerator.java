/*
 * Copyright 2018-2024 The Context Mapper Project Team
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
import java.util.Optional;

import org.contextmapper.dsl.cml.CMLModelDomainAndSubdomainResolver;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Flow;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.contextMappingDSL.Stakeholders;
import org.contextmapper.dsl.contextMappingDSL.UseCase;
import org.contextmapper.dsl.contextMappingDSL.UserRequirement;
import org.contextmapper.dsl.contextMappingDSL.ValueElicitation;
import org.contextmapper.dsl.contextMappingDSL.ValueRegister;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.contextmapper.dsl.generator.plantuml.PlantUMLAggregateClassDiagramCreator;
import org.contextmapper.dsl.generator.plantuml.PlantUMLBoundedContextClassDiagramCreator;
import org.contextmapper.dsl.generator.plantuml.PlantUMLComponentDiagramCreator;
import org.contextmapper.dsl.generator.plantuml.PlantUMLModuleClassDiagramCreator;
import org.contextmapper.dsl.generator.plantuml.PlantUMLStakeholderMapGenerator;
import org.contextmapper.dsl.generator.plantuml.PlantUMLStateDiagramCreator4Aggregate;
import org.contextmapper.dsl.generator.plantuml.PlantUMLStateDiagramCreator4Flow;
import org.contextmapper.dsl.generator.plantuml.PlantUMLSubdomainClassDiagramCreator;
import org.contextmapper.dsl.generator.plantuml.PlantUMLUseCaseDiagramCreator;
import org.contextmapper.dsl.generator.plantuml.PlantUMLUseCaseInteractionsSequenceDiagramCreator;
import org.contextmapper.dsl.generator.plantuml.PlantUMLValueImpactMapGenerator;
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation;
import org.contextmapper.tactic.dsl.tacticdsl.StateTransition;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import com.google.common.collect.Lists;

public class PlantUMLGenerator extends AbstractContextMappingModelGenerator {

	private static final String PLANT_UML_FILE_EXT = "puml";

	private CMLModelDomainAndSubdomainResolver subdomainResolver;

	@Override
	protected void generateFromContextMappingModel(ContextMappingModel model, IFileSystemAccess2 fsa,
			URI inputFileURI) {
		this.subdomainResolver = new CMLModelDomainAndSubdomainResolver(this.contextMappingModel);
		checkPreconditions();
		String fileName = inputFileURI.trimFileExtension().lastSegment();

		generateComponentDiagramIfContextMapAvailable(model, fsa, fileName);
		generateClassAndStateDiagramsForBoundedContexts(model, fsa, fileName);
		generateClassDiagramsForSubdomains(fsa, fileName);
		generateUseCaseDiagram(model, fsa, fileName);
		generateSequenceDiagramsForUseCases(model, fsa, fileName);
		generateStakeholderDiagrams(model, fsa, fileName);
		generateValueImpactMapsForValueRegisters(model, fsa, fileName);
	}

	private void generateStakeholderDiagrams(ContextMappingModel model, IFileSystemAccess2 fsa, String fileName) {
		int index = 1;
		for (Stakeholders stakeholders : model.getStakeholders()) {
			PlantUMLStakeholderMapGenerator stakeholderDiagramGenerator = new PlantUMLStakeholderMapGenerator();
			fsa.generateFile(fileName + "_BC_"
					+ stakeholderDiagramGenerator.getStakeholderDiagramContextName(stakeholders.getContexts())
							.replace(", ", "-").replace(" ", "-")
					+ "_StakeholderMap-" + index++ + "." + PLANT_UML_FILE_EXT,
					stakeholderDiagramGenerator.createDiagram(stakeholders));
		}
	}

	private void generateValueImpactMapsForValueRegisters(ContextMappingModel model, IFileSystemAccess2 fsa,
			String fileName) {
		for (ValueRegister valueRegister : model.getValueRegisters()) {
			if (!(valueRegister.getValueClusters().isEmpty() && valueRegister.getValues().isEmpty())
					&& !EcoreUtil2.eAllOfType(valueRegister, ValueElicitation.class).isEmpty()) {
				fsa.generateFile(
						fileName + "_ValueRegister_" + valueRegister.getName() + "_Value-Impact-Map" + "."
								+ PLANT_UML_FILE_EXT,
						new PlantUMLValueImpactMapGenerator().createDiagram(valueRegister));
			}
		}
	}

	private void generateSequenceDiagramsForUseCases(ContextMappingModel model, IFileSystemAccess2 fsa,
			String fileName) {
		for (UserRequirement userRequirement : model.getUserRequirements()) {
			if (userRequirement instanceof UseCase && !userRequirement.getFeatures().isEmpty()) {
				fsa.generateFile(
						fileName + "_UseCase_" + userRequirement.getName() + "_Interactions." + PLANT_UML_FILE_EXT,
						new PlantUMLUseCaseInteractionsSequenceDiagramCreator()
								.createDiagram((UseCase) userRequirement));
			}
		}
	}

	private void generateUseCaseDiagram(ContextMappingModel model, IFileSystemAccess2 fsa, String fileName) {
		if (!model.getUserRequirements().isEmpty())
			fsa.generateFile(fileName + "_UseCases." + PLANT_UML_FILE_EXT,
					new PlantUMLUseCaseDiagramCreator().createDiagram(model));
	}

	private void generateClassDiagramsForSubdomains(IFileSystemAccess2 fsa, String fileName) {
		subdomainResolver.resolveAllSubdomains().stream().filter(subdomain -> !subdomain.getEntities().isEmpty())
				.forEach(subdomain -> {
					fsa.generateFile(fileName + "_SD_" + subdomain.getName() + "." + PLANT_UML_FILE_EXT,
							new PlantUMLSubdomainClassDiagramCreator(
									subdomainResolver.resolveDomain4Subdomain(subdomain.getName()).getName())
									.createDiagram(subdomain));
				});
	}

	private void generateClassAndStateDiagramsForBoundedContexts(ContextMappingModel model, IFileSystemAccess2 fsa,
			String fileName) {
		for (BoundedContext boundedContext : model.getBoundedContexts()) {

			// class diagram for complete BC
			fsa.generateFile(fileName + "_BC_" + boundedContext.getName() + "." + PLANT_UML_FILE_EXT,
					new PlantUMLBoundedContextClassDiagramCreator().createDiagram(boundedContext));

			// class diagram for aggregates
			for (Aggregate aggregate : boundedContext.getAggregates()) {
				fsa.generateFile(
						fileName + "_BC_" + boundedContext.getName() + "_" + aggregate.getName() + "."
								+ PLANT_UML_FILE_EXT,
						new PlantUMLAggregateClassDiagramCreator().createDiagram(aggregate));
			}

			// class diagram for modules
			for (SculptorModule module : boundedContext.getModules()) {
				fsa.generateFile(fileName + "_BC_" + boundedContext.getName() + "_" + module.getName() + "."
						+ PLANT_UML_FILE_EXT, new PlantUMLModuleClassDiagramCreator().createDiagram(module));
			}

			// state diagram for aggregates
			List<Aggregate> aggregatesWithStates = getAggregatesWithStatesAndTransitions(boundedContext);
			for (Aggregate aggregate : aggregatesWithStates) {
				fsa.generateFile(
						fileName + "_BC_" + boundedContext.getName() + "_" + aggregate.getName() + "_StateDiagram" + "."
								+ PLANT_UML_FILE_EXT,
						new PlantUMLStateDiagramCreator4Aggregate().createDiagram(aggregate));
			}

			// state diagram for flows
			for (Flow flow : getFlowsWithStates(boundedContext)) {
				fsa.generateFile(fileName + "_BC_" + boundedContext.getName() + "_" + flow.getName() + "_StateDiagram."
						+ PLANT_UML_FILE_EXT, new PlantUMLStateDiagramCreator4Flow().createDiagram(flow));
			}
		}
	}

	private void generateComponentDiagramIfContextMapAvailable(ContextMappingModel model, IFileSystemAccess2 fsa,
			String fileName) {
		if (model.getMap() != null)
			fsa.generateFile(fileName + "_ContextMap." + PLANT_UML_FILE_EXT,
					new PlantUMLComponentDiagramCreator().createDiagram(model.getMap()));
	}

	private void checkPreconditions() {
		if (this.contextMappingModel.getMap() == null && this.contextMappingModel.getBoundedContexts().isEmpty()
				&& !modelHasSubdomainWithEntities() && this.contextMappingModel.getUserRequirements().isEmpty()
				&& this.contextMappingModel.getStakeholders().isEmpty()
				&& this.contextMappingModel.getValueRegisters().isEmpty())
			throw new GeneratorInputException(
					"Your model does not contain a) a Context Map, b) Bounded Contexts or Subdomains with domain objects (Entities, Value Objects, etc.), c) Use Cases or User Stories, or d) Stakeholders or Value Registers. Create at least one of the mentioned model elements.");
	}

	private List<Flow> getFlowsWithStates(BoundedContext bc) {
		List<Flow> flows = Lists.newLinkedList();
		if (bc.getApplication() != null) {
			for (Flow flow : bc.getApplication().getFlows()) {
				if (!EcoreUtil2.eAllOfType(flow, StateTransition.class).isEmpty())
					flows.add(flow);
			}
		}
		return flows;
	}

	private List<Aggregate> getAggregatesWithStatesAndTransitions(BoundedContext bc) {
		List<Aggregate> aggregates = Lists.newLinkedList();
		for (Aggregate aggregate : EcoreUtil2.eAllOfType(bc, Aggregate.class)) {
			Optional<org.contextmapper.tactic.dsl.tacticdsl.Enum> statesEnum = EcoreUtil2
					.eAllOfType(aggregate, org.contextmapper.tactic.dsl.tacticdsl.Enum.class).stream()
					.filter(e -> e.isDefinesAggregateLifecycle()).findFirst();
			if (!statesEnum.isPresent())
				continue;
			List<StateTransition> stateTransitions = EcoreUtil2.eAllOfType(aggregate, StateTransition.class);
			if (!stateTransitions.isEmpty())
				aggregates.add(aggregate);
		}
		return aggregates;
	}

	private boolean modelHasSubdomainWithEntities() {
		return subdomainResolver.resolveAllSubdomains().stream().filter(subdomain -> !subdomain.getEntities().isEmpty())
				.findAny().isPresent();
	}

}

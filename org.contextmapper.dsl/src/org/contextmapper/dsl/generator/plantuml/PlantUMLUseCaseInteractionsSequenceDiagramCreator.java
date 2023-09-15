/*
 * Copyright 2023 The Context Mapper Project Team
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
package org.contextmapper.dsl.generator.plantuml;

import org.contextmapper.dsl.contextMappingDSL.Feature;
import org.contextmapper.dsl.contextMappingDSL.UseCase;

public class PlantUMLUseCaseInteractionsSequenceDiagramCreator extends AbstractPlantUMLDiagramCreator<UseCase>
		implements PlantUMLDiagramCreator<UseCase> {

	private UseCase useCase;

	private static final String SYSTEM_NAME = "System";

	@Override
	protected void printDiagramContent(final UseCase useCase) {
		this.useCase = useCase;

		printStartOfGroup();

		printPrimaryActorUsingSystem();
		printInteractions();
		printSecondaryActorsTriggeredBySystem();

		printEndOfGroup();
		printUseCaseBenefit();
		printDisclaimerNote();
	}

	private void printStartOfGroup() {
		sb.append("group ").append("Use Case ").append(useCase.getName());
		linebreak();
	}

	private void printEndOfGroup() {
		sb.append("end");
		linebreak();
	}

	private void printPrimaryActorUsingSystem() {
		if (useCase.getRole() != null && !"".equals(useCase.getRole())) {
			Feature firstFeature = useCase.getFeatures().get(0);
			sb.append("\"" + useCase.getRole() + "\"").append(" -> ").append(SYSTEM_NAME).append(" : ")
					.append(firstFeature.getVerb()).append(" ").append(firstFeature.getEntity());
			linebreak();
		}
		linebreak();
	}

	private void printInteractions() {
		int initialInteraction = (useCase.getRole() != null && !"".equals(useCase.getRole())) ? 1 : 0;
		for (int i = initialInteraction; i < useCase.getFeatures().size(); i++) {
			Feature feature = useCase.getFeatures().get(i);
			sb.append(SYSTEM_NAME).append(" -> ").append(SYSTEM_NAME).append(" : ").append(feature.getVerb())
					.append(" ").append(feature.getEntity());
			linebreak();
		}
		linebreak();
	}

	private void printSecondaryActorsTriggeredBySystem() {
		if (!useCase.getSecondaryActors().isEmpty()) {
			for (String secondaryActor : useCase.getSecondaryActors()) {
				sb.append(SYSTEM_NAME).append(" -> ").append("\"" + secondaryActor + "\"");
				linebreak();
			}
		}
		linebreak();
	}

	private void printUseCaseBenefit() {
		if (useCase.getBenefit() != null && !"".equals(useCase.getBenefit())) {
			sb.append("note over " + SYSTEM_NAME + " : ").append(useCase.getBenefit());
			linebreak();
		}
		linebreak();
	}

	private void printDisclaimerNote() {
		sb.append("note right").append(System.lineSeparator())
				.append("  Note: This diagram only illustrates the interactions of the").append(System.lineSeparator())
				.append("  use case in an early analysis state, as modelled in CML.").append(System.lineSeparator())
				.append("  Hence, some interactions might not be triggered/connected").append(System.lineSeparator())
				.append("  by the correct actor(s) (known limitation).").append(System.lineSeparator());
		sb.append("end note").append(System.lineSeparator());
		linebreak();
	}

}

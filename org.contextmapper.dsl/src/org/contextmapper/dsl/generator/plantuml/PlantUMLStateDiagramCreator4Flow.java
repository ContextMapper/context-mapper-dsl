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
package org.contextmapper.dsl.generator.plantuml;

import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.DomainEventProductionStep;
import org.contextmapper.dsl.contextMappingDSL.Flow;
import org.contextmapper.tactic.dsl.tacticdsl.StateTransition;

public class PlantUMLStateDiagramCreator4Flow extends AbstractPlantUMLStateDiagramCreator<Flow> implements PlantUMLDiagramCreator<Flow> {

	@Override
	protected void printDiagramContent(Flow flow) {
		List<DomainEventProductionStep> eventProductionStepsWithStateTransitions = flow.getSteps().stream().filter(s -> s instanceof DomainEventProductionStep)
				.map(s -> (DomainEventProductionStep) s).filter(s -> s.getStateTransition() != null).collect(Collectors.toList());
		List<StateTransition> allStateTransitions = eventProductionStepsWithStateTransitions.stream().map(s -> s.getStateTransition()).collect(Collectors.toList());
		for (String state : collectStates(allStateTransitions)) {
			printState(state);
		}
		for (DomainEventProductionStep step : eventProductionStepsWithStateTransitions)
			printTransition(step.getStateTransition(), step.getAction().getCommand() != null ? step.getAction().getCommand().getName() : step.getAction().getOperation().getName());
		printEndTransitions(allStateTransitions);
		linebreak(2);
		sb.append("legend top center");
		linebreak();
		sb.append("  " + flow.getName() + " State Transitions");
		linebreak();
		sb.append("endlegend");
	}

}

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

import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Flow;
import org.contextmapper.tactic.dsl.tacticdsl.StateTransition;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Sets;

public class PlantUMLStateDiagramCreator4Flow extends AbstractPlantUMLStateDiagramCreator<Flow> implements PlantUMLDiagramCreator<Flow> {

	@Override
	protected void printDiagramContent(Flow flow) {
		for (String state : collectStates(flow)) {
			printState(state);
		}
		for (StateTransition transition : EcoreUtil2.eAllOfType(flow, StateTransition.class))
			printTransition(transition);
	}

	private Set<String> collectStates(Flow flow) {
		Set<String> states = Sets.newHashSet();
		for (StateTransition transition : EcoreUtil2.eAllOfType(flow, StateTransition.class)) {
			if (transition.getFrom() != null && !transition.getFrom().isEmpty())
				states.addAll(transition.getFrom().stream().map(s -> s.getName()).collect(Collectors.toSet()));
			states.addAll(transition.getTarget().getTo().stream().map(s -> s.getName()).collect(Collectors.toSet()));
		}
		return states;
	}

}

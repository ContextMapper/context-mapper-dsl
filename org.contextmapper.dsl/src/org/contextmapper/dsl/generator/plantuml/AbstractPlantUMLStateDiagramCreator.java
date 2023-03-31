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
package org.contextmapper.dsl.generator.plantuml;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.tactic.dsl.tacticdsl.EnumValue;
import org.contextmapper.tactic.dsl.tacticdsl.StateTransition;
import org.contextmapper.tactic.dsl.tacticdsl.TargetState;
import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

abstract public class AbstractPlantUMLStateDiagramCreator<T extends EObject> extends AbstractPlantUMLDiagramCreator<T> {

	protected void printState(String state) {
		sb.append(state + " : ");
		linebreak();
	}

	protected void printTransition(final StateTransition transition, final String label) {
		if (transition.getFrom() == null || transition.getFrom().isEmpty()) {
			for (String target : mapStatesToStrings(
					transition.getTarget().getTo().stream().map(s -> s.getValue()).collect(Collectors.toList()))) {
				sb.append("[*] --> ").append(target).append(" : ").append(label);
				linebreak();
			}
		}
		for (String from : transition.getFrom().stream().map(s -> s.getName()).collect(Collectors.toSet())) {
			for (String target : mapStatesToStrings(
					transition.getTarget().getTo().stream().map(s -> s.getValue()).collect(Collectors.toList()))) {
				sb.append(from).append(" --> ").append(target).append(" : ").append(label);
				linebreak();
			}
		}
	}

	protected void printEndTransitions(final List<StateTransition> allStateTransitions) {
		final Set<String> allEndStates = Sets.newHashSet();
		allStateTransitions.forEach(t -> {
			allEndStates.addAll(t.getTarget().getTo().stream().filter(to -> to.isEndState())
					.map(to -> to.getValue().getName()).collect(Collectors.toSet()));
		});
		allEndStates.stream().forEach(t -> {
			sb.append(t).append(" --> [*]");
			linebreak();
		});
	}

	protected Set<String> collectStates(final List<StateTransition> transitions) {
		Set<String> states = Sets.newHashSet();
		for (StateTransition transition : transitions) {
			states.addAll(mapStatesToStrings(transition.getFrom()));
			states.addAll(mapStatesToStrings(
					transition.getTarget().getTo().stream().map(s -> s.getValue()).collect(Collectors.toList())));
		}
		return states;
	}

	private Set<String> mapStatesToStrings(final List<EnumValue> states) {
		if (states != null && !states.isEmpty())
			return states.stream().map(s -> s.getName()).collect(Collectors.toSet());
		return Sets.newHashSet();
	}

}

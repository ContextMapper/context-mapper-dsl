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

import java.util.stream.Collectors;

import org.contextmapper.tactic.dsl.tacticdsl.StateTransition;
import org.eclipse.emf.ecore.EObject;

abstract public class AbstractPlantUMLStateDiagramCreator<T extends EObject> extends AbstractPlantUMLDiagramCreator<T> {

	protected void printState(String state) {
		sb.append(state + " : ");
		linebreak();
	}

	protected void printTransition(StateTransition transition) {
		if (transition.getFrom() == null || transition.getFrom().isEmpty()) {
			for (String target : transition.getTarget().getTo().stream().map(s -> s.getName()).collect(Collectors.toSet())) {
				sb.append("[*] --> ").append(target);
				linebreak();
			}
		}
		for (String from : transition.getFrom().stream().map(s -> s.getName()).collect(Collectors.toSet())) {
			for (String target : transition.getTarget().getTo().stream().map(s -> s.getName()).collect(Collectors.toSet())) {
				sb.append(from).append(" --> ").append(target);
				linebreak();
			}
		}
	}

}

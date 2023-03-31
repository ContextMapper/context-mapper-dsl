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

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObjectOperation;
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation;
import org.contextmapper.tactic.dsl.tacticdsl.StateTransition;
import org.eclipse.xtext.EcoreUtil2;

public class PlantUMLStateDiagramCreator4Aggregate extends AbstractPlantUMLStateDiagramCreator<Aggregate> implements PlantUMLDiagramCreator<Aggregate> {

	@Override
	protected void printDiagramContent(Aggregate aggregate) {
		List<ServiceOperation> serviceOperationsWithStateTransitions = EcoreUtil2.eAllOfType(aggregate, ServiceOperation.class).stream().filter(o -> o.getStateTransition() != null)
				.collect(Collectors.toList());
		List<DomainObjectOperation> domainObjectOperationsWithStateTransitions = EcoreUtil2.eAllOfType(aggregate, DomainObjectOperation.class).stream()
				.filter(o -> o.getStateTransition() != null).collect(Collectors.toList());
		List<StateTransition> allStateTransitions = serviceOperationsWithStateTransitions.stream().map(o -> o.getStateTransition()).collect(Collectors.toList());
		allStateTransitions.addAll(domainObjectOperationsWithStateTransitions.stream().map(o -> o.getStateTransition()).collect(Collectors.toList()));
		for (String state : collectStates(allStateTransitions)) {
			printState(state);
		}
		for (ServiceOperation operation : serviceOperationsWithStateTransitions)
			printTransition(operation.getStateTransition(), operation.getName());
		for (DomainObjectOperation operation : domainObjectOperationsWithStateTransitions)
			printTransition(operation.getStateTransition(), operation.getName());
		printEndTransitions(allStateTransitions);
		linebreak(2);
		sb.append("legend top center");
		linebreak();
		sb.append("  " + aggregate.getName() + " Aggregate Lifecycle");
		linebreak();
		sb.append("endlegend");

	}

}

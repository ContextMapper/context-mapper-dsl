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

import org.contextmapper.dsl.contextMappingDSL.Flow;
import org.contextmapper.tactic.dsl.tacticdsl.StateTransition;
import org.eclipse.xtext.EcoreUtil2;

public class PlantUMLStateDiagramCreator4Flow extends AbstractPlantUMLStateDiagramCreator<Flow> implements PlantUMLDiagramCreator<Flow> {

	@Override
	protected void printDiagramContent(Flow flow) {
		for (String state : collectStates(EcoreUtil2.eAllOfType(flow, StateTransition.class))) {
			printState(state);
		}
		for (StateTransition transition : EcoreUtil2.eAllOfType(flow, StateTransition.class))
			printTransition(transition);
	}

}

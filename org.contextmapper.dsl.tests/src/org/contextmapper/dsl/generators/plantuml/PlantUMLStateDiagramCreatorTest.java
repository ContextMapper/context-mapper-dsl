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
package org.contextmapper.dsl.generators.plantuml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Flow;
import org.contextmapper.dsl.generator.plantuml.PlantUMLStateDiagramCreator4Aggregate;
import org.contextmapper.dsl.generator.plantuml.PlantUMLStateDiagramCreator4Flow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlantUMLStateDiagramCreatorTest extends AbstractCMLInputFileTest {

	@BeforeEach
	public void prepare() {
		super.prepare();
	}

	@Test
	public void canCreateStateDiagram4FlowDefinition() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("state-diagram-generation-flow-test.cml").getContextMappingModel();

		// when
		Flow flow = model.getBoundedContexts().get(0).getApplication().getFlows().get(0);
		String stateDiagram = new PlantUMLStateDiagramCreator4Flow().createDiagram(flow);

		// then
		assertEquals("@startuml\n"
				+ "\n"
				+ "skinparam componentStyle uml2\n"
				+ "\n"
				+ "RECEIVED : \n"
				+ "ACCEPTED : \n"
				+ "POLICY_CREATED : \n"
				+ "EXPIRED : \n"
				+ "SUBMITTED : \n"
				+ "REJECTED : \n"
				+ "[*] --> SUBMITTED\n"
				+ "SUBMITTED --> RECEIVED\n"
				+ "SUBMITTED --> REJECTED\n"
				+ "RECEIVED --> ACCEPTED\n"
				+ "RECEIVED --> EXPIRED\n"
				+ "RECEIVED --> REJECTED\n"
				+ "ACCEPTED --> POLICY_CREATED\n"
				+ "ACCEPTED --> EXPIRED\n"
				+ "\n"
				+ "\n"
				+ "@enduml\n" + "", stateDiagram);
	}

	@Test
	public void canCreateStateDiagram4Aggregate() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("state-diagram-generation-aggregate-test.cml").getContextMappingModel();

		// when
		Aggregate aggregate = model.getBoundedContexts().get(0).getAggregates().get(0);
		String stateDiagram = new PlantUMLStateDiagramCreator4Aggregate().createDiagram(aggregate);

		// then
		assertEquals("@startuml\n"
				+ "\n"
				+ "skinparam componentStyle uml2\n"
				+ "\n"
				+ "QUOTE_EXPIRED : \n"
				+ "REQUEST_SUBMITTED : \n"
				+ "QUOTE_ACCEPTED : \n"
				+ "POLICY_CREATED : \n"
				+ "QUOTE_RECEIVED : \n"
				+ "QUOTE_REJECTED : \n"
				+ "REQUEST_REJECTED : \n"
				+ "[*] --> REQUEST_SUBMITTED\n"
				+ "REQUEST_SUBMITTED --> REQUEST_REJECTED\n"
				+ "REQUEST_SUBMITTED --> QUOTE_RECEIVED\n"
				+ "QUOTE_RECEIVED --> QUOTE_EXPIRED\n"
				+ "QUOTE_RECEIVED --> QUOTE_ACCEPTED\n"
				+ "QUOTE_RECEIVED --> QUOTE_REJECTED\n"
				+ "QUOTE_ACCEPTED --> QUOTE_EXPIRED\n"
				+ "QUOTE_ACCEPTED --> POLICY_CREATED\n"
				+ "\n"
				+ "\n"
				+ "@enduml\n"
				+ "", stateDiagram);
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/generators/plantuml/";
	}

}

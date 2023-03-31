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
		assertEquals("@startuml" + System.lineSeparator()
				+ System.lineSeparator()
				+ "skinparam componentStyle uml2" + System.lineSeparator()
				+ System.lineSeparator()
				+ "RECEIVED : " + System.lineSeparator()
				+ "ACCEPTED : " + System.lineSeparator()
				+ "POLICY_CREATED : " + System.lineSeparator()
				+ "EXPIRED : " + System.lineSeparator()
				+ "SUBMITTED : " + System.lineSeparator()
				+ "REJECTED : " + System.lineSeparator()
				+ "[*] --> SUBMITTED : SubmitRequest" + System.lineSeparator()
				+ "SUBMITTED --> RECEIVED : checkRequest" + System.lineSeparator()
				+ "SUBMITTED --> REJECTED : checkRequest" + System.lineSeparator()
				+ "RECEIVED --> ACCEPTED : receiveAndCheckQuote" + System.lineSeparator()
				+ "RECEIVED --> EXPIRED : receiveAndCheckQuote" + System.lineSeparator()
				+ "RECEIVED --> REJECTED : receiveAndCheckQuote" + System.lineSeparator()
				+ "ACCEPTED --> POLICY_CREATED : accept" + System.lineSeparator()
				+ "ACCEPTED --> EXPIRED : accept" + System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "legend top center" + System.lineSeparator()
				+ "  QuoteFlow State Transitions" + System.lineSeparator()
				+ "endlegend"
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "@enduml" + System.lineSeparator() + "", stateDiagram);
	}
	
	@Test
	public void canCreateStateDiagram4FlowDefinitionAndRespectEndStates() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("state-diagram-generation-flow-with-end-states-test.cml").getContextMappingModel();

		// when
		Flow flow = model.getBoundedContexts().get(0).getApplication().getFlows().get(0);
		String stateDiagram = new PlantUMLStateDiagramCreator4Flow().createDiagram(flow);

		// then
		assertEquals("@startuml" + System.lineSeparator()
				+ System.lineSeparator()
				+ "skinparam componentStyle uml2" + System.lineSeparator()
				+ System.lineSeparator()
				+ "RECEIVED : " + System.lineSeparator()
				+ "ACCEPTED : " + System.lineSeparator()
				+ "POLICY_CREATED : " + System.lineSeparator()
				+ "EXPIRED : " + System.lineSeparator()
				+ "SUBMITTED : " + System.lineSeparator()
				+ "REJECTED : " + System.lineSeparator()
				+ "[*] --> SUBMITTED : SubmitRequest" + System.lineSeparator()
				+ "SUBMITTED --> RECEIVED : checkRequest" + System.lineSeparator()
				+ "SUBMITTED --> REJECTED : checkRequest" + System.lineSeparator()
				+ "RECEIVED --> ACCEPTED : receiveAndCheckQuote" + System.lineSeparator()
				+ "RECEIVED --> EXPIRED : receiveAndCheckQuote" + System.lineSeparator()
				+ "RECEIVED --> REJECTED : receiveAndCheckQuote" + System.lineSeparator()
				+ "ACCEPTED --> POLICY_CREATED : accept" + System.lineSeparator()
				+ "ACCEPTED --> EXPIRED : accept" + System.lineSeparator()
				+ "POLICY_CREATED --> [*]" + System.lineSeparator()
				+ "EXPIRED --> [*]" + System.lineSeparator()
				+ "REJECTED --> [*]" + System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "legend top center" + System.lineSeparator()
				+ "  QuoteFlow State Transitions" + System.lineSeparator()
				+ "endlegend"
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "@enduml" + System.lineSeparator() + "", stateDiagram);
	}

	@Test
	public void canCreateStateDiagram4AggregateViaService() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("state-diagram-generation-aggregate-test-1.cml").getContextMappingModel();

		// when
		Aggregate aggregate = model.getBoundedContexts().get(0).getAggregates().get(0);
		String stateDiagram = new PlantUMLStateDiagramCreator4Aggregate().createDiagram(aggregate);

		// then
		assertEquals("@startuml" + System.lineSeparator()
				+ System.lineSeparator()
				+ "skinparam componentStyle uml2" + System.lineSeparator()
				+ System.lineSeparator()
				+ "QUOTE_EXPIRED : " + System.lineSeparator()
				+ "REQUEST_SUBMITTED : " + System.lineSeparator()
				+ "QUOTE_ACCEPTED : " + System.lineSeparator()
				+ "POLICY_CREATED : " + System.lineSeparator()
				+ "QUOTE_RECEIVED : " + System.lineSeparator()
				+ "QUOTE_REJECTED : " + System.lineSeparator()
				+ "REQUEST_REJECTED : " + System.lineSeparator()
				+ "[*] --> REQUEST_SUBMITTED : submitRequest" + System.lineSeparator()
				+ "REQUEST_SUBMITTED --> REQUEST_REJECTED : rejectRequest" + System.lineSeparator()
				+ "REQUEST_SUBMITTED --> QUOTE_RECEIVED : receiveQuote" + System.lineSeparator()
				+ "QUOTE_RECEIVED --> QUOTE_EXPIRED : checkQuote" + System.lineSeparator()
				+ "QUOTE_RECEIVED --> QUOTE_ACCEPTED : checkQuote" + System.lineSeparator()
				+ "QUOTE_RECEIVED --> QUOTE_REJECTED : checkQuote" + System.lineSeparator()
				+ "QUOTE_ACCEPTED --> QUOTE_EXPIRED : createPolicy" + System.lineSeparator()
				+ "QUOTE_ACCEPTED --> POLICY_CREATED : createPolicy" + System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "legend top center" + System.lineSeparator()
				+ "  QuoteRequest Aggregate Lifecycle" + System.lineSeparator()
				+ "endlegend"
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "@enduml" + System.lineSeparator()
				+ "", stateDiagram);
	}
	
	@Test
	public void canCreateStateDiagram4AggregateAndRespectEndStates() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("class-diagram-generation-aggregate-end-state-test-1.cml").getContextMappingModel();

		// when
		Aggregate aggregate = model.getBoundedContexts().get(0).getAggregates().get(0);
		String stateDiagram = new PlantUMLStateDiagramCreator4Aggregate().createDiagram(aggregate);

		// then
		assertEquals("@startuml" + System.lineSeparator()
				+ System.lineSeparator()
				+ "skinparam componentStyle uml2" + System.lineSeparator()
				+ System.lineSeparator()
				+ "QUOTE_EXPIRED : " + System.lineSeparator()
				+ "REQUEST_SUBMITTED : " + System.lineSeparator()
				+ "QUOTE_ACCEPTED : " + System.lineSeparator()
				+ "POLICY_CREATED : " + System.lineSeparator()
				+ "QUOTE_RECEIVED : " + System.lineSeparator()
				+ "QUOTE_REJECTED : " + System.lineSeparator()
				+ "REQUEST_REJECTED : " + System.lineSeparator()
				+ "[*] --> REQUEST_SUBMITTED : submitRequest" + System.lineSeparator()
				+ "REQUEST_SUBMITTED --> REQUEST_REJECTED : rejectRequest" + System.lineSeparator()
				+ "REQUEST_SUBMITTED --> QUOTE_RECEIVED : receiveQuote" + System.lineSeparator()
				+ "QUOTE_RECEIVED --> QUOTE_EXPIRED : checkQuote" + System.lineSeparator()
				+ "QUOTE_RECEIVED --> QUOTE_ACCEPTED : checkQuote" + System.lineSeparator()
				+ "QUOTE_RECEIVED --> QUOTE_REJECTED : checkQuote" + System.lineSeparator()
				+ "QUOTE_ACCEPTED --> QUOTE_EXPIRED : createPolicy" + System.lineSeparator()
				+ "QUOTE_ACCEPTED --> POLICY_CREATED : createPolicy" + System.lineSeparator()
				+ "QUOTE_EXPIRED --> [*]" + System.lineSeparator()
				+ "POLICY_CREATED --> [*]" + System.lineSeparator()
				+ "QUOTE_REJECTED --> [*]" + System.lineSeparator()
				+ "REQUEST_REJECTED --> [*]" + System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "legend top center" + System.lineSeparator()
				+ "  QuoteRequest Aggregate Lifecycle" + System.lineSeparator()
				+ "endlegend"
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "@enduml" + System.lineSeparator()
				+ "", stateDiagram);
	}
	
	@Test
	public void canCreateStateDiagram4AggregateViaEntity() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("state-diagram-generation-aggregate-test-2.cml").getContextMappingModel();

		// when
		Aggregate aggregate = model.getBoundedContexts().get(0).getAggregates().get(0);
		String stateDiagram = new PlantUMLStateDiagramCreator4Aggregate().createDiagram(aggregate);

		// then
		assertEquals("@startuml" + System.lineSeparator()
				+ System.lineSeparator()
				+ "skinparam componentStyle uml2" + System.lineSeparator()
				+ System.lineSeparator()
				+ "QUOTE_EXPIRED : " + System.lineSeparator()
				+ "REQUEST_SUBMITTED : " + System.lineSeparator()
				+ "QUOTE_ACCEPTED : " + System.lineSeparator()
				+ "POLICY_CREATED : " + System.lineSeparator()
				+ "QUOTE_RECEIVED : " + System.lineSeparator()
				+ "QUOTE_REJECTED : " + System.lineSeparator()
				+ "REQUEST_REJECTED : " + System.lineSeparator()
				+ "[*] --> REQUEST_SUBMITTED : submitRequest" + System.lineSeparator()
				+ "REQUEST_SUBMITTED --> REQUEST_REJECTED : rejectRequest" + System.lineSeparator()
				+ "REQUEST_SUBMITTED --> QUOTE_RECEIVED : receiveQuote" + System.lineSeparator()
				+ "QUOTE_RECEIVED --> QUOTE_EXPIRED : checkQuote" + System.lineSeparator()
				+ "QUOTE_RECEIVED --> QUOTE_ACCEPTED : checkQuote" + System.lineSeparator()
				+ "QUOTE_RECEIVED --> QUOTE_REJECTED : checkQuote" + System.lineSeparator()
				+ "QUOTE_ACCEPTED --> QUOTE_EXPIRED : createPolicy" + System.lineSeparator()
				+ "QUOTE_ACCEPTED --> POLICY_CREATED : createPolicy" + System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "legend top center" + System.lineSeparator()
				+ "  QuoteRequest Aggregate Lifecycle" + System.lineSeparator()
				+ "endlegend"
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "@enduml" + System.lineSeparator()
				+ "", stateDiagram);
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/generators/plantuml/";
	}

}

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
package org.contextmapper.dsl.generators.plantuml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.plantuml.PlantUMLUseCaseDiagramCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlantUMLUseCaseDiagramCreatorTest extends AbstractCMLInputFileTest {

	@BeforeEach
	public void prepare() {
		super.prepare();
	}

	@Test
	public void canCreateUseCaseDiagram() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("use-case-diagram-generation-test-1.cml").getContextMappingModel();

		// when
		String useCaseDiagram = new PlantUMLUseCaseDiagramCreator().createDiagram(model);

		// then
		assertEquals("@startuml" + System.lineSeparator()
		 		+ System.lineSeparator()
				+ "skinparam componentStyle uml2" + System.lineSeparator()
				+ System.lineSeparator()
				+ "\"Get_paid_for_car_accident\" as (Get_paid_for_car_accident)" + System.lineSeparator()
				+ "\"Just_do_something\" as (Just_do_something)" + System.lineSeparator()
				+ "\"Order_Offer_for_new_Insurance\" as (Order_Offer_for_new_Insurance)" + System.lineSeparator()
				+ System.lineSeparator()
				+ "\"Claimant\" as Actor_0" + System.lineSeparator()
				+ "\"Chiller\" as Actor_1" + System.lineSeparator()
				+ System.lineSeparator()
				+ "Actor_0 --> Get_paid_for_car_accident" + System.lineSeparator()
				+ "Actor_0 --> Order_Offer_for_new_Insurance" + System.lineSeparator()
				+ "Actor_1 --> Just_do_something" + System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "@enduml" + System.lineSeparator(), useCaseDiagram);
	}
	
	@Test
	public void canCreateUseCaseDiagramWithSecondaryActors() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("use-case-diagram-generation-test-2.cml").getContextMappingModel();

		// when
		String useCaseDiagram = new PlantUMLUseCaseDiagramCreator().createDiagram(model);

		// then
		assertEquals("@startuml" + System.lineSeparator()
				+ System.lineSeparator()
				+ "skinparam componentStyle uml2" + System.lineSeparator()
				+ System.lineSeparator()
				+ "\"Get_paid_for_car_accident\" as (Get_paid_for_car_accident)" + System.lineSeparator()
				+ "\"Just_do_something\" as (Just_do_something)" + System.lineSeparator()
				+ "\"Order_Offer_for_new_Insurance\" as (Order_Offer_for_new_Insurance)" + System.lineSeparator()
				+ System.lineSeparator()
				+ "\"Claimant\" as Actor_0" + System.lineSeparator()
				+ "\"Chiller\" as Actor_1" + System.lineSeparator()
				+ "\"YetAnotherActor\" as Actor_2" + System.lineSeparator()
				+ System.lineSeparator()
				+ "Actor_0 --> Get_paid_for_car_accident" + System.lineSeparator()
				+ "Actor_0 --> Order_Offer_for_new_Insurance" + System.lineSeparator()
				+ "Actor_1 --> Get_paid_for_car_accident" + System.lineSeparator()
				+ "Actor_1 --> Just_do_something" + System.lineSeparator()
				+ "Actor_2 --> Get_paid_for_car_accident" + System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "@enduml" + System.lineSeparator(), useCaseDiagram);
	}
	
	@Test
	public void canCreateUseCaseDiagramForMixedUseCasesAndUserStories() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("use-case-diagram-generation-test-3.cml").getContextMappingModel();

		// when
		String useCaseDiagram = new PlantUMLUseCaseDiagramCreator().createDiagram(model);

		// then
		assertEquals("@startuml" + System.lineSeparator()
		 		+ System.lineSeparator()
				+ "skinparam componentStyle uml2" + System.lineSeparator()
				+ System.lineSeparator()
				+ "\"Get_paid_for_car_accident\" as (Get_paid_for_car_accident)" + System.lineSeparator()
				+ "\"Just_do_something\" as (Just_do_something)" + System.lineSeparator()
				+ "\"Order_Offer_for_new_Insurance\" as (Order_Offer_for_new_Insurance)" + System.lineSeparator()
				+ System.lineSeparator()
				+ "\"Claimant\" as Actor_0" + System.lineSeparator()
				+ "\"Chiller\" as Actor_1" + System.lineSeparator()
				+ System.lineSeparator()
				+ "Actor_0 --> Get_paid_for_car_accident" + System.lineSeparator()
				+ "Actor_0 --> Order_Offer_for_new_Insurance" + System.lineSeparator()
				+ "Actor_1 --> Just_do_something" + System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "@enduml" + System.lineSeparator(), useCaseDiagram);
	}
	
	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/generators/plantuml/";
	}

}

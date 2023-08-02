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
import org.contextmapper.dsl.contextMappingDSL.UseCase;
import org.contextmapper.dsl.generator.plantuml.PlantUMLUseCaseInteractionsSequenceDiagramCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlantUMLUseCaseInteractionsSequenceDiagramCreatorTest extends AbstractCMLInputFileTest {

	@BeforeEach
	public void prepare() {
		super.prepare();
	}

	@Test
	public void canCreateSequenceDiagram4UseCaseInteractions() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("use-case-interactions-sequence-diagram-generation-test-1.cml").getContextMappingModel();

		// when
		String useCaseDiagram = new PlantUMLUseCaseInteractionsSequenceDiagramCreator().createDiagram((UseCase) model.getUserRequirements().get(0));

		// then
		assertEquals("@startuml" + System.lineSeparator()
				+ System.lineSeparator()
				+ "skinparam componentStyle uml2" + System.lineSeparator()
				+ System.lineSeparator()
				+ "group Use Case Get_paid_for_car_accident" + System.lineSeparator()
				+ "\"Claimant\" -> System : submit Claim" + System.lineSeparator()
				+ System.lineSeparator()
				+ "System -> System : verifyExistanceOf Policy" + System.lineSeparator()
				+ "System -> System : assign Agent" + System.lineSeparator()
				+ "System -> System : verify Policy" + System.lineSeparator()
				+ "System -> System : pay Claimant" + System.lineSeparator()
				+ "System -> System : close Claim" + System.lineSeparator()
				+ System.lineSeparator()
				+ "System -> \"Insurance Employee\"" + System.lineSeparator()
				+ System.lineSeparator()
				+ "end" + System.lineSeparator()
				+ "note over System : A claimant submits a claim and and gets paid from the insurance company." + System.lineSeparator()
				+ System.lineSeparator()
				+ "note right" + System.lineSeparator()
				+ "  Note: This diagram only illustrates the interactions of the" + System.lineSeparator()
				+ "  use case in an early analysis state, as modelled in CML." + System.lineSeparator()
				+ "  Hence, some interactions might not be triggered/connected" + System.lineSeparator()
				+ "  by the correct actor(s) (known limitation)." + System.lineSeparator()
				+ "end note" + System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "@enduml" + System.lineSeparator(), useCaseDiagram);
	}
	
	@Test
	public void canCreateSequenceDiagramWithoutPrimaryActor() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("use-case-interactions-sequence-diagram-generation-test-2.cml").getContextMappingModel();

		// when
		String useCaseDiagram = new PlantUMLUseCaseInteractionsSequenceDiagramCreator().createDiagram((UseCase) model.getUserRequirements().get(0));

		// then
		assertEquals("@startuml" + System.lineSeparator()
				+ System.lineSeparator()
				+ "skinparam componentStyle uml2" + System.lineSeparator()
				+ System.lineSeparator()
				+ "group Use Case Get_paid_for_car_accident" + System.lineSeparator()
				+ System.lineSeparator()
				+ "System -> System : submit Claim" + System.lineSeparator()
				+ "System -> System : verifyExistanceOf Policy" + System.lineSeparator()
				+ "System -> System : assign Agent" + System.lineSeparator()
				+ "System -> System : verify Policy" + System.lineSeparator()
				+ "System -> System : pay Claimant" + System.lineSeparator()
				+ "System -> System : close Claim" + System.lineSeparator()
				+ System.lineSeparator()
				+ "System -> \"Insurance Employee\"" + System.lineSeparator()
				+ System.lineSeparator()
				+ "end" + System.lineSeparator()
				+ "note over System : A claimant submits a claim and and gets paid from the insurance company." + System.lineSeparator()
				+ System.lineSeparator()
				+ "note right" + System.lineSeparator()
				+ "  Note: This diagram only illustrates the interactions of the" + System.lineSeparator()
				+ "  use case in an early analysis state, as modelled in CML." + System.lineSeparator()
				+ "  Hence, some interactions might not be triggered/connected" + System.lineSeparator()
				+ "  by the correct actor(s) (known limitation)." + System.lineSeparator()
				+ "end note" + System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "@enduml" + System.lineSeparator(), useCaseDiagram);
	}
	
	@Test
	public void canCreateSequenceDiagramWithoutSecondaryActors() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("use-case-interactions-sequence-diagram-generation-test-3.cml").getContextMappingModel();

		// when
		String useCaseDiagram = new PlantUMLUseCaseInteractionsSequenceDiagramCreator().createDiagram((UseCase) model.getUserRequirements().get(0));

		// then
		assertEquals("@startuml" + System.lineSeparator()
				+ System.lineSeparator()
				+ "skinparam componentStyle uml2" + System.lineSeparator()
				+ System.lineSeparator()
				+ "group Use Case Get_paid_for_car_accident" + System.lineSeparator()
				+ "\"Claimant\" -> System : submit Claim" + System.lineSeparator()
				+ System.lineSeparator()
				+ "System -> System : verifyExistanceOf Policy" + System.lineSeparator()
				+ "System -> System : assign Agent" + System.lineSeparator()
				+ "System -> System : verify Policy" + System.lineSeparator()
				+ "System -> System : pay Claimant" + System.lineSeparator()
				+ "System -> System : close Claim" + System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "end" + System.lineSeparator()
				+ "note over System : A claimant submits a claim and and gets paid from the insurance company." + System.lineSeparator()
				+ System.lineSeparator()
				+ "note right" + System.lineSeparator()
				+ "  Note: This diagram only illustrates the interactions of the" + System.lineSeparator()
				+ "  use case in an early analysis state, as modelled in CML." + System.lineSeparator()
				+ "  Hence, some interactions might not be triggered/connected" + System.lineSeparator()
				+ "  by the correct actor(s) (known limitation)." + System.lineSeparator()
				+ "end note" + System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "@enduml" + System.lineSeparator(), useCaseDiagram);
	}
	
	@Test
	public void canCreateSequenceDiagramWithoutBenefitNote() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("use-case-interactions-sequence-diagram-generation-test-4.cml").getContextMappingModel();

		// when
		String useCaseDiagram = new PlantUMLUseCaseInteractionsSequenceDiagramCreator().createDiagram((UseCase) model.getUserRequirements().get(0));

		// then
		assertEquals("@startuml" + System.lineSeparator()
				+ System.lineSeparator()
				+ "skinparam componentStyle uml2" + System.lineSeparator()
				+ System.lineSeparator()
				+ "group Use Case Get_paid_for_car_accident" + System.lineSeparator()
				+ "\"Claimant\" -> System : submit Claim" + System.lineSeparator()
				+ System.lineSeparator()
				+ "System -> System : verifyExistanceOf Policy" + System.lineSeparator()
				+ "System -> System : assign Agent" + System.lineSeparator()
				+ "System -> System : verify Policy" + System.lineSeparator()
				+ "System -> System : pay Claimant" + System.lineSeparator()
				+ "System -> System : close Claim" + System.lineSeparator()
				+ System.lineSeparator()
				+ "System -> \"Insurance Employee\"" + System.lineSeparator()
				+ System.lineSeparator()
				+ "end" + System.lineSeparator()
				+ System.lineSeparator()
				+ "note right" + System.lineSeparator()
				+ "  Note: This diagram only illustrates the interactions of the" + System.lineSeparator()
				+ "  use case in an early analysis state, as modelled in CML." + System.lineSeparator()
				+ "  Hence, some interactions might not be triggered/connected" + System.lineSeparator()
				+ "  by the correct actor(s) (known limitation)." + System.lineSeparator()
				+ "end note" + System.lineSeparator()
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

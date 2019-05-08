/*
 * Copyright 2018 The Context Mapper Project Team
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
package org.contextmapper.dsl.tests.generators.plantuml;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.CustomerSupplierRelationship;
import org.contextmapper.dsl.contextMappingDSL.DownstreamRole;
import org.contextmapper.dsl.contextMappingDSL.Partnership;
import org.contextmapper.dsl.contextMappingDSL.SharedKernel;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamRole;
import org.contextmapper.dsl.generator.plantuml.PlantUMLComponentDiagramCreator;
import org.contextmapper.dsl.validation.ValidationMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlantUMLComponentDiagramCreatorTest {

	private PlantUMLComponentDiagramCreator creator;

	@BeforeEach
	public void prepare() {
		this.creator = new PlantUMLComponentDiagramCreator();
	}

	@Test
	public void canCreateComponentForBoundedContext() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext.setName("mySuperBoundedContext");
		contextMap.getBoundedContexts().add(boundedContext);

		// when
		String plantUML = this.creator.createDiagram(contextMap);

		// then
		assertTrue(plantUML.contains("component [mySuperBoundedContext]" + System.lineSeparator()));
	}

	@Test
	public void canCreatePartnershipRelationship() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext1 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext1.setName("myContext1");
		BoundedContext boundedContext2 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext2.setName("myContext2");
		contextMap.getBoundedContexts().add(boundedContext1);
		contextMap.getBoundedContexts().add(boundedContext2);
		Partnership partnership = ContextMappingDSLFactory.eINSTANCE.createPartnership();
		partnership.setParticipant1(boundedContext1);
		partnership.setParticipant2(boundedContext2);
		partnership.setImplementationTechnology("ourTechnology");
		contextMap.getRelationships().add(partnership);

		// when
		String plantUML = this.creator.createDiagram(contextMap);

		// then
		assertTrue(plantUML.contains("component [myContext1]" + System.lineSeparator()));
		assertTrue(plantUML.contains("component [myContext2]" + System.lineSeparator()));
		assertTrue(plantUML
				.contains("[myContext1]<-->[myContext2] : Partnership (ourTechnology)" + System.lineSeparator()));
	}

	@Test
	public void canCreatePartnershipRelationshipWithName() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext1 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext1.setName("myContext1");
		BoundedContext boundedContext2 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext2.setName("myContext2");
		contextMap.getBoundedContexts().add(boundedContext1);
		contextMap.getBoundedContexts().add(boundedContext2);
		Partnership partnership = ContextMappingDSLFactory.eINSTANCE.createPartnership();
		partnership.setParticipant1(boundedContext1);
		partnership.setParticipant2(boundedContext2);
		partnership.setImplementationTechnology("ourTechnology");
		partnership.setName("myPartnershipTest");
		contextMap.getRelationships().add(partnership);

		// when
		String plantUML = this.creator.createDiagram(contextMap);

		// then
		assertTrue(plantUML.contains("component [myContext1]" + System.lineSeparator()));
		assertTrue(plantUML.contains("component [myContext2]" + System.lineSeparator()));
		assertTrue(plantUML
				.contains("[myContext1]<-->[myContext2] : myPartnershipTest (ourTechnology)" + System.lineSeparator()));
	}

	@Test
	public void canCreateSharedKernelRelationship() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext1 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext1.setName("myContext1");
		BoundedContext boundedContext2 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext2.setName("myContext2");
		contextMap.getBoundedContexts().add(boundedContext1);
		contextMap.getBoundedContexts().add(boundedContext2);
		SharedKernel sharedKernel = ContextMappingDSLFactory.eINSTANCE.createSharedKernel();
		sharedKernel.setParticipant1(boundedContext1);
		sharedKernel.setParticipant2(boundedContext2);
		sharedKernel.setImplementationTechnology("ourTechnology");
		contextMap.getRelationships().add(sharedKernel);

		// when
		String plantUML = this.creator.createDiagram(contextMap);

		// then
		assertTrue(plantUML.contains("component [myContext1]" + System.lineSeparator()));
		assertTrue(plantUML.contains("component [myContext2]" + System.lineSeparator()));
		assertTrue(plantUML
				.contains("[myContext1]<-->[myContext2] : Shared Kernel (ourTechnology)" + System.lineSeparator()));
	}

	@Test
	public void canCreateSharedKernelRelationshipWithName() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext1 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext1.setName("myContext1");
		BoundedContext boundedContext2 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext2.setName("myContext2");
		contextMap.getBoundedContexts().add(boundedContext1);
		contextMap.getBoundedContexts().add(boundedContext2);
		SharedKernel sharedKernel = ContextMappingDSLFactory.eINSTANCE.createSharedKernel();
		sharedKernel.setParticipant1(boundedContext1);
		sharedKernel.setParticipant2(boundedContext2);
		sharedKernel.setImplementationTechnology("ourTechnology");
		sharedKernel.setName("mySharedKernel");
		contextMap.getRelationships().add(sharedKernel);

		// when
		String plantUML = this.creator.createDiagram(contextMap);

		// then
		assertTrue(plantUML.contains("component [myContext1]" + System.lineSeparator()));
		assertTrue(plantUML.contains("component [myContext2]" + System.lineSeparator()));
		assertTrue(plantUML
				.contains("[myContext1]<-->[myContext2] : mySharedKernel (ourTechnology)" + System.lineSeparator()));
	}

	@Test
	public void canCreateUpstreamDownstreamRelationship() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext1 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext1.setName("myContext1");
		BoundedContext boundedContext2 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext2.setName("myContext2");
		contextMap.getBoundedContexts().add(boundedContext1);
		contextMap.getBoundedContexts().add(boundedContext2);
		UpstreamDownstreamRelationship relationship = ContextMappingDSLFactory.eINSTANCE
				.createUpstreamDownstreamRelationship();
		relationship.setUpstream(boundedContext1);
		relationship.setDownstream(boundedContext2);
		relationship.setImplementationTechnology("SOAP");
		relationship.getUpstreamRoles().add(UpstreamRole.OPEN_HOST_SERVICE);
		relationship.getDownstreamRoles().add(DownstreamRole.ANTICORRUPTION_LAYER);
		contextMap.getRelationships().add(relationship);

		// when
		String plantUML = this.creator.createDiagram(contextMap);

		// then
		assertTrue(plantUML.contains("component [myContext1]" + System.lineSeparator()));
		assertTrue(plantUML.contains("component [myContext2]" + System.lineSeparator()));
		assertTrue(plantUML.contains("interface \"SOAP\" as myContext2_to_myContext1" + System.lineSeparator()));
		assertTrue(plantUML
				.contains("[myContext1] --> myContext2_to_myContext1 : OPEN_HOST_SERVICE" + System.lineSeparator()));
		assertTrue(plantUML.contains(
				"myContext2_to_myContext1 <.. [myContext2] : use : ANTICORRUPTION_LAYER" + System.lineSeparator()));
	}

	@Test
	public void canCreateUpstreamDownstreamRelationshipWithName() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext1 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext1.setName("myContext1");
		BoundedContext boundedContext2 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext2.setName("myContext2");
		contextMap.getBoundedContexts().add(boundedContext1);
		contextMap.getBoundedContexts().add(boundedContext2);
		UpstreamDownstreamRelationship relationship = ContextMappingDSLFactory.eINSTANCE
				.createUpstreamDownstreamRelationship();
		relationship.setUpstream(boundedContext1);
		relationship.setDownstream(boundedContext2);
		relationship.setImplementationTechnology("SOAP");
		relationship.setName("myRel");
		relationship.getUpstreamRoles().add(UpstreamRole.OPEN_HOST_SERVICE);
		relationship.getDownstreamRoles().add(DownstreamRole.ANTICORRUPTION_LAYER);
		contextMap.getRelationships().add(relationship);

		// when
		String plantUML = this.creator.createDiagram(contextMap);

		// then
		assertTrue(plantUML.contains("component [myContext1]" + System.lineSeparator()));
		assertTrue(plantUML.contains("component [myContext2]" + System.lineSeparator()));
		assertTrue(plantUML.contains("interface \"myRel (SOAP)\" as myRel" + System.lineSeparator()));
		assertTrue(plantUML.contains("[myContext1] --> myRel : OPEN_HOST_SERVICE" + System.lineSeparator()));
		assertTrue(plantUML.contains("myRel <.. [myContext2] : use : ANTICORRUPTION_LAYER" + System.lineSeparator()));
	}

	@Test
	public void canCreateIfRelationshipExistsMultipleTimes() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext1 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext1.setName("myContext1");
		BoundedContext boundedContext2 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext2.setName("myContext2");
		contextMap.getBoundedContexts().add(boundedContext1);
		contextMap.getBoundedContexts().add(boundedContext2);
		UpstreamDownstreamRelationship relationship1 = ContextMappingDSLFactory.eINSTANCE
				.createUpstreamDownstreamRelationship();
		relationship1.setUpstream(boundedContext1);
		relationship1.setDownstream(boundedContext2);
		relationship1.setImplementationTechnology("SOAP");
		relationship1.setName("myRel");
		relationship1.getUpstreamRoles().add(UpstreamRole.OPEN_HOST_SERVICE);
		relationship1.getDownstreamRoles().add(DownstreamRole.ANTICORRUPTION_LAYER);
		UpstreamDownstreamRelationship relationship2 = ContextMappingDSLFactory.eINSTANCE
				.createUpstreamDownstreamRelationship();
		relationship2.setUpstream(boundedContext1);
		relationship2.setDownstream(boundedContext2);
		relationship2.setImplementationTechnology("SOAP");
		relationship2.setName("myRel");
		relationship2.getUpstreamRoles().add(UpstreamRole.OPEN_HOST_SERVICE);
		relationship2.getDownstreamRoles().add(DownstreamRole.ANTICORRUPTION_LAYER);
		UpstreamDownstreamRelationship relationship3 = ContextMappingDSLFactory.eINSTANCE
				.createUpstreamDownstreamRelationship();
		relationship3.setUpstream(boundedContext1);
		relationship3.setDownstream(boundedContext2);
		relationship3.setImplementationTechnology("SOAP");
		relationship3.setName("myRel");
		relationship3.getUpstreamRoles().add(UpstreamRole.OPEN_HOST_SERVICE);
		relationship3.getDownstreamRoles().add(DownstreamRole.ANTICORRUPTION_LAYER);
		contextMap.getRelationships().add(relationship1);
		contextMap.getRelationships().add(relationship2);
		contextMap.getRelationships().add(relationship3);

		// when
		String plantUML = this.creator.createDiagram(contextMap);

		// then
		assertTrue(plantUML.contains("component [myContext1]" + System.lineSeparator()));
		assertTrue(plantUML.contains("component [myContext2]" + System.lineSeparator()));
		assertTrue(plantUML.contains("interface \"myRel (SOAP)\" as myRel" + System.lineSeparator()));
		assertTrue(plantUML.contains("[myContext1] --> myRel : OPEN_HOST_SERVICE" + System.lineSeparator()));
		assertTrue(plantUML.contains("myRel <.. [myContext2] : use : ANTICORRUPTION_LAYER" + System.lineSeparator()));
		assertTrue(plantUML.contains("interface \"myRel (SOAP)\" as myContext2_to_myContext1" + System.lineSeparator()));
		assertTrue(plantUML.contains("[myContext1] --> myContext2_to_myContext1 : OPEN_HOST_SERVICE" + System.lineSeparator()));
		assertTrue(plantUML.contains("myContext2_to_myContext1 <.. [myContext2] : use : ANTICORRUPTION_LAYER" + System.lineSeparator()));
		assertTrue(plantUML.contains("interface \"myRel (SOAP)\" as Interface_0" + System.lineSeparator()));
		assertTrue(plantUML.contains("[myContext1] --> Interface_0 : OPEN_HOST_SERVICE" + System.lineSeparator()));
		assertTrue(plantUML.contains("Interface_0 <.. [myContext2] : use : ANTICORRUPTION_LAYER" + System.lineSeparator()));
	}

	@Test
	public void canCreateCustomerSupplierRelationship() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext1 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext1.setName("myContext1");
		BoundedContext boundedContext2 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext2.setName("myContext2");
		contextMap.getBoundedContexts().add(boundedContext1);
		contextMap.getBoundedContexts().add(boundedContext2);
		CustomerSupplierRelationship relationship = ContextMappingDSLFactory.eINSTANCE
				.createCustomerSupplierRelationship();
		relationship.setUpstream(boundedContext1);
		relationship.setDownstream(boundedContext2);
		relationship.setImplementationTechnology("SOAP");
		relationship.getUpstreamRoles().add(UpstreamRole.OPEN_HOST_SERVICE);
		relationship.getDownstreamRoles().add(DownstreamRole.ANTICORRUPTION_LAYER);
		contextMap.getRelationships().add(relationship);

		// when
		String plantUML = this.creator.createDiagram(contextMap);

		// then
		assertTrue(plantUML.contains("component [myContext1]" + System.lineSeparator()));
		assertTrue(plantUML.contains("component [myContext2]" + System.lineSeparator()));
		assertTrue(plantUML.contains(
				"interface \"Customer-Supplier (SOAP)\" as myContext2_to_myContext1" + System.lineSeparator()));
		assertTrue(plantUML
				.contains("[myContext1] --> myContext2_to_myContext1 : OPEN_HOST_SERVICE" + System.lineSeparator()));
		assertTrue(plantUML.contains(
				"myContext2_to_myContext1 <.. [myContext2] : use : ANTICORRUPTION_LAYER" + System.lineSeparator()));
	}

	@Test
	public void canCreateCustomerSupplierRelationshipWithName() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext1 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext1.setName("myContext1");
		BoundedContext boundedContext2 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext2.setName("myContext2");
		contextMap.getBoundedContexts().add(boundedContext1);
		contextMap.getBoundedContexts().add(boundedContext2);
		CustomerSupplierRelationship relationship = ContextMappingDSLFactory.eINSTANCE
				.createCustomerSupplierRelationship();
		relationship.setUpstream(boundedContext1);
		relationship.setDownstream(boundedContext2);
		relationship.setImplementationTechnology("SOAP");
		relationship.setName("MyCS");
		relationship.getUpstreamRoles().add(UpstreamRole.OPEN_HOST_SERVICE);
		relationship.getDownstreamRoles().add(DownstreamRole.ANTICORRUPTION_LAYER);
		contextMap.getRelationships().add(relationship);

		// when
		String plantUML = this.creator.createDiagram(contextMap);

		// then
		assertTrue(plantUML.contains("component [myContext1]" + System.lineSeparator()));
		assertTrue(plantUML.contains("component [myContext2]" + System.lineSeparator()));
		assertTrue(plantUML.contains("interface \"MyCS (SOAP)\" as MyCS" + System.lineSeparator()));
		assertTrue(plantUML.contains("[myContext1] --> MyCS : OPEN_HOST_SERVICE" + System.lineSeparator()));
		assertTrue(plantUML.contains("MyCS <.. [myContext2] : use : ANTICORRUPTION_LAYER" + System.lineSeparator()));
	}

	@Test
	public void canCreateUpstreamDownstreamRelationshipWithoutNameAndTechnology() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext1 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext1.setName("myContext1");
		BoundedContext boundedContext2 = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext2.setName("myContext2");
		contextMap.getBoundedContexts().add(boundedContext1);
		contextMap.getBoundedContexts().add(boundedContext2);
		UpstreamDownstreamRelationship relationship = ContextMappingDSLFactory.eINSTANCE
				.createUpstreamDownstreamRelationship();
		relationship.setUpstream(boundedContext1);
		relationship.setDownstream(boundedContext2);
		contextMap.getRelationships().add(relationship);

		// when
		String plantUML = this.creator.createDiagram(contextMap);

		// then
		assertTrue(plantUML.contains("component [myContext1]" + System.lineSeparator()));
		assertTrue(plantUML.contains("component [myContext2]" + System.lineSeparator()));
		assertTrue(plantUML
				.contains("interface \"Upstream-Downstream\" as myContext2_to_myContext1" + System.lineSeparator()));
		assertTrue(plantUML.contains("[myContext1] --> myContext2_to_myContext1" + System.lineSeparator()));
		assertTrue(plantUML.contains("myContext2_to_myContext1 <.. [myContext2] : use" + System.lineSeparator()));
	}

	@Test
	public void createsNoteIfDiagramIsEmpty() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();

		// when
		String plantUML = this.creator.createDiagram(contextMap);

		// then
		assertTrue(plantUML.contains("note \"" + ValidationMessages.EMPTY_UML_COMPONENT_DIAGRAM_MESSAGE
				+ "\" as EmptyDiagramError" + System.lineSeparator()));
	}

	@Test
	public void canAddDomainVisionStatementForBC() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext.setName("mySuperBoundedContext");
		boundedContext.setDomainVisionStatement("this is my super test vision statement");
		contextMap.getBoundedContexts().add(boundedContext);

		// when
		String plantUML = this.creator.createDiagram(contextMap);

		// then
		assertTrue(plantUML.contains("component [mySuperBoundedContext]" + System.lineSeparator()));
		assertTrue(plantUML.contains("note right of [mySuperBoundedContext]" + System.lineSeparator()));
		assertTrue(plantUML.contains("this is my super test vision statement " + System.lineSeparator()));
		assertTrue(plantUML.contains("end note" + System.lineSeparator()));
	}

	@Test
	public void canAddDomainVisionStatementForBCWithLongLine() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext.setName("mySuperBoundedContext");
		boundedContext.setDomainVisionStatement("this is my super and very long test vision statement");
		contextMap.getBoundedContexts().add(boundedContext);

		// when
		String plantUML = this.creator.createDiagram(contextMap);

		// then
		assertTrue(plantUML.contains("component [mySuperBoundedContext]" + System.lineSeparator()));
		assertTrue(plantUML.contains("note right of [mySuperBoundedContext]" + System.lineSeparator()));
		assertTrue(plantUML.contains("this is my super and very long " + System.lineSeparator()));
		assertTrue(plantUML.contains("test vision statement " + System.lineSeparator()));
		assertTrue(plantUML.contains("end note" + System.lineSeparator()));
	}

}

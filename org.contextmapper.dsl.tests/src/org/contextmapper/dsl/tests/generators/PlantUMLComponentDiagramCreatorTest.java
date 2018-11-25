package org.contextmapper.dsl.tests.generators;

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
		assertTrue(plantUML.contains("component [mySuperBoundedContext]\n"));
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
		assertTrue(plantUML.contains("component [myContext1]\n"));
		assertTrue(plantUML.contains("component [myContext2]\n"));
		assertTrue(plantUML.contains("[myContext1]<-->[myContext2] : Partnership (ourTechnology)\n"));
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
		assertTrue(plantUML.contains("component [myContext1]\n"));
		assertTrue(plantUML.contains("component [myContext2]\n"));
		assertTrue(plantUML.contains("[myContext1]<-->[myContext2] : Shared Kernel (ourTechnology)\n"));
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
		UpstreamDownstreamRelationship relationship = ContextMappingDSLFactory.eINSTANCE.createUpstreamDownstreamRelationship();
		relationship.setUpstream(boundedContext1);
		relationship.setDownstream(boundedContext2);
		relationship.setImplementationTechnology("SOAP");
		relationship.getUpstreamRoles().add(UpstreamRole.OPEN_HOST_SERVICE);
		relationship.getDownstreamRoles().add(DownstreamRole.ANTICORRUPTION_LAYER);
		contextMap.getRelationships().add(relationship);

		// when
		String plantUML = this.creator.createDiagram(contextMap);

		// then
		assertTrue(plantUML.contains("component [myContext1]\n"));
		assertTrue(plantUML.contains("component [myContext2]\n"));
		assertTrue(plantUML.contains("interface \"SOAP\" as myContext2_to_myContext1\n"));
		assertTrue(plantUML.contains("[myContext1] --> myContext2_to_myContext1 : OPEN_HOST_SERVICE\n"));
		assertTrue(plantUML.contains("myContext2_to_myContext1 <.. [myContext2] : use : ANTICORRUPTION_LAYER\n"));
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
		CustomerSupplierRelationship relationship = ContextMappingDSLFactory.eINSTANCE.createCustomerSupplierRelationship();
		relationship.setUpstream(boundedContext1);
		relationship.setDownstream(boundedContext2);
		relationship.setImplementationTechnology("SOAP");
		relationship.getUpstreamRoles().add(UpstreamRole.OPEN_HOST_SERVICE);
		relationship.getDownstreamRoles().add(DownstreamRole.ANTICORRUPTION_LAYER);
		contextMap.getRelationships().add(relationship);

		// when
		String plantUML = this.creator.createDiagram(contextMap);

		// then
		assertTrue(plantUML.contains("component [myContext1]\n"));
		assertTrue(plantUML.contains("component [myContext2]\n"));
		assertTrue(plantUML.contains("interface \"Customer-Supplier (SOAP)\" as myContext2_to_myContext1\n"));
		assertTrue(plantUML.contains("[myContext1] --> myContext2_to_myContext1 : OPEN_HOST_SERVICE\n"));
		assertTrue(plantUML.contains("myContext2_to_myContext1 <.. [myContext2] : use : ANTICORRUPTION_LAYER\n"));
	}

}

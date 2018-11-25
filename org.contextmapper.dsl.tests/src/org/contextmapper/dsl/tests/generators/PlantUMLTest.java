package org.contextmapper.dsl.tests.generators;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.generator.plantuml.AbstractPlantUMLDiagramCreator;
import org.contextmapper.dsl.generator.plantuml.PlantUMLDiagramCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlantUMLTest {

	private PlantUMLDiagramCreator<ContextMap> creator;

	@BeforeEach
	public void prepare() {
		this.creator = new AbstractPlantUMLDiagramCreator<ContextMap>() {
			@Override
			protected void printDiagramContent(ContextMap modelObject) {
				// no diagram content needed for this tests
			}
		};
	}

	@Test
	public void testHeaderAndFooter() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();

		// when
		String plantUML = creator.createDiagram(contextMap);

		// then
		assertTrue(plantUML.startsWith("@startuml"));
		assertTrue(plantUML.endsWith("@enduml\n"));
	}

	@Test
	public void testUML2Formatting() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();

		// when
		String plantUML = creator.createDiagram(contextMap);

		// then
		assertTrue(plantUML.contains("skinparam componentStyle uml2\n"));
	}

}

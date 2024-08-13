/*
 * Copyright 2024 The Context Mapper Project Team
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
import org.contextmapper.dsl.generator.plantuml.PlantUMLValueImpactMapGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlantUMLValueImpactMapDiagramCreatorTest extends AbstractCMLInputFileTest {

	@BeforeEach
	public void prepare() {
		super.prepare();
	}

	@Test
	public void canCreateValueImpactMapDiagramDiagramWithoutBoundedContext() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("value-impact-map-diagram-generation-test-2.cml").getContextMappingModel();

		// when
		String valueImpactMapDiagram = new PlantUMLValueImpactMapGenerator().createDiagram(model.getValueRegisters().get(0));

		// then
		assertEquals("@startmindmap" + System.lineSeparator()
				+ System.lineSeparator()
				+ "<style>" + System.lineSeparator()
				+ "node {" + System.lineSeparator()
				+ "    MinimumWidth 200" + System.lineSeparator()
				+ "    MaximumWidth 200" + System.lineSeparator()
				+ "}" + System.lineSeparator()
				+ "mindmapDiagram {" + System.lineSeparator()
				+ "  .green {" + System.lineSeparator()
				+ "    BackgroundColor lightgreen" + System.lineSeparator()
				+ "  }" + System.lineSeparator()
				+ "  .red {" + System.lineSeparator()
				+ "    BackgroundColor tomato" + System.lineSeparator()
				+ "  }" + System.lineSeparator()
				+ "}" + System.lineSeparator()
				+ "</style>" + System.lineSeparator()
				+ System.lineSeparator()
				+ "* System of Interest (SOI)" + System.lineSeparator()
				+ "** Drivers" + System.lineSeparator()
				+ "***:<b>WorkLifeBalance" + System.lineSeparator()
				+ "----" + System.lineSeparator()
				+ "Priority HIGH" + System.lineSeparator()
				+ "Impact HIGH" + System.lineSeparator()
				+ ";" + System.lineSeparator()
				+ "@endmindmap" + System.lineSeparator(), valueImpactMapDiagram);
	}
	
	@Test
	public void canCreateSimpleValueImpactMapDiagram() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("value-impact-map-diagram-generation-test-3.cml").getContextMappingModel();

		// when
		String valueImpactMapDiagram = new PlantUMLValueImpactMapGenerator().createDiagram(model.getValueRegisters().get(0));

		// then
		assertEquals("@startmindmap" + System.lineSeparator()
				+ System.lineSeparator()
				+ "<style>" + System.lineSeparator()
				+ "node {" + System.lineSeparator()
				+ "    MinimumWidth 200" + System.lineSeparator()
				+ "    MaximumWidth 200" + System.lineSeparator()
				+ "}" + System.lineSeparator()
				+ "mindmapDiagram {" + System.lineSeparator()
				+ "  .green {" + System.lineSeparator()
				+ "    BackgroundColor lightgreen" + System.lineSeparator()
				+ "  }" + System.lineSeparator()
				+ "  .red {" + System.lineSeparator()
				+ "    BackgroundColor tomato" + System.lineSeparator()
				+ "  }" + System.lineSeparator()
				+ "}" + System.lineSeparator()
				+ "</style>" + System.lineSeparator()
				+ System.lineSeparator()
				+ "* SameDayDelivery" + System.lineSeparator()
				+ "** Drivers" + System.lineSeparator()
				+ "***:<b>WorkLifeBalance" + System.lineSeparator()
				+ "----" + System.lineSeparator()
				+ "Priority HIGH" + System.lineSeparator()
				+ "Impact HIGH" + System.lineSeparator()
				+ "-- Consequence --" + System.lineSeparator()
				+ "SDD will harm work-life-balance of drivers" + System.lineSeparator()
				+ "-- Demonstrators --" + System.lineSeparator()
				+ "* Drivers value a healthy work-life-balance" + System.lineSeparator()
				+ "; <<red>>" + System.lineSeparator()
				+ "**** ACT: hire more drivers" + System.lineSeparator()
				+ "@endmindmap" + System.lineSeparator(), valueImpactMapDiagram);
	}
	
	@Test
	public void canCreateSimpleValueImpactMapDiagram4ValueCluster() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("value-impact-map-diagram-generation-test-4.cml").getContextMappingModel();

		// when
		String valueImpactMapDiagram = new PlantUMLValueImpactMapGenerator().createDiagram(model.getValueRegisters().get(0));

		// then
		assertEquals("@startmindmap" + System.lineSeparator()
				+ System.lineSeparator()
				+ "<style>" + System.lineSeparator()
				+ "node {" + System.lineSeparator()
				+ "    MinimumWidth 200" + System.lineSeparator()
				+ "    MaximumWidth 200" + System.lineSeparator()
				+ "}" + System.lineSeparator()
				+ "mindmapDiagram {" + System.lineSeparator()
				+ "  .green {" + System.lineSeparator()
				+ "    BackgroundColor lightgreen" + System.lineSeparator()
				+ "  }" + System.lineSeparator()
				+ "  .red {" + System.lineSeparator()
				+ "    BackgroundColor tomato" + System.lineSeparator()
				+ "  }" + System.lineSeparator()
				+ "}" + System.lineSeparator()
				+ "</style>" + System.lineSeparator()
				+ System.lineSeparator()
				+ "* System of Interest (SOI)" + System.lineSeparator()
				+ "** Customers_and_Shoppers" + System.lineSeparator()
				+ "***:<b>AUTONOMY" + System.lineSeparator()
				+ "----" + System.lineSeparator()
				+ "Priority HIGH" + System.lineSeparator()
				+ "Impact MEDIUM" + System.lineSeparator()
				+ "-- Consequence --" + System.lineSeparator()
				+ "increased freedom" + System.lineSeparator()
				+ "-- Demonstrators --" + System.lineSeparator()
				+ "* customer values potentially increased freedom" + System.lineSeparator()
				+ "* delivery staff's freedom might suffer because of work-life-balance" + System.lineSeparator()
				+ "; <<green>>" + System.lineSeparator()
				+ "@endmindmap" + System.lineSeparator(), valueImpactMapDiagram);
	}
	
	@Test
	public void canRespectValuesInValueClusters() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("value-impact-map-diagram-generation-test-5.cml").getContextMappingModel();

		// when
		String valueImpactMapDiagram = new PlantUMLValueImpactMapGenerator().createDiagram(model.getValueRegisters().get(0));

		// then
		assertEquals("@startmindmap" + System.lineSeparator()
				+ System.lineSeparator()
				+ "<style>" + System.lineSeparator()
				+ "node {" + System.lineSeparator()
				+ "    MinimumWidth 200" + System.lineSeparator()
				+ "    MaximumWidth 200" + System.lineSeparator()
				+ "}" + System.lineSeparator()
				+ "mindmapDiagram {" + System.lineSeparator()
				+ "  .green {" + System.lineSeparator()
				+ "    BackgroundColor lightgreen" + System.lineSeparator()
				+ "  }" + System.lineSeparator()
				+ "  .red {" + System.lineSeparator()
				+ "    BackgroundColor tomato" + System.lineSeparator()
				+ "  }" + System.lineSeparator()
				+ "}" + System.lineSeparator()
				+ "</style>" + System.lineSeparator()
				+ System.lineSeparator()
				+ "* System of Interest (SOI)" + System.lineSeparator()
				+ "** Delivery_Staff_of_Suppliers" + System.lineSeparator()
				+ "***:<b>Freedom" + System.lineSeparator()
				+ "----" + System.lineSeparator()
				+ "Priority HIGH" + System.lineSeparator()
				+ "Impact HIGH" + System.lineSeparator()
				+ "-- Consequence --" + System.lineSeparator()
				+ "work-life-balance" + System.lineSeparator()
				+ "; <<red>>" + System.lineSeparator()
				+ "***:<b>Freedom" + System.lineSeparator()
				+ "----" + System.lineSeparator()
				+ "Priority HIGH" + System.lineSeparator()
				+ "Impact HIGH" + System.lineSeparator()
				+ "-- Consequence --" + System.lineSeparator()
				+ "tbd" + System.lineSeparator()
				+ "; <<green>>" + System.lineSeparator()
				+ "** Customers_and_Shoppers" + System.lineSeparator()
				+ "***:<b>Freedom" + System.lineSeparator()
				+ "----" + System.lineSeparator()
				+ "Priority HIGH" + System.lineSeparator()
				+ "Impact MEDIUM" + System.lineSeparator()
				+ "-- Consequence --" + System.lineSeparator()
				+ "increased freedom" + System.lineSeparator()
				+ "; <<green>>" + System.lineSeparator()
				+ "@endmindmap" + System.lineSeparator(), valueImpactMapDiagram);
	}
	
	@Test
	public void canHandleRecurringStakeholders() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("value-impact-map-diagram-generation-test-6.cml").getContextMappingModel();

		// when
		String valueImpactMapDiagram = new PlantUMLValueImpactMapGenerator().createDiagram(model.getValueRegisters().get(0));

		// then
		assertEquals("@startmindmap" + System.lineSeparator()
				+ System.lineSeparator()
				+ "<style>" + System.lineSeparator()
				+ "node {" + System.lineSeparator()
				+ "    MinimumWidth 200" + System.lineSeparator()
				+ "    MaximumWidth 200" + System.lineSeparator()
				+ "}" + System.lineSeparator()
				+ "mindmapDiagram {" + System.lineSeparator()
				+ "  .green {" + System.lineSeparator()
				+ "    BackgroundColor lightgreen" + System.lineSeparator()
				+ "  }" + System.lineSeparator()
				+ "  .red {" + System.lineSeparator()
				+ "    BackgroundColor tomato" + System.lineSeparator()
				+ "  }" + System.lineSeparator()
				+ "}" + System.lineSeparator()
				+ "</style>" + System.lineSeparator()
				+ System.lineSeparator()
				+ "* System of Interest (SOI)" + System.lineSeparator()
				+ "** Delivery_Staff_of_Suppliers" + System.lineSeparator()
				+ "***:<b>Freedom" + System.lineSeparator()
				+ "----" + System.lineSeparator()
				+ "Priority HIGH" + System.lineSeparator()
				+ "Impact HIGH" + System.lineSeparator()
				+ "-- Consequence --" + System.lineSeparator()
				+ "work-life-balance" + System.lineSeparator()
				+ "; <<red>>" + System.lineSeparator()
				+ "***:<b>Freedom" + System.lineSeparator()
				+ "----" + System.lineSeparator()
				+ "Priority HIGH" + System.lineSeparator()
				+ "Impact HIGH" + System.lineSeparator()
				+ "-- Consequence --" + System.lineSeparator()
				+ "tbd" + System.lineSeparator()
				+ "; <<green>>" + System.lineSeparator()
				+ "** Customers_and_Shoppers" + System.lineSeparator()
				+ "***:<b>Freedom" + System.lineSeparator()
				+ "----" + System.lineSeparator()
				+ "Priority HIGH" + System.lineSeparator()
				+ "Impact MEDIUM" + System.lineSeparator()
				+ "-- Consequence --" + System.lineSeparator()
				+ "increased freedom" + System.lineSeparator()
				+ "; <<green>>" + System.lineSeparator()
				+ "***:<b>Sustainability" + System.lineSeparator()
				+ "----" + System.lineSeparator()
				+ "Priority HIGH" + System.lineSeparator()
				+ "Impact LOW" + System.lineSeparator()
				+ "-- Consequence --" + System.lineSeparator()
				+ "fostering unsustainable behavior (always ordering last minute)" + System.lineSeparator()
				+ "; <<red>>" + System.lineSeparator()
				+ "@endmindmap" + System.lineSeparator(), valueImpactMapDiagram);
	}
	
	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/generators/plantuml/";
	}

}

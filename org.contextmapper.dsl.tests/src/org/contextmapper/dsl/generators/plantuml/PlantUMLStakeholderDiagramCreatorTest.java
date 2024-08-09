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
import org.contextmapper.dsl.generator.plantuml.PlantUMLStakeholderMapGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlantUMLStakeholderDiagramCreatorTest extends AbstractCMLInputFileTest {

	@BeforeEach
	public void prepare() {
		super.prepare();
	}

	@Test
	public void canCreateStakeholderDiagramWithContext() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("stakeholder-diagram-generation-test-2.cml").getContextMappingModel();

		// when
		String stakeholderDiagram = new PlantUMLStakeholderMapGenerator().createDiagram(model.getStakeholders().get(0));

		// then
		assertEquals("@startmindmap" + System.lineSeparator()
				+ System.lineSeparator()
				+ "* SameDayDelivery" + System.lineSeparator()
				+ "** Product_Suppliers" + System.lineSeparator()
				+ "*** Managers" + System.lineSeparator()
				+ "*** Logistics_Warehouse_Staff_of_Suppliers" + System.lineSeparator()
				+ "*** Delivery_Staff_of_Suppliers" + System.lineSeparator()
				+ "** Competing_Companies" + System.lineSeparator()
				+ "** Government" + System.lineSeparator()
				+ System.lineSeparator()
				+ "left side"  + System.lineSeparator()
				+ "** Online_Shopping_Company" + System.lineSeparator()
				+ "*** Development_Team" + System.lineSeparator()
				+ "*** Product_Management" + System.lineSeparator()
				+ "*** Customer_Relationship_Manager" + System.lineSeparator()
				+ "** Delivery_Partners" + System.lineSeparator()
				+ "*** Route_Planners" + System.lineSeparator()
				+ "*** Drivers" + System.lineSeparator()
				+ "** Logistics_Team" + System.lineSeparator()
				+ "*** Logistics_Manager" + System.lineSeparator()
				+ "*** Warehouse_Staff" + System.lineSeparator()
				+ "** Customers_and_Shoppers" + System.lineSeparator()
				+ "*** Shoppers_in_Emergency_Situations" + System.lineSeparator()
				+ "*** Others" + System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "@endmindmap" + System.lineSeparator(), stakeholderDiagram);
	}
	
	@Test
	public void canCreateStakeholderDiagramWithMultipleContext() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("stakeholder-diagram-generation-test-3.cml").getContextMappingModel();

		// when
		String stakeholderDiagram = new PlantUMLStakeholderMapGenerator().createDiagram(model.getStakeholders().get(0));

		// then
		assertEquals("@startmindmap" + System.lineSeparator()
				+ System.lineSeparator()
				+ "* SameDayDelivery, Context2" + System.lineSeparator()
				+ "** Product_Suppliers" + System.lineSeparator()
				+ "*** Managers" + System.lineSeparator()
				+ "*** Logistics_Warehouse_Staff_of_Suppliers" + System.lineSeparator()
				+ "*** Delivery_Staff_of_Suppliers" + System.lineSeparator()
				+ "** Competing_Companies" + System.lineSeparator()
				+ "** Government" + System.lineSeparator()
				+ System.lineSeparator()
				+ "left side"  + System.lineSeparator()
				+ "** Online_Shopping_Company" + System.lineSeparator()
				+ "*** Development_Team" + System.lineSeparator()
				+ "*** Product_Management" + System.lineSeparator()
				+ "*** Customer_Relationship_Manager" + System.lineSeparator()
				+ "** Delivery_Partners" + System.lineSeparator()
				+ "*** Route_Planners" + System.lineSeparator()
				+ "*** Drivers" + System.lineSeparator()
				+ "** Logistics_Team" + System.lineSeparator()
				+ "*** Logistics_Manager" + System.lineSeparator()
				+ "*** Warehouse_Staff" + System.lineSeparator()
				+ "** Customers_and_Shoppers" + System.lineSeparator()
				+ "*** Shoppers_in_Emergency_Situations" + System.lineSeparator()
				+ "*** Others" + System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "@endmindmap" + System.lineSeparator(), stakeholderDiagram);
	}
	
	@Test
	public void canCreateStakeholderDiagramWithDefaultContext() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("stakeholder-diagram-generation-test-4.cml").getContextMappingModel();

		// when
		String stakeholderDiagram = new PlantUMLStakeholderMapGenerator().createDiagram(model.getStakeholders().get(0));

		// then
		assertEquals("@startmindmap" + System.lineSeparator()
				+ System.lineSeparator()
				+ "* System of Interest" + System.lineSeparator()
				+ "** Product_Suppliers" + System.lineSeparator()
				+ "*** Managers" + System.lineSeparator()
				+ "*** Logistics_Warehouse_Staff_of_Suppliers" + System.lineSeparator()
				+ "*** Delivery_Staff_of_Suppliers" + System.lineSeparator()
				+ "** Competing_Companies" + System.lineSeparator()
				+ "** Government" + System.lineSeparator()
				+ System.lineSeparator()
				+ "left side"  + System.lineSeparator()
				+ "** Online_Shopping_Company" + System.lineSeparator()
				+ "*** Development_Team" + System.lineSeparator()
				+ "*** Product_Management" + System.lineSeparator()
				+ "*** Customer_Relationship_Manager" + System.lineSeparator()
				+ "** Delivery_Partners" + System.lineSeparator()
				+ "*** Route_Planners" + System.lineSeparator()
				+ "*** Drivers" + System.lineSeparator()
				+ "** Logistics_Team" + System.lineSeparator()
				+ "*** Logistics_Manager" + System.lineSeparator()
				+ "*** Warehouse_Staff" + System.lineSeparator()
				+ "** Customers_and_Shoppers" + System.lineSeparator()
				+ "*** Shoppers_in_Emergency_Situations" + System.lineSeparator()
				+ "*** Others" + System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "@endmindmap" + System.lineSeparator(), stakeholderDiagram);
	}
	
	
	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/generators/plantuml/";
	}

}

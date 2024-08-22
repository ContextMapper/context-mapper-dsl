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
				+ "<style>" + System.lineSeparator()
				+ "node {" + System.lineSeparator()
				+ "    MaximumWidth 300" + System.lineSeparator()
				+ "}" + System.lineSeparator()
				+ "</style>" + System.lineSeparator()
				+ "" + System.lineSeparator()
				+ "* SameDayDelivery" + System.lineSeparator()
				+ "** <b>Product_Suppliers</b>" + System.lineSeparator()
				+ "*** <b>Managers</b>" + System.lineSeparator()
				+ "*** <b>Logistics_Warehouse_Staff_of_Suppliers</b>" + System.lineSeparator()
				+ "*** <b>Delivery_Staff_of_Suppliers</b>" + System.lineSeparator()
				+ "** <b>Competing_Companies</b>" + System.lineSeparator()
				+ "** <b>Government</b>" + System.lineSeparator()
				+ System.lineSeparator()
				+ "left side" + System.lineSeparator()
				+ "** <b>Online_Shopping_Company</b>" + System.lineSeparator()
				+ "*** <b>Development_Team</b>" + System.lineSeparator()
				+ "*** <b>Product_Management</b>" + System.lineSeparator()
				+ "*** <b>Customer_Relationship_Manager</b>" + System.lineSeparator()
				+ "** <b>Delivery_Partners</b>" + System.lineSeparator()
				+ "*** <b>Route_Planners</b>" + System.lineSeparator()
				+ "*** <b>Drivers</b>" + System.lineSeparator()
				+ "** <b>Logistics_Team</b>" + System.lineSeparator()
				+ "*** <b>Logistics_Manager</b>" + System.lineSeparator()
				+ "*** <b>Warehouse_Staff</b>" + System.lineSeparator()
				+ "** <b>Customers_and_Shoppers</b>" + System.lineSeparator()
				+ "*** <b>Shoppers_in_Emergency_Situations</b>" + System.lineSeparator()
				+ "*** <b>Others</b>" + System.lineSeparator()
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
				+ "<style>" + System.lineSeparator()
				+ "node {" + System.lineSeparator()
				+ "    MaximumWidth 300" + System.lineSeparator()
				+ "}" + System.lineSeparator()
				+ "</style>" + System.lineSeparator()
				+ System.lineSeparator()
				+ "* SameDayDelivery, Context2" + System.lineSeparator()
				+ "** <b>Product_Suppliers</b>" + System.lineSeparator()
				+ "*** <b>Managers</b>" + System.lineSeparator()
				+ "*** <b>Logistics_Warehouse_Staff_of_Suppliers</b>" + System.lineSeparator()
				+ "*** <b>Delivery_Staff_of_Suppliers</b>" + System.lineSeparator()
				+ "** <b>Competing_Companies</b>" + System.lineSeparator()
				+ "** <b>Government</b>" + System.lineSeparator()
				+ "" + System.lineSeparator()
				+ "left side" + System.lineSeparator()
				+ "** <b>Online_Shopping_Company</b>" + System.lineSeparator()
				+ "*** <b>Development_Team</b>" + System.lineSeparator()
				+ "*** <b>Product_Management</b>" + System.lineSeparator()
				+ "*** <b>Customer_Relationship_Manager</b>" + System.lineSeparator()
				+ "** <b>Delivery_Partners</b>" + System.lineSeparator()
				+ "*** <b>Route_Planners</b>" + System.lineSeparator()
				+ "*** <b>Drivers</b>" + System.lineSeparator()
				+ "** <b>Logistics_Team</b>" + System.lineSeparator()
				+ "*** <b>Logistics_Manager</b>" + System.lineSeparator()
				+ "*** <b>Warehouse_Staff</b>" + System.lineSeparator()
				+ "** <b>Customers_and_Shoppers</b>" + System.lineSeparator()
				+ "*** <b>Shoppers_in_Emergency_Situations</b>" + System.lineSeparator()
				+ "*** <b>Others</b>" + System.lineSeparator()
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
				+ "<style>" + System.lineSeparator()
				+ "node {" + System.lineSeparator()
				+ "    MaximumWidth 300" + System.lineSeparator()
				+ "}" + System.lineSeparator()
				+ "</style>" + System.lineSeparator()
				+ System.lineSeparator()
				+ "* System of Interest" + System.lineSeparator()
				+ "** <b>Product_Suppliers</b>" + System.lineSeparator()
				+ "*** <b>Managers</b>" + System.lineSeparator()
				+ "*** <b>Logistics_Warehouse_Staff_of_Suppliers</b>" + System.lineSeparator()
				+ "*** <b>Delivery_Staff_of_Suppliers</b>" + System.lineSeparator()
				+ "** <b>Competing_Companies</b>" + System.lineSeparator()
				+ "** <b>Government</b>" + System.lineSeparator()
				+ System.lineSeparator()
				+ "left side" + System.lineSeparator()
				+ "** <b>Online_Shopping_Company</b>" + System.lineSeparator()
				+ "*** <b>Development_Team</b>" + System.lineSeparator()
				+ "*** <b>Product_Management</b>" + System.lineSeparator()
				+ "*** <b>Customer_Relationship_Manager</b>" + System.lineSeparator()
				+ "** <b>Delivery_Partners</b>" + System.lineSeparator()
				+ "*** <b>Route_Planners</b>" + System.lineSeparator()
				+ "*** <b>Drivers</b>" + System.lineSeparator()
				+ "** <b>Logistics_Team</b>" + System.lineSeparator()
				+ "*** <b>Logistics_Manager</b>" + System.lineSeparator()
				+ "*** <b>Warehouse_Staff</b>" + System.lineSeparator()
				+ "** <b>Customers_and_Shoppers</b>" + System.lineSeparator()
				+ "*** <b>Shoppers_in_Emergency_Situations</b>" + System.lineSeparator()
				+ "*** <b>Others</b>" + System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "@endmindmap" + System.lineSeparator(), stakeholderDiagram);
	}
	
	
	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/generators/plantuml/";
	}

}

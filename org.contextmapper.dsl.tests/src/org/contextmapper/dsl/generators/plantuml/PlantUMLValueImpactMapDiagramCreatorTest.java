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
import org.contextmapper.dsl.generator.plantuml.PlantUMLValueImpactMapGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlantUMLValueImpactMapDiagramCreatorTest extends AbstractCMLInputFileTest {

	@BeforeEach
	public void prepare() {
		super.prepare();
	}

	@Test
	public void canCreateStakeholderDiagramWithContext() throws IOException {
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
	
	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/generators/plantuml/";
	}

}

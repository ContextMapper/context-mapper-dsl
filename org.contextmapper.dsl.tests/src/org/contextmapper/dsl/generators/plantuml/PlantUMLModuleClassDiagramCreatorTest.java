/*
 * Copyright 2021 The Context Mapper Project Team
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

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.generator.plantuml.PlantUMLModuleClassDiagramCreator;
import org.contextmapper.dsl.validation.ValidationMessages;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.CollectionType;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlantUMLModuleClassDiagramCreatorTest extends AbstractCMLInputFileTest {

	private PlantUMLModuleClassDiagramCreator creator;

	@BeforeEach
	public void prepare() {
		super.prepare();
		this.creator = new PlantUMLModuleClassDiagramCreator();
	}

	@Test
	public void canCreateModulePackage() {
		// given
		SculptorModule module = ContextMappingDSLFactory.eINSTANCE.createSculptorModule();
		module.setName("testModule");
		module.getDomainObjects().add(TacticdslFactory.eINSTANCE.createSimpleDomainObject());

		// when
		String plantUML = this.creator.createDiagram(module);

		// then
		assertTrue(plantUML.contains(
				"package testModule {" + System.lineSeparator() + "}" + System.lineSeparator()));
	}

	@Test
	public void canCreateClassFromEntity() {
		// given
		SculptorModule module = ContextMappingDSLFactory.eINSTANCE.createSculptorModule();
		module.setName("testAggregate");
		Entity entity = TacticdslFactory.eINSTANCE.createEntity();
		entity.setName("Test");
		Attribute attribute = TacticdslFactory.eINSTANCE.createAttribute();
		attribute.setType("int");
		attribute.setName("amount");
		entity.getAttributes().add(attribute);
		Attribute listAttribute = TacticdslFactory.eINSTANCE.createAttribute();
		listAttribute.setCollectionType(CollectionType.LIST);
		listAttribute.setName("myList");
		listAttribute.setType("String");
		entity.getAttributes().add(listAttribute);
		module.getDomainObjects().add(entity);

		// when
		String plantUML = this.creator.createDiagram(module);

		// then
		assertTrue(plantUML.contains("	class Test <<(E,DarkSeaGreen) Entity>> {" + System.lineSeparator()
				+ "		int amount" + System.lineSeparator() + "		List<String> myList" + System.lineSeparator()
				+ "	}" + System.lineSeparator()));
	}

	@Test
	public void createsNoteIfBoundedContextIsEmpty() {
		// given
		SculptorModule module = ContextMappingDSLFactory.eINSTANCE.createSculptorModule();

		// when
		String plantUML = this.creator.createDiagram(module);

		// then
		assertTrue(plantUML.contains("note \"" + ValidationMessages.EMPTY_UML_CLASS_DIAGRAM_MESSAGE_MODULE
				+ "\" as EmptyDiagramError" + System.lineSeparator()));
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/plantuml/";
	}

}

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
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.generator.plantuml.PlantUMLAggregateClassDiagramCreator;
import org.contextmapper.dsl.validation.ValidationMessages;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.CollectionType;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlantUMLAggregateClassDiagramCreatorTest extends AbstractCMLInputFileTest {

	private PlantUMLAggregateClassDiagramCreator creator;

	@BeforeEach
	public void prepare() {
		super.prepare();
		this.creator = new PlantUMLAggregateClassDiagramCreator();
	}

	@Test
	public void canCreateAggregatePackage() {
		// given
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		aggregate.getDomainObjects().add(TacticdslFactory.eINSTANCE.createSimpleDomainObject());

		// when
		String plantUML = this.creator.createDiagram(aggregate);

		// then
		assertTrue(plantUML.contains("package \"'testAggregate' Aggregate\" <<Rectangle>> {" + System.lineSeparator() + "}" + System.lineSeparator()));
	}

	@Test
	public void canCreateClassFromEntity() {
		// given
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
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
		aggregate.getDomainObjects().add(entity);

		// when
		String plantUML = this.creator.createDiagram(aggregate);

		// then
		assertTrue(plantUML.contains("	class Test <<(E,DarkSeaGreen) Entity>> {" + System.lineSeparator() + "		int amount" + System.lineSeparator()
				+ "		List<String> myList" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void createsNoteIfBoundedContextIsEmpty() {
		// given
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();

		// when
		String plantUML = this.creator.createDiagram(aggregate);

		// then
		assertTrue(plantUML.contains("note \"" + ValidationMessages.EMPTY_UML_CLASS_DIAGRAM_MESSAGE_AGGREGATE + "\" as EmptyDiagramError" + System.lineSeparator()));
	}


	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/plantuml/";
	}

}

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
package org.contextmapper.dsl.tests.generators;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.generator.plantuml.PlantUMLClassDiagramCreator;
import org.contextmapper.dsl.validation.ValidationMessages;
import org.contextmapper.tactic.dsl.tacticdsl.Aggregate;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.CommandEvent;
import org.contextmapper.tactic.dsl.tacticdsl.DomainEvent;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Enum;
import org.contextmapper.tactic.dsl.tacticdsl.EnumValue;
import org.contextmapper.tactic.dsl.tacticdsl.Module;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlantUMLClassDiagramCreatorTest {

	private PlantUMLClassDiagramCreator creator;

	@BeforeEach
	public void prepare() {
		this.creator = new PlantUMLClassDiagramCreator();
	}

	@Test
	public void canCreatePackageFromModule() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Module testModule = TacticdslFactory.eINSTANCE.createModule();
		testModule.setName("mySuperModule");
		boundedContext.getModules().add(testModule);
		testModule.getDomainObjects().add(TacticdslFactory.eINSTANCE.createSimpleDomainObject());

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("package mySuperModule"));
	}

	@Test
	public void canCreatePackageFromModuleWithBasePackage() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Module testModule = TacticdslFactory.eINSTANCE.createModule();
		testModule.setName("mySuperModule");
		testModule.setBasePackage("org.contextmapper");
		boundedContext.getModules().add(testModule);
		Entity testEntity = TacticdslFactory.eINSTANCE.createEntity();
		testEntity.setName("TestEntity");
		testModule.getDomainObjects().add(testEntity);
		
		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("package org.contextmapper.mySuperModule"));
	}

	@Test
	public void canCreateAggregatePackageInModule() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Module testModule = TacticdslFactory.eINSTANCE.createModule();
		testModule.setName("mySuperModule");
		boundedContext.getModules().add(testModule);
		Aggregate aggregate = TacticdslFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		testModule.getAggregates().add(aggregate);
		aggregate.getDomainObjects().add(TacticdslFactory.eINSTANCE.createSimpleDomainObject());

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("package mySuperModule {" + System.lineSeparator()));
		assertTrue(plantUML
				.contains("	package \"'testAggregate' Aggregate\" <<Rectangle>> {" + System.lineSeparator() + "	}" + System.lineSeparator() + "}" + System.lineSeparator()));
	}

	@Test
	public void canCreateAggregatePackage() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = TacticdslFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		aggregate.getDomainObjects().add(TacticdslFactory.eINSTANCE.createSimpleDomainObject());

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("package \"'testAggregate' Aggregate\" <<Rectangle>> {" + System.lineSeparator() + "}" + System.lineSeparator()));
	}

	@Test
	public void canCreateClassFromEntity() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = TacticdslFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		Entity entity = TacticdslFactory.eINSTANCE.createEntity();
		entity.setName("Test");
		Attribute attribute = TacticdslFactory.eINSTANCE.createAttribute();
		attribute.setType("int");
		attribute.setName("amount");
		entity.getAttributes().add(attribute);
		aggregate.getDomainObjects().add(entity);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class Test <<Entity>> {" + System.lineSeparator() + "		int amount" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void canCreateClassFromAggregateRoot() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = TacticdslFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		Entity entity = TacticdslFactory.eINSTANCE.createEntity();
		entity.setName("Test");
		entity.setAggregateRoot(true);
		aggregate.getDomainObjects().add(entity);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class Test <<Aggregate Root>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void canCreateClassFromValueObject() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = TacticdslFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		ValueObject valueObject = TacticdslFactory.eINSTANCE.createValueObject();
		valueObject.setName("TestValue");
		aggregate.getDomainObjects().add(valueObject);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class TestValue <<Value Object>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void canCreateClassFromDomainEvent() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = TacticdslFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		DomainEvent domainEvent = TacticdslFactory.eINSTANCE.createDomainEvent();
		domainEvent.setName("TestEvent");
		aggregate.getDomainObjects().add(domainEvent);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class TestEvent <<Domain Event>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void canCreateClassFromCommandEvent() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = TacticdslFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		CommandEvent commandEvent = TacticdslFactory.eINSTANCE.createCommandEvent();
		commandEvent.setName("TestCommandEvent");
		aggregate.getDomainObjects().add(commandEvent);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class TestCommandEvent <<Command Event>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void canCreateEnum() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = TacticdslFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		Enum enumm = TacticdslFactory.eINSTANCE.createEnum();
		enumm.setName("TestType");
		EnumValue value1 = TacticdslFactory.eINSTANCE.createEnumValue();
		value1.setName("VAL1");
		EnumValue value2 = TacticdslFactory.eINSTANCE.createEnumValue();
		value2.setName("VAL2");
		enumm.getValues().add(value1);
		enumm.getValues().add(value2);
		aggregate.getDomainObjects().add(enumm);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains(
				"	enum TestType {" + System.lineSeparator() + "		VAL1" + System.lineSeparator() + "		VAL2" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void canCreateReferences() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = TacticdslFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		Entity entity1 = TacticdslFactory.eINSTANCE.createEntity();
		entity1.setName("Customer");
		Entity entity2 = TacticdslFactory.eINSTANCE.createEntity();
		entity2.setName("Address");
		Reference reference = TacticdslFactory.eINSTANCE.createReference();
		reference.setDomainObjectType(entity2);
		reference.setName("entity2Ref");
		entity1.getReferences().add(reference);
		aggregate.getDomainObjects().add(entity1);
		aggregate.getDomainObjects().add(entity2);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class Address <<Entity>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
		assertTrue(plantUML
				.contains("	class Customer <<Entity>> {" + System.lineSeparator() + "		Address entity2Ref" + System.lineSeparator() + "	}" + System.lineSeparator()));
		assertTrue(plantUML.contains("Customer --> Address" + System.lineSeparator()));
	}
	
	@Test
	public void createsNoteIfBoundedContextIsEmpty() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		
		// when
		String plantUML = this.creator.createDiagram(boundedContext);
		
		// then
		assertTrue(plantUML.contains("note \"" + ValidationMessages.EMPTY_UML_CLASS_DIAGRAM_MESSAGE + "\" as EmptyDiagramError" + System.lineSeparator()));
	}

}

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
package org.contextmapper.dsl.generators.plantuml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.Application;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.generator.plantuml.PlantUMLBoundedContextClassDiagramCreator;
import org.contextmapper.dsl.validation.ValidationMessages;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.CollectionType;
import org.contextmapper.tactic.dsl.tacticdsl.CommandEvent;
import org.contextmapper.tactic.dsl.tacticdsl.ComplexType;
import org.contextmapper.tactic.dsl.tacticdsl.DomainEvent;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObjectOperation;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Enum;
import org.contextmapper.tactic.dsl.tacticdsl.EnumValue;
import org.contextmapper.tactic.dsl.tacticdsl.Parameter;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.Service;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlantUMLBoundedContextClassDiagramCreatorTest extends AbstractCMLInputFileTest {

	private PlantUMLBoundedContextClassDiagramCreator creator;

	@BeforeEach
	public void prepare() {
		super.prepare();
		this.creator = new PlantUMLBoundedContextClassDiagramCreator();
	}

	@Test
	public void canCreatePackageFromModule() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		SculptorModule testModule = ContextMappingDSLFactory.eINSTANCE.createSculptorModule();
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
		SculptorModule testModule = ContextMappingDSLFactory.eINSTANCE.createSculptorModule();
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
		SculptorModule testModule = ContextMappingDSLFactory.eINSTANCE.createSculptorModule();
		testModule.setName("mySuperModule");
		boundedContext.getModules().add(testModule);
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
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
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
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
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
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
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class Test <<(E,DarkSeaGreen) Entity>> {" + System.lineSeparator() + "		int amount" + System.lineSeparator()
				+ "		List<String> myList" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}
	
	@Test
	public void canUseServicesOutsideAggregates() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("class-diagram-generation-services-test-1.cml").getContextMappingModel();
		BoundedContext boundedContext = model.getBoundedContexts().get(0);
		
		// when
		String plantUML = this.creator.createDiagram(boundedContext);
		
		// then
		assertEquals("@startuml" + System.lineSeparator()
				+ System.lineSeparator()
				+ "skinparam componentStyle uml2"
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "package \"'QuoteRequest' Aggregate\" <<Rectangle>> {" + System.lineSeparator()
				+ "	class QuoteRequest <<(A,#fffab8) Aggregate Root>> {" + System.lineSeparator()
				+ "	}" + System.lineSeparator()
				+ "	enum RequestState {" + System.lineSeparator()
				+ "		REQUEST_SUBMITTED" + System.lineSeparator()
				+ "		QUOTE_RECEIVED" + System.lineSeparator()
				+ "		REQUEST_REJECTED" + System.lineSeparator()
				+ "		QUOTE_ACCEPTED" + System.lineSeparator()
				+ "		QUOTE_REJECTED" + System.lineSeparator()
				+ "		QUOTE_EXPIRED" + System.lineSeparator()
				+ "		POLICY_CREATED" + System.lineSeparator()
				+ "	}" + System.lineSeparator()
				+ "}" + System.lineSeparator()
				+ "package \"'AnotherAggregateThatMustBeIgnored' Aggregate\" <<Rectangle>> {" + System.lineSeparator()
				+ "	enum States {" + System.lineSeparator()
				+ "		REQUEST_SUBMITTED" + System.lineSeparator()
				+ "		QUOTE_RECEIVED" + System.lineSeparator()
				+ "		REQUEST_REJECTED" + System.lineSeparator()
				+ "		QUOTE_ACCEPTED" + System.lineSeparator()
				+ "		QUOTE_REJECTED" + System.lineSeparator()
				+ "		QUOTE_EXPIRED" + System.lineSeparator()
				+ "		POLICY_CREATED" + System.lineSeparator()
				+ "	}" + System.lineSeparator()
				+ "}" + System.lineSeparator()
				+ "class QuoteRequestService <<(S,DarkSeaGreen) Service>> {" + System.lineSeparator()
				+ "	void testOp()" + System.lineSeparator()
				+ "	void submitRequest(QuoteRequest request)" + System.lineSeparator()
				+ "	void rejectRequest(QuoteRequest request)" + System.lineSeparator()
				+ "	void receiveQuote(QuoteRequest request)" + System.lineSeparator()
				+ "	void checkQuote(QuoteRequest request)" + System.lineSeparator()
				+ "	void createPolicy(QuoteRequest request)" + System.lineSeparator()
				+ "}" + System.lineSeparator()
				+ System.lineSeparator()
				+ System.lineSeparator()
				+ "@enduml" + System.lineSeparator(), plantUML);
	}

	@Test
	public void respectNullableOnAttributes() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		Entity entity = TacticdslFactory.eINSTANCE.createEntity();
		entity.setName("Test");
		Attribute attribute = TacticdslFactory.eINSTANCE.createAttribute();
		attribute.setType("int");
		attribute.setName("amount");
		attribute.setNullable(true);
		entity.getAttributes().add(attribute);
		aggregate.getDomainObjects().add(entity);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains(
				"	class Test <<(E,DarkSeaGreen) Entity>> {" + System.lineSeparator() + "		int[0..1] amount" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void respectNullableOnReferences() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		Entity entity = TacticdslFactory.eINSTANCE.createEntity();
		entity.setName("Test");
		Entity referencedEntity = TacticdslFactory.eINSTANCE.createEntity();
		referencedEntity.setName("ReferencedEntity");
		Reference reference = TacticdslFactory.eINSTANCE.createReference();
		reference.setName("otherEntity");
		reference.setDomainObjectType(referencedEntity);
		reference.setNullable(true);
		entity.getReferences().add(reference);
		aggregate.getDomainObjects().add(entity);
		aggregate.getDomainObjects().add(referencedEntity);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class Test <<(E,DarkSeaGreen) Entity>> {" + System.lineSeparator() + "		ReferencedEntity[0..1] otherEntity" + System.lineSeparator()
				+ "	}" + System.lineSeparator()));
	}

	@Test
	public void associationLabelOnReferencesIsUsed() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		Entity entity = TacticdslFactory.eINSTANCE.createEntity();
		entity.setName("Test");
		Entity referencedEntity = TacticdslFactory.eINSTANCE.createEntity();
		referencedEntity.setName("ReferencedEntity");
		Reference reference = TacticdslFactory.eINSTANCE.createReference();
		reference.setAssociationLabel("uses");
		reference.setName("otherEntity");
		reference.setDomainObjectType(referencedEntity);
		entity.getReferences().add(reference);
		aggregate.getDomainObjects().add(entity);
		aggregate.getDomainObjects().add(referencedEntity);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("Test --> ReferencedEntity : uses"));
	}

	@Test
	public void canCreateClassFromAggregateRoot() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		Entity entity = TacticdslFactory.eINSTANCE.createEntity();
		entity.setName("Test");
		entity.setAggregateRoot(true);
		aggregate.getDomainObjects().add(entity);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class Test <<(A,#fffab8) Aggregate Root>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void canCreateClassFromValueObject() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		ValueObject valueObject = TacticdslFactory.eINSTANCE.createValueObject();
		valueObject.setName("TestValue");
		aggregate.getDomainObjects().add(valueObject);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class TestValue <<(V,DarkSeaGreen) Value Object>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void canCreateClassFromDomainEvent() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		DomainEvent domainEvent = TacticdslFactory.eINSTANCE.createDomainEvent();
		domainEvent.setName("TestEvent");
		aggregate.getDomainObjects().add(domainEvent);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class TestEvent <<(E,#ff9f4b) Domain Event>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void canCreateClassFromCommandEvent() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		CommandEvent commandEvent = TacticdslFactory.eINSTANCE.createCommandEvent();
		commandEvent.setName("TestCommandEvent");
		aggregate.getDomainObjects().add(commandEvent);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class TestCommandEvent <<(C,#3bc5e9) Command>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void canCreateEnum() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
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
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		Entity customer = TacticdslFactory.eINSTANCE.createEntity();
		customer.setName("Customer");
		Entity address = TacticdslFactory.eINSTANCE.createEntity();
		address.setName("Address");
		Entity anotherObject = TacticdslFactory.eINSTANCE.createEntity();
		anotherObject.setName("AnotherObject");
		Reference reference = TacticdslFactory.eINSTANCE.createReference();
		reference.setDomainObjectType(address);
		reference.setName("entity2Ref");
		Reference listReference = TacticdslFactory.eINSTANCE.createReference();
		listReference.setCollectionType(CollectionType.LIST);
		listReference.setName("myListReference");
		listReference.setDomainObjectType(anotherObject);
		customer.getReferences().add(reference);
		customer.getReferences().add(listReference);
		aggregate.getDomainObjects().add(customer);
		aggregate.getDomainObjects().add(address);
		aggregate.getDomainObjects().add(anotherObject);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class Address <<(E,DarkSeaGreen) Entity>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
		assertTrue(plantUML.contains("	class Customer <<(E,DarkSeaGreen) Entity>> {" + System.lineSeparator() + "		Address entity2Ref" + System.lineSeparator()
				+ "		List<AnotherObject> myListReference" + System.lineSeparator() + "	}" + System.lineSeparator()));
		assertTrue(plantUML.contains("Customer --> Address : entity2Ref" + System.lineSeparator()));
		assertTrue(plantUML.contains("Customer \"1\" *--> \"*\" AnotherObject : myListReference" + System.lineSeparator()));
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

	@Test
	public void createsNoteForImplementedSubdomain() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Subdomain subdomain = ContextMappingDSLFactory.eINSTANCE.createSubdomain();
		subdomain.setName("mySubdomain");
		boundedContext.setName("myBoundedContext");
		boundedContext.getImplementedDomainParts().add(subdomain);
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		aggregate.getDomainObjects().add(TacticdslFactory.eINSTANCE.createSimpleDomainObject());

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("legend left"));
		assertTrue(plantUML.contains("  This bounded context implements the subdomain '" + subdomain.getName() + "'." + System.lineSeparator()));
		assertTrue(plantUML.contains("end legend"));
	}

	@Test
	public void createsNoteForImplementedDomain() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Subdomain subdomain1 = ContextMappingDSLFactory.eINSTANCE.createSubdomain();
		subdomain1.setName("mySubdomain1");
		Subdomain subdomain2 = ContextMappingDSLFactory.eINSTANCE.createSubdomain();
		subdomain2.setName("mySubdomain2");
		Domain domain = ContextMappingDSLFactory.eINSTANCE.createDomain();
		domain.setName("TestDomain");
		domain.getSubdomains().add(subdomain1);
		domain.getSubdomains().add(subdomain2);
		boundedContext.setName("myBoundedContext");
		boundedContext.getImplementedDomainParts().add(domain);
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		aggregate.getDomainObjects().add(TacticdslFactory.eINSTANCE.createSimpleDomainObject());

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("legend left"));
		assertTrue(plantUML.contains("  This bounded context implements the subdomain '" + subdomain1.getName() + "'." + System.lineSeparator()));
		assertTrue(plantUML.contains("  This bounded context implements the subdomain '" + subdomain2.getName() + "'." + System.lineSeparator()));
		assertTrue(plantUML.contains("end legend"));
	}

	@Test
	public void createsNoteForImplementedSubdomainWithEntities() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Subdomain subdomain = ContextMappingDSLFactory.eINSTANCE.createSubdomain();
		subdomain.setName("mySubdomain");
		boundedContext.setName("myBoundedContext");
		boundedContext.getImplementedDomainParts().add(subdomain);
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		aggregate.getDomainObjects().add(TacticdslFactory.eINSTANCE.createSimpleDomainObject());

		Entity entity1 = TacticdslFactory.eINSTANCE.createEntity();
		Entity entity2 = TacticdslFactory.eINSTANCE.createEntity();
		entity1.setName("TestEntity1");
		entity2.setName("TestEntity2");

		subdomain.getEntities().add(entity1);
		subdomain.getEntities().add(entity2);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("legend left"));
		assertTrue(plantUML
				.contains("  This bounded context implements the subdomain '" + subdomain.getName() + "', which contains the following entities:" + System.lineSeparator()));
		assertTrue(plantUML.contains("   - TestEntity1"));
		assertTrue(plantUML.contains("   - TestEntity2"));
		assertTrue(plantUML.contains("end legend"));
	}

	@Test
	public void createsNoteForRefinedBoundedContext() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext.setName("myBoundedContext");
		BoundedContext refinedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		refinedContext.setName("superContext");
		boundedContext.setRefinedBoundedContext(refinedContext);
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		aggregate.getDomainObjects().add(TacticdslFactory.eINSTANCE.createSimpleDomainObject());

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("legend left"));
		assertTrue(plantUML.contains("  This Bounded Context 'myBoundedContext' refines the 'superContext' Bounded Context." + System.lineSeparator()));
		assertTrue(plantUML.contains("end legend"));
	}

	@Test
	public void canCreateMethodForDomainObjectOperations() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		Entity entity = TacticdslFactory.eINSTANCE.createEntity();
		entity.setName("Test");
		DomainObjectOperation operation = TacticdslFactory.eINSTANCE.createDomainObjectOperation();
		operation.setName("doSomething");
		ComplexType returnType = TacticdslFactory.eINSTANCE.createComplexType();
		returnType.setType("ReturnType");
		operation.setReturnType(returnType);
		ComplexType parameterTypeString = TacticdslFactory.eINSTANCE.createComplexType();
		parameterTypeString.setType("String");
		Parameter parameter = TacticdslFactory.eINSTANCE.createParameter();
		parameter.setName("someParameter");
		parameter.setParameterType(parameterTypeString);
		operation.getParameters().add(parameter);
		entity.getOperations().add(operation);
		aggregate.getDomainObjects().add(entity);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class Test <<(E,DarkSeaGreen) Entity>> {" + System.lineSeparator() + "		ReturnType doSomething(String someParameter)"
				+ System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	private String generatePlantUMLFromFile(String file) throws IOException {
		CMLResource input = getResourceCopyOfTestCML(file);
		BoundedContext bc = input.getContextMappingModel().getBoundedContexts().get(0);

		return this.creator.createDiagram(bc);
	}

	@Test
	public void usesAssociationLabelOfReference() throws IOException {
		String plantUML = generatePlantUMLFromFile("associations.cml");

		assertTrue(plantUML.contains("Customer --> Thing : things"));
	}

	@Test
	public void manyToManyRelationsAreRecognized() throws IOException {
		String plantUML = generatePlantUMLFromFile("associations.cml");

		assertTrue(plantUML.contains("ManyToManyFirst \"*\" <--> \"*\" ManyToManySecond : interact"));
	}

	@Test
	public void suppressesMultiplicityForOneToOne() throws IOException {
		String plantUML = generatePlantUMLFromFile("associations.cml");

		assertTrue(plantUML.contains("Address --> City : city"));
	}

	@Test
	public void showsStarAsMultiplicityForCollection() throws IOException {
		String plantUML = generatePlantUMLFromFile("associations.cml");

		assertTrue(plantUML.contains("Customer \"1\" *--> \"*\" City : cities"));
	}

	@Test
	public void showsSizePropertyAsMultiplicityWhenPresent() throws IOException {
		String plantUML = generatePlantUMLFromFile("associations.cml");

		assertTrue(plantUML.contains("Customer \"1\" *--> \"1..4\" City : towns"));
	}

	@Test
	public void showCompositionForCollectionReferenceWithinAggregate() throws IOException {
		String plantUML = generatePlantUMLFromFile("associations.cml");

		assertTrue(plantUML.contains("Customer \"1\" *--> \"*\" City : cities"));
	}

	@Test
	public void showAggregationForCollectionReferenceOutsideAggregate() throws IOException {
		String plantUML = generatePlantUMLFromFile("associations.cml");

		assertTrue(plantUML.contains("Customer \"1\" o--> \"*\" Order : orders"));
	}

	@Test
	public void showLabelWhenSpecifiedInAssociation() throws IOException {
		String plantUML = generatePlantUMLFromFile("associations.cml");

		assertTrue(plantUML.contains("Customer -- ReturnTypeEntity : creates >"));
		assertTrue(plantUML.contains("Customer -- Name : uses >"));
	}

	@Test
	public void canCreateMethodForDomainObjectOperationsWithDomainObjectType() throws IOException {
		// given
		String inputModelName = "operations-domain-object-types-test.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);
		BoundedContext bc = input.getContextMappingModel().getBoundedContexts().get(0);

		// when
		String plantUML = this.creator.createDiagram(bc);

		// then
		assertTrue(plantUML.contains("	class Customer <<(A,#fffab8) Aggregate Root>> {" + System.lineSeparator() + "		ReturnType updateAddress(Address address)"
				+ System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void canCreateMethodForOperationsWithCollectionTypes() throws IOException {
		// given
		String inputModelName = "operations-with-list-types-test.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);
		BoundedContext bc = input.getContextMappingModel().getBoundedContexts().get(0);

		// when
		String plantUML = this.creator.createDiagram(bc);

		// then
		assertTrue(plantUML.contains("	class Customer <<(A,#fffab8) Aggregate Root>> {" + System.lineSeparator() + "		List<ReturnType> updateAddress(Address address)"
				+ System.lineSeparator() + "		void anotherMethod(Set<Address> addresses)" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void canCreateMethodForOperationsWithoutReturnTypes() throws IOException {
		// given
		String inputModelName = "operations-with-no-return-value.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);
		BoundedContext bc = input.getContextMappingModel().getBoundedContexts().get(0);

		// when
		String plantUML = this.creator.createDiagram(bc);

		// then
		assertTrue(plantUML.contains("	class Customer <<(A,#fffab8) Aggregate Root>> {" + System.lineSeparator() + "		void anotherMethod(String param)"
				+ System.lineSeparator() + "		void yetAnotherMethod(String param)" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void canCreateService() throws IOException {
		// given
		String inputModelName = "services-test.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);
		BoundedContext bc = input.getContextMappingModel().getBoundedContexts().get(0);

		// when
		String plantUML = this.creator.createDiagram(bc);

		// then
		assertTrue(plantUML.contains("	class MyService <<(S,DarkSeaGreen) Service>> {" + System.lineSeparator() + "		ReturnType serviceMethod(Address address)"
				+ System.lineSeparator() + "	}" + System.lineSeparator()));
		assertTrue(plantUML.contains("	class MyModuleService <<(S,DarkSeaGreen) Service>> {" + System.lineSeparator() + "		void myModuleServiceMethod()"
				+ System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void canCreateInheritance4Entity() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		Entity entity1 = TacticdslFactory.eINSTANCE.createEntity();
		entity1.setName("Customer");
		Entity entity2 = TacticdslFactory.eINSTANCE.createEntity();
		entity2.setName("AbstractEntity");
		entity1.setExtends(entity2);
		aggregate.getDomainObjects().add(entity1);
		aggregate.getDomainObjects().add(entity2);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class Customer <<(E,DarkSeaGreen) Entity>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
		assertTrue(plantUML.contains("	class AbstractEntity <<(E,DarkSeaGreen) Entity>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
		assertTrue(plantUML.contains("Customer --|> AbstractEntity" + System.lineSeparator()));
	}

	@Test
	public void canCreateInheritance4VO() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		ValueObject vo1 = TacticdslFactory.eINSTANCE.createValueObject();
		vo1.setName("Customer");
		ValueObject vo2 = TacticdslFactory.eINSTANCE.createValueObject();
		vo2.setName("AbstractVO");
		vo1.setExtends(vo2);
		aggregate.getDomainObjects().add(vo1);
		aggregate.getDomainObjects().add(vo2);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class Customer <<(V,DarkSeaGreen) Value Object>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
		assertTrue(plantUML.contains("	class AbstractVO <<(V,DarkSeaGreen) Value Object>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
		assertTrue(plantUML.contains("Customer --|> AbstractVO" + System.lineSeparator()));
	}

	@Test
	public void canCreateInheritance4CommandEvent() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		CommandEvent event1 = TacticdslFactory.eINSTANCE.createCommandEvent();
		event1.setName("Customer");
		CommandEvent event2 = TacticdslFactory.eINSTANCE.createCommandEvent();
		event2.setName("AbstractEvent");
		event1.setExtends(event2);
		aggregate.getDomainObjects().add(event1);
		aggregate.getDomainObjects().add(event2);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class Customer <<(C,#3bc5e9) Command>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
		assertTrue(plantUML.contains("	class AbstractEvent <<(C,#3bc5e9) Command>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
		assertTrue(plantUML.contains("Customer --|> AbstractEvent" + System.lineSeparator()));
	}

	@Test
	public void canCreateInheritance4DomainEvent() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		DomainEvent event1 = TacticdslFactory.eINSTANCE.createDomainEvent();
		event1.setName("Customer");
		DomainEvent event2 = TacticdslFactory.eINSTANCE.createDomainEvent();
		event2.setName("AbstractEvent");
		event1.setExtends(event2);
		aggregate.getDomainObjects().add(event1);
		aggregate.getDomainObjects().add(event2);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class Customer <<(E,#ff9f4b) Domain Event>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
		assertTrue(plantUML.contains("	class AbstractEvent <<(E,#ff9f4b) Domain Event>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
		assertTrue(plantUML.contains("Customer --|> AbstractEvent" + System.lineSeparator()));
	}

	@Test
	public void canCreateInheritance4UndefinedBaseType() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		Entity entity1 = TacticdslFactory.eINSTANCE.createEntity();
		entity1.setName("Customer");
		entity1.setExtendsName("AbstractEntity");
		aggregate.getDomainObjects().add(entity1);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class Customer <<(E,DarkSeaGreen) Entity>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
		assertTrue(plantUML.contains("Customer --|> AbstractEntity" + System.lineSeparator()));
	}

	@Test
	public void respectEventsAndCommandsAndServicesFromApplicationLayer() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Application app = ContextMappingDSLFactory.eINSTANCE.createApplication();
		boundedContext.setApplication(app);
		DomainEvent testEvent = TacticdslFactory.eINSTANCE.createDomainEvent();
		testEvent.setName("TestEvent");
		app.getEvents().add(testEvent);
		CommandEvent testCommand = TacticdslFactory.eINSTANCE.createCommandEvent();
		testCommand.setName("TestCommand");
		app.getCommands().add(testCommand);
		Service testService = TacticdslFactory.eINSTANCE.createService();
		testService.setName("TestService");
		app.getServices().add(testService);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("package \"'Application'\" <<Rectangle>> {" + System.lineSeparator()));
		assertTrue(plantUML.contains("	class TestEvent <<(E,#ff9f4b) Domain Event>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
		assertTrue(plantUML.contains("	class TestCommand <<(C,#3bc5e9) Command>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
		assertTrue(plantUML.contains("	class TestService <<(S,DarkSeaGreen) Service>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void respectApplicationLayerName() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Application app = ContextMappingDSLFactory.eINSTANCE.createApplication();
		app.setName("MyAppLayer");
		boundedContext.setApplication(app);
		DomainEvent testEvent = TacticdslFactory.eINSTANCE.createDomainEvent();
		testEvent.setName("TestEvent");
		app.getEvents().add(testEvent);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("package \"'MyAppLayer'\" <<Rectangle>> {" + System.lineSeparator()));
		assertTrue(plantUML.contains("	class TestEvent <<(E,#ff9f4b) Domain Event>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void canAddCommentThatAppLayerContainsFlow() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("application-layer-test-1.cml");
		BoundedContext bc = input.getContextMappingModel().getBoundedContexts().get(0);

		// when
		String plantUML = this.creator.createDiagram(bc);

		// then
		assertTrue(plantUML.contains("package \"'Application'\" <<Rectangle>> {" + System.lineSeparator()));
		assertTrue(plantUML.contains("	legend left" + System.lineSeparator()));
		assertTrue(plantUML.contains("		This application layer contains flow definitions (visualization available via BPMN Sketch Miner)." + System.lineSeparator()));
		assertTrue(plantUML.contains("	end legend" + System.lineSeparator()));
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/plantuml/";
	}

}

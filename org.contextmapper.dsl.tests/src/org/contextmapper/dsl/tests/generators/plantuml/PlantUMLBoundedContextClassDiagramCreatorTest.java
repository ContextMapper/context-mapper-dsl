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
package org.contextmapper.dsl.tests.generators.plantuml;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Module;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.generator.plantuml.PlantUMLBoundedContextClassDiagramCreator;
import org.contextmapper.dsl.tests.AbstractCMLInputFileTest;
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
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Iterators;

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
		Module testModule = ContextMappingDSLFactory.eINSTANCE.createModule();
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
		Module testModule = ContextMappingDSLFactory.eINSTANCE.createModule();
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
		Module testModule = ContextMappingDSLFactory.eINSTANCE.createModule();
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
		assertTrue(plantUML.contains("	class Test <<Entity>> {" + System.lineSeparator() + "		int amount" + System.lineSeparator() + "		List<String> myList"
				+ System.lineSeparator() + "	}" + System.lineSeparator()));
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
		assertTrue(plantUML.contains("	class Test <<Aggregate Root>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
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
		assertTrue(plantUML.contains("	class TestValue <<Value Object>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
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
		assertTrue(plantUML.contains("	class TestEvent <<Domain Event>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
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
		assertTrue(plantUML.contains("	class TestCommandEvent <<Command Event>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
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
		Entity entity1 = TacticdslFactory.eINSTANCE.createEntity();
		entity1.setName("Customer");
		Entity entity2 = TacticdslFactory.eINSTANCE.createEntity();
		entity2.setName("Address");
		Entity entity3 = TacticdslFactory.eINSTANCE.createEntity();
		entity3.setName("AnotherObject");
		Reference reference = TacticdslFactory.eINSTANCE.createReference();
		reference.setDomainObjectType(entity2);
		reference.setName("entity2Ref");
		Reference listReference = TacticdslFactory.eINSTANCE.createReference();
		listReference.setCollectionType(CollectionType.LIST);
		listReference.setName("myListReference");
		listReference.setDomainObjectType(entity3);
		entity1.getReferences().add(reference);
		entity1.getReferences().add(listReference);
		aggregate.getDomainObjects().add(entity1);
		aggregate.getDomainObjects().add(entity2);
		aggregate.getDomainObjects().add(entity3);

		// when
		String plantUML = this.creator.createDiagram(boundedContext);

		// then
		assertTrue(plantUML.contains("	class Address <<Entity>> {" + System.lineSeparator() + "	}" + System.lineSeparator()));
		assertTrue(plantUML.contains("	class Customer <<Entity>> {" + System.lineSeparator() + "		Address entity2Ref" + System.lineSeparator()
				+ "		List<AnotherObject> myListReference" + System.lineSeparator() + "	}" + System.lineSeparator()));
		assertTrue(plantUML.contains("Customer --> Address" + System.lineSeparator()));
		assertTrue(plantUML.contains("Customer --> AnotherObject" + System.lineSeparator()));
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
		boundedContext.getImplementedSubdomains().add(subdomain);
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
	public void createsNoteForImplementedSubdomainWithEntities() {
		// given
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		Subdomain subdomain = ContextMappingDSLFactory.eINSTANCE.createSubdomain();
		subdomain.setName("mySubdomain");
		boundedContext.setName("myBoundedContext");
		boundedContext.getImplementedSubdomains().add(subdomain);
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
		assertTrue(plantUML.contains("	class Test <<Entity>> {" + System.lineSeparator() + "		ReturnType doSomething(String someParameter)" + System.lineSeparator() + "	}"
				+ System.lineSeparator()));
	}

	@Test
	public void canCreateMethodForDomainObjectOperationsWithDomainObjectType() throws IOException {
		// given
		String inputModelName = "operations-domain-object-types-test.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		List<ContextMappingModel> models = IteratorExtensions.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(input.getAllContents(), ContextMappingModel.class));
		BoundedContext bc = models.get(0).getBoundedContexts().get(0);

		// when
		String plantUML = this.creator.createDiagram(bc);

		// then
		assertTrue(plantUML.contains("	class Customer <<Aggregate Root>> {" + System.lineSeparator() + "		ReturnType updateAddress(Address address)" + System.lineSeparator()
				+ "	}" + System.lineSeparator()));
	}

	@Test
	public void canCreateMethodForOperationsWithCollectionTypes() throws IOException {
		// given
		String inputModelName = "operations-with-list-types-test.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		List<ContextMappingModel> models = IteratorExtensions.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(input.getAllContents(), ContextMappingModel.class));
		BoundedContext bc = models.get(0).getBoundedContexts().get(0);

		// when
		String plantUML = this.creator.createDiagram(bc);

		// then
		assertTrue(plantUML.contains("	class Customer <<Aggregate Root>> {" + System.lineSeparator() + "		List<ReturnType> updateAddress(Address address)"
				+ System.lineSeparator() + "		void anotherMethod(Set<Address> addresses)" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void canCreateMethodForOperationsWithoutReturnTypes() throws IOException {
		// given
		String inputModelName = "operations-with-no-return-value.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		List<ContextMappingModel> models = IteratorExtensions.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(input.getAllContents(), ContextMappingModel.class));
		BoundedContext bc = models.get(0).getBoundedContexts().get(0);

		// when
		String plantUML = this.creator.createDiagram(bc);

		// then
		assertTrue(plantUML.contains("	class Customer <<Aggregate Root>> {" + System.lineSeparator() + "		void anotherMethod(String param)" + System.lineSeparator()
				+ "		void yetAnotherMethod(String param)" + System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void canCreateService() throws IOException {
		// given
		String inputModelName = "services-test.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		List<ContextMappingModel> models = IteratorExtensions.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(input.getAllContents(), ContextMappingModel.class));
		BoundedContext bc = models.get(0).getBoundedContexts().get(0);

		// when
		String plantUML = this.creator.createDiagram(bc);

		// then
		assertTrue(plantUML.contains("	class MyService <<Service>> {" + System.lineSeparator() + "		ReturnType serviceMethod(Address address)" + System.lineSeparator() + "	}"
				+ System.lineSeparator()));
		assertTrue(plantUML.contains("	class MyModuleService <<Service>> {" + System.lineSeparator() + "		void myModuleServiceMethod()" + System.lineSeparator() + "	}"
				+ System.lineSeparator()));
	}
	
	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/plantuml/";
	}

}

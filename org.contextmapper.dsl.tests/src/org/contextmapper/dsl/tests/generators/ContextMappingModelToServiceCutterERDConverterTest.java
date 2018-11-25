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
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.servicecutter.input.converter.ContextMappingModelToServiceCutterERDConverter;
import org.contextmapper.dsl.generator.servicecutter.input.model.EntityRelationshipDiagram;
import org.contextmapper.dsl.generator.servicecutter.input.model.Relationtype;
import org.contextmapper.tactic.dsl.tacticdsl.Aggregate;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.DomainEvent;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContextMappingModelToServiceCutterERDConverterTest {

	private ContextMappingModelToServiceCutterERDConverter converter;

	@BeforeEach
	public void prepare() {
		this.converter = new ContextMappingModelToServiceCutterERDConverter();
	}

	@Test
	public void canCreateEntity4BoundedContext() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext.setName("testBC");
		contextMap.getBoundedContexts().add(boundedContext);

		// when
		EntityRelationshipDiagram scDiagram = this.converter.convert("TestModel", contextMap);

		// then
		assertEquals("TestModel", scDiagram.getName());
		assertEquals(1, scDiagram.getEntities().size());
		assertEquals("testBC_BC", scDiagram.getEntities().get(0).getName());
		assertEquals(0, scDiagram.getEntities().get(0).getNanoentities().size());
	}

	@Test
	public void canCreateEntity4Aggregate() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext.setName("testBC");
		contextMap.getBoundedContexts().add(boundedContext);
		Aggregate aggregate = TacticdslFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);

		// when
		EntityRelationshipDiagram scDiagram = this.converter.convert("TestModel", contextMap);

		// then
		assertEquals(2, scDiagram.getEntities().size());
		List<String> entityNames = scDiagram.getEntities().stream().map(e -> e.getName()).collect(Collectors.toList());
		assertTrue(entityNames.contains("testBC_BC"));
		assertTrue(entityNames.contains("testAggregate_Aggregate"));
		assertEquals(0, scDiagram.getEntities().get(0).getNanoentities().size());
		assertEquals(0, scDiagram.getEntities().get(1).getNanoentities().size());
	}

	@Test
	public void canCreateEntity4Entity() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext.setName("testBC");
		contextMap.getBoundedContexts().add(boundedContext);
		Aggregate aggregate = TacticdslFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		Entity entity = TacticdslFactory.eINSTANCE.createEntity();
		entity.setName("TestEntity");
		Attribute attribute = TacticdslFactory.eINSTANCE.createAttribute();
		attribute.setName("attribute1");
		aggregate.getDomainObjects().add(entity);
		entity.getAttributes().add(attribute);

		// when
		EntityRelationshipDiagram scDiagram = this.converter.convert("TestModel", contextMap);

		// then
		List<String> entityNames = scDiagram.getEntities().stream().map(e -> e.getName()).collect(Collectors.toList());
		assertTrue(entityNames.contains("TestEntity"));
		org.contextmapper.dsl.generator.servicecutter.input.model.Entity scEntity = getEntity(scDiagram.getEntities(), "TestEntity");
		assertEquals(1, scEntity.getNanoentities().size());
		assertEquals("attribute1", scEntity.getNanoentities().get(0));
	}

	@Test
	public void canCreateEntity4DomainEvent() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext.setName("testBC");
		contextMap.getBoundedContexts().add(boundedContext);
		Aggregate aggregate = TacticdslFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		DomainEvent domainEvent = TacticdslFactory.eINSTANCE.createDomainEvent();
		domainEvent.setName("TestDomainEvent");
		Attribute attribute = TacticdslFactory.eINSTANCE.createAttribute();
		attribute.setName("attribute1");
		aggregate.getDomainObjects().add(domainEvent);
		domainEvent.getAttributes().add(attribute);

		// when
		EntityRelationshipDiagram scDiagram = this.converter.convert("TestModel", contextMap);

		// then
		List<String> entityNames = scDiagram.getEntities().stream().map(e -> e.getName()).collect(Collectors.toList());
		assertTrue(entityNames.contains("TestDomainEvent"));
		org.contextmapper.dsl.generator.servicecutter.input.model.Entity scEntity = getEntity(scDiagram.getEntities(), "TestDomainEvent");
		assertEquals(1, scEntity.getNanoentities().size());
		assertEquals("attribute1", scEntity.getNanoentities().get(0));
	}

	@Test
	public void canCreateEntity4ValueObject() {
		// given
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext.setName("testBC");
		contextMap.getBoundedContexts().add(boundedContext);
		Aggregate aggregate = TacticdslFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		ValueObject valueObject = TacticdslFactory.eINSTANCE.createValueObject();
		valueObject.setName("TestValueObject");
		Attribute attribute = TacticdslFactory.eINSTANCE.createAttribute();
		attribute.setName("attribute1");
		aggregate.getDomainObjects().add(valueObject);
		valueObject.getAttributes().add(attribute);

		// when
		EntityRelationshipDiagram scDiagram = this.converter.convert("TestModel", contextMap);

		// then
		List<String> entityNames = scDiagram.getEntities().stream().map(e -> e.getName()).collect(Collectors.toList());
		assertTrue(entityNames.contains("TestValueObject"));
		org.contextmapper.dsl.generator.servicecutter.input.model.Entity scEntity = getEntity(scDiagram.getEntities(), "TestValueObject");
		assertEquals(1, scEntity.getNanoentities().size());
		assertEquals("attribute1", scEntity.getNanoentities().get(0));
	}

	@Test
	public void canCreateReference() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext.setName("testBC");
		contextMap.getBoundedContexts().add(boundedContext);
		Aggregate aggregate = TacticdslFactory.eINSTANCE.createAggregate();
		aggregate.setName("testAggregate");
		boundedContext.getAggregates().add(aggregate);
		Entity entity1 = TacticdslFactory.eINSTANCE.createEntity();
		Entity entity2 = TacticdslFactory.eINSTANCE.createEntity();
		entity1.setName("Entity1");
		entity2.setName("Entity2");
		Reference reference = TacticdslFactory.eINSTANCE.createReference();
		reference.setDomainObjectType(entity2);
		reference.setName("entity2Ref");
		entity1.getReferences().add(reference);
		aggregate.getDomainObjects().add(entity1);
		aggregate.getDomainObjects().add(entity2);
		model.setMap(contextMap);
		model.getBoundedContexts().add(boundedContext);
		EcoreUtil.resolveAll(contextMap);

		// when
		EntityRelationshipDiagram scDiagram = this.converter.convert("TestModel", contextMap);

		// then
		List<String> relationStrings = scDiagram.getRelations().stream().map(r -> r.getOrigin() + "_" + r.getDestination() + "_" + r.getType()).collect(Collectors.toList());
		assertTrue(relationStrings.contains("Entity1_Entity2_" + Relationtype.AGGREGATION));
	}

	private org.contextmapper.dsl.generator.servicecutter.input.model.Entity getEntity(List<org.contextmapper.dsl.generator.servicecutter.input.model.Entity> entities,
			String name) {
		for (org.contextmapper.dsl.generator.servicecutter.input.model.Entity entity : entities) {
			if (name.equals(entity.getName()))
				return entity;
		}
		return null;
	}

}

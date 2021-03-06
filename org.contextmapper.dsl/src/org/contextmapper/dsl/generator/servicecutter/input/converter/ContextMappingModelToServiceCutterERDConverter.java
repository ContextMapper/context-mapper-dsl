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
package org.contextmapper.dsl.generator.servicecutter.input.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.DomainEvent;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.Event;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;
import org.eclipse.xtext.EcoreUtil2;

import ch.hsr.servicecutter.api.model.Entity;
import ch.hsr.servicecutter.api.model.EntityRelation;
import ch.hsr.servicecutter.api.model.EntityRelation.RelationType;
import ch.hsr.servicecutter.api.model.EntityRelationDiagram;

/**
 * Converter to convert context map to ServiceCutter ERD model.
 * 
 * @author Stefan Kapferer
 *
 */
public class ContextMappingModelToServiceCutterERDConverter {

	private EntityRelationDiagram target;
	private Map<String, DomainObject> dslEntityLookupTable = new HashMap<>();
	private Map<String, Entity> entityLookupTable = new HashMap<>();

	public EntityRelationDiagram convert(String modelName, ContextMappingModel cmlModel) {
		this.target = new EntityRelationDiagram();
		this.target.setName(modelName);
		this.target.setEntities(new ArrayList<>());
		this.target.setRelations(new ArrayList<>());
		initializeEntityLookupTable(cmlModel);
		for (BoundedContext bc : cmlModel.getBoundedContexts()) {
			mapBoundedContext(bc);
		}
		mapReferences4DomainObjects(EcoreUtil2.getAllContentsOfType(cmlModel, SimpleDomainObject.class));
		return target;
	}

	private void mapBoundedContext(BoundedContext bc) {
		Entity boundedContextEntity = new Entity(bc.getName() + "_BC");
		List<Aggregate> allAggregates = EcoreUtil2.<Aggregate>eAllOfType(bc, Aggregate.class);
		for (Aggregate aggregate : allAggregates) {
			// boundedContextEntity.addNanoEntity(aggregate.getName() + "_Aggregate");
			EntityRelation entityRelation = new EntityRelation();
			entityRelation.setOrigin(boundedContextEntity);
			entityRelation.setDestination(mapAggregate(aggregate));
			entityRelation.setType(RelationType.AGGREGATION);
			target.getRelations().add(entityRelation);
		}
		target.getEntities().add(boundedContextEntity);
	}

	private Entity mapAggregate(Aggregate aggregate) {
		Entity aggregrateEntity = new Entity(aggregate.getName() + "_Aggregate");
		for (SimpleDomainObject simpleDomainObject : aggregate.getDomainObjects()) {
			// aggregrateEntity.addNanoEntity(simpleDomainObject.getName());
			if (isDomainObjectUsed4ServiceCutter(simpleDomainObject)) {
				DomainObject dslDomainObject = (DomainObject) simpleDomainObject;
				EntityRelation relation = new EntityRelation();
				relation.setOrigin(aggregrateEntity);
				relation.setDestination(mapDomainObject(dslDomainObject));
				relation.setType(RelationType.AGGREGATION);
				target.getRelations().add(relation);
			}
		}
		target.getEntities().add(aggregrateEntity);
		return aggregrateEntity;
	}

	private boolean isDomainObjectUsed4ServiceCutter(SimpleDomainObject simpleDomainObject) {
		return simpleDomainObject instanceof org.contextmapper.tactic.dsl.tacticdsl.Entity || simpleDomainObject instanceof Event || simpleDomainObject instanceof ValueObject;
	}

	private Entity mapDomainObject(DomainObject dslDomainObject) {
		Entity entityEntity = getEntity(dslDomainObject.getName());
		entityEntity.setNanoentities(new ArrayList<>());
		for (Attribute attribute : dslDomainObject.getAttributes()) {
			entityEntity.getNanoentities().add(attribute.getName());
		}
		for (Reference reference : dslDomainObject.getReferences()) {
			entityEntity.getNanoentities().add(reference.getName());
		}
		target.getEntities().add(entityEntity);
		return entityEntity;
	}

	private void mapReferences4DomainObjects(Collection<SimpleDomainObject> domainObjects) {
		for (SimpleDomainObject domainObject : domainObjects) {
			if (isDomainObjectUsed4ServiceCutter(domainObject))
				mapReferences((DomainObject) domainObject, ((DomainObject) domainObject).getReferences());
		}
	}

	private void mapReferences(DomainObject sourceObject, Collection<Reference> references) {
		for (Reference reference : references) {
			// Handle enums as attributes for now
			if (reference.getDomainObjectType() instanceof org.contextmapper.tactic.dsl.tacticdsl.Enum) {
				entityLookupTable.get(sourceObject.getName()).getNanoentities().add(reference.getName());
			} else {
				String refType = reference.getDomainObjectType().getName();
				// entityEntity.addNanoEntity(reference.getName());
				if (this.dslEntityLookupTable.containsKey(refType)) {
					EntityRelation relation = new EntityRelation();
					relation.setOrigin(entityLookupTable.get(sourceObject.getName()));
					relation.setDestination(getEntity(refType));
					relation.setType(RelationType.AGGREGATION);
					target.getRelations().add(relation);
				}
			}
		}
	}

	private void initializeEntityLookupTable(ContextMappingModel cmlModel) {
		// use Entities
		addDomainObjectToLookupTable(EcoreUtil2.<org.contextmapper.tactic.dsl.tacticdsl.Entity>getAllContentsOfType(cmlModel, org.contextmapper.tactic.dsl.tacticdsl.Entity.class));

		// use Domain Events
		addDomainObjectToLookupTable(EcoreUtil2.<DomainEvent>getAllContentsOfType(cmlModel, DomainEvent.class));

		// use Value Objects
		addDomainObjectToLookupTable(EcoreUtil2.<ValueObject>getAllContentsOfType(cmlModel, ValueObject.class));
	}

	private void addDomainObjectToLookupTable(List<? extends DomainObject> domainObjects) {
		for (DomainObject domainObject : domainObjects) {
			this.dslEntityLookupTable.put(domainObject.getName(), domainObject);
		}
	}

	private Entity getEntity(String name) {
		if (this.entityLookupTable.containsKey(name))
			return this.entityLookupTable.get(name);
		Entity entity = new Entity(name);
		this.entityLookupTable.put(name, entity);
		return entity;
	}
}

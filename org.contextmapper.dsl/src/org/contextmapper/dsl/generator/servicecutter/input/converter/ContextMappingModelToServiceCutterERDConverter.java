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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.generator.servicecutter.input.model.Entity;
import org.contextmapper.dsl.generator.servicecutter.input.model.EntityRelation;
import org.contextmapper.dsl.generator.servicecutter.input.model.EntityRelationshipDiagram;
import org.contextmapper.dsl.generator.servicecutter.input.model.Relationtype;
import org.contextmapper.tactic.dsl.tacticdsl.Aggregate;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.DomainEvent;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;

/**
 * Converter to convert context map to ServiceCutter ERD model.
 * 
 * @author Stefan Kapferer
 *
 */
public class ContextMappingModelToServiceCutterERDConverter {

	private EntityRelationshipDiagram target;
	private Map<String, DomainObject> dslEntityLookupTable = new HashMap<>();
	private Map<String, Entity> entityLookupTable = new HashMap<>();

	public EntityRelationshipDiagram convert(String modelName, ContextMap contextMap) {
		this.target = new EntityRelationshipDiagram(modelName);
		initializeEntityLookupTable(contextMap);
		for (BoundedContext bc : contextMap.getBoundedContexts()) {
			mapBoundedContext(bc);
		}
		return target;
	}

	private void mapBoundedContext(BoundedContext bc) {
		Entity boundedContextEntity = new Entity(bc.getName() + "_BC");
		List<Aggregate> allAggregates = EcoreUtil2.<Aggregate>eAllOfType(bc, Aggregate.class);
		for (Aggregate aggregate : allAggregates) {
			// boundedContextEntity.addNanoEntity(aggregate.getName() + "_Aggregate");
			target.addEntityRelation(new EntityRelation(boundedContextEntity.getName(),
					mapAggregate(aggregate).getName(), Relationtype.AGGREGATION));
		}
		target.addEntity(boundedContextEntity);
	}

	private Entity mapAggregate(Aggregate aggregate) {
		Entity aggregrateEntity = new Entity(aggregate.getName() + "_Aggregate");
		for (SimpleDomainObject simpleDomainObject : aggregate.getDomainObjects()) {
			// aggregrateEntity.addNanoEntity(simpleDomainObject.getName());
			if (isDomainObjectUsed4ServiceCutter(simpleDomainObject)) {
				DomainObject dslDomainObject = (DomainObject) simpleDomainObject;
				target.addEntityRelation(new EntityRelation(aggregrateEntity.getName(),
						mapDomainObject(dslDomainObject).getName(), Relationtype.AGGREGATION));
			}
		}
		target.addEntity(aggregrateEntity);
		return aggregrateEntity;
	}

	private boolean isDomainObjectUsed4ServiceCutter(SimpleDomainObject simpleDomainObject) {
		return simpleDomainObject instanceof org.contextmapper.tactic.dsl.tacticdsl.Entity
				|| simpleDomainObject instanceof DomainEvent || simpleDomainObject instanceof ValueObject;
	}

	private Entity mapDomainObject(DomainObject dslDomainObject) {
		Entity entityEntity = getEntity(dslDomainObject.getName());
		for (Attribute attribute : dslDomainObject.getAttributes()) {
			entityEntity.addNanoEntity(attribute.getName());
		}
		for (Reference reference : dslDomainObject.getReferences()) {
			// Handle enums as attributes for now
			if (reference.getDomainObjectType() instanceof org.contextmapper.tactic.dsl.tacticdsl.Enum) {
				entityEntity.addNanoEntity(reference.getName());
			} else {
				String refType = reference.getDomainObjectType().getName();
				// entityEntity.addNanoEntity(reference.getName());
				if (this.dslEntityLookupTable.containsKey(refType)) {
					target.addEntityRelation(new EntityRelation(entityEntity.getName(), getEntity(refType).getName(),
							Relationtype.AGGREGATION));
				}
			}
		}
		target.addEntity(entityEntity);
		return entityEntity;
	}

	private void initializeEntityLookupTable(ContextMap contextMap) {
		// use Entities
		addDomainObjectToLookupTable(EcoreUtil2.<org.contextmapper.tactic.dsl.tacticdsl.Entity>getAllContentsOfType(
				EcoreUtil.getRootContainer(contextMap), org.contextmapper.tactic.dsl.tacticdsl.Entity.class));

		// use Domain Events
		addDomainObjectToLookupTable(EcoreUtil2
				.<DomainEvent>getAllContentsOfType(EcoreUtil.getRootContainer(contextMap), DomainEvent.class));

		// use Value Objects
		addDomainObjectToLookupTable(EcoreUtil2
				.<ValueObject>getAllContentsOfType(EcoreUtil.getRootContainer(contextMap), ValueObject.class));
	}

	private void addDomainObjectToLookupTable(List<? extends DomainObject> domainObjects) {
		for (DomainObject domainObject : domainObjects) {
			this.dslEntityLookupTable.put(domainObject.getName(), domainObject);
		}
	}

	private Entity getEntity(String name) {
		if (this.entityLookupTable.containsKey(name))
			return this.entityLookupTable.get(name);
		return new Entity(name);
	}
}

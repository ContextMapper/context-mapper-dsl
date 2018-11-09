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
package org.contextmapper.dsl.generator.servicecutter.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.generator.servicecutter.model.Entity;
import org.contextmapper.dsl.generator.servicecutter.model.EntityRelation;
import org.contextmapper.dsl.generator.servicecutter.model.EntityRelationshipDiagram;
import org.contextmapper.dsl.generator.servicecutter.model.Nanoentity;
import org.contextmapper.dsl.generator.servicecutter.model.Relationtype;
import org.contextmapper.tactic.dsl.tacticdsl.Aggregate;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
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
	private Map<String, org.contextmapper.tactic.dsl.tacticdsl.Entity> dslEntityLookupTable = new HashMap<>();
	private Map<String, Entity> entityLookupTable = new HashMap<>();

	public EntityRelationshipDiagram convert(ContextMap contextMap) {
		this.target = new EntityRelationshipDiagram();
		initializeEntityLookupTable(contextMap);
		for (BoundedContext bc : contextMap.getBoundedContexts()) {
			mapBoundedContext(bc);
		}
		return target;
	}

	private void mapBoundedContext(BoundedContext bc) {
		Entity boundedContextEntity = new Entity(bc.getName());
		List<Aggregate> allAggregates = EcoreUtil2.<Aggregate>eAllOfType(EcoreUtil.getRootContainer(bc),
				Aggregate.class);
		for (Aggregate aggregate : allAggregates) {
			boundedContextEntity.addNanoEntity(new Nanoentity(aggregate.getName()));
			target.addEntityRelation(
					new EntityRelation(boundedContextEntity, mapAggregate(aggregate), Relationtype.AGGREGATION));
		}
		target.addEntity(boundedContextEntity);
	}

	private Entity mapAggregate(Aggregate aggregate) {
		Entity aggregrateEntity = new Entity(aggregate.getName());
		for (SimpleDomainObject domainObject : aggregate.getDomainObjects()) {
			if (domainObject instanceof org.contextmapper.tactic.dsl.tacticdsl.Entity) {
				org.contextmapper.tactic.dsl.tacticdsl.Entity dslEntity = (org.contextmapper.tactic.dsl.tacticdsl.Entity) domainObject;
				aggregrateEntity.addNanoEntity(new Nanoentity(dslEntity.getName()));
				target.addEntityRelation(
						new EntityRelation(aggregrateEntity, mapEntity(dslEntity), Relationtype.AGGREGATION));
			}
		}
		target.addEntity(aggregrateEntity);
		return aggregrateEntity;
	}

	private Entity mapEntity(org.contextmapper.tactic.dsl.tacticdsl.Entity dslEntity) {
		Entity entityEntity = getEntity(dslEntity.getName());
		for (Attribute attribute : dslEntity.getAttributes()) {
			entityEntity.addNanoEntity(new Nanoentity(attribute.getName()));
			if (this.dslEntityLookupTable.containsKey(attribute.getType())) {
				target.addEntityRelation(
						new EntityRelation(entityEntity, getEntity(attribute.getType()), Relationtype.AGGREGATION));
			}
		}
		target.addEntity(entityEntity);
		return entityEntity;
	}

	private void initializeEntityLookupTable(ContextMap contextMap) {
		List<org.contextmapper.tactic.dsl.tacticdsl.Entity> allEntities = EcoreUtil2.<org.contextmapper.tactic.dsl.tacticdsl.Entity>eAllOfType(
				EcoreUtil.getRootContainer(contextMap), org.contextmapper.tactic.dsl.tacticdsl.Entity.class);
		for (org.contextmapper.tactic.dsl.tacticdsl.Entity entity : allEntities) {
			this.dslEntityLookupTable.put(entity.getName(), entity);
		}
	}

	private Entity getEntity(String name) {
		if (this.entityLookupTable.containsKey(name))
			return this.entityLookupTable.get(name);
		return new Entity(name);
	}
}

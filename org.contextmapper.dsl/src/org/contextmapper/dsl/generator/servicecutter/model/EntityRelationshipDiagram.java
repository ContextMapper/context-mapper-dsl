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
package org.contextmapper.dsl.generator.servicecutter.model;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * ServiceCutter EntityRelationshipDiagram root input object.
 * 
 * @author Stefan Kapferer
 *
 */
public class EntityRelationshipDiagram {

	private List<Entity> entities;
	private List<EntityRelation> entityRelations;

	public EntityRelationshipDiagram() {
		this.entities = Lists.newArrayList();
		this.entityRelations = Lists.newArrayList();
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void addEntity(Entity entity) {
		this.entities.add(entity);
	}

	public List<EntityRelation> getEntityRelations() {
		return entityRelations;
	}

	public void addEntityRelation(EntityRelation entityRelation) {
		this.entityRelations.add(entityRelation);
	}

}

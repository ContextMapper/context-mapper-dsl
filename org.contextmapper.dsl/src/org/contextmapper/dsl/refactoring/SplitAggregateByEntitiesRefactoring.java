/*
 * Copyright 2018-2021 The Context Mapper Project Team
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
package org.contextmapper.dsl.refactoring;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.eclipse.xtext.EcoreUtil2;

public class SplitAggregateByEntitiesRefactoring extends AbstractRefactoring implements SemanticCMLRefactoring {

	private String aggregateName;
	private List<Aggregate> newAggregates;

	public SplitAggregateByEntitiesRefactoring(String aggregateName) {
		this.aggregateName = aggregateName;
		this.newAggregates = new LinkedList<>();
	}

	@Override
	protected void doRefactor() {
		Aggregate inputAggregate = getSelectedAggregate();
		if (inputAggregate == null)
			return;
		
		final List<Entity> entities = EcoreUtil2.eAllOfType(inputAggregate, Entity.class);
		if(entities.size() == 1)
			return;
		
		for(int i = 1; i < entities.size(); i++)
			moveEntityToNewAggregate(inputAggregate, entities.get(i));
		
		entities.get(0).setAggregateRoot(true);
		
		postProcessing();
	}
	
	private void moveEntityToNewAggregate(Aggregate inputAggregate, Entity movingEntity) {
		Aggregate newAggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		newAggregate.setName(getUniqueAggregateName(movingEntity.getName()));
		removeElementFromEList(inputAggregate.getDomainObjects(), movingEntity);
		addElementToEList(newAggregate.getDomainObjects(), movingEntity);
		movingEntity.setAggregateRoot(true);
		
		if(inputAggregate.eContainer() instanceof SculptorModule) {
			SculptorModule container = (SculptorModule) inputAggregate.eContainer();
			addElementToEList(container.getAggregates(), newAggregate);
		} else {
			BoundedContext container = (BoundedContext) inputAggregate.eContainer();
			addElementToEList(container.getAggregates(), newAggregate);
		}
		this.newAggregates.add(newAggregate);
	}
	
	private void postProcessing() {
		for (ContextMap contextMap : getAllContextMaps()) {
			addNewAggregatesToExposedAggregatesIfOriginalIsExposed(contextMap);
		}
	}

	private Aggregate getSelectedAggregate() {
		List<Aggregate> allAggregates = EcoreUtil2.<Aggregate>getAllContentsOfType(model, Aggregate.class);
		List<Aggregate> aggregatesWithInputName = allAggregates.stream().filter(agg -> agg.getName().equals(aggregateName)).collect(Collectors.toList());
		if (aggregatesWithInputName.isEmpty())
			return null;

		return aggregatesWithInputName.get(0);
	}

	private void addNewAggregatesToExposedAggregatesIfOriginalIsExposed(ContextMap contextMap) {
		for (Relationship relationship : contextMap.getRelationships()) {
			if (!(relationship instanceof UpstreamDownstreamRelationship))
				continue;

			UpstreamDownstreamRelationship upDownRelationship = (UpstreamDownstreamRelationship) relationship;
			if (upDownRelationship.getUpstreamExposedAggregates().stream().map(a -> a.getName()).collect(Collectors.toList()).contains(aggregateName)) {
				addElementsToEList(upDownRelationship.getUpstreamExposedAggregates(), newAggregates);
			}
		}
	}
	
	private String getUniqueAggregateName(final String entityName) {
		String name = entityName;
		Set<String> allAggregateNames = EcoreUtil2.eAllOfType(model, Aggregate.class).stream()
				.map(a -> a.getName())
				.collect(Collectors.toSet());
		int i = 2;
		while(allAggregateNames.contains(name)) {
			name = entityName + "_" + i;
			i++;
		}
		return name;
	}

}

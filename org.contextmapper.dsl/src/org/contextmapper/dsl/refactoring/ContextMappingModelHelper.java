/*
 * Copyright 2019 The Context Mapper Project Team
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
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.contextMappingDSL.SymmetricRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ContextMappingModelHelper {

	private ContextMappingModel model;

	public ContextMappingModelHelper(ContextMappingModel model) {
		this.model = model;
	}

	/**
	 * Finds all relationships between the two given contexts.
	 */
	public List<Relationship> findAnyRelationshipsBetweenTwoContexts(BoundedContext bc1, BoundedContext bc2) {
		List<Relationship> relationships = Lists.newArrayList();
		for (Relationship relationship : model.getMap().getRelationships()) {
			if (relationship instanceof SymmetricRelationship) {
				SymmetricRelationship symRelationship = (SymmetricRelationship) relationship;
				if ((symRelationship.getParticipant1().getName().equals(bc1.getName()) && symRelationship.getParticipant2().getName().equals(bc2.getName()))
						|| (symRelationship.getParticipant1().getName().equals(bc2.getName()) && symRelationship.getParticipant2().getName().equals(bc1.getName())))
					relationships.add(symRelationship);
			} else if (relationship instanceof UpstreamDownstreamRelationship) {
				UpstreamDownstreamRelationship upDownRelationship = (UpstreamDownstreamRelationship) relationship;
				if ((upDownRelationship.getUpstream().getName().equals(bc1.getName()) && upDownRelationship.getDownstream().getName().equals(bc2.getName()))
						|| (upDownRelationship.getUpstream().getName().equals(bc2.getName()) && upDownRelationship.getDownstream().getName().equals(bc1.getName())))
					relationships.add(upDownRelationship);
			}
		}
		return relationships;
	}

	/**
	 * Replaces the bounded context 'originalBC' in all relationships on the context
	 * map with the bounded context 'replacementBC'. Returns true if a replacement
	 * has been done, false otherwise.
	 */
	public boolean replaceBCInAllRelationships(BoundedContext originalBC, BoundedContext replacementBC) {
		boolean replacedAtLeastInOneRelationship = false;
		for (Relationship relationship : model.getMap().getRelationships()) {
			if (relationship instanceof SymmetricRelationship) {
				SymmetricRelationship symRelationship = (SymmetricRelationship) relationship;
				if (symRelationship.getParticipant1().getName().equals(originalBC.getName())) {
					symRelationship.setParticipant1(replacementBC);
					replacedAtLeastInOneRelationship = true;
				}
				if (symRelationship.getParticipant2().getName().equals(originalBC.getName())) {
					symRelationship.setParticipant2(replacementBC);
					replacedAtLeastInOneRelationship = true;
				}
			} else if (relationship instanceof UpstreamDownstreamRelationship) {
				UpstreamDownstreamRelationship upDownRelationship = (UpstreamDownstreamRelationship) relationship;
				if (upDownRelationship.getDownstream().getName().equals(originalBC.getName())) {
					upDownRelationship.setDownstream(replacementBC);
					replacedAtLeastInOneRelationship = true;
				}
				if (upDownRelationship.getUpstream().getName().equals(originalBC.getName())) {
					upDownRelationship.setUpstream(replacementBC);
					replacedAtLeastInOneRelationship = true;
				}
			}
		}
		if (replacedAtLeastInOneRelationship && !model.getMap().getBoundedContexts().stream().map(bc -> bc.getName()).collect(Collectors.toSet()).contains(replacementBC.getName()))
			model.getMap().getBoundedContexts().add(replacementBC);
		return replacedAtLeastInOneRelationship;
	}

	/**
	 * Finds duplicate entities within a bounded context.
	 */
	public List<String> findDuplicateEntities(String boundedContextName) {
		List<String> duplicates = Lists.newArrayList();
		Set<String> uniqueNameCheckSet = Sets.newHashSet();
		List<Entity> entities = EcoreUtil2.<Entity>eAllOfType(model.getBoundedContexts().stream().filter(b -> b.getName().equals(boundedContextName)).findFirst().get(),
				Entity.class);
		for (Entity entity : entities) {
			if (!uniqueNameCheckSet.contains(entity.getName())) {
				uniqueNameCheckSet.add(entity.getName());
			} else {
				duplicates.add(entity.getName());
			}
		}
		return duplicates;
	}

	/**
	 * Post-AR-Method: Fixes 'exposedAggregates' in context map relationships after
	 * some aggregates have been moved to a new bounded context.
	 */
	public void moveExposedAggregatesToNewRelationshipsIfNeeded(List<String> movedAggregates, BoundedContext newBoundedContext) {
		ContextMap map = model.getMap();
		if (map == null)
			return;

		for (Relationship relationship : new LinkedList<>(map.getRelationships())) {
			if (!(relationship instanceof UpstreamDownstreamRelationship))
				continue;
			moveExposedAggregates4RelationshipIfNeeded((UpstreamDownstreamRelationship) relationship, movedAggregates, newBoundedContext);
		}
	}

	private void moveExposedAggregates4RelationshipIfNeeded(UpstreamDownstreamRelationship relationship, List<String> movedAggregates, BoundedContext newBoundedContext) {
		List<String> exposedAggregates = relationship.getUpstreamExposedAggregates().stream().map(a -> a.getName()).collect(Collectors.toList());
		List<String> aggregatesToMove = exposedAggregates.stream().distinct().filter(movedAggregates::contains).collect(Collectors.toList());

		if (aggregatesToMove.isEmpty())
			return;

		UpstreamDownstreamRelationship newRelationship = ContextMappingDSLFactory.eINSTANCE.createUpstreamDownstreamRelationship();
		newRelationship.setUpstream(newBoundedContext);
		newRelationship.setDownstream(relationship.getDownstream());
		newRelationship.setImplementationTechnology(relationship.getImplementationTechnology());
		newRelationship.getUpstreamRoles().addAll(relationship.getUpstreamRoles());
		newRelationship.getDownstreamRoles().addAll(relationship.getDownstreamRoles());

		for (Aggregate aggregate : new LinkedList<>(relationship.getUpstreamExposedAggregates())) {
			if (!aggregatesToMove.contains(aggregate.getName()))
				continue;

			relationship.getUpstreamExposedAggregates().remove(aggregate);
			newRelationship.getUpstreamExposedAggregates().add(aggregate);
		}
		if (!model.getMap().getBoundedContexts().contains(newBoundedContext))
			model.getMap().getBoundedContexts().add(newBoundedContext);
		model.getMap().getRelationships().add(newRelationship);
	}

}

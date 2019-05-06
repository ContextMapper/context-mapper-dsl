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
import java.util.Optional;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.refactoring.henshin.Refactoring;
import org.eclipse.xtext.EcoreUtil2;

public class ExtractAggregatesByNFR extends AbstractRefactoring implements Refactoring {

	private String boundedContextName;
	private BoundedContext originalBC;
	private String newBoundedContextName;
	private List<String> aggregatesToExtract;

	public ExtractAggregatesByNFR(String boundedContextName, String newBoundedContextName, List<String> aggregatesToExtract) {
		this.boundedContextName = boundedContextName;
		this.newBoundedContextName = newBoundedContextName;
		this.aggregatesToExtract = aggregatesToExtract;
	}

	@Override
	protected void doRefactor() {
		initOriginalBC();

		// nothing to do if no aggregates given
		if (this.aggregatesToExtract.size() < 1)
			return;

		BoundedContext newBC = createNewBoundedContext();
		for (String aggregateName : aggregatesToExtract) {
			Optional<Aggregate> aggregate = originalBC.getAggregates().stream().filter(agg -> agg.getName().equals(aggregateName)).findFirst();

			// we ignore aggregates given which do not exist
			if (!aggregate.isPresent())
				continue;

			originalBC.getAggregates().remove(aggregate.get());
			newBC.getAggregates().add(aggregate.get());
		}

		this.model.getBoundedContexts().add(newBC);
		moveExposedAggregatesInContextMap(aggregatesToExtract, newBC);
		saveResource();
	}

	private BoundedContext createNewBoundedContext() {
		BoundedContext bc = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		bc.setName(newBoundedContextName);
		return bc;
	}

	private void initOriginalBC() {
		List<BoundedContext> allBCs = EcoreUtil2.<BoundedContext>getAllContentsOfType(model, BoundedContext.class);
		List<BoundedContext> bcsWithGivenInputName = allBCs.stream().filter(bc -> bc.getName().equals(boundedContextName)).collect(Collectors.toList());
		this.originalBC = bcsWithGivenInputName.get(0);
	}

	private void moveExposedAggregatesInContextMap(List<String> movedAggregates, BoundedContext newBoundedContext) {
		ContextMap map = model.getMap();
		if (map == null)
			return;

		for (Relationship relationship : new LinkedList<>(map.getRelationships())) {
			if (!(relationship instanceof UpstreamDownstreamRelationship))
				continue;
			moveExposedAggregatesIfNeeded((UpstreamDownstreamRelationship) relationship, movedAggregates, newBoundedContext);
		}
	}

	private void moveExposedAggregatesIfNeeded(UpstreamDownstreamRelationship relationship, List<String> movedAggregates, BoundedContext newBoundedContext) {
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

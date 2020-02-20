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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Lists;

public class MergeAggregatesRefactoring extends AbstractRefactoring implements Refactoring {

	private String aggregate1;
	private String aggregate2;
	private boolean takeAttributesFromSecondAggregate = false;

	public MergeAggregatesRefactoring(String aggregate1, String aggregate2) {
		this.aggregate1 = aggregate1;
		this.aggregate2 = aggregate2;
	}

	public MergeAggregatesRefactoring(String aggregate1, String aggregate2, boolean takeAttributesFromSecondAggregate) {
		this(aggregate1, aggregate2);
		this.takeAttributesFromSecondAggregate = takeAttributesFromSecondAggregate;
	}

	@Override
	protected void doRefactor() {
		// do nothing if the two parameters point to same aggregate
		if (aggregate1.equals(aggregate2))
			return;

		Optional<Aggregate> agg1Opt = getAllAggregates().stream().filter(bc -> bc.getName().equals(aggregate1)).findFirst();
		Optional<Aggregate> agg2Opt = getAllAggregates().stream().filter(bc -> bc.getName().equals(aggregate2)).findFirst();

		// do nothing if one of the aggregates does not exist
		if (!agg1Opt.isPresent() || !agg2Opt.isPresent())
			return;

		Aggregate agg1 = agg1Opt.get();
		Aggregate agg2 = agg2Opt.get();

		// inverse merging, if requested
		if (takeAttributesFromSecondAggregate) {
			agg1 = agg2Opt.get();
			agg2 = agg1Opt.get();
		}

		// precondition check, may throw exception
		checkForPossibleDomainObjectNameClashes(agg1, agg2);

		// ensure there is only one aggregate root
		if (containsAggregateRoot(agg1))
			changeAggregateRootsToNormalObjects(agg2);

		// move content from agg2 to agg1
		addElementsToEList(agg1.getConsumers(), agg2.getConsumers());
		addElementsToEList(agg1.getDomainObjects(), agg2.getDomainObjects());
		addElementsToEList(agg1.getResources(), agg2.getResources());
		addElementsToEList(agg1.getResponsibilities(), agg2.getResponsibilities());
		addElementsToEList(agg1.getServices(), agg2.getServices());
		addElementsToEList(agg1.getUseCases(), agg2.getUseCases());

		// update context maps
		for (ContextMap contextMap : getAllContextMaps()) {
			handleContextMapChanges(contextMap, agg1, agg2);
			markResourceChanged(contextMap);
		}

		// remove agg2 from its container
		if (agg2.eContainer() instanceof BoundedContext) {
			BoundedContext container = (BoundedContext) agg2.eContainer();
			removeElementFromEList(container.getAggregates(), agg2);
			markResourceChanged(container);
		} else if (agg2.eContainer() instanceof SculptorModule) {
			BoundedContext container = (BoundedContext) ((SculptorModule) agg2.eContainer()).eContainer();
			removeElementFromEList(((SculptorModule) agg2.eContainer()).getAggregates(), agg2);
			markResourceChanged(container);
		}
		saveResources();
	}

	private void checkForPossibleDomainObjectNameClashes(Aggregate aggregate1, Aggregate aggregate2) {
		List<String> aggregate1DomainObjectNames = aggregate1.getDomainObjects().stream().map(obj -> obj.getName()).collect(Collectors.toList());
		List<String> aggregate2DomainObjectNames = aggregate2.getDomainObjects().stream().map(obj -> obj.getName()).collect(Collectors.toList());

		Set<String> commonDomainObjectNames = aggregate1DomainObjectNames.stream().distinct().filter(aggregate2DomainObjectNames::contains).collect(Collectors.toSet());
		if (!commonDomainObjectNames.isEmpty())
			throw new RefactoringInputException("Sorry, we cannot execute this refactoring. The selected Aggregates contain the following duplicate domain objects: "
					+ String.join(", ", commonDomainObjectNames));
	}

	/**
	 * Sets aggregateRoot flag to false for all objects in given aggregate
	 */
	private void changeAggregateRootsToNormalObjects(Aggregate aggregate) {
		getAggregateRoots(aggregate).forEach(o -> o.setAggregateRoot(false));
	}

	private boolean containsAggregateRoot(Aggregate aggregate) {
		return !getAggregateRoots(aggregate).isEmpty();
	}

	private List<DomainObject> getAggregateRoots(Aggregate aggregate) {
		return aggregate.getDomainObjects().stream().filter(o -> o instanceof DomainObject).map(o -> (DomainObject) o).filter(o -> o.isAggregateRoot())
				.collect(Collectors.toList());
	}

	private List<Aggregate> getAllAggregates() {
		List<Aggregate> aggregates = Lists.newArrayList();
		for (BoundedContext bc : getAllBoundedContexts()) {
			aggregates.addAll(EcoreUtil2.<Aggregate>getAllContentsOfType(bc, Aggregate.class));
		}
		return aggregates;
	}

	private void handleContextMapChanges(ContextMap map, Aggregate agg1, Aggregate agg2) {
		for (Relationship relationship : map.getRelationships()) {
			if (!(relationship instanceof UpstreamDownstreamRelationship))
				continue;

			UpstreamDownstreamRelationship upDownRelationship = (UpstreamDownstreamRelationship) relationship;
			if (upDownRelationship.getUpstreamExposedAggregates().contains(agg2)) {
				// ugly workaround (clear list and add all again); otherwise list is not
				// properly updated when saving ecore model :(
				List<Aggregate> exposedAggregates = Lists.newArrayList();
				exposedAggregates.addAll(upDownRelationship.getUpstreamExposedAggregates());
				exposedAggregates.remove(agg2);
				exposedAggregates.add(agg1);
				upDownRelationship.getUpstreamExposedAggregates().clear();
				upDownRelationship.getUpstreamExposedAggregates().addAll(exposedAggregates);
			}
		}
	}

}

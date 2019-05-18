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
import org.contextmapper.dsl.contextMappingDSL.Module;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.contextmapper.dsl.refactoring.henshin.Refactoring;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.base.Strings;

public class MergeAggregatesRefactoring extends AbstractRefactoring implements Refactoring {

	private String aggregate1;
	private String aggregate2;

	public MergeAggregatesRefactoring(String aggregate1, String aggregate2) {
		this.aggregate1 = aggregate1;
		this.aggregate2 = aggregate2;
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

		checkForPossibleDomainObjectNameClashes(agg1, agg2);

		// move content from agg2 to agg1
		agg1.getConsumers().addAll(agg2.getConsumers());
		agg1.getDomainObjects().addAll(agg2.getDomainObjects());
		agg1.getResources().addAll(agg2.getResources());
		agg1.getResponsibilities().addAll(agg2.getResponsibilities());
		agg1.getServices().addAll(agg2.getServices());
		agg1.getUseCases().addAll(agg2.getUseCases());

		// update context map
		handleContextMapChanges(agg1, agg2);

		// remove agg2 from its container
		if (agg2.eContainer() instanceof BoundedContext) {
			BoundedContext container = (BoundedContext) agg2.eContainer();
			container.getAggregates().remove(agg2);
		} else if (agg2.eContainer() instanceof Module) {
			Module container = (Module) agg2.eContainer();
			container.getAggregates().remove(agg2);
		}
		this.model.eAllContents();
		saveResource();
	}

	private void checkForPossibleDomainObjectNameClashes(Aggregate aggregate1, Aggregate aggregate2) {
		List<String> aggregate1DomainObjectNames = aggregate1.getDomainObjects().stream().map(obj -> obj.getName()).collect(Collectors.toList());
		List<String> aggregate2DomainObjectNames = aggregate2.getDomainObjects().stream().map(obj -> obj.getName()).collect(Collectors.toList());

		Set<String> commonDomainObjectNames = aggregate1DomainObjectNames.stream().distinct().filter(aggregate2DomainObjectNames::contains).collect(Collectors.toSet());
		if (!commonDomainObjectNames.isEmpty())
			throw new RefactoringInputException("Sorry, we cannot execute this refactoring. The selected Aggregates contain the following duplicate domain objects: "
					+ String.join(", ", commonDomainObjectNames));
	}

	private List<Aggregate> getAllAggregates() {
		return EcoreUtil2.<Aggregate>getAllContentsOfType(model, Aggregate.class);
	}

	private void handleContextMapChanges(Aggregate agg1, Aggregate agg2) {
		ContextMap map = model.getMap();

		// maybe there is no context map
		if (map == null)
			return;

		for (Relationship relationship : map.getRelationships()) {
			if (!(relationship instanceof UpstreamDownstreamRelationship))
				continue;

			UpstreamDownstreamRelationship upDownRelationship = (UpstreamDownstreamRelationship) relationship;
			if (upDownRelationship.getUpstreamExposedAggregates().contains(agg2)) {
				upDownRelationship.getUpstreamExposedAggregates().remove(agg2);
				upDownRelationship.getUpstreamExposedAggregates().add(agg1);
			}
		}
	}

}

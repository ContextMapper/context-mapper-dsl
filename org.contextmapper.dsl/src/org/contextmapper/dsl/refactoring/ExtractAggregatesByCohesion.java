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
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;

public class ExtractAggregatesByCohesion extends AbstractRefactoring implements SemanticCMLRefactoring {

	private String boundedContextName;
	private BoundedContext originalBC;
	private String newBoundedContextName;
	private List<String> aggregatesToExtract;

	public ExtractAggregatesByCohesion(String boundedContextName, String newBoundedContextName, List<String> aggregatesToExtract) {
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

			removeElementFromEList(originalBC.getAggregates(), aggregate.get());
			addElementToEList(newBC.getAggregates(), aggregate.get());
		}

		getResource(originalBC).getContextMappingModel().getBoundedContexts().add(newBC);
		for (ContextMap contextMap : getAllContextMaps()) {
			new ContextMappingModelHelper(contextMap).moveExposedAggregatesToNewRelationshipsIfNeeded(aggregatesToExtract, newBC);
		}
	}

	private BoundedContext createNewBoundedContext() {
		BoundedContext bc = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		bc.setName(newBoundedContextName);
		return bc;
	}

	private void initOriginalBC() {
		List<BoundedContext> bcsWithGivenInputName = getAllBoundedContexts().stream().filter(bc -> bc.getName().equals(boundedContextName)).collect(Collectors.toList());
		this.originalBC = bcsWithGivenInputName.get(0);
	}

}

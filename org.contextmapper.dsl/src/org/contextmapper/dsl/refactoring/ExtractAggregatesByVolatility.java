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
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.LikelihoodForChange;

public class ExtractAggregatesByVolatility extends AbstractRefactoring implements SemanticCMLRefactoring {

	private String boundedContextName;
	private BoundedContext originalBC;
	private LikelihoodForChange likelihoodForChange;

	public ExtractAggregatesByVolatility(String boundedContextName, LikelihoodForChange likelihoodForChange) {
		this.boundedContextName = boundedContextName;
		this.likelihoodForChange = likelihoodForChange;
	}

	@Override
	protected void doRefactor() {
		initOriginalBC();

		// do nothing if there is only one aggregate
		if (originalBC.getAggregates().size() < 2)
			return;

		List<Aggregate> aggregates = collectAggregatesByVolatility();
		if (aggregates.size() < 1)
			return;

		BoundedContext newBC = createNewBoundedContext();
		for (Aggregate aggregate : aggregates) {
			// move the matching aggregates to the new Bounded Context
			addElementToEList(newBC.getAggregates(), aggregate);
			removeElementFromEList(originalBC.getAggregates(), aggregate);
			markResourceChanged(originalBC);
		}
		addElementToEList(getResource(originalBC).getContextMappingModel().getBoundedContexts(), newBC);
		for (ContextMap contextMap : getAllContextMaps()) {
			new ContextMappingModelHelper(contextMap).moveExposedAggregatesToNewRelationshipsIfNeeded(aggregates.stream().map(a -> a.getName()).collect(Collectors.toList()),
					newBC);
			markResourceChanged(contextMap);
		}
		saveResources();
	}

	private BoundedContext createNewBoundedContext() {
		BoundedContext newBC = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		newBC.setName(boundedContextName + "_Volatility_" + this.likelihoodForChange.getName());
		return newBC;
	}

	private List<Aggregate> collectAggregatesByVolatility() {
		return this.originalBC.getAggregates().stream().filter(agg -> agg.getLikelihoodForChange().equals(likelihoodForChange)).collect(Collectors.toList());
	}

	private void initOriginalBC() {
		List<BoundedContext> bcsWithGivenInputName = getAllBoundedContexts().stream().filter(bc -> bc.getName().equals(boundedContextName)).collect(Collectors.toList());
		this.originalBC = bcsWithGivenInputName.get(0);
	}

}

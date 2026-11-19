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
package org.contextmapper.dsl.refactoring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;

import com.google.common.collect.Lists;

public class SplitBoundedContextByAggregateAttribute extends AbstractRefactoring implements SemanticCMLRefactoring {

	private String boundedContextName;
	private BoundedContext originalBC;
	private Map<CompoundKey, List<Aggregate>> splittingKeyToAggregatesMapping;

	private Function<Aggregate, CompoundKey> aggregateToSetKeyMappingFunction;
	private Function<BoundedContext, CompoundKey> keyOfSetStayingInOriginalBCFunction = bc -> null;
	private BiConsumer<BoundedContext, BoundedContext> newBCInitializer = (originalBC, newBC) -> {
	};

	public SplitBoundedContextByAggregateAttribute(Function<Aggregate, CompoundKey> aggregateToSetKeyMappingFunction, String boundedContextName) {
		this.aggregateToSetKeyMappingFunction = aggregateToSetKeyMappingFunction;
		this.boundedContextName = boundedContextName;
	}

	/**
	 * Defines which aggregate set stays in the original Bounded Context. If the
	 * function returns null (default) or a key without aggregates, the first set
	 * stays.
	 */
	public void setKeyOfSetStayingInOriginalBCFunction(Function<BoundedContext, CompoundKey> keyOfSetStayingInOriginalBCFunction) {
		this.keyOfSetStayingInOriginalBCFunction = keyOfSetStayingInOriginalBCFunction;
	}

	/**
	 * Called with the original and the new Bounded Context for every new context,
	 * after its aggregates have been moved.
	 */
	public void setNewBCInitializer(BiConsumer<BoundedContext, BoundedContext> newBCInitializer) {
		this.newBCInitializer = newBCInitializer;
	}

	@Override
	public void doRefactor() {
		initOriginalBC();
		createAggregateMapping();
		splitOriginalBC();
	}

	private void createAggregateMapping() {
		splittingKeyToAggregatesMapping = new HashMap<>();
		for (Aggregate aggregate : originalBC.getAggregates()) {
			CompoundKey splittingGroupKey = this.aggregateToSetKeyMappingFunction.apply(aggregate);
			if (this.splittingKeyToAggregatesMapping.containsKey(splittingGroupKey)) {
				this.splittingKeyToAggregatesMapping.get(splittingGroupKey).add(aggregate);
			} else {
				this.splittingKeyToAggregatesMapping.put(splittingGroupKey, Lists.newArrayList(aggregate));
			}
		}
	}

	private void splitOriginalBC() {
		// if there is only one aggregate set, we don't need new BCs (nothing to split)
		if (this.splittingKeyToAggregatesMapping.size() <= 1)
			return;

		CompoundKey keyOfSetStayingInOriginalBC = this.keyOfSetStayingInOriginalBCFunction.apply(originalBC);
		if (keyOfSetStayingInOriginalBC == null || !this.splittingKeyToAggregatesMapping.containsKey(keyOfSetStayingInOriginalBC))
			keyOfSetStayingInOriginalBC = this.splittingKeyToAggregatesMapping.keySet().iterator().next();

		// extract the other sets to new BCs
		int i = 1;
		for (Entry<CompoundKey, List<Aggregate>> set : this.splittingKeyToAggregatesMapping.entrySet()) {
			if (set.getKey().equals(keyOfSetStayingInOriginalBC))
				continue;
			List<Aggregate> aggregates = set.getValue();
			this.originalBC.getAggregates().removeAll(aggregates);
			ContextMappingModel bcModel = getResource(originalBC).getContextMappingModel();

			BoundedContext newBC = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
			newBC.setName("NewBoundedContext" + i++);
			addElementsToEList(newBC.getAggregates(), aggregates);
			addElementToEList(bcModel.getBoundedContexts(), newBC);
			adjustContextMaps(newBC, aggregates);
			newBCInitializer.accept(originalBC, newBC);
		}
	}

	private void initOriginalBC() {
		Set<BoundedContext> allBCs = getAllBoundedContexts();
		Set<BoundedContext> bcsWithGivenInputName = allBCs.stream().filter(bc -> bc.getName().equals(boundedContextName)).collect(Collectors.toSet());

		// xtext validators must ensure that we get only one BC here:
		this.originalBC = bcsWithGivenInputName.iterator().next();
	}

	private void adjustContextMaps(BoundedContext newBC, List<Aggregate> extractedAggregates) {
		for (ContextMap contextMap : getAllContextMaps()) {
			new ContextMappingModelHelper(contextMap)
					.moveExposedAggregatesToNewRelationshipsIfNeeded(extractedAggregates.stream().map(agg -> agg.getName()).collect(Collectors.toList()), newBC);
		}
	}

}

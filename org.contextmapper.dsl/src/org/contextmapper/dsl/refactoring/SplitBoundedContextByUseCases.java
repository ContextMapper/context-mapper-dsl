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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.refactoring.henshin.Refactoring;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Lists;

public class SplitBoundedContextByUseCases extends AbstractRefactoring implements Refactoring {

	private String boundedContextName;
	private BoundedContext originalBC;
	private Map<AggregateSetKey, List<Aggregate>> useCaseAggregateMapping;

	public SplitBoundedContextByUseCases(String boundedContextName) {
		this.boundedContextName = boundedContextName;
	}

	@Override
	public void doRefactor() {
		initOriginalBC();
		createAggregateMapping();
		splitOriginalBC();
		saveResource();
	}

	private void createAggregateMapping() {
		useCaseAggregateMapping = new HashMap<>();
		for (Aggregate aggregate : originalBC.getAggregates()) {
			AggregateSetKey ucsKey = createAggregateSetKeyForUCs(aggregate);
			if (this.useCaseAggregateMapping.containsKey(ucsKey)) {
				this.useCaseAggregateMapping.get(ucsKey).add(aggregate);
			} else {
				this.useCaseAggregateMapping.put(ucsKey, Lists.newArrayList(aggregate));
			}
		}
	}

	private void splitOriginalBC() {
		// if there is only one aggregate set, we don't need new BCs (nothing to split)
		if (this.useCaseAggregateMapping.size() <= 1)
			return;

		Iterator<Entry<AggregateSetKey, List<Aggregate>>> it = this.useCaseAggregateMapping.entrySet().iterator();

		// first set stays in current BC
		it.next();

		// extract other sets to new BCs
		int i = 1;
		while (it.hasNext()) {
			List<Aggregate> aggregates = it.next().getValue();
			this.originalBC.getAggregates().removeAll(aggregates);

			BoundedContext newBC = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
			newBC.setName("NewBoundedContext" + i++);
			newBC.getAggregates().addAll(aggregates);
			this.model.getBoundedContexts().add(newBC);
		}
	}

	private void initOriginalBC() {
		List<BoundedContext> allBCs = EcoreUtil2.<BoundedContext>getAllContentsOfType(model, BoundedContext.class);
		List<BoundedContext> bcsWithGivenInputName = allBCs.stream().filter(bc -> bc.getName().equals(boundedContextName)).collect(Collectors.toList());

		// xtext validators must ensure that we get only one BC here:
		this.originalBC = bcsWithGivenInputName.get(0);
	}

	private AggregateSetKey createAggregateSetKeyForUCs(Aggregate aggregate) {
		return new AggregateSetKey(aggregate.getUseCases().stream().map(uc -> uc.getName()).collect(Collectors.toSet()));
	}

	private class AggregateSetKey {
		Set<String> keys;

		AggregateSetKey(Set<String> keys) {
			this.keys = keys;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AggregateSetKey))
				return false;
			AggregateSetKey otherKey = (AggregateSetKey) obj;
			return (this.keys.size() == otherKey.keys.size()) && this.keys.containsAll(otherKey.keys);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.keys.toArray());
		}
	}

}

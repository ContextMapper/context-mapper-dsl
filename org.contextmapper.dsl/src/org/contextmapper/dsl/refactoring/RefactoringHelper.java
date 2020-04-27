/*
 * Copyright 2020 The Context Mapper Project Team
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
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Some common logic that is used in multiple ARs to ensure model validity.
 * 
 * @author Stefan Kapferer
 *
 */
public class RefactoringHelper {

	private AbstractRefactoring ar;

	public RefactoringHelper(AbstractRefactoring ar) {
		this.ar = ar;
	}

	/**
	 * If we create a deep-copy of a Bounded Context, we have to rename all
	 * Aggregates, Modules, and domain objects. (to avoid validation errors
	 * regarding uniqueness)
	 */
	public void adjustAggregateAndModuleNames(BoundedContext bc, String suffix) {
		Set<String> allAggregateNames = collectAllAggregateNames();
		Set<String> allModuleNames = collectAllModuleNames();
		for (Aggregate aggregate : bc.getAggregates()) {
			aggregate.setName(getUniqueName(aggregate.getName() + suffix, allAggregateNames));
			adjustDomainObjectNames(aggregate.getDomainObjects(), suffix);
		}
		for (SculptorModule module : bc.getModules()) {
			module.setName(getUniqueName(module.getName() + suffix, allModuleNames));
			adjustDomainObjectNames(module.getDomainObjects(), suffix);
			for (Aggregate aggregate : module.getAggregates()) {
				aggregate.setName(getUniqueName(aggregate.getName() + suffix, allAggregateNames));
				adjustDomainObjectNames(aggregate.getDomainObjects(), suffix);
			}
		}
	}

	/**
	 * If we create a deep-copy of a Bounded Context, we have to rename all domain
	 * objects. (to avoid validation errors regarding uniqueness)
	 */
	public void adjustDomainObjectNames(List<SimpleDomainObject> domainObjects, String suffix) {
		for (SimpleDomainObject simpleDO : domainObjects) {
			simpleDO.setName(simpleDO.getName() + suffix);
		}
	}

	/**
	 * Collects all Aggregate names of the current model.
	 */
	public Set<String> collectAllAggregateNames() {
		Set<String> aggregateNames = Sets.newHashSet();
		for (BoundedContext bc : ar.getAllBoundedContexts()) {
			aggregateNames.addAll(collectAggregates(bc).stream().map(agg -> agg.getName()).collect(Collectors.toSet()));
		}
		return aggregateNames;
	}

	/**
	 * Collects all Aggregates in the current CML model.
	 */
	public List<Aggregate> collectAggregates(BoundedContext bc) {
		List<Aggregate> aggregates = Lists.newLinkedList();
		aggregates.addAll(bc.getAggregates());
		for (SculptorModule module : bc.getModules()) {
			aggregates.addAll(module.getAggregates());
		}
		return aggregates;
	}

	/**
	 * Collects all module names of the current CML model.
	 */
	public Set<String> collectAllModuleNames() {
		Set<String> moduleNames = Sets.newHashSet();
		for (BoundedContext bc : ar.getAllBoundedContexts()) {
			moduleNames.addAll(bc.getModules().stream().map(m -> m.getName()).collect(Collectors.toSet()));
		}
		return moduleNames;
	}

	/**
	 * Creates a unique name using a counter. Given a set of already existing domain
	 * object names, this method searches for a name that is not already used
	 * (simple method with counter as suffix).
	 */
	public String getUniqueName(String initialName, Set<String> givenNames) {
		String name = initialName;
		int counter = 2;
		while (givenNames.contains(name)) {
			name = initialName + "_" + counter;
			counter++;
		}
		return name;
	}
}

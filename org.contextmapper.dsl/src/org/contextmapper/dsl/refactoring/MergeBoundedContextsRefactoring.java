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

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.refactoring.henshin.Refactoring;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Lists;

public class MergeBoundedContextsRefactoring extends AbstractRefactoring implements Refactoring {

	private String boundedContext1;
	private String boundedContext2;

	public MergeBoundedContextsRefactoring(String boundedContext1, String boundedContext2) {
		this.boundedContext1 = boundedContext1;
		this.boundedContext2 = boundedContext2;
	}

	@Override
	protected void doRefactor() {
		// do nothing if the two parameters point to same BC
		if (boundedContext1.equals(boundedContext2))
			return;

		Optional<BoundedContext> bc1 = getAllBoundedContexts().stream().filter(bc -> bc.getName().equals(boundedContext1)).findFirst();
		Optional<BoundedContext> bc2 = getAllBoundedContexts().stream().filter(bc -> bc.getName().equals(boundedContext2)).findFirst();

		// do nothing if one of the BCs does not exist
		if (!bc1.isPresent() || !bc2.isPresent())
			return;

		// move content from BC2 to BC1
		bc1.get().getAggregates().addAll(bc2.get().getAggregates());
		bc1.get().getModules().addAll(bc2.get().getModules());
		bc1.get().getImplementedSubdomains().addAll(bc2.get().getImplementedSubdomains());
		bc1.get().getRealizedBoundedContexts().addAll(bc2.get().getRealizedBoundedContexts());
		bc1.get().getResponsibilities().addAll(bc2.get().getResponsibilities());

		// remove BC2
		handleContextMapChanges(bc1.get(), bc2.get());
		this.model.getBoundedContexts().remove(bc2.get());
		this.model.eAllContents();
		saveResource();
	}

	private List<BoundedContext> getAllBoundedContexts() {
		return EcoreUtil2.<BoundedContext>getAllContentsOfType(model, BoundedContext.class);
	}

	private void handleContextMapChanges(BoundedContext bc1, BoundedContext bc2) {
		ContextMap map = model.getMap();

		// maybe there is no context map
		if (map == null)
			return;

		ContextMappingModelHelper helper = new ContextMappingModelHelper(model);
		List<Relationship> relationshipsToRemove = helper.findAnyRelationshipsBetweenTwoContexts(bc1, bc2);
		map.getRelationships().removeAll(relationshipsToRemove);
		helper.replaceBCInAllRelationships(bc2, bc1);

		map.getBoundedContexts().remove(bc2);

		// ugly workaround (clear list and add all again); otherwise list is not
		// properly updated when saving ecore model :(
		List<BoundedContext> list = Lists.newArrayList(map.getBoundedContexts());
		map.getBoundedContexts().clear();
		map.getBoundedContexts().addAll(list);
	}

}

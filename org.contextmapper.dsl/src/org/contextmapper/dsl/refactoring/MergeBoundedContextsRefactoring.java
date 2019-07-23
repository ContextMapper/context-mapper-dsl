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

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class MergeBoundedContextsRefactoring extends AbstractRefactoring implements Refactoring {

	private String boundedContext1;
	private String boundedContext2;
	private boolean takeAttributesFromSecondBoundedContext = false;

	public MergeBoundedContextsRefactoring(String boundedContext1, String boundedContext2) {
		this.boundedContext1 = boundedContext1;
		this.boundedContext2 = boundedContext2;
	}

	public MergeBoundedContextsRefactoring(String boundedContext1, String boundedContext2, boolean takeAttributesFromSecondBoundedContext) {
		this(boundedContext1, boundedContext2);
		this.takeAttributesFromSecondBoundedContext = takeAttributesFromSecondBoundedContext;
	}

	@Override
	protected void doRefactor() {
		// do nothing if the two parameters point to same BC
		if (boundedContext1.equals(boundedContext2))
			return;

		Optional<BoundedContext> optionalBC1 = getAllBoundedContexts().stream().filter(bc -> bc.getName().equals(boundedContext1)).findFirst();
		Optional<BoundedContext> optionalBC2 = getAllBoundedContexts().stream().filter(bc -> bc.getName().equals(boundedContext2)).findFirst();

		// do nothing if one of the BCs does not exist
		if (!optionalBC1.isPresent() || !optionalBC2.isPresent())
			return;

		BoundedContext bc1 = optionalBC1.get();
		BoundedContext bc2 = optionalBC2.get();

		// inverse merging, if requested
		if (takeAttributesFromSecondBoundedContext) {
			bc1 = optionalBC2.get();
			bc2 = optionalBC1.get();
		}

		// move content from BC2 to BC1
		bc1.getAggregates().addAll(bc2.getAggregates());
		bc1.getModules().addAll(bc2.getModules());
		bc1.getImplementedSubdomains().addAll(bc2.getImplementedSubdomains());
		if (bc1.getType().equals(BoundedContextType.TEAM))
			bc1.getRealizedBoundedContexts().addAll(bc2.getRealizedBoundedContexts());
		bc1.getResponsibilities().addAll(bc2.getResponsibilities());
		bc1.setImplementationTechnology(mergeImplementationTechnologies(bc1.getImplementationTechnology(), bc2.getImplementationTechnology()));

		// remove BC2
		handleContextMapChanges(bc1, bc2);
		this.model.getBoundedContexts().remove(bc2);
		this.model.eAllContents();
		saveResource();
	}

	private String mergeImplementationTechnologies(String implementationTechnology1, String implementationTechnology2) {
		if (implementationTechnology1 == null && implementationTechnology2 == null)
			return null;

		Set<String> implementationTechnologies = Sets.newHashSet();
		if (implementationTechnology1 != null && !"".equals(implementationTechnology1))
			implementationTechnologies.add(implementationTechnology1);
		if (implementationTechnology2 != null && !"".equals(implementationTechnology2))
			implementationTechnologies.add(implementationTechnology2);
		return String.join(", ", implementationTechnologies);
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

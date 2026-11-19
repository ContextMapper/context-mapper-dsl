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

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.serializer.ISerializer;

import com.google.common.collect.Sets;

public class SplitBoundedContextByOwner implements SemanticCMLRefactoring {

	private SplitBoundedContextByAggregateAttribute coreAR;

	public SplitBoundedContextByOwner(String boundedContextName) {
		this.coreAR = new SplitBoundedContextByAggregateAttribute(aggregate -> toKey(getOwner(aggregate)), boundedContextName);

		// if the context has an owner, the owner's aggregates stay in the context and the new contexts are owned by the team of their aggregates
		this.coreAR.setKeyOfSetStayingInOriginalBCFunction(bc -> bc.getOwner() != null ? toKey(bc.getOwner()) : null);
		this.coreAR.setNewBCInitializer((originalBC, newBC) -> {
			if (originalBC.getOwner() != null && !newBC.getAggregates().isEmpty())
				newBC.setOwner(newBC.getAggregates().get(0).getOwner());
		});
	}

	// aggregates without an owner belong to the owner of their context
	private BoundedContext getOwner(Aggregate aggregate) {
		if (aggregate.getOwner() != null)
			return aggregate.getOwner();
		BoundedContext boundedContext = EcoreUtil2.getContainerOfType(aggregate, BoundedContext.class);
		return boundedContext != null ? boundedContext.getOwner() : null;
	}

	private CompoundKey toKey(BoundedContext owner) {
		return owner != null ? new CompoundKey(Sets.newHashSet(owner.getName())) : new CompoundKey(Sets.newHashSet());
	}

	@Override
	public void refactor(CMLResource resource) {
		this.coreAR.refactor(resource);
	}

	@Override
	public void refactor(CMLResource resource, ResourceSet consistencyCheckResources) {
		this.coreAR.refactor(resource, consistencyCheckResources);
	}

	@Override
	public void persistChanges(ISerializer serializer) {
		this.coreAR.persistChanges(serializer);
	}

}

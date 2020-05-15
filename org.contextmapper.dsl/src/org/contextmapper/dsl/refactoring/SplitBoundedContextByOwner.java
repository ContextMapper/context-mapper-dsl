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

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.google.common.collect.Sets;

public class SplitBoundedContextByOwner implements SemanticCMLRefactoring {

	private SplitBoundedContextByAggregateAttribute coreAR;

	public SplitBoundedContextByOwner(String boundedContextName) {
		this.coreAR = new SplitBoundedContextByAggregateAttribute(aggregate -> {
			if (aggregate.getOwner() != null)
				return new CompoundKey(Sets.newHashSet(aggregate.getOwner().getName()));
			else
				return new CompoundKey(Sets.newHashSet());
		}, boundedContextName);
	}

	@Override
	public void refactor(CMLResourceContainer resource) {
		this.coreAR.refactor(resource);
	}

	@Override
	public void refactor(CMLResourceContainer resource, ResourceSet consistencyCheckResources) {
		this.coreAR.refactor(resource, consistencyCheckResources);
	}

	@Override
	public void persistChanges() {
		this.coreAR.persistChanges();
	}

}

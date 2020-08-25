/*
 * Copyright 2018-2020 The Context Mapper Project Team
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

import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResource;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class SplitBoundedContextByFeatures implements SemanticCMLRefactoring {

	private SplitBoundedContextByAggregateAttribute coreAR;

	public SplitBoundedContextByFeatures(String boundedContextName) {
		this.coreAR = new SplitBoundedContextByAggregateAttribute(
				aggregate -> new CompoundKey(aggregate.getUserRequirements().stream().map(uc -> uc.getName()).collect(Collectors.toSet())), boundedContextName);
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
	public void persistChanges() {
		this.coreAR.persistChanges();
	}

}

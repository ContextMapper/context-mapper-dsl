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
package org.contextmapper.dsl.ide.actions.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.ide.actions.CMLCodeAction;
import org.eclipse.emf.ecore.EObject;

/**
 * Action that calls the "Split Bounded Context by Owner" refactoring.
 * 
 * @author Stefan Kapferer
 *
 */
public class SplitBoundedContextByOwnerAction extends CMLCodeAction {

	public SplitBoundedContextByOwnerAction() {
		this.setTitle("Split Bounded Context by Owner");
		this.setCommand("cml.ar.splitBCByOwner");
	}

	@Override
	public boolean isApplicable(List<EObject> editorSelection) {
		Set<BoundedContext> boundedContexts = editorSelection.stream().filter(o -> o instanceof BoundedContext).map(o -> (BoundedContext) o).collect(Collectors.toSet());

		if (boundedContexts.isEmpty() || boundedContexts.size() > 1)
			return false;

		BoundedContext selectedContext = boundedContexts.iterator().next();
		Set<String> owners = selectedContext.getAggregates().stream().map(agg -> agg.getOwner() == null ? "" : agg.getOwner().getName()).collect(Collectors.toSet());
		return owners.size() > 1;
	}

}

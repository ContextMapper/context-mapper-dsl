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

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.ide.actions.CMLCodeAction;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.lsp4j.Command;

import com.google.common.collect.Lists;

/**
 * Action that calls the "Extract Aggregates by Cohesion" refactoring.
 * 
 * @author Stefan Kapferer
 *
 */
public class ExtractAggregatesByCohesionAction implements CMLCodeAction {

	private CMLResourceContainer cmlResource;
	private List<EObject> editorSelection;

	public ExtractAggregatesByCohesionAction(CMLResourceContainer cmlResource, List<EObject> editorSelection) {
		this.cmlResource = cmlResource;
		this.editorSelection = editorSelection;
	}

	@Override
	public boolean isApplicable() {
		Set<BoundedContext> bcs = getSelectedSystemBoundedContexts();
		return bcs.size() == 1 && bcs.iterator().next().getAggregates().size() > 1;
	}

	@Override
	public Command getCommand() {
		List<Object> commandArguments = Lists.newLinkedList();
		commandArguments.add(cmlResource.getResource().getURI().toString());
		commandArguments.add(getSelectedSystemBoundedContext().getName());
		return new Command("Extract Aggregates By Cohesion", "cml.ar.extractAggregatesByCohesion.proxy", commandArguments);
	}

	private BoundedContext getSelectedSystemBoundedContext() {
		return getSelectedSystemBoundedContexts().iterator().next();
	}

	private Set<BoundedContext> getSelectedSystemBoundedContexts() {
		return editorSelection.stream().filter(o -> o instanceof BoundedContext).map(o -> (BoundedContext) o).collect(Collectors.toSet());
	}

}

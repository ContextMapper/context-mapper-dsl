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

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.ide.actions.CMLCodeAction;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.lsp4j.Command;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Lists;

/**
 * Action that calls the "Merge Bounded Contexts" refactoring.
 * 
 * @author Stefan Kapferer
 *
 */
public class MergeBoundedContextsAction implements CMLCodeAction {

	private CMLResource cmlResource;
	private List<EObject> editorSelection;

	public MergeBoundedContextsAction(CMLResource cmlResource, List<EObject> editorSelection) {
		this.cmlResource = cmlResource;
		this.editorSelection = editorSelection;
	}

	@Override
	public boolean isApplicable() {
		Set<BoundedContext> boundedContexts = getSelectedBoundedContexts();
		if (boundedContexts.isEmpty() || boundedContexts.size() > 1)
			return false;

		ContextMappingModel model = (ContextMappingModel) EcoreUtil2.getRootContainer(boundedContexts.iterator().next());
		return model.getBoundedContexts().size() > 1;
	}

	@Override
	public Command getCommand() {
		BoundedContext firstContext = getSelectedBoundedContext();
		ContextMappingModel model = (ContextMappingModel) EcoreUtil2.getRootContainer(firstContext);
		List<Object> commandArguments = Lists.newLinkedList();
		commandArguments.add(cmlResource.getURI().toString());
		commandArguments.add(firstContext.getName());
		commandArguments.addAll(EcoreUtil2.eAllOfType(model, BoundedContext.class).stream().map(bc -> bc.getName()).filter(name -> !name.equals(firstContext.getName())).collect(Collectors.toSet()));

		return new Command("Merge Bounded Contexts", "cml.ar.mergeBoundedContexts.proxy", commandArguments);
	}

	private BoundedContext getSelectedBoundedContext() {
		return getSelectedBoundedContexts().iterator().next();
	}

	private Set<BoundedContext> getSelectedBoundedContexts() {
		return editorSelection.stream().filter(o -> o instanceof BoundedContext).map(o -> (BoundedContext) o).collect(Collectors.toSet());
	}

}

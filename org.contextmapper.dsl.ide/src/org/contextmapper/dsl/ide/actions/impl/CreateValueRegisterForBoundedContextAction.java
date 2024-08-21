/*
 * Copyright 2024 The Context Mapper Project Team
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

import org.contextmapper.dsl.cml.CMLModelObjectsResolvingHelper;
import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.ide.actions.CMLCodeAction;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.lsp4j.Command;

import com.google.common.collect.Lists;

/**
 * Action that calls the "Create Value Register for Bounded Context"
 * refactoring.
 * 
 * @author Stefan Kapferer
 *
 */
public class CreateValueRegisterForBoundedContextAction implements CMLCodeAction {

	private CMLResource cmlResource;
	private List<EObject> editorSelection;

	public CreateValueRegisterForBoundedContextAction(CMLResource cmlResource, List<EObject> editorSelection) {
		this.cmlResource = cmlResource;
		this.editorSelection = editorSelection;
	}

	@Override
	public boolean isApplicable() {
		Set<BoundedContext> contexts = getSelectedBoundedContexts();
		if (contexts.isEmpty() || contexts.size() > 1)
			return false;
		BoundedContext context = contexts.iterator().next();
		return !(new CMLModelObjectsResolvingHelper((ContextMappingModel) context.eContainer())
				.isReferencedInAValueRegister(context));
	}

	@Override
	public Command getCommand() {
		BoundedContext context = getSelectedBoundedContext();
		List<Object> commandArguments = Lists.newLinkedList();
		commandArguments.add(cmlResource.getURI().toString());
		commandArguments.add(context.getName());
		return new Command("Create Value Register For Bounded Context",
				"cml.ar.createValueRegisterForBoundedContext.proxy", commandArguments);
	}

	private BoundedContext getSelectedBoundedContext() {
		return getSelectedBoundedContexts().iterator().next();
	}

	private Set<BoundedContext> getSelectedBoundedContexts() {
		return editorSelection.stream().filter(o -> o instanceof BoundedContext).map(o -> (BoundedContext) o)
				.collect(Collectors.toSet());
	}

}

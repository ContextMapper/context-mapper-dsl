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
import org.contextmapper.dsl.refactoring.ExtractSuggestedService;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.lsp4j.Command;

import com.google.common.collect.Lists;

/**
 * Action that calls the "Extract Suggested Service in Original Model"
 * refactoring.
 * 
 * @author Stefan Kapferer
 *
 */
public class ExtractSuggestedServiceAction implements CMLCodeAction {

	private CMLResourceContainer cmlResource;
	private List<EObject> editorSelection;

	public ExtractSuggestedServiceAction(CMLResourceContainer cmlResource, List<EObject> editorSelection) {
		this.cmlResource = cmlResource;
		this.editorSelection = editorSelection;
	}

	@Override
	public boolean isApplicable() {
		Set<BoundedContext> bcs = getSelectedSystemBoundedContexts();
		return cmlResource.getResource().getURI().lastSegment().matches(ExtractSuggestedService.SERVICE_CUTTER_SUGGESTION_FILE_NAME_PATTERN) && bcs.size() == 1;
	}

	@Override
	public Command getCommand() {
		BoundedContext bc = getSelectedSystemBoundedContext();
		List<Object> commandArguments = Lists.newLinkedList();
		URI originalURI = new ExtractSuggestedService(bc, "NoNameBC").constructOriginalModelUri();
		commandArguments.add(originalURI.toString());
		commandArguments.add(cmlResource.getResource().getURI().toString());
		commandArguments.add(bc.getName());
		return new Command("Extract Suggested Service in Original Model", "cml.ar.extractSuggestedService.proxy", commandArguments);
	}

	private BoundedContext getSelectedSystemBoundedContext() {
		return getSelectedSystemBoundedContexts().iterator().next();
	}

	private Set<BoundedContext> getSelectedSystemBoundedContexts() {
		return editorSelection.stream().filter(o -> o instanceof BoundedContext).map(o -> (BoundedContext) o).collect(Collectors.toSet());
	}

}

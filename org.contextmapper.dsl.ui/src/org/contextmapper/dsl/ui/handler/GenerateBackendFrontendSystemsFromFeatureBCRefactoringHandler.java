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
package org.contextmapper.dsl.ui.handler;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLImportResolver;
import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType;
import org.contextmapper.dsl.refactoring.DeriveFrontendAndBackendSystemsFromFeatureBoundedContext;
import org.contextmapper.dsl.ui.handler.wizard.DeriveBackendFrontendFromFeatureContext;
import org.contextmapper.dsl.ui.handler.wizard.DeriveBackendFrontendSystemsFromFeatureWizard;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.common.collect.Lists;

public class GenerateBackendFrontendSystemsFromFeatureBCRefactoringHandler extends AbstractRefactoringWithUserInputHandler {

	@Override
	protected void executeRefactoring(CMLResourceContainer resource, ExecutionEvent event) {
		BoundedContext selectedContext = (BoundedContext) getSelectedElement();
		Set<String> allBCNames = collectAllBoundedContexts().stream().map(bc -> bc.getName()).collect(Collectors.toSet());

		DeriveBackendFrontendFromFeatureContext refactoringContext = new DeriveBackendFrontendFromFeatureContext(selectedContext.getName(), allBCNames);
		refactoringContext.setFrontendImplementationTechnology(selectedContext.getImplementationTechnology() != null ? selectedContext.getImplementationTechnology() : "");
		refactoringContext.setBackendImplementationTechnology(selectedContext.getImplementationTechnology() != null ? selectedContext.getImplementationTechnology() : "");
		refactoringContext.setRelationshipImplementationTechnology("");
		new WizardDialog(HandlerUtil.getActiveShell(event), new DeriveBackendFrontendSystemsFromFeatureWizard(refactoringContext, executionContext -> {
			DeriveFrontendAndBackendSystemsFromFeatureBoundedContext ar = new DeriveFrontendAndBackendSystemsFromFeatureBoundedContext(
					executionContext.getFeatureBoundedContextName(), executionContext.getRelationshipType());
			ar.deriveViewModelInFronted(executionContext.deriveViewModelInFrontend());
			ar.setBackendName(executionContext.getBackendName());
			ar.setFrontendName(executionContext.getFrontendName());
			ar.setBackendImplementationTechnology(executionContext.getBackendImplementationTechnology());
			ar.setFrontendImplementationTechnology(executionContext.getFrontendImplementationTechnology());
			ar.setRelationshipImplTechnology(executionContext.getRelationshipImplementationTechnology());
			return finishRefactoring(ar, resource, event);
		})).open();
	}

	@Override
	public boolean isEnabled() {
		EObject obj = getSelectedElement();

		if (!(obj instanceof BoundedContext))
			return false;

		BoundedContext selectedContext = (BoundedContext) obj;
		if (selectedContext.getType() != BoundedContextType.FEATURE && selectedContext.getType() != BoundedContextType.APPLICATION)
			return false;

		return super.isEnabled();
	}

	private List<BoundedContext> collectAllBoundedContexts() {
		List<BoundedContext> bcList = Lists.newLinkedList();
		Resource currentResource = getCurrentResource();
		bcList.addAll(getCurrentContextMappingModel().getBoundedContexts());
		for (CMLResourceContainer cmlResource : new CMLImportResolver().resolveImportedResources(new CMLResourceContainer(currentResource))) {
			bcList.addAll(cmlResource.getContextMappingModel().getBoundedContexts());
		}
		return bcList;
	}

}

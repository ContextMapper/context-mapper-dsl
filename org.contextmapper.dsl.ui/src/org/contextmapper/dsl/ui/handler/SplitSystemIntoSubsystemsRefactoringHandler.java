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
import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType;
import org.contextmapper.dsl.refactoring.SplitSystemIntoSubsystems;
import org.contextmapper.dsl.ui.handler.wizard.SplitSystemIntoSubsystemsContext;
import org.contextmapper.dsl.ui.handler.wizard.SplitSystemIntoSubsystemsWizard;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.common.collect.Lists;

public class SplitSystemIntoSubsystemsRefactoringHandler extends AbstractRefactoringWithUserInputHandler {

	@Override
	protected void executeRefactoring(CMLResource resource, ExecutionEvent event) {
		BoundedContext selectedContext = (BoundedContext) getSelectedElement();
		Set<String> allBCNames = collectAllBoundedContexts().stream().map(bc -> bc.getName()).collect(Collectors.toSet());

		SplitSystemIntoSubsystemsContext refactoringContext = new SplitSystemIntoSubsystemsContext(selectedContext.getName(), selectedContext.getName(),
				selectedContext.getName() + "_New_Subsystem", allBCNames);
		new WizardDialog(HandlerUtil.getActiveShell(event), new SplitSystemIntoSubsystemsWizard(refactoringContext, executionContext -> {
			SplitSystemIntoSubsystems ar = new SplitSystemIntoSubsystems(executionContext.getOriginalSystemName(), executionContext.getExistingContextSubsystemName(),
					executionContext.getNewSubsystemName());
			ar.setIntegrationType(executionContext.getIntegrationType());
			ar.setRelationshipType(executionContext.getRelationshipType());
			ar.setNewSubsystemImplementationTechnology(executionContext.getNewSubsystemImplementationTechnology());
			ar.setNewRelationshipImplementationTechnology(executionContext.getNewRelationshipImplementationTechnology());
			ar.copyDomainModel(executionContext.copyDomainModel());
			return finishRefactoring(ar, resource, event);
		})).open();
	}

	@Override
	public boolean isEnabled() {
		EObject obj = getSelectedElement();

		if (!(obj instanceof BoundedContext))
			return false;

		BoundedContext selectedContext = (BoundedContext) obj;
		if (selectedContext.getType() != BoundedContextType.SYSTEM)
			return false;

		return super.isEnabled();
	}

	private List<BoundedContext> collectAllBoundedContexts() {
		List<BoundedContext> bcList = Lists.newLinkedList();
		Resource currentResource = getCurrentResource();
		bcList.addAll(getCurrentContextMappingModel().getBoundedContexts());
		for (CMLResource cmlResource : new CMLImportResolver().resolveImportedResources(new CMLResource(currentResource))) {
			bcList.addAll(cmlResource.getContextMappingModel().getBoundedContexts());
		}
		return bcList;
	}

}

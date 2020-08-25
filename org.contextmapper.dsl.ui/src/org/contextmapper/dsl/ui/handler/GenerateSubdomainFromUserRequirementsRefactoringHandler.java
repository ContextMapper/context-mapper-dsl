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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.UserRequirement;
import org.contextmapper.dsl.refactoring.DeriveSubdomainFromUserRequirements;
import org.contextmapper.dsl.ui.handler.wizard.DeriveSubdomainsFromRequirementsContext;
import org.contextmapper.dsl.ui.handler.wizard.DeriveSubdomainsFromRequirementsWizard;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.common.collect.Maps;

public class GenerateSubdomainFromUserRequirementsRefactoringHandler extends AbstractRefactoringWithUserInputHandler {

	@Override
	public boolean isEnabled() {
		Set<EObject> objects = getAllSelectedElements();

		if (objects == null || objects.isEmpty() || !super.isEnabled())
			return false;

		// selection must at least contain one user requirements (use case or user
		// story)
		return objects.stream().filter(o -> o instanceof UserRequirement).findFirst().isPresent() && super.isEnabled();
	}

	@Override
	protected void executeRefactoring(CMLResource resource, ExecutionEvent event) {
		Set<UserRequirement> userRequirements = getAllSelectedElements().stream().filter(o -> o instanceof UserRequirement).map(o -> (UserRequirement) o)
				.collect(Collectors.toSet());

		List<String> allDomains = getCurrentContextMappingModel().getDomains().stream().map(d -> d.getName()).collect(Collectors.toList());
		String initialDomain = allDomains.isEmpty() ? "" : allDomains.iterator().next();

		DeriveSubdomainsFromRequirementsContext refactoringContext = new DeriveSubdomainsFromRequirementsContext(initialDomain,
				collectAllDomainsAndSubdomains(getCurrentContextMappingModel()));
		new WizardDialog(HandlerUtil.getActiveShell(event), new DeriveSubdomainsFromRequirementsWizard(refactoringContext, executionContext -> {
			return finishRefactoring(new DeriveSubdomainFromUserRequirements(executionContext.getDomainName(), executionContext.getSubdomainName(),
					userRequirements.stream().map(ur -> ur.getName()).collect(Collectors.toSet())), resource, event);
		})).open();
	}

	private Map<String, Set<String>> collectAllDomainsAndSubdomains(ContextMappingModel model) {
		Map<String, Set<String>> allDomainsAndSubdomains = Maps.newHashMap();
		for (Domain domain : model.getDomains()) {
			allDomainsAndSubdomains.put(domain.getName(), domain.getSubdomains().stream().map(sd -> sd.getName()).collect(Collectors.toSet()));
		}
		return allDomainsAndSubdomains;
	}

}

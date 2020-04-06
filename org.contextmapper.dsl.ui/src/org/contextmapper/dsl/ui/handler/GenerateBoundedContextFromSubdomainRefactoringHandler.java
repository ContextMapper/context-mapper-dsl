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

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.refactoring.DeriveBoundedContextFromSubdomains;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.ecore.EObject;

public class GenerateBoundedContextFromSubdomainRefactoringHandler extends AbstractRefactoringHandler {

	@Override
	protected void executeRefactoring(CMLResourceContainer resource, ExecutionEvent event) {
		Set<Subdomain> subdomains = getAllSelectedElements().stream().filter(sd -> sd instanceof Subdomain).map(sd -> (Subdomain) sd).collect(Collectors.toSet());
		Set<String> ids = subdomains.stream().map(sd -> sd.getName()).collect(Collectors.toSet());
		new DeriveBoundedContextFromSubdomains("NewContextFromSubdomains", ids).doRefactor(resource, getAllResources());
	}

	@Override
	public boolean isEnabled() {
		Set<EObject> objs = getAllSelectedElements();

		if (objs.isEmpty())
			return false;

		// selection must at least contain one subdomain
		Optional<Subdomain> optSubdomain = objs.stream().filter(o -> o instanceof Subdomain).map(o -> (Subdomain) o).findFirst();
		if (!optSubdomain.isPresent())
			return false;

		return true;
	}

}

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
package org.contextmapper.dsl.quickfixes;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class CreateMissingBoundedContextQuickFix implements CMLQuickFix<ContextMappingModel> {

	private String missingContextName;

	public CreateMissingBoundedContextQuickFix(String missingContextName) {
		this.missingContextName = missingContextName;
	}

	@Override
	public void applyQuickfix(ContextMappingModel model) {
		BoundedContext bc = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		bc.setName(missingContextName);

		model.getBoundedContexts().add(bc);
	}

	@Override
	public void applyQuickfix2EObject(EObject contextObject) {
		EObject rootContainer = EcoreUtil.getRootContainer(contextObject);
		if (rootContainer != null)
			CMLQuickFix.super.applyQuickfix2EObject(rootContainer);
	}

	@Override
	public String getName() {
		return "Create a Bounded Context named '" + this.missingContextName + "'.";
	}

	@Override
	public String getDescription() {
		return "Creates the missing Bounded Context.";
	}

}

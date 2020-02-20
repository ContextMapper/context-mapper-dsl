/*
 * Copyright 2019 The Context Mapper Project Team
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

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.Partnership;
import org.contextmapper.dsl.refactoring.SwitchFromPartnershipToSharedKernelRefactoring;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.ecore.EObject;

public class SwitchFromPartnershipToSharedKernelRefactoringHandler extends AbstractRefactoringHandler {

	@Override
	protected void executeRefactoring(CMLResourceContainer resource, ExecutionEvent event) {
		Partnership partnership = (Partnership) getSelectedElement();
		new SwitchFromPartnershipToSharedKernelRefactoring(partnership.getParticipant1().getName(), partnership.getParticipant2().getName()).doRefactor(resource);
	}

	@Override
	public boolean isEnabled() {
		EObject obj = getSelectedElement();

		if (obj == null || !super.isEnabled())
			return false;

		// only allowed on Partnership relationship
		if (!(obj instanceof Partnership))
			return false;

		return true;
	}

}

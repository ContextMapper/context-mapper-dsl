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
package org.contextmapper.dsl.ui.handler.value_registers;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.Value;
import org.contextmapper.dsl.contextMappingDSL.ValueRegister;
import org.contextmapper.dsl.refactoring.SemanticCMLRefactoring;
import org.contextmapper.dsl.refactoring.value_registers.WrapValueInClusterRefactoring;
import org.contextmapper.dsl.ui.handler.AbstractRefactoringHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.ecore.EObject;

public class WrapValueInClusterHandler extends AbstractRefactoringHandler {

	@Override
	protected void executeRefactoring(CMLResource resource, ExecutionEvent event) {
		Value value = (Value) getSelectedElement();
		SemanticCMLRefactoring ar = new WrapValueInClusterRefactoring(value.getName());
		ar.refactor(resource, getAllResources());
		ar.persistChanges(serializer);
	}

	@Override
	public boolean isEnabled() {
		if (moreThenOneElementSelected())
			return false;
		
		EObject obj = getSelectedElement();

		if (obj == null || !super.isEnabled())
			return false;

		if (!(obj instanceof Value))
			return false;

		Value value = (Value) obj;
		return value.eContainer() instanceof ValueRegister;
	}

}
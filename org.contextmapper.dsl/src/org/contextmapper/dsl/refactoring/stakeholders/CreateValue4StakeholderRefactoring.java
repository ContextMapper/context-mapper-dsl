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
package org.contextmapper.dsl.refactoring.stakeholders;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.Stakeholder;
import org.contextmapper.dsl.contextMappingDSL.Value;
import org.contextmapper.dsl.contextMappingDSL.ValueElicitation;
import org.contextmapper.dsl.contextMappingDSL.ValueRegister;
import org.contextmapper.dsl.refactoring.AbstractRefactoring;
import org.contextmapper.dsl.refactoring.SemanticCMLRefactoring;
import org.eclipse.xtext.EcoreUtil2;

public class CreateValue4StakeholderRefactoring extends AbstractRefactoring implements SemanticCMLRefactoring {

	private String stakeholderName;

	public CreateValue4StakeholderRefactoring(String stakeholderName) {
		this.stakeholderName = stakeholderName;
	}

	@Override
	protected void doRefactor() {
		Stakeholder s = getSelectedStakeholder();

		ValueRegister valueRegister = getOrCreateValueRegister();
		Value value = ContextMappingDSLFactory.eINSTANCE.createValue();
		value.setName("To_Be_Defined");
		ValueElicitation elicitation = ContextMappingDSLFactory.eINSTANCE.createValueElicitation();
		elicitation.setStakeholder(s);
		value.getElicitations().add(elicitation);
		valueRegister.getValues().add(value);
	}

	private Stakeholder getSelectedStakeholder() {
		return EcoreUtil2.<Stakeholder>getAllContentsOfType(model, Stakeholder.class).stream()
				.filter(s -> s.getName().equals(stakeholderName)).findFirst().get();
	}

	private ValueRegister getOrCreateValueRegister() {
		if (model.getValueRegisters().isEmpty()) {
			ValueRegister valueRegister = ContextMappingDSLFactory.eINSTANCE.createValueRegister();
			valueRegister.setName("Register_Name_To_Be_Changed");
			model.getValueRegisters().add(valueRegister);
			return valueRegister;
		}
		return model.getValueRegisters().get(0);
	}

}

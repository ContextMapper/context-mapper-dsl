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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.refactoring.AbstractRefactoringTest;
import org.junit.jupiter.api.Test;

public class CreateValue4StakeholderRefactoringTest extends AbstractRefactoringTest {

	@Test
	void canCreateValueIfARegisterAlreadyExists() throws IOException {
		// given
		String inputModelName = "create-value-for-stakeholder-1.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		new CreateValue4StakeholderRefactoring("Tester").refactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getValueRegisters().size());
		assertEquals(1, model.getValueRegisters().get(0).getValues().size());
		assertEquals("To_Be_Defined", model.getValueRegisters().get(0).getValues().get(0).getName());
		assertEquals(1, model.getValueRegisters().get(0).getValues().get(0).getDemonstrators().size());
		assertEquals("Tester", model.getValueRegisters().get(0).getValues().get(0).getElicitations().get(0)
				.getStakeholder().getName());
		assertEquals("MEDIUM", model.getValueRegisters().get(0).getValues().get(0).getElicitations().get(0)
				.getPriority().getLiteral());
		assertEquals(1,
				model.getValueRegisters().get(0).getValues().get(0).getElicitations().get(0).getConsequences().size());
	}

	@Test
	void canCreateValueIfNoRegisterExistsYet() throws IOException {
		// given
		String inputModelName = "create-value-for-stakeholder-2.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		new CreateValue4StakeholderRefactoring("Tester").refactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getValueRegisters().size());
		assertEquals("Register_Name_To_Be_Changed", model.getValueRegisters().get(0).getName());
		assertEquals(1, model.getValueRegisters().get(0).getValues().size());
		assertEquals("To_Be_Defined", model.getValueRegisters().get(0).getValues().get(0).getName());
	}

}

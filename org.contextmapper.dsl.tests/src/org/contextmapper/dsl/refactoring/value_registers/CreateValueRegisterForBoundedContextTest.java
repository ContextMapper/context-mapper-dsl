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
package org.contextmapper.dsl.refactoring.value_registers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.refactoring.AbstractRefactoringTest;
import org.junit.jupiter.api.Test;

public class CreateValueRegisterForBoundedContextTest extends AbstractRefactoringTest {

	@Test
	void doesNotCreateValueRegisterForBoundedContextIfAlreadyPresent() throws IOException {
		// given
		String inputModelName = "create-value-register-for-bounded-context-1.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		new CreateValueRegisterForBoundedContext("TestContextWithRegister").refactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getValueRegisters().size());
	}

	@Test
	void canCreateValueRegisterForBoundedContext() throws IOException {
		// given
		String inputModelName = "create-value-register-for-bounded-context-1.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		new CreateValueRegisterForBoundedContext("TestContextWithoutRegister").refactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(2, model.getValueRegisters().size());
		assertEquals("ValueRegisterFor_TestContextWithoutRegister", model.getValueRegisters().get(1).getName());
		assertEquals("Compliance with values in value register",
				model.getBoundedContexts().get(1).getResponsibilities().get(0));
	}
}

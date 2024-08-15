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

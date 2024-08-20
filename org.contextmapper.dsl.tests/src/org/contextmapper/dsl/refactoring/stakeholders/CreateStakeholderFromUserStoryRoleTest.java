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

public class CreateStakeholderFromUserStoryRoleTest extends AbstractRefactoringTest {

	@Test
	void canCreateValueIfNameIsOK() throws IOException {
		// given
		String inputModelName = "create-stakeholder-for-roleinstory-1.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		new CreateStakeholderForUserStoryRole("SampleStory1").refactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getStakeholders().size());
		assertEquals(1, model.getStakeholders().get(0).getStakeholders().size());
		assertEquals("SampleUserOfFutureSystem", model.getStakeholders().get(0).getStakeholders().get(0).getName());
	}

	@Test
	void canCreateValueIfNameContainsBlanksOrOtherSpecialCharacters() throws IOException {
		// given
		String inputModelName = "create-stakeholder-for-roleinstory-1.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		new CreateStakeholderForUserStoryRole("SampleStory2").refactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getStakeholders().size());
		assertEquals(1, model.getStakeholders().get(0).getStakeholders().size());
		assertEquals("Sample_User_Of_Future_System", model.getStakeholders().get(0).getStakeholders().get(0).getName());
	}

	@Test
	void createOnlyOneStakeholderEvenIfRefactoringIsExecutedTwice() throws IOException {
		// given
		String inputModelName = "create-stakeholder-for-roleinstory-1.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		CreateStakeholderForUserStoryRole ar = new CreateStakeholderForUserStoryRole("SampleStory1");
		ar.refactor(input);
		ar.refactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getStakeholders().size());
		assertEquals(1, model.getStakeholders().get(0).getStakeholders().size());
		assertEquals("SampleUserOfFutureSystem", model.getStakeholders().get(0).getStakeholders().get(0).getName());
	}
}

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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.StakeholderGroup;
import org.contextmapper.dsl.refactoring.AbstractRefactoringTest;
import org.junit.jupiter.api.Test;

public class MoveStakeholderToNewStakeholderGroupRefactoringTest extends AbstractRefactoringTest {

	@Test
	void canMoveStakeholderIntoGroup() throws IOException {
		// given
		String inputModelName = "move-stakeholder-to-group-1.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		new MoveStakeholderToNewStakeholderGroupRefactoring("Tester").refactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getStakeholders().get(0).getStakeholders().size());
		assertEquals("Groupname_To_Be_Changed", model.getStakeholders().get(0).getStakeholders().get(0).getName());
		assertTrue(model.getStakeholders().get(0).getStakeholders().get(0) instanceof StakeholderGroup);
		assertEquals(1,
				((StakeholderGroup) model.getStakeholders().get(0).getStakeholders().get(0)).getStakeholders().size());
		assertEquals("Tester", ((StakeholderGroup) model.getStakeholders().get(0).getStakeholders().get(0))
				.getStakeholders().get(0).getName());
	}

	@Test
	void canMoveStakeholderFromOneGroupToOther() throws IOException {
		// given
		String inputModelName = "move-stakeholder-to-group-2.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		new MoveStakeholderToNewStakeholderGroupRefactoring("Tester").refactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(2, model.getStakeholders().get(0).getStakeholders().size());
		assertTrue(model.getStakeholders().get(0).getStakeholders().get(0) instanceof StakeholderGroup);
		assertTrue(model.getStakeholders().get(0).getStakeholders().get(1) instanceof StakeholderGroup);
		StakeholderGroup newGroup = (StakeholderGroup) model.getStakeholders().get(0).getStakeholders().stream()
				.filter(s -> s.getName().equals("Groupname_To_Be_Changed")).findFirst().get();
		assertEquals("Tester", newGroup.getStakeholders().get(0).getName());
	}

}

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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.junit.jupiter.api.Test;

public class CreateMissingBoundedContextQuickFixTest extends AbstractQuickFixTest {

	@Test
	public void canCreateBoundedContext() throws IOException {
		// given
		CMLResourceContainer cmlResource = getResourceCopyOfTestCML("create-missing-bounded-context-test-1.cml");
		ContextMappingModel model = cmlResource.getContextMappingModel();

		// when
		new CreateMissingBoundedContextQuickFix("MissingContext").applyQuickfix(model);

		// then
		assertEquals(1, model.getBoundedContexts().size());
		assertEquals("MissingContext", model.getBoundedContexts().get(0).getName());
	}

	@Test
	public void canCreateBoundedContext4ContextMapContext() throws IOException {
		// given
		CMLResourceContainer cmlResource = getResourceCopyOfTestCML("create-missing-bounded-context-test-1.cml");
		ContextMappingModel model = cmlResource.getContextMappingModel();

		// when
		new CreateMissingBoundedContextQuickFix("MissingContext").applyQuickfix2EObject(model.getMap());

		// then
		assertEquals(1, model.getBoundedContexts().size());
		assertEquals("MissingContext", model.getBoundedContexts().get(0).getName());
	}

	@Test
	public void canProvideName() {
		// given
		CreateMissingBoundedContextQuickFix quickFix = new CreateMissingBoundedContextQuickFix("MissingContext");

		// when
		String name = quickFix.getName();

		// then
		assertEquals("Create a Bounded Context named 'MissingContext'.", name);
	}

	@Test
	public void canProvideDescription() {
		// given
		CreateMissingBoundedContextQuickFix quickFix = new CreateMissingBoundedContextQuickFix("MissingContext");

		// when
		String description = quickFix.getDescription();

		// then
		assertEquals("Creates the missing Bounded Context.", description);
	}

}

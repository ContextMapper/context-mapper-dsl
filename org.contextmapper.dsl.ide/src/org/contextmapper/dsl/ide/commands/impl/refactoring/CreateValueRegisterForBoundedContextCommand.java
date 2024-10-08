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
package org.contextmapper.dsl.ide.commands.impl.refactoring;

import org.contextmapper.dsl.ide.edit.WorkspaceEditRecorder;
import org.contextmapper.dsl.refactoring.SemanticCMLRefactoring;
import org.contextmapper.dsl.refactoring.value_registers.CreateValueRegisterForBoundedContext;
import org.eclipse.lsp4j.ExecuteCommandParams;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

public class CreateValueRegisterForBoundedContextCommand extends AbstractRefactoringCommand {

	public CreateValueRegisterForBoundedContextCommand(WorkspaceEditRecorder editRecorder) {
		super(editRecorder);
	}

	@Override
	protected SemanticCMLRefactoring getRefactoring(ExecuteCommandParams params) {
		JsonArray refactoringParams = (JsonArray) params.getArguments().get(1);
		JsonPrimitive boundedContextName = (JsonPrimitive) refactoringParams.get(0);
		return new CreateValueRegisterForBoundedContext(boundedContextName.getAsString());
	}

}

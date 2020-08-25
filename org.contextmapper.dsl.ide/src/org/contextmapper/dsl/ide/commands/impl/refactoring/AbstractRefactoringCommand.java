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
package org.contextmapper.dsl.ide.commands.impl.refactoring;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.ide.commands.CMLResourceCommand;
import org.contextmapper.dsl.ide.edit.WorkspaceEditRecorder;
import org.contextmapper.dsl.refactoring.SemanticCMLRefactoring;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.lsp4j.ApplyWorkspaceEditParams;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.xtext.ide.server.Document;
import org.eclipse.xtext.ide.server.ILanguageServerAccess;

public abstract class AbstractRefactoringCommand implements CMLResourceCommand {

	private WorkspaceEditRecorder editRecorder;

	public AbstractRefactoringCommand(WorkspaceEditRecorder editRecorder) {
		this.editRecorder = editRecorder;
	}

	/**
	 * Override this method to define the refactoring that shall be called.
	 */
	protected abstract SemanticCMLRefactoring getRefactoring(ExecuteCommandParams params);

	@Override
	public void executeCommand(CMLResource cmlResource, Document document, ILanguageServerAccess access, ExecuteCommandParams params) {
		access.getLanguageClient().applyEdit(new ApplyWorkspaceEditParams(editRecorder.recordWorkspaceEdit(access, cmlResource.getURI(), document, (Resource copiedResource) -> {
			CMLResource copiedCMLResource = new CMLResource(copiedResource);
			SemanticCMLRefactoring ar = getRefactoring(params);
			ar.refactor(copiedCMLResource);
		}), "Apply Architectural Refactoring (AR)"));
	}

}

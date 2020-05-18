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

import java.util.ArrayList;
import java.util.List;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.ide.commands.CMLResourceCommand;
import org.contextmapper.dsl.refactoring.SemanticCMLRefactoring;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.lsp4j.ApplyWorkspaceEditParams;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.xtext.formatting2.regionaccess.ITextReplacement;
import org.eclipse.xtext.ide.serializer.IChangeSerializer;
import org.eclipse.xtext.ide.serializer.IEmfResourceChange;
import org.eclipse.xtext.ide.serializer.ITextDocumentChange;
import org.eclipse.xtext.ide.server.Document;
import org.eclipse.xtext.ide.server.ILanguageServerAccess;
import org.eclipse.xtext.util.CollectionBasedAcceptor;
import org.eclipse.xtext.xbase.lib.ListExtensions;

import com.google.common.collect.Iterables;
import com.google.inject.Provider;

public abstract class AbstractRefactoringCommand implements CMLResourceCommand {

	@SuppressWarnings("restriction")
	private Provider<IChangeSerializer> serializerProvider;

	@SuppressWarnings("restriction")
	public AbstractRefactoringCommand(Provider<IChangeSerializer> serializerProvider) {
		this.serializerProvider = serializerProvider;
	}

	/**
	 * Override this method to define the refactoring that shall be called.
	 */
	protected abstract SemanticCMLRefactoring getRefactoring(ExecuteCommandParams params);

	@Override
	public void executeCommand(CMLResourceContainer cmlResource, Document document, ILanguageServerAccess access, ExecuteCommandParams params) {
		access.getLanguageClient().applyEdit(new ApplyWorkspaceEditParams(recordWorkspaceEdit(access, cmlResource.getResource().getURI(), document, (Resource copiedResource) -> {
			CMLResourceContainer copiedCMLResource = new CMLResourceContainer(copiedResource);
			SemanticCMLRefactoring ar = getRefactoring(params);
			ar.refactor(copiedCMLResource);
		}), "Apply Architectural Refactoring (AR)"));
	}

	@SuppressWarnings("restriction")
	private WorkspaceEdit recordWorkspaceEdit(ILanguageServerAccess access, URI resourceURI, Document document, IChangeSerializer.IModification<Resource> mod) {
		ResourceSet rs = access.newLiveScopeResourceSet(resourceURI);
		Resource copy = rs.getResource(resourceURI, true);
		IChangeSerializer serializer = serializerProvider.get();
		EcoreUtil.resolveAll(copy);
		serializer.addModification(copy, mod);
		List<IEmfResourceChange> documentchanges = new ArrayList<>();
		serializer.applyModifications(CollectionBasedAcceptor.of(documentchanges));
		WorkspaceEdit workspaceEdit = new WorkspaceEdit();
		for (ITextDocumentChange documentchange : Iterables.filter(documentchanges, ITextDocumentChange.class)) {
			List<TextEdit> edits = ListExtensions.map(documentchange.getReplacements(), (ITextReplacement replacement) -> {
				TextEdit textEdit = new TextEdit();
				textEdit.setNewText(replacement.getReplacementText());
				textEdit.setRange(new Range(document.getPosition(replacement.getOffset()), document.getPosition(replacement.getEndOffset())));
				return textEdit;
			});
			workspaceEdit.getChanges().put(documentchange.getNewURI().toString(), edits);
		}
		return workspaceEdit;
	}

}

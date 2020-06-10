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
package org.contextmapper.dsl.ui.quickfix

import org.contextmapper.dsl.quickfixes.CMLQuickFix
import org.contextmapper.dsl.quickfixes.tactic.ExtractIDValueObjectQuickFix
import org.contextmapper.dsl.validation.DomainObjectValidator
import org.eclipse.emf.ecore.EObject
import org.eclipse.jface.text.source.ISourceViewer
import org.eclipse.jface.text.source.SourceViewer
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext
import org.eclipse.xtext.ui.editor.quickfix.DefaultQuickfixProvider
import org.eclipse.xtext.ui.editor.quickfix.Fix
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor
import org.eclipse.xtext.ui.editor.utils.EditorUtils
import org.eclipse.xtext.validation.Issue

/**
 * Custom quickfix registry.
 */
class ContextMappingDSLQuickfixProvider extends DefaultQuickfixProvider {

	@Fix(DomainObjectValidator.ID_IS_PRIMITIVE_CODE)
	def extractValueObject(Issue issue, IssueResolutionAcceptor acceptor) {
		applyCMLQuickfix(issue, acceptor, new ExtractIDValueObjectQuickFix());
	}

	def applyCMLQuickfix(Issue issue, IssueResolutionAcceptor acceptor, CMLQuickFix<? extends EObject> quickfix) {
		acceptor.accept(issue, quickfix.name, quickfix.description,
			"upcase.png", [ EObject element, IModificationContext context |
				quickfix.applyQuickfix2EObject(element)
			]);
		val xEditor = EditorUtils.getActiveXtextEditor();
		if (xEditor !== null) {
			(xEditor.internalSourceViewer as SourceViewer).doOperation(ISourceViewer.FORMAT);
		}
	}
}

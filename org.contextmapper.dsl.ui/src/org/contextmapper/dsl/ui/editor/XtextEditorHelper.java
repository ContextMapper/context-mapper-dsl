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
package org.contextmapper.dsl.ui.editor;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.inject.Inject;

public class XtextEditorHelper {

	@Inject
	private EObjectAtOffsetHelper eObjectAtOffsetHelper;

	public EObject getSelectedElement(XtextEditor editor) {
		if (editor != null) {
			final ITextSelection selection = (ITextSelection) editor.getSelectionProvider().getSelection();
			ContextMapperRefactoringContext context = editor.getDocument().priorityReadOnly(new IUnitOfWork<ContextMapperRefactoringContext, XtextResource>() {
				@Override
				public ContextMapperRefactoringContext exec(XtextResource resource) throws Exception {
					EObject selectedElement = eObjectAtOffsetHelper.resolveElementAt(resource, selection.getOffset());
					if (selectedElement != null) {
						return new ContextMapperRefactoringContext(selectedElement);
					}
					return null;
				}

			});
			if (context != null) {
				return context.selectedObject;
			}
		}
		return null;
	}

	protected class ContextMapperRefactoringContext {
		private EObject selectedObject;

		public ContextMapperRefactoringContext(EObject selectedObject) {
			this.selectedObject = selectedObject;
		}
	}
}

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

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class XtextEditorHelper {

	@Inject
	private EObjectAtOffsetHelper eObjectAtOffsetHelper;

	public EObject getFirstSelectedElement(XtextEditor editor) {
		if (editor == null)
			return null;

		final ITextSelection selection = (ITextSelection) editor.getSelectionProvider().getSelection();
		EObject selectedObject = editor.getDocument().priorityReadOnly(new IUnitOfWork<EObject, XtextResource>() {
			@Override
			public EObject exec(XtextResource resource) throws Exception {
				EObject selectedElement = eObjectAtOffsetHelper.resolveElementAt(resource, selection.getOffset());
				if (selectedElement != null) {
					return selectedElement;
				}
				return null;
			}
		});
		return selectedObject;
	}

	public Set<EObject> getAllSelectedElements(XtextEditor editor) {
		if (editor == null)
			return Sets.newHashSet();

		final ITextSelection selection = (ITextSelection) editor.getSelectionProvider().getSelection();
		Set<EObject> selectedObjects = editor.getDocument().priorityReadOnly(new IUnitOfWork<Set<EObject>, XtextResource>() {
			@Override
			public Set<EObject> exec(XtextResource resource) throws Exception {
				Set<EObject> objects = Sets.newHashSet();
				int offset = selection.getOffset();
				for (int i = selection.getLength(); i >= 0; i--) {
					EObject selectedElement = eObjectAtOffsetHelper.resolveElementAt(resource, offset);
					if (selectedElement != null) {
						objects.add(selectedElement);
					}
					offset++;
				}
				return objects;
			}
		});
		return selectedObjects;
	}
}

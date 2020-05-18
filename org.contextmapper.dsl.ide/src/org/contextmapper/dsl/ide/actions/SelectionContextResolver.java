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
package org.contextmapper.dsl.ide.actions;

import java.util.List;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * Helper method to find all selected EObject (currently selected in editor).
 * 
 * @author Stefan Kapferer
 *
 */
public class SelectionContextResolver {

	@Inject
	private EObjectAtOffsetHelper offsetHelper;

	public List<EObject> resolveAllSelectedEObjects(CMLResourceContainer resource, int startOffset, int endOffset) {
		List<EObject> objectList = Lists.newLinkedList();

		for (int i = startOffset; i <= endOffset; i++) {
			XtextResource cmlResource = (XtextResource) resource.getResource();
			EObject object = offsetHelper.resolveElementAt(cmlResource, i);
			if (!objectList.contains(object))
				objectList.add(object);
		}

		return objectList;
	}

}

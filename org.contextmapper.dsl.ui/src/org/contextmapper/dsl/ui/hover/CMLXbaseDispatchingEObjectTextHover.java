/*
 * Copyright 2019 The Context Mapper Project Team
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
package org.contextmapper.dsl.ui.hover;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider.IInformationControlCreatorProvider;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.xbase.ui.hover.XbaseDispatchingEObjectTextHover;

import com.google.inject.Inject;

public class CMLXbaseDispatchingEObjectTextHover extends XbaseDispatchingEObjectTextHover {

	@Inject
	CMLKeywordAtOffsetHelper keywordAtOffsetHelper;

	@Inject
	IEObjectHoverProvider hoverProvider;

	@Inject
	CMLKeywordHovers keywordHovers;

	IInformationControlCreatorProvider lastCreatorProvider = null;

	@Override
	public Object getHoverInfo(EObject first, ITextViewer textViewer, IRegion hoverRegion) {
		if (first instanceof Keyword && keywordHovers.hasKeywordHoverText(((Keyword) first).getValue())) {
			lastCreatorProvider = hoverProvider.getHoverInfo(first, textViewer, hoverRegion);
			return lastCreatorProvider == null ? null : lastCreatorProvider.getInfo();
		}
		lastCreatorProvider = null;
		return super.getHoverInfo(first, textViewer, hoverRegion);
	}

	@Override
	public IInformationControlCreator getHoverControlCreator() {
		return this.lastCreatorProvider == null ? super.getHoverControlCreator()
				: lastCreatorProvider.getHoverControlCreator();
	}

	@Override
	protected Pair<EObject, IRegion> getXtextElementAt(XtextResource resource, final int offset) {
		Pair<EObject, IRegion> result = super.getXtextElementAt(resource, offset);
		if (result == null) {
			result = keywordAtOffsetHelper.resolveKeywordAt(resource, offset);
		}
		return result;
	}
}

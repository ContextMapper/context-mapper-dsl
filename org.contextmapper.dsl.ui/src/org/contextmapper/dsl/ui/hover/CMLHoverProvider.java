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

import org.contextmapper.dsl.hover.CMLHoverTextProvider;
import org.contextmapper.dsl.hover.impl.HTMLHoverTextProvider4CML;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.internal.text.html.HTMLPrinter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.hover.html.XtextBrowserInformationControlInput;

public class CMLHoverProvider extends DefaultEObjectHoverProvider {

	private CMLHoverTextProvider hoverTextProvider;

	public CMLHoverProvider() {
		super();
		this.hoverTextProvider = new HTMLHoverTextProvider4CML();
	}

	@Override
	protected XtextBrowserInformationControlInput getHoverInfo(EObject obj, IRegion region, XtextBrowserInformationControlInput prev) {
		if (obj instanceof Keyword) {
			String html = getHoverInfoAsHtml(obj);
			if (html != null && !"".equals(html)) {
				StringBuffer buffer = new StringBuffer(html);
				HTMLPrinter.insertPageProlog(buffer, 0, getStyleSheet());
				HTMLPrinter.addPageEpilog(buffer);
				return new XtextBrowserInformationControlInput(prev, obj, buffer.toString(), getLabelProvider());
			}
		}
		return super.getHoverInfo(obj, region, prev);
	}

	@Override
	protected String getHoverInfoAsHtml(EObject o) {
		if (o instanceof Keyword)
			return hoverTextProvider.getHoverText(((Keyword) o).getValue());
		return super.getHoverInfoAsHtml(o);
	}
}

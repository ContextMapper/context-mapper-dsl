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
package org.contextmapper.dsl.ide.hover;

import org.contextmapper.dsl.hover.CMLHoverTextProvider;
import org.contextmapper.dsl.hover.impl.MarkdownHoverTextProvider4CML;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EnumLiteralDeclaration;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ide.server.Document;
import org.eclipse.xtext.ide.server.hover.HoverContext;
import org.eclipse.xtext.ide.server.hover.HoverService;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

public class CMLHoverService extends HoverService {

	private CMLHoverTextProvider textProvider;

	public CMLHoverService() {
		super();
		this.textProvider = new MarkdownHoverTextProvider4CML();
	}

	@Override
	public String getContents(EObject element) {
		if (element instanceof Keyword)
			return textProvider.getHoverText(((Keyword) element).getValue());
		if(element instanceof EnumLiteralDeclaration)
			return textProvider.getHoverText(((EnumLiteralDeclaration) element).getEnumLiteral().toString());
		return "";
	}

	@Override
	protected HoverContext createContext(Document document, XtextResource resource, int offset) {
		// handle keyword case; in case cursor if over a keyword
		if (resource != null && resource.getParseResult() != null && resource.getParseResult().getRootNode() != null) {
			ILeafNode leaf = NodeModelUtils.findLeafNodeAtOffset(resource.getParseResult().getRootNode(), offset);
			if (leaf != null && leaf.getGrammarElement() != null && leaf.getGrammarElement() instanceof Keyword) {
				return new HoverContext(document, resource, offset, leaf.getTextRegion(), (Keyword) leaf.getGrammarElement());
			} else if  (leaf != null && leaf.getGrammarElement() != null && leaf.getGrammarElement() instanceof EnumLiteralDeclaration) {
				return new HoverContext(document, resource, offset, leaf.getTextRegion(), (EnumLiteralDeclaration) leaf.getGrammarElement());
			}
		}

		// default case
		return super.createContext(document, resource, offset);
	}
}

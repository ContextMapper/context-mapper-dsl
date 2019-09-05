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
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;

public class CMLKeywordAtOffsetHelper {
	public Pair resolveKeywordAt(XtextResource resource, int offset) {
		IParseResult parseResult = resource.getParseResult();
		if (parseResult != null) {
			ILeafNode leaf = NodeModelUtils.findLeafNodeAtOffset(parseResult.getRootNode(), offset);
			if (leaf != null && leaf.isHidden() && leaf.getOffset() == offset) {
				leaf = NodeModelUtils.findLeafNodeAtOffset(parseResult.getRootNode(), offset - 1);
			}
			if (leaf != null && leaf.getGrammarElement() instanceof Keyword) {
				Keyword keyword = (Keyword) leaf.getGrammarElement();
				return Tuples.create((EObject) keyword, (IRegion) new Region(leaf.getOffset(), leaf.getLength()));
			}
		}
		return null;
	}
}

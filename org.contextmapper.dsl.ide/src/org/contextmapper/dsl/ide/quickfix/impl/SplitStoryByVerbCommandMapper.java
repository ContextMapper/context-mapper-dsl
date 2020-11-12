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
package org.contextmapper.dsl.ide.quickfix.impl;

import java.util.Arrays;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.Feature;
import org.contextmapper.dsl.contextMappingDSL.UserStory;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.ide.quickfix.QuickfixCommandMapper;
import org.contextmapper.dsl.quickfixes.CMLQuickFix;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Command;

import com.google.common.collect.Lists;

public class SplitStoryByVerbCommandMapper implements QuickfixCommandMapper {

	private CMLQuickFix<? extends EObject> quickFix;

	public SplitStoryByVerbCommandMapper(CMLQuickFix<? extends EObject> quickFix) {
		this.quickFix = quickFix;
	}

	@Override
	public CodeAction getCodeAction(CMLResource cmlResource, EObject selectedObject) {
		if (!(selectedObject instanceof Feature))
			throw new ContextMapperApplicationException("Mapping exception: this quickfix was mapped to an object that is not of the type 'Feature'.");

		UserStory story = getSelectedStory((Feature) selectedObject);

		CodeAction codeAction = new CodeAction(quickFix.getName());
		codeAction.setKind(CodeActionKind.QuickFix);

		Command command = new Command(quickFix.getName(), "cml.quickfix.command.splitStoryByVerb.proxy");
		command.setArguments(Lists.newLinkedList(Arrays.asList(new String[] { cmlResource.getURI().toString(), story.getName() })));
		codeAction.setCommand(command);

		return codeAction;
	}

	private UserStory getSelectedStory(Feature feature) {
		if (!(feature.eContainer() instanceof UserStory))
			throw new ContextMapperApplicationException("Quick fix mapping exception: this quickfix can only be applied on User Stories.");
		return (UserStory) feature.eContainer();
	}

}

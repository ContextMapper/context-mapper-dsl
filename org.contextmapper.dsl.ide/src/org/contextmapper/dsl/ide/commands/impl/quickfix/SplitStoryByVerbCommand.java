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
package org.contextmapper.dsl.ide.commands.impl.quickfix;

import java.util.Set;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.UserRequirement;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.ide.edit.WorkspaceEditRecorder;
import org.contextmapper.dsl.quickfixes.CMLQuickFix;
import org.contextmapper.dsl.quickfixes.SplitStoryByVerb;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.lsp4j.ExecuteCommandParams;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class SplitStoryByVerbCommand extends AbstractQuickFixCommand {

	public SplitStoryByVerbCommand(WorkspaceEditRecorder editRecorder) {
		super(editRecorder);
	}

	@Override
	protected CMLQuickFix<? extends EObject> getQuickfix(ExecuteCommandParams params) {
		JsonArray quickFixParams = (JsonArray) params.getArguments().get(1);
		JsonArray selectedVerbs = (JsonArray) quickFixParams.get(1);

		Set<String> verbs = Sets.newHashSet();
		for (JsonElement element : selectedVerbs) {
			verbs.add(element.getAsString());
		}

		SplitStoryByVerb qf = new SplitStoryByVerb();
		qf.setVerbs(verbs);
		return qf;
	}

	@Override
	protected EObject getSubject(CMLResource cmlResource, ExecuteCommandParams params) {
		JsonArray quickFixParams = (JsonArray) params.getArguments().get(1);
		JsonPrimitive storyName = (JsonPrimitive) quickFixParams.get(0);
		UserRequirement story = cmlResource.getContextMappingModel().getUserRequirements().stream().filter(ur -> ur.getName().equals(storyName.getAsString())).findFirst().get();
		if (story.getFeatures().size() != 1)
			throw new ContextMapperApplicationException("Wrong validation implementation! This quickfix is only applicable to user stories with one and only one 'I want to' parts (features).");
		return story.getFeatures().get(0);
	}

}

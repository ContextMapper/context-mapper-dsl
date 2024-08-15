/*
 * Copyright 2024 The Context Mapper Project Team
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
package org.contextmapper.dsl.ide.actions.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.UserStory;
import org.contextmapper.dsl.ide.actions.CMLCodeAction;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.lsp4j.Command;

import com.google.common.collect.Lists;

/**
 * Action that calls the "Add Ethical Value Assessment" refactoring.
 * 
 * @author Stefan Kapferer
 *
 */
public class AddEthicalValueAssessmentAction implements CMLCodeAction {

	private CMLResource cmlResource;
	private List<EObject> editorSelection;

	public AddEthicalValueAssessmentAction(CMLResource cmlResource, List<EObject> editorSelection) {
		this.cmlResource = cmlResource;
		this.editorSelection = editorSelection;
	}

	@Override
	public boolean isApplicable() {
		Set<UserStory> userStories = getSelectedUserStories();
		return !(userStories.isEmpty() || userStories.size() > 1);
	}

	@Override
	public Command getCommand() {
		UserStory story = getSelectedUserStory();
		List<Object> commandArguments = Lists.newLinkedList();
		commandArguments.add(cmlResource.getURI().toString());
		commandArguments.add(story.getName());
		return new Command("Add Ethical Value Assessment", "cml.ar.addEthicalValueAssessment.proxy", commandArguments);
	}

	private UserStory getSelectedUserStory() {
		return getSelectedUserStories().iterator().next();
	}

	private Set<UserStory> getSelectedUserStories() {
		return editorSelection.stream().filter(o -> o instanceof UserStory).map(o -> (UserStory) o)
				.collect(Collectors.toSet());
	}

}

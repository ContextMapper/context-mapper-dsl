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
package org.contextmapper.dsl.refactoring;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.StoryValuation;
import org.contextmapper.dsl.contextMappingDSL.UserStory;
import org.eclipse.xtext.EcoreUtil2;

public class AddEthicalValueAssessmentToStory extends AbstractRefactoring implements SemanticCMLRefactoring {

	private String userStoryName;

	public AddEthicalValueAssessmentToStory(String userStoryName) {
		this.userStoryName = userStoryName;
	}

	@Override
	protected void doRefactor() {
		UserStory story = getSelectedUserStory();

		StoryValuation valuation = ContextMappingDSLFactory.eINSTANCE.createStoryValuation();
		valuation.getHarmedValues().add("tbd_harmed");
		valuation.getPromotedValues().add("tbd_promoted");

		story.setValuation(valuation);
	}

	private UserStory getSelectedUserStory() {
		return EcoreUtil2.<UserStory>getAllContentsOfType(model, UserStory.class).stream()
				.filter(s -> s.getName().equals(userStoryName)).findFirst().get();
	}

}

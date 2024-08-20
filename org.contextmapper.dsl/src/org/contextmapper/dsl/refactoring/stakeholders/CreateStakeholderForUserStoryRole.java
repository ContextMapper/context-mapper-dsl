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
package org.contextmapper.dsl.refactoring.stakeholders;

import java.util.List;

import org.contextmapper.dsl.cml.XtextIdHelper;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.INFLUENCE;
import org.contextmapper.dsl.contextMappingDSL.INTEREST;
import org.contextmapper.dsl.contextMappingDSL.Stakeholder;
import org.contextmapper.dsl.contextMappingDSL.Stakeholders;
import org.contextmapper.dsl.contextMappingDSL.UserStory;
import org.contextmapper.dsl.refactoring.AbstractRefactoring;
import org.contextmapper.dsl.refactoring.SemanticCMLRefactoring;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

public class CreateStakeholderForUserStoryRole extends AbstractRefactoring implements SemanticCMLRefactoring {

	private String userStoryName;
	private XtextIdHelper idHelper;

	public CreateStakeholderForUserStoryRole(String userStoryName) {
		this.userStoryName = userStoryName;
		this.idHelper = new XtextIdHelper();
	}

	protected void doRefactor() {
		UserStory story = this.getSelectedUserStory();
		if (!stakeholderForRoleAlreadyExists(story)) {
			EObject parentObject = story.eContainer();
			if (parentObject instanceof ContextMappingModel) {
				ContextMappingModel modelRoot = (ContextMappingModel) story.eContainer();
				Stakeholders stakeholders = createStakeholderForStoryRole(story);
				modelRoot.getStakeholders().add(stakeholders);
			}
		}
	}

	private Stakeholders createStakeholderForStoryRole(UserStory story) {
		Stakeholder stakeholderForRole = ContextMappingDSLFactory.eINSTANCE.createStakeholder();
		stakeholderForRole.setName(idHelper.convertStringToXtextID(story.getRole()));
		stakeholderForRole.setDescription("Role of/in " + userStoryName);
		stakeholderForRole.setInfluence(INFLUENCE.MEDIUM);
		stakeholderForRole.setInterest(INTEREST.MEDIUM);
		Stakeholders stakeholders = ContextMappingDSLFactory.eINSTANCE.createStakeholders();
		stakeholders.getStakeholders().add(stakeholderForRole);
		return stakeholders;
	}

	private UserStory getSelectedUserStory() {
		return EcoreUtil2.<UserStory>getAllContentsOfType(model, UserStory.class).stream()
				.filter(s -> s.getName().equals(userStoryName)).findFirst().get();
	}

	public boolean stakeholderForRoleAlreadyExists(UserStory story) {
		ContextMappingModel model = (ContextMappingModel) story.eContainer();
		List<Stakeholder> allStakeholders = EcoreUtil2.<Stakeholder>getAllContentsOfType(model, Stakeholder.class);
		for (Stakeholder stakeholder : allStakeholders) {
			if (stakeholder.getName().equals(idHelper.convertStringToXtextID(story.getRole()))) {
				return true;
			}
		}
		return false;
	}
}

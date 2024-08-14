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

import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.Stakeholder;
import org.contextmapper.dsl.contextMappingDSL.StakeholderGroup;
import org.contextmapper.dsl.contextMappingDSL.Stakeholders;
import org.contextmapper.dsl.refactoring.AbstractRefactoring;
import org.contextmapper.dsl.refactoring.SemanticCMLRefactoring;
import org.eclipse.xtext.EcoreUtil2;

public class MoveStakeholderToNewStakeholderGroupRefactoring extends AbstractRefactoring
		implements SemanticCMLRefactoring {

	private String stakeholderName;

	public MoveStakeholderToNewStakeholderGroupRefactoring(String stakeholderName) {
		this.stakeholderName = stakeholderName;
	}

	@Override
	protected void doRefactor() {
		Stakeholder s = getSelectedStakeholder();
		Stakeholders container = getContainerAndRemoveStakeholder(s);

		if (container != null) {
			StakeholderGroup newGroup = ContextMappingDSLFactory.eINSTANCE.createStakeholderGroup();
			newGroup.setName("Groupname_To_Be_Changed");
			container.getStakeholders().add(newGroup);
			newGroup.getStakeholders().add(s);
		}
	}

	private Stakeholder getSelectedStakeholder() {
		return EcoreUtil2.<Stakeholder>getAllContentsOfType(model, Stakeholder.class).stream()
				.filter(s -> s.getName().equals(stakeholderName)).findFirst().get();
	}

	private Stakeholders getContainerAndRemoveStakeholder(final Stakeholder s) {
		if (s.eContainer() instanceof Stakeholders) {
			Stakeholders container = (Stakeholders) s.eContainer();
			container.getStakeholders().remove(s);
			return container;
		} else if (s.eContainer() instanceof StakeholderGroup) {
			StakeholderGroup group = (StakeholderGroup) s.eContainer();
			group.getStakeholders().remove(s);
			if (group.eContainer() instanceof Stakeholders) {
				return (Stakeholders) group.eContainer();
			}
		}
		return null;
	}

}

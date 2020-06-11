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
package org.contextmapper.dsl.validation;

import static org.contextmapper.dsl.validation.ValidationMessages.ENTITY_NAME_CONTAINS_INVALID_CHARACTERS;
import static org.contextmapper.dsl.validation.ValidationMessages.STRING_IS_NOT_NANOENTITY;
import static org.contextmapper.dsl.validation.ValidationMessages.VERB_CONTAINS_INVALID_CHARACTERS;

import java.util.List;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage;
import org.contextmapper.dsl.contextMappingDSL.Feature;
import org.contextmapper.dsl.contextMappingDSL.UserRequirement;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

public class UserRequirementsValidator extends AbstractDeclarativeValidator {

	private static final String NANOENTITY_FORMAT_REGEX = "[A-Z]{1}[a-zA-Z0-9_]*\\.[a-z]{1}[a-zA-Z0-9_]*";

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}

	@Check
	public void validateFeature(final Feature feature) {
		if (feature.getEntity() == null)
			return;

		if (!feature.getEntity().matches(AbstractCMLValidator.ID_VALIDATION_PATTERN))
			warning(ENTITY_NAME_CONTAINS_INVALID_CHARACTERS, feature, ContextMappingDSLPackage.Literals.FEATURE__ENTITY);

		if (feature.getVerb() == null)
			return;

		if (!feature.getVerb().matches(AbstractCMLValidator.ID_VALIDATION_PATTERN))
			warning(VERB_CONTAINS_INVALID_CHARACTERS, feature, ContextMappingDSLPackage.Literals.FEATURE__VERB);
	}

	@Check
	public void validateNanoentityStrings(UserRequirement userRequirement) {
		if (!userRequirement.getNanoentitiesRead().isEmpty())
			validateNanoentityNamesList(userRequirement, userRequirement.getNanoentitiesRead(), ContextMappingDSLPackage.Literals.USER_REQUIREMENT__NANOENTITIES_READ);
		if (!userRequirement.getNanoentitiesWritten().isEmpty())
			validateNanoentityNamesList(userRequirement, userRequirement.getNanoentitiesWritten(), ContextMappingDSLPackage.Literals.USER_REQUIREMENT__NANOENTITIES_WRITTEN);
	}

	private void validateNanoentityNamesList(UserRequirement userRequirement, List<String> nanoEntities, EAttribute attribute) {
		for (int i = 0; i < nanoEntities.size(); i++) {
			if (!nanoEntities.get(i).matches(NANOENTITY_FORMAT_REGEX))
				error(String.format(STRING_IS_NOT_NANOENTITY, nanoEntities.get(i)), userRequirement, attribute, i);
		}
	}

}

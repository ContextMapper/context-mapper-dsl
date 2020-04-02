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
import static org.contextmapper.dsl.validation.ValidationMessages.VERB_CONTAINS_INVALID_CHARACTERS;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage;
import org.contextmapper.dsl.contextMappingDSL.Feature;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

public class UserRequirementsValidator extends AbstractDeclarativeValidator {

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}

	@Check
	public void validateFeature(final Feature feature) {
		if (feature.getEntity() == null)
			return;

		if (!feature.getEntity().matches("^[a-zA-Z_][a-zA-Z0-9_]*"))
			error(ENTITY_NAME_CONTAINS_INVALID_CHARACTERS, feature, ContextMappingDSLPackage.Literals.FEATURE__ENTITY);
		
		if(feature.getVerb() == null)
			return;
		
		if (!feature.getVerb().matches("^[a-zA-Z_][a-zA-Z0-9_]*"))
			error(VERB_CONTAINS_INVALID_CHARACTERS, feature, ContextMappingDSLPackage.Literals.FEATURE__VERB);
	}

}

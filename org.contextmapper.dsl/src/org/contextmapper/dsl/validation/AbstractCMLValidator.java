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

import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;

public class AbstractCMLValidator extends AbstractDeclarativeValidator {

	protected ContextMappingModel getRootCMLModel(EObject modelElement) {
		return (ContextMappingModel) EcoreUtil.getRootContainer(modelElement);
	}

}

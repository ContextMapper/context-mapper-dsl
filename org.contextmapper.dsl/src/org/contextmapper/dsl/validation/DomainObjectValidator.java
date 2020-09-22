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

import static org.contextmapper.dsl.validation.ValidationMessages.DOMAIN_OBJECT_NAME_ALREADY_EXISTS;
import static org.contextmapper.dsl.validation.ValidationMessages.PRIMITIVE_ID_TYPE;
import static org.contextmapper.dsl.validation.ValidationMessages.REFERENCE_IS_AMBIGUOUS;

import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLTypeChecker;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.CollectionType;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

public class DomainObjectValidator extends AbstractDeclarativeValidator {

	public static final String ID_IS_PRIMITIVE_CODE = "primitive-id-detected";

	private CMLTypeChecker typeChecker;

	public DomainObjectValidator() {
		this.typeChecker = new CMLTypeChecker();
	}

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}

	@Check
	public void suggestValueObject4ID(final Attribute attribute) {
		if (attribute.getName() == null)
			return;
		if (!(attribute.getName().endsWith("Id") || attribute.getName().endsWith("ID")))
			return;
		// only for entities
		if (!(attribute.eContainer() instanceof Entity))
			return;
		// but not in subdomains
		if (attribute.eContainer().eContainer() instanceof Subdomain)
			return;

		if (attribute.getCollectionType() == CollectionType.NONE && typeChecker.isPrimitiveType(attribute.getType()))
			info(PRIMITIVE_ID_TYPE, attribute, TacticdslPackage.Literals.ANY_PROPERTY__NAME, ID_IS_PRIMITIVE_CODE);
	}

	@Check
	public void checkForAmbiguousReferences(Reference reference) {
		if (reference.getDomainObjectType() == null)
			return;
		List<SimpleDomainObject> referencedObjects = EcoreUtil2.eAllOfType(EcoreUtil2.getRootContainer(reference), SimpleDomainObject.class).stream()
				.filter(o -> o.getName().equals(reference.getDomainObjectType().getName())).collect(Collectors.toList());
		if (referencedObjects.size() > 1)
			warning(String.format(REFERENCE_IS_AMBIGUOUS, reference.getDomainObjectType().getName()), reference, TacticdslPackage.Literals.REFERENCE__DOMAIN_OBJECT_TYPE);
	}

	@Check
	public void checkForDuplicateDomainNames(SimpleDomainObject simpleDomainObject) {
		List<SimpleDomainObject> objects = EcoreUtil2.eAllOfType(EcoreUtil2.getRootContainer(simpleDomainObject), SimpleDomainObject.class).stream()
				.filter(o -> o.getName().equals(simpleDomainObject.getName())).collect(Collectors.toList());
		if (objects.size() > 1)
			warning(String.format(DOMAIN_OBJECT_NAME_ALREADY_EXISTS, simpleDomainObject.getName()), simpleDomainObject, TacticdslPackage.Literals.SIMPLE_DOMAIN_OBJECT__NAME);
	}

}

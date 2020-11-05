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

import static org.contextmapper.dsl.validation.ValidationMessages.SUBDOMAIN_NAME_EQUALS_DOMAIN_NAME;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

public class DomainValidator extends AbstractDeclarativeValidator {

	public DomainValidator() {
	}

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}

	@Check
	public void subdomainNameCheck(final Subdomain subdomain) {
		if (subdomain.eContainer() instanceof Domain) {
			Domain domain = (Domain) subdomain.eContainer();
			if (domain.getName().equals(subdomain.getName()))
				error(String.format(SUBDOMAIN_NAME_EQUALS_DOMAIN_NAME, subdomain.getName()), subdomain, ContextMappingDSLPackage.Literals.DOMAIN_PART__NAME);
		}
	}

}

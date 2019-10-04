/*
 * Copyright 2019 The Context Mapper Project Team
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

import static org.contextmapper.dsl.validation.ValidationMessages.ALREADY_IMPLEMENTED_SUBDOMAIN;
import static org.contextmapper.dsl.validation.ValidationMessages.MULTIPLE_DOMAINS_IMPLEMENTED;

import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

public class BoundedContextSemanticsValidator extends AbstractDeclarativeValidator {

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}

	@Check
	public void validateThatAggregateContainsOnlyOneAggregateRoot(final BoundedContext boundedContext) {
		List<Domain> domains = boundedContext.getImplementedDomainParts().stream().filter(domainPart -> domainPart instanceof Domain).map(domainPart -> (Domain) domainPart)
				.collect(Collectors.toList());

		if (domains.isEmpty())
			return;

		boundedContext.getImplementedDomainParts().stream().filter(domainPart -> domainPart instanceof Subdomain).map(domainPart -> (Subdomain) domainPart).forEach(subdomain -> {
			Domain parentDomain = (Domain) subdomain.eContainer();
			if (domains.contains(parentDomain))
				error(String.format(ALREADY_IMPLEMENTED_SUBDOMAIN, subdomain.getName(), parentDomain.getName()), boundedContext,
						ContextMappingDSLPackage.Literals.BOUNDED_CONTEXT__IMPLEMENTED_DOMAIN_PARTS, boundedContext.getImplementedDomainParts().indexOf(subdomain));
		});
	}

	@Check
	public void warnUserIfABoundedContextImplementsMultipleDomains(final BoundedContext boundedContext) {
		List<Domain> domains = boundedContext.getImplementedDomainParts().stream().filter(domainPart -> domainPart instanceof Domain).map(domainPart -> (Domain) domainPart)
				.collect(Collectors.toList());
		if (domains.size() > 1) {
			for (Domain domain : domains) {
				warning(MULTIPLE_DOMAINS_IMPLEMENTED, boundedContext, ContextMappingDSLPackage.Literals.BOUNDED_CONTEXT__IMPLEMENTED_DOMAIN_PARTS,
						boundedContext.getImplementedDomainParts().indexOf(domain));
			}
		}
	}

}

/*
 * Copyright 2018 The Context Mapper Project Team
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

import static org.contextmapper.dsl.validation.ValidationMessages.AGGREGATE_NAME_NOT_UNIQUE;
import static org.contextmapper.dsl.validation.ValidationMessages.BOUNDED_CONTEXT_NAME_NOT_UNIQUE;
import static org.contextmapper.dsl.validation.ValidationMessages.DOMAIN_OBJECT_NOT_UNIQUE;
import static org.contextmapper.dsl.validation.ValidationMessages.MODULE_NAME_NOT_UNIQUE;
import static org.contextmapper.dsl.validation.ValidationMessages.SERVICE_NAME_NOT_UNIQUE_IN_BC;
import static org.contextmapper.dsl.validation.ValidationMessages.SERVICE_NAME_NOT_UNIQUE_IN_SUBDOMAIN;
import static org.contextmapper.dsl.validation.ValidationMessages.SUBDOMAIN_OBJECT_NOT_UNIQUE;
import static org.contextmapper.dsl.validation.ValidationMessages.USE_CASE_NAME_NOT_UNIQUE;
import static org.contextmapper.dsl.validation.ValidationMessages.DOMAIN_NOT_UNIQUE;
import static org.contextmapper.dsl.validation.ValidationMessages.FLOW_NAME_NOT_UNIQUE;
import static org.contextmapper.dsl.validation.ValidationMessages.FUNCTIONALITY_NAME_NOT_UNIQUE;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLModelObjectsResolvingHelper;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.Flow;
import org.contextmapper.dsl.contextMappingDSL.Functionality;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.contextMappingDSL.UserRequirement;
import org.contextmapper.tactic.dsl.tacticdsl.Service;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import com.google.common.collect.Sets;

public class UniquenessValidator extends AbstractCMLValidator {

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}

	@Check
	public void validateThatBoundedContextNameIsUnique(final BoundedContext bc) {
		if (bc != null) {
			Iterator<BoundedContext> allBoundedContexts = new CMLModelObjectsResolvingHelper(getRootCMLModel(bc)).resolveAllObjectsOfType(BoundedContext.class).iterator();
			Iterator<BoundedContext> duplicateBoundedContexts = IteratorExtensions.filter(allBoundedContexts,
					((Function1<BoundedContext, Boolean>) (BoundedContext boundedcontext) -> {
						return boundedcontext.getName().equals(bc.getName());
					}));
			if (IteratorExtensions.size(duplicateBoundedContexts) > 1)
				error(String.format(BOUNDED_CONTEXT_NAME_NOT_UNIQUE, bc.getName()), bc, ContextMappingDSLPackage.Literals.BOUNDED_CONTEXT__NAME);
		}
	}

	@Check
	public void validateThatDomainNameIsUnique(final Domain domain) {
		if (domain != null) {
			Iterator<Domain> allDomains = new CMLModelObjectsResolvingHelper(getRootCMLModel(domain)).resolveAllObjectsOfType(Domain.class).iterator();
			Iterator<Domain> duplicateDomains = IteratorExtensions.filter(allDomains, ((Function1<Domain, Boolean>) (Domain d) -> {
				return domain.getName().equals(d.getName());
			}));
			if (IteratorExtensions.size(duplicateDomains) > 1)
				error(String.format(DOMAIN_NOT_UNIQUE, domain.getName()), domain, ContextMappingDSLPackage.Literals.DOMAIN_PART__NAME);
		}
	}

	@Check
	public void validateThatSubdomainNameIsUnique(final Subdomain subdomain) {
		if (subdomain != null) {
			Iterator<Subdomain> allSubdomains = new CMLModelObjectsResolvingHelper(getRootCMLModel(subdomain)).resolveAllObjectsOfType(Subdomain.class).iterator();
			Iterator<Subdomain> duplicateSubdomains = IteratorExtensions.filter(allSubdomains, ((Function1<Subdomain, Boolean>) (Subdomain sd) -> {
				return subdomain.getName().equals(sd.getName());
			}));
			if (IteratorExtensions.size(duplicateSubdomains) > 1)
				error(String.format(SUBDOMAIN_OBJECT_NOT_UNIQUE, subdomain.getName()), subdomain, ContextMappingDSLPackage.Literals.DOMAIN_PART__NAME);
		}
	}

	@Check
	public void validateThatModuleNameIsUnique(final SculptorModule module) {
		if (module != null) {
			Iterator<SculptorModule> allModules = new CMLModelObjectsResolvingHelper(getRootCMLModel(module)).resolveAllObjectsOfType(SculptorModule.class).iterator();
			Iterator<SculptorModule> duplicateModules = IteratorExtensions.filter(allModules, ((Function1<SculptorModule, Boolean>) (SculptorModule m) -> {
				return m.getName().equals(module.getName());
			}));
			if (IteratorExtensions.size(duplicateModules) > 1)
				error(String.format(MODULE_NAME_NOT_UNIQUE, module.getName()), module, ContextMappingDSLPackage.Literals.SCULPTOR_MODULE__NAME);
		}
	}

	@Check
	public void validateThatAggregateNameIsUnique(final Aggregate aggregate) {
		if (aggregate != null) {
			Iterator<Aggregate> allAggregates = new CMLModelObjectsResolvingHelper(getRootCMLModel(aggregate)).resolveAllObjectsOfType(Aggregate.class).iterator();
			Iterator<Aggregate> duplicateAggregates = IteratorExtensions.filter(allAggregates, ((Function1<Aggregate, Boolean>) (Aggregate a) -> {
				return a.getName().equals(aggregate.getName());
			}));
			if (IteratorExtensions.size(duplicateAggregates) > 1)
				error(String.format(AGGREGATE_NAME_NOT_UNIQUE, aggregate.getName()), aggregate, ContextMappingDSLPackage.Literals.AGGREGATE__NAME);
		}
	}

	@Check
	public void validateThatServiceNamesAreUniqueInBoundedContext(BoundedContext bc) {
		Set<String> serviceNames = Sets.newHashSet();
		Iterator<Service> allServices = Sets.newHashSet(IteratorExtensions.filter(EcoreUtil2.eAll(bc), Service.class)).iterator();
		while (allServices.hasNext()) {
			Service service = allServices.next();
			if (serviceNames.contains(service.getName())) {
				error(String.format(SERVICE_NAME_NOT_UNIQUE_IN_BC, service.getName()), service, TacticdslPackage.Literals.SERVICE_REPOSITORY_OPTION__NAME);
			} else {
				serviceNames.add(service.getName());
			}
		}
	}

	@Check
	public void validateThatServiceNamesAreUniqueInSubdomain(Subdomain subdomain) {
		Set<String> serviceNames = Sets.newHashSet();
		Iterator<Service> allServices = Sets.newHashSet(IteratorExtensions.filter(EcoreUtil2.eAll(subdomain), Service.class)).iterator();
		while (allServices.hasNext()) {
			Service service = allServices.next();
			if (serviceNames.contains(service.getName())) {
				error(String.format(SERVICE_NAME_NOT_UNIQUE_IN_SUBDOMAIN, service.getName()), service, TacticdslPackage.Literals.SERVICE_REPOSITORY_OPTION__NAME);
			} else {
				serviceNames.add(service.getName());
			}
		}
	}

	@Check
	public void validateThatUseCaseNameIsUnique(final UserRequirement uc) {
		if (uc != null) {
			Iterator<UserRequirement> allUseCases = new CMLModelObjectsResolvingHelper(getRootCMLModel(uc)).resolveAllObjectsOfType(UserRequirement.class).iterator();
			Iterator<UserRequirement> duplicateUseCases = IteratorExtensions.filter(allUseCases, ((Function1<UserRequirement, Boolean>) (UserRequirement u) -> {
				return u.getName().equals(uc.getName());
			}));
			if (IteratorExtensions.size(duplicateUseCases) > 1)
				error(String.format(USE_CASE_NAME_NOT_UNIQUE, uc.getName()), uc, ContextMappingDSLPackage.Literals.USER_REQUIREMENT__NAME);
		}
	}

	@Check
	public void validateThatDomainObjectsAreUniqueInAggregate(final Aggregate agg) {
		if (agg == null)
			return;
		checkDomainObjectUnique(agg.getDomainObjects());
	}

	@Check
	public void validateThatDomainObjectsAreUniqueInModule(final SculptorModule module) {
		if (module == null)
			return;
		checkDomainObjectUnique(module.getDomainObjects());
	}

	@Check
	public void validateThatDomainObjectsAreUniqueInSubdomain(final Subdomain subdomain) {
		if (subdomain == null)
			return;
		checkDomainObjectUnique(subdomain.getEntities().stream().map(e -> (SimpleDomainObject) e).collect(Collectors.toList()));
	}

	@Check
	public void validateThatFlowNameIsUnique(final Flow flow) {
		if (flow != null) {
			Iterator<Flow> allFlows = EcoreUtil2.eAllOfType(getRootCMLModel(flow), Flow.class).iterator();
			Iterator<Flow> duplicateFlows = IteratorExtensions.filter(allFlows, ((Function1<Flow, Boolean>) (Flow f) -> {
				return f.getName().equals(flow.getName());
			}));
			if (IteratorExtensions.size(duplicateFlows) > 1)
				error(String.format(FLOW_NAME_NOT_UNIQUE, flow.getName()), flow, ContextMappingDSLPackage.Literals.FLOW__NAME);
		}
	}
	
	@Check
	public void validateThatFunctionalityNameIsUnique(final Functionality functionality) {
		if (functionality != null) {
			Iterator<Functionality> allFunctionalities = EcoreUtil2.eAllOfType(getRootCMLModel(functionality), Functionality.class).iterator();
			Iterator<Functionality> duplicateFunctionalities = IteratorExtensions.filter(allFunctionalities, ((Function1<Functionality, Boolean>) (Functionality f) -> {
				return f.getName().equals(functionality.getName());
			}));
			if (IteratorExtensions.size(duplicateFunctionalities) > 1)
				error(String.format(FUNCTIONALITY_NAME_NOT_UNIQUE, functionality.getName()), functionality, ContextMappingDSLPackage.Literals.FUNCTIONALITY__NAME);
		}
	}
	
	private void checkDomainObjectUnique(List<SimpleDomainObject> domainObjects) {
		Set<String> usedNames = new HashSet<>();
		for (SimpleDomainObject o : domainObjects) {
			if (o.getName() == null)
				continue;

			if (usedNames.contains(o.getName())) {
				error(String.format(DOMAIN_OBJECT_NOT_UNIQUE, o.getName()), o, TacticdslPackage.Literals.SIMPLE_DOMAIN_OBJECT__NAME);
			} else {
				usedNames.add(o.getName());
			}
		}
	}

}

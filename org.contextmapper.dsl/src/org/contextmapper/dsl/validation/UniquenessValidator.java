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
import static org.contextmapper.dsl.validation.ValidationMessages.USE_CASE_NAME_NOT_UNIQUE;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLModelObjectsResolvingHelper;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.contextMappingDSL.UseCase;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

public class UniquenessValidator extends AbstractCMLValidator {

	private CMLModelObjectsResolvingHelper resolvingHelper = new CMLModelObjectsResolvingHelper();
	
	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}

	@Check
	public void validateThatBoundedContextNameIsUnique(final BoundedContext bc) {
		if (bc != null) {
			Iterator<BoundedContext> allBoundedContexts = resolvingHelper.resolveAllObjectsOfType(getRootCMLModel(bc), BoundedContext.class).iterator();
			Iterator<BoundedContext> duplicateBoundedContexts = IteratorExtensions.filter(allBoundedContexts,
					((Function1<BoundedContext, Boolean>) (BoundedContext boundedcontext) -> {
						return boundedcontext.getName().equals(bc.getName());
					}));
			if (IteratorExtensions.size(duplicateBoundedContexts) > 1)
				error(String.format(BOUNDED_CONTEXT_NAME_NOT_UNIQUE, bc.getName()), bc, ContextMappingDSLPackage.Literals.BOUNDED_CONTEXT__NAME);
		}
	}

	@Check
	public void validateThatModuleNameIsUnique(final SculptorModule module) {
		if (module != null) {
			Iterator<SculptorModule> allModules = resolvingHelper.resolveAllObjectsOfType(getRootCMLModel(module), SculptorModule.class).iterator();
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
			Iterator<Aggregate> allAggregates = resolvingHelper.resolveAllObjectsOfType(getRootCMLModel(aggregate), Aggregate.class).iterator();
			Iterator<Aggregate> duplicateAggregates = IteratorExtensions.filter(allAggregates, ((Function1<Aggregate, Boolean>) (Aggregate a) -> {
				return a.getName().equals(aggregate.getName());
			}));
			if (IteratorExtensions.size(duplicateAggregates) > 1)
				error(String.format(AGGREGATE_NAME_NOT_UNIQUE, aggregate.getName()), aggregate, ContextMappingDSLPackage.Literals.AGGREGATE__NAME);
		}
	}

	@Check
	public void validateThatUseCaseNameIsUnique(final UseCase uc) {
		if (uc != null) {
			Iterator<UseCase> allUseCases = resolvingHelper.resolveAllObjectsOfType(getRootCMLModel(uc), UseCase.class).iterator();
			Iterator<UseCase> duplicateUseCases = IteratorExtensions.filter(allUseCases, ((Function1<UseCase, Boolean>) (UseCase u) -> {
				return u.getName().equals(uc.getName());
			}));
			if (IteratorExtensions.size(duplicateUseCases) > 1)
				error(String.format(USE_CASE_NAME_NOT_UNIQUE, uc.getName()), uc, ContextMappingDSLPackage.Literals.USE_CASE__NAME);
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

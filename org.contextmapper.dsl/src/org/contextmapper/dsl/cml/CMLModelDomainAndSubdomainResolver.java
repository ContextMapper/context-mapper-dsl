/*
 * Copyright 2023 The Context Mapper Project Team
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
package org.contextmapper.dsl.cml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;

public class CMLModelDomainAndSubdomainResolver {

	private final ContextMappingModel model;
	private final Map<String, Subdomain> declaredSubdomains;
	private final Map<String, Domain> domainsBySubdomainNames;

	public CMLModelDomainAndSubdomainResolver(final ContextMappingModel model) {
		this.model = model;
		this.declaredSubdomains = new HashMap<>();
		this.domainsBySubdomainNames = new HashMap<>();
		initAllDeclaredSubdomainsIncludingImportedOnes();
	}

	/**
	 * 
	 * Resolves all subdomains that are:
	 * 
	 * 1) declared in the "root" CML file/model
	 * 
	 * 2) subdomains implemented by Bounded Contexts of the root model, but declared
	 * in an imported CML file/model
	 * 
	 */
	public Set<Subdomain> resolveAllSubdomains() {
		Map<String, Subdomain> subdomains4Model = new HashMap<>();
		for (Domain domain : model.getDomains()) {
			domain.getSubdomains().forEach(subdomain -> {
				subdomains4Model.put(subdomain.getName(), this.declaredSubdomains.get(subdomain.getName()));
			});
		}
		for (BoundedContext boundedContext : model.getBoundedContexts()) {
			boundedContext.getImplementedDomainParts().forEach(domainPart -> {
				if (domainPart instanceof Domain) {
					Domain domain = (Domain) domainPart;
					domain.getSubdomains().forEach(subdomain -> {
						subdomains4Model.put(subdomain.getName(), this.declaredSubdomains.get(subdomain.getName()));
					});
				} else if (domainPart instanceof Subdomain) {
					Subdomain subdomain = (Subdomain) domainPart;
					subdomains4Model.put(subdomain.getName(), this.declaredSubdomains.get(subdomain.getName()));
				}
			});
		}
		return new HashSet<>(subdomains4Model.values());
	}

	/**
	 * 
	 * Takes a name of the subdomain and returns the domain to which it belongs.
	 * 
	 */
	public Domain resolveDomain4Subdomain(final String subdomainName) {
		return this.domainsBySubdomainNames.get(subdomainName);
	}

	private void initAllDeclaredSubdomainsIncludingImportedOnes() {
		addDeclaredSubdomains(this.model);
		if (this.model.eResource() != null) {
			for (CMLResource cmlResource : new CMLImportResolver()
					.resolveImportedResources(new CMLResource(this.model.eResource()))) {
				addDeclaredSubdomains(cmlResource.getContextMappingModel());
			}
		}
	}

	private void addDeclaredSubdomains(final ContextMappingModel model) {
		for (Domain domain : model.getDomains()) {
			domain.getSubdomains().forEach(subdomain -> {
				declaredSubdomains.put(subdomain.getName(), subdomain);
				domainsBySubdomainNames.put(subdomain.getName(), domain);
			});
		}
	}

}

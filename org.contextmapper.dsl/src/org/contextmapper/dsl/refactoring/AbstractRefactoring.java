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
package org.contextmapper.dsl.refactoring;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.contextmapper.dsl.cml.CMLImportResolver;
import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.Import;
import org.contextmapper.dsl.contextMappingDSL.UserRequirement;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.SaveOptions;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public abstract class AbstractRefactoring implements Refactoring {

	protected ContextMappingModel model;
	protected CMLResourceContainer rootResource;
	protected Set<CMLResourceContainer> importedResources;
	private Map<BoundedContext, CMLResourceContainer> boundedContextsMap = Maps.newHashMap();
	private Map<ContextMap, CMLResourceContainer> contextMapMap = Maps.newHashMap();
	private Map<Domain, CMLResourceContainer> domainMap = Maps.newHashMap();
	private Map<UserRequirement, CMLResourceContainer> userRequirementMap = Maps.newHashMap();
	private List<CMLResourceContainer> changedResources = Lists.newArrayList();

	protected ResourceSet consistencyCheckResources;
	protected Set<CMLResourceContainer> additionalResourcesToCheck = Sets.newHashSet();

	@Override
	public void doRefactor(CMLResourceContainer resource) {
		this.rootResource = resource;
		this.importedResources = new CMLImportResolver().resolveImportedResources(rootResource);
		this.model = resource.getContextMappingModel();
		resolveRootElements();
		doRefactor();
	}

	@Override
	public void doRefactor(CMLResourceContainer resource, ResourceSet consistencyCheckResources) {
		this.consistencyCheckResources = consistencyCheckResources;
		for (Resource resourceToCheck : consistencyCheckResources.getResources()) {
			if (resourceToCheck.getContents().isEmpty() || !(resourceToCheck.getContents().get(0) instanceof ContextMappingModel))
				continue;
			this.additionalResourcesToCheck.add(new CMLResourceContainer(resourceToCheck));
		}
		doRefactor(resource);
	}

	protected abstract void doRefactor();

	protected Set<BoundedContext> getAllBoundedContexts() {
		return Sets.newHashSet(this.boundedContextsMap.keySet());
	}

	protected Set<ContextMap> getAllContextMaps() {
		return Sets.newHashSet(this.contextMapMap.keySet());
	}

	protected Set<Domain> getAllDomains() {
		return Sets.newHashSet(this.domainMap.keySet());
	}

	protected Set<UserRequirement> getAllUserRequirements() {
		return Sets.newHashSet(this.userRequirementMap.keySet());
	}

	protected void saveResource(Resource resource) {
		try {
			resource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
		} catch (IOException e) {
			throw new RuntimeException("Document cannot be formatted.");
		}
	}

	protected void saveResources() {
		rootResource.getContextMappingModel().eAllContents();
		saveResource(rootResource.getResource());
		for (CMLResourceContainer changedResource : changedResources) {
			changedResource.getContextMappingModel().eAllContents();
			saveResource(changedResource.getResource());
		}
	}

	protected void markResourceChanged(BoundedContext changedBoundedContext) {
		CMLResourceContainer resource = getResource(changedBoundedContext);
		markResourceChanged(resource);
	}

	protected void markResourceChanged(ContextMap changedContextMap) {
		markResourceChanged(getResource(changedContextMap));
	}

	protected void markResourceChanged(Domain domain) {
		markResourceChanged(getResource(domain));
	}

	protected void markResourceChanged(UserRequirement userRequirement) {
		markResourceChanged(getResource(userRequirement));
	}

	protected void markResourceChanged(CMLResourceContainer resource) {
		this.changedResources.add(resource);
	}

	protected CMLResourceContainer getResource(BoundedContext bc) {
		CMLResourceContainer result = this.boundedContextsMap.get(bc);
		return result;
	}

	protected CMLResourceContainer getResource(ContextMap contextMap) {
		return this.contextMapMap.get(contextMap);
	}

	protected CMLResourceContainer getResource(Domain domain) {
		if (!this.domainMap.containsKey(domain))
			this.domainMap.put(domain, rootResource);
		return this.domainMap.get(domain);
	}

	protected CMLResourceContainer getResource(UserRequirement userRequirement) {
		return this.userRequirementMap.get(userRequirement);
	}

	private void resolveRootElements() {
		resolveAllRootElements(rootResource);
		for (CMLResourceContainer importedResource : importedResources) {
			resolveAllRootElements(importedResource);
		}
	}

	private void resolveAllRootElements(CMLResourceContainer importedResource) {
		resolveBoundedContexts(importedResource);
		resolveContextMaps(importedResource);
		resolveDomains(importedResource);
		resolveUserRequirements(importedResource);
	}

	private void resolveBoundedContexts(CMLResourceContainer resource) {
		for (BoundedContext bc : resource.getContextMappingModel().getBoundedContexts()) {
			this.boundedContextsMap.put(bc, resource);
		}
	}

	private void resolveContextMaps(CMLResourceContainer resource) {
		if (resource.getContextMappingModel().getMap() != null)
			this.contextMapMap.put(resource.getContextMappingModel().getMap(), resource);
		for (CMLResourceContainer extResourceToCheck : this.additionalResourcesToCheck) {
			if (extResourceToCheck.getContextMappingModel().getMap() != null && containsImport(extResourceToCheck, resource))
				this.contextMapMap.put(extResourceToCheck.getContextMappingModel().getMap(), extResourceToCheck);
		}
	}

	private void resolveDomains(CMLResourceContainer resource) {
		for (Domain domain : resource.getContextMappingModel().getDomains()) {
			this.domainMap.put(domain, resource);
		}
	}

	private void resolveUserRequirements(CMLResourceContainer resource) {
		for (UserRequirement userRequirement : resource.getContextMappingModel().getUserRequirements()) {
			this.userRequirementMap.put(userRequirement, resource);
		}
	}

	/**
	 * Checks whether source contains an import statement to target.
	 */
	private boolean containsImport(CMLResourceContainer source, CMLResourceContainer target) {
		for (Import cmlImport : source.getContextMappingModel().getImports()) {
			if (URI.createURI(cmlImport.getImportURI()).resolve(source.getResource().getURI()).toString().equals(target.getResource().getURI().toString()))
				return true;
		}
		return false;
	}

	protected <T> void addElementsToEList(EList<T> list, List<T> elementsToAdd) {
		// ugly workaround (clear list and add all again); otherwise list is not
		// properly updated when saving ecore model :(
		List<T> tempList = Lists.newArrayList(list);
		list.clear();
		list.addAll(tempList);
		list.addAll(elementsToAdd);
	}

	protected <T> void addElementToEList(EList<T> list, T elementToAdd) {
		// ugly workaround (clear list and add all again); otherwise list is not
		// properly updated when saving ecore model :(
		List<T> tempList = Lists.newArrayList(list);
		list.clear();
		list.addAll(tempList);
		list.add(elementToAdd);
	}

	protected <T> void removeElementFromEList(EList<T> list, T object) {
		// ugly workaround (clear list and add all again); otherwise list is not
		// properly updated when saving ecore model :(
		list.remove(object);
		List<T> tempList = Lists.newArrayList(list);
		list.clear();
		list.addAll(tempList);
	}
}

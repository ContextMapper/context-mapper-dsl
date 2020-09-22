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
import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.Import;
import org.contextmapper.dsl.contextMappingDSL.UserRequirement;
import org.contextmapper.dsl.exception.RefactoringSerializationException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.serializer.ISerializer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public abstract class AbstractRefactoring implements SemanticCMLRefactoring {

	protected ContextMappingModel model;
	protected CMLResource rootResource;
	protected Set<CMLResource> importedResources;
	private Map<BoundedContext, CMLResource> boundedContextsMap = Maps.newHashMap();
	private Map<ContextMap, CMLResource> contextMapMap = Maps.newHashMap();
	private Map<Domain, CMLResource> domainMap = Maps.newHashMap();
	private Map<UserRequirement, CMLResource> userRequirementMap = Maps.newHashMap();

	protected ResourceSet consistencyCheckResources;
	protected Set<CMLResource> additionalResourcesToCheck = Sets.newHashSet();

	@Override
	public void refactor(CMLResource resource) {
		this.rootResource = resource;
		this.importedResources = new CMLImportResolver().resolveImportedResources(rootResource);
		this.model = resource.getContextMappingModel();
		enableModificationTracking(resource);
		resolveRootElements();
		doRefactor();
	}

	@Override
	public void refactor(CMLResource resource, ResourceSet consistencyCheckResources) {
		this.consistencyCheckResources = consistencyCheckResources;
		enableModificationTracking(consistencyCheckResources);
		for (Resource resourceToCheck : consistencyCheckResources.getResources()) {
			if (resourceToCheck.getContents().isEmpty() || !(resourceToCheck.getContents().get(0) instanceof ContextMappingModel))
				continue;
			this.additionalResourcesToCheck.add(new CMLResource(resourceToCheck));
		}
		refactor(resource);
	}

	@Override
	public void persistChanges(ISerializer serializer) {
		CMLResource rootResource = this.rootResource;
		if (rootResource.isModified())
			persistResource(rootResource, serializer);
		if (rootResource.getResourceSet() != null)
			persistChanges(rootResource.getResourceSet(), serializer);
		if (this.consistencyCheckResources != null)
			persistChanges(consistencyCheckResources, serializer);
	}

	private void persistChanges(ResourceSet rs, ISerializer serializer) {
		for (Resource resource : rs.getResources()) {
			if (resource.isModified())
				persistResource(new CMLResource(resource), serializer);
		}
	}

	protected abstract void doRefactor();

	private void enableModificationTracking(CMLResource cmlResource) {
		cmlResource.setTrackingModification(true);
		if (cmlResource.getResourceSet() != null)
			enableModificationTracking(cmlResource.getResourceSet());
	}

	private void enableModificationTracking(ResourceSet rs) {
		for (Resource resource : rs.getResources()) {
			resource.setTrackingModification(true);
		}
	}

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

	private void persistResource(CMLResource resource, ISerializer serializer) {
		Set<String> serializationErrors = serializationDryRun(resource, serializer);
		if (!serializationErrors.isEmpty())
			throw new RefactoringSerializationException(serializationErrors);

		try {
			resource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
		} catch (IOException e) {
			throw new RuntimeException("Document cannot be formatted.");
		}
	}

	/**
	 * Tries to serialize a CML resource and returns a set of error messages if it
	 * does not work.
	 * 
	 * @param resource the resource that shall be persisted
	 * @return an empty set, if there was no error, a non-empty set (with error
	 *         messages) otherwise
	 */
	private Set<String> serializationDryRun(CMLResource resource, ISerializer serializer) {
		Set<String> errors = Sets.newHashSet();
		try {
			serializer.serialize(resource.getContextMappingModel());
		} catch (Exception e) {
			errors.add(e.getMessage());
		}
		return errors;
	}

	protected CMLResource getResource(BoundedContext bc) {
		CMLResource result = this.boundedContextsMap.get(bc);
		return result;
	}

	private void resolveRootElements() {
		resolveAllRootElements(rootResource);
		for (CMLResource importedResource : importedResources) {
			resolveAllRootElements(importedResource);
		}
	}

	private void resolveAllRootElements(CMLResource importedResource) {
		resolveBoundedContexts(importedResource);
		resolveContextMaps(importedResource);
		resolveDomains(importedResource);
		resolveUserRequirements(importedResource);
	}

	private void resolveBoundedContexts(CMLResource resource) {
		for (BoundedContext bc : resource.getContextMappingModel().getBoundedContexts()) {
			this.boundedContextsMap.put(bc, resource);
		}
	}

	private void resolveContextMaps(CMLResource resource) {
		if (resource.getContextMappingModel().getMap() != null)
			this.contextMapMap.put(resource.getContextMappingModel().getMap(), resource);
		for (CMLResource extResourceToCheck : this.additionalResourcesToCheck) {
			if (extResourceToCheck.getContextMappingModel().getMap() != null && containsImport(extResourceToCheck, resource))
				this.contextMapMap.put(extResourceToCheck.getContextMappingModel().getMap(), extResourceToCheck);
		}
	}

	private void resolveDomains(CMLResource resource) {
		for (Domain domain : resource.getContextMappingModel().getDomains()) {
			this.domainMap.put(domain, resource);
		}
	}

	private void resolveUserRequirements(CMLResource resource) {
		for (UserRequirement userRequirement : resource.getContextMappingModel().getUserRequirements()) {
			this.userRequirementMap.put(userRequirement, resource);
		}
	}

	/**
	 * Checks whether source contains an import statement to target.
	 */
	private boolean containsImport(CMLResource source, CMLResource target) {
		for (Import cmlImport : source.getContextMappingModel().getImports()) {
			if (URI.createURI(cmlImport.getImportURI()).resolve(source.getURI()).toString().equals(target.getURI().toString()))
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

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
package org.contextmapper.dsl.cml;

import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.DomainPart;
import org.contextmapper.dsl.contextMappingDSL.Feature;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.contextMappingDSL.UserRequirement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import com.google.common.collect.Sets;

public class CMLModelObjectsResolvingHelper {

	private Set<ContextMappingModel> resolveImportedModels(ContextMappingModel rootModel) {
		Set<CMLResourceContainer> importedResources = new CMLImportResolver().resolveImportedResources(new CMLResourceContainer(rootModel.eResource()));
		return importedResources.stream().map(r -> r.getContextMappingModel()).collect(Collectors.toSet());
	}

	public <T extends EObject> Set<T> resolveAllObjectsOfType(ContextMappingModel rootModel, Class<T> type) {
		Set<T> resultSet = Sets.newHashSet();
		resultSet.addAll(Sets.newHashSet(IteratorExtensions.filter(EcoreUtil2.eAll(rootModel), type)));
		for (ContextMappingModel model : resolveImportedModels(rootModel)) {
			resultSet.addAll(Sets.newHashSet(IteratorExtensions.filter(EcoreUtil2.eAll(model), type)));
		}
		return resultSet;
	}

	public BoundedContext resolveBoundedContext(Aggregate aggregate) {
		if (aggregate.eContainer() != null && aggregate.eContainer() instanceof SculptorModule)
			return resolveBoundedContext((SculptorModule) aggregate.eContainer());
		else
			return (BoundedContext) aggregate.eContainer();
	}

	public BoundedContext resolveBoundedContext(SculptorModule module) {
		return (BoundedContext) module.eContainer();
	}

	public Set<UserRequirement> resolveUserRequirements(BoundedContext boundedContext) {
		Set<UserRequirement> requirements = Sets.newHashSet();
		for (DomainPart domainPart : boundedContext.getImplementedDomainParts()) {
			if (!(domainPart instanceof Subdomain))
				continue;

			Subdomain subdomain = (Subdomain) domainPart;
			requirements.addAll(subdomain.getSupportedFeatures());
		}
		return requirements;
	}

	public Set<Feature> resolveFeatures(BoundedContext boundedContext) {
		Set<Feature> features = Sets.newHashSet();
		resolveUserRequirements(boundedContext).forEach(ur -> {
			features.addAll(ur.getFeatures());
		});
		return features;
	}

}

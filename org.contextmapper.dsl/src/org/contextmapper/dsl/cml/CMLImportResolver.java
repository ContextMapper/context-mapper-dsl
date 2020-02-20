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

import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Import;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.google.common.collect.Sets;

/**
 * Resolves all imported resources for a root CML model.
 * 
 * @author Stefan Kapferer
 *
 */
public class CMLImportResolver {

	public Set<CMLResourceContainer> resolveImportedResources(CMLResourceContainer rootResource) {
		Set<CMLResourceContainer> importedResources = Sets.newHashSet();
		Resource resource = rootResource.getResource();
		ResourceSet rs = resource.getResourceSet();
		ContextMappingModel cmlModel = (ContextMappingModel) resource.getContents().get(0);
		for (Import cmlImport : cmlModel.getImports()) {
			URI importURI = URI.createURI(cmlImport.getImportURI()).resolve(resource.getURI());
			Resource importedResource = rs.getResource(importURI, true);
			if(isResourceCMLModel(importedResource))
				importedResources.add(new CMLResourceContainer(importedResource));
		}
		return importedResources;
	}

	private boolean isResourceCMLModel(Resource resource) {
		if (resource.getContents().isEmpty())
			return false;
		if (!(resource.getContents().get(0) instanceof ContextMappingModel))
			return false;
		return true;
	}
	
}

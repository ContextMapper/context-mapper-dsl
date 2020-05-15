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

import java.io.IOException;
import java.util.List;

import org.contextmapper.dsl.cml.exception.ResourceIsNoCMLModelException;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import com.google.common.collect.Iterators;

/**
 * Contains an Eclipse resource for which we can be sure that it contains a CML
 * model.
 * 
 * @author Stefan Kapferer
 */
public class CMLResourceContainer {

	private final Resource resource;

	public CMLResourceContainer(Resource resource) {
		if (resource.getContents().isEmpty())
			throwResourceIsNoCMLModelException(resource);
		if (!(resource.getContents().get(0) instanceof ContextMappingModel))
			throwResourceIsNoCMLModelException(resource);

		this.resource = resource;
		this.resource.setTrackingModification(true);
	}

	public Resource getResource() {
		return resource;
	}

	public ContextMappingModel getContextMappingModel() {
		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(resource.getAllContents(), ContextMappingModel.class));
		// CML resource only contains one ContextMappingModel
		return contextMappingModels.get(0);
	}

	private void throwResourceIsNoCMLModelException(Resource resource) {
		if (resource.getURI() != null)
			throw new ResourceIsNoCMLModelException(resource.getURI());
		else
			throw new ResourceIsNoCMLModelException();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CMLResourceContainer))
			return false;

		CMLResourceContainer otherResource = (CMLResourceContainer) obj;
		return resource.getURI().equals(otherResource.resource.getURI());
	}

	@Override
	public int hashCode() {
		return resource.getURI().hashCode();
	}

}

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
package org.contextmapper.servicecutter.dsl.scl;

import java.util.List;

import org.contextmapper.servicecutter.dsl.scl.exception.ResourceIsNoSCLModelException;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterUserRepresentationsModel;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import com.google.common.collect.Iterators;

/**
 * Contains an Eclipse resource for which we can be sure that it contains an SCL
 * model.
 * 
 * @author Stefan Kapferer
 */
public class SCLResourceContainer {

	private final Resource resource;

	public SCLResourceContainer(Resource resource) {
		if (resource.getContents().isEmpty())
			throwResourceIsNoSCLModelException(resource);
		if (!(resource.getContents().get(0) instanceof ServiceCutterUserRepresentationsModel))
			throwResourceIsNoSCLModelException(resource);

		this.resource = resource;
		this.resource.setTrackingModification(true);
	}

	public Resource getResource() {
		return resource;
	}

	public ServiceCutterUserRepresentationsModel getServiceCutterConfigurationModel() {
		List<ServiceCutterUserRepresentationsModel> serviceCutterModels = IteratorExtensions
				.<ServiceCutterUserRepresentationsModel>toList(Iterators.<ServiceCutterUserRepresentationsModel>filter(resource.getAllContents(), ServiceCutterUserRepresentationsModel.class));
		// SCL resource only contains one ContextMappingModel
		return serviceCutterModels.get(0);
	}

	private void throwResourceIsNoSCLModelException(Resource resource) {
		if (resource.getURI() != null)
			throw new ResourceIsNoSCLModelException(resource.getURI());
		else
			throw new ResourceIsNoSCLModelException();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SCLResourceContainer))
			return false;

		SCLResourceContainer otherResource = (SCLResourceContainer) obj;
		return resource.getURI().equals(otherResource.resource.getURI());
	}

	@Override
	public int hashCode() {
		return resource.getURI().hashCode();
	}

}

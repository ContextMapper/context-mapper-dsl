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
package org.contextmapper.dsl.generator;

import java.io.IOException;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.servicecutter.input.userrepresentations.UserRepresentationsBuilder;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterUserRepresentationsModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.generator.IFileSystemAccess2;

public class ServiceCutterUserRepresentationsGenerator extends AbstractContextMappingModelGenerator {

	@Override
	protected void generateFromContextMappingModel(ContextMappingModel model, IFileSystemAccess2 fsa, URI inputFileURI) {
		ResourceSet resourceSet = model.eResource().getResourceSet();
		URI sclURI = inputFileURI.trimFileExtension().appendFileExtension("scl");

		UserRepresentationsBuilder builder;
		Resource sclResource;

		if (resourceSet.getURIConverter().exists(sclURI, null)) {
			sclResource = resourceSet.getResource(sclURI, true);
			ServiceCutterUserRepresentationsModel scInputModel = (ServiceCutterUserRepresentationsModel) sclResource.getContents().get(0);
			builder = new UserRepresentationsBuilder(model, scInputModel);
		} else {
			sclResource = resourceSet.createResource(sclURI);
			builder = new UserRepresentationsBuilder(model);
		}

		ServiceCutterUserRepresentationsModel scModel = builder.build();
		sclResource.getContents().clear();
		sclResource.getContents().add(scModel);
		try {
			sclResource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

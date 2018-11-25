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
package org.contextmapper.dsl.generator;

import java.io.IOException;
import java.util.List;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.servicecutter.input.userrepresentations.UserRepresentationsExampleFactory;
import org.contextmapper.servicecutter.dsl.ServiceCutterConfigurationDSLStandaloneSetup;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterUserRepresentationsModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import com.google.common.collect.Iterators;
import com.google.inject.Injector;

public class ServiceCutterUserRepresentationsExampleGenerator extends AbstractGenerator {

	@Override
	public void doGenerate(final Resource resource, final IFileSystemAccess2 fsa, final IGeneratorContext context) {
		List<ContextMappingModel> contextMappingModels = IteratorExtensions.<ContextMappingModel>toList(
				Iterators.<ContextMappingModel>filter(resource.getAllContents(), ContextMappingModel.class));

		Injector injector = new ServiceCutterConfigurationDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);

		if (contextMappingModels.size() > 0) {
			ContextMappingModel contextMappingModel = contextMappingModels.get(0);
			UserRepresentationsExampleFactory factory = new UserRepresentationsExampleFactory();
			ServiceCutterUserRepresentationsModel model = factory.createExampleModel(contextMappingModel);

			EcoreUtil.resolveAll(model);
			URI inputURI = resource.getURI().trimFileExtension();
			String fileName = inputURI.lastSegment();
			URI outputURI = inputURI.trimSegments(1).appendSegment(fileName + "_user-representations")
					.appendFileExtension("scl");
			Resource sclResource = resourceSet.createResource(outputURI);
			sclResource.getContents().add(model);
			try {
				sclResource.save(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

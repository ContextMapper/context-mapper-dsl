/*
 * Copyright 2018-2020 The Context Mapper Project Team
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
import org.contextmapper.dsl.generator.servicecutter.input.userrepresentations.UserRepresentationsExampleFactory;
import org.contextmapper.servicecutter.dsl.ServiceCutterConfigurationDSLStandaloneSetup;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterUserRepresentationsModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

public class ServiceCutterUserRepresentationsExampleGenerator extends AbstractContextMappingModelGenerator {

	@Override
	protected void generateFromContextMappingModel(ContextMappingModel model, IFileSystemAccess2 fsa, URI inputFileURI) {
		Injector injector = new ServiceCutterConfigurationDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		EcoreUtil.resolveAll(contextMappingModel);
		
		UserRepresentationsExampleFactory factory = new UserRepresentationsExampleFactory();
		ServiceCutterUserRepresentationsModel sclModel = factory.createExampleModel(contextMappingModel);

		EcoreUtil.resolveAll(sclModel);
		URI inputURI = inputFileURI.trimFileExtension();
		String fileName = inputURI.lastSegment();
		URI outputURI = inputURI.trimSegments(1).appendSegment(fileName + "_user-representations").appendFileExtension("scl");
		Resource sclResource = resourceSet.createResource(outputURI);
		sclResource.getContents().add(sclModel);
		try {
			sclResource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

}

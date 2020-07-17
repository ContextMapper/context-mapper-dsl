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

import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.servicecutter.output.converter.ServiceCutterOutputToContextMappingModelConverter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import ch.hsr.servicecutter.api.model.SolverResult;

public class CMLByServiceCutterOutputGenerator {

	public void doGenerate(final ResourceSet resourceSet, final URI jsonFileURI, final SolverResult serviceCutterResult) {
		ContextMappingModel model = new ServiceCutterOutputToContextMappingModelConverter().convert(serviceCutterResult);

		EcoreUtil.resolveAll(model);
		Resource cmlResource = resourceSet.createResource(jsonFileURI.trimFileExtension().appendFileExtension("cml"));
		cmlResource.getContents().add(model);
		try {
			cmlResource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

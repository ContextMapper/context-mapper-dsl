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

import org.contextmapper.dsl.ContextMappingDSLStandaloneSetup;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

public class XMIGenerator extends AbstractContextMapGenerator {

	@Override
	protected void generateFromContextMap(ContextMap contextmap, IFileSystemAccess2 fsa, URI inputFileURI) {
		Injector injector = new ContextMappingDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);

		EcoreUtil.resolveAll(contextMappingModel);
		Resource xmiResource = resourceSet.createResource(inputFileURI.trimFileExtension().appendFileExtension("xmi"));
		xmiResource.getContents().add(contextMappingModel);
		try {
			xmiResource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

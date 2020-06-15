/*
 * Copyright 2019 The Context Mapper Project Team
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

import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.mdsl.MDSLAPIDescriptionCreator;
import org.contextmapper.dsl.generator.mdsl.MDSLModelCreator;
import org.contextmapper.dsl.generator.mdsl.ProtectedRegionContext;
import org.contextmapper.dsl.generator.mdsl.ProtectedRegionContextFactory;
import org.contextmapper.dsl.generator.mdsl.model.ServiceSpecification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.generator.IFileSystemAccess2;

public class MDSLContractsGenerator extends AbstractContextMappingModelGenerator {

	private static final String MDSL_FILE_EXT = "mdsl";

	@Override
	protected void generateFromContextMappingModel(ContextMappingModel model, IFileSystemAccess2 fsa, URI inputFileURI) {
		MDSLModelCreator mdslModelCreator = new MDSLModelCreator(model);
		for (ServiceSpecification serviceSpecification : mdslModelCreator.createServiceSpecifications()) {
			String mdslFileName = inputFileURI.trimFileExtension().lastSegment() + "_" + serviceSpecification.getName() + "." + MDSL_FILE_EXT;
			ProtectedRegionContext protectedRegionContext = createProtectedRegionContext(mdslFileName, fsa);
			MDSLAPIDescriptionCreator dslCreator = new MDSLAPIDescriptionCreator(protectedRegionContext);
			fsa.generateFile(mdslFileName, dslCreator.createAPIDescriptionText(serviceSpecification, inputFileURI.lastSegment()));
		}
	}

	private ProtectedRegionContext createProtectedRegionContext(String mdslFileName, IFileSystemAccess2 fsa) {
		ProtectedRegionContextFactory factory = new ProtectedRegionContextFactory();
		if (fsa.isFile(mdslFileName)) {
			return factory.createProtectedRegionContextForExistingMDSLFile(fsa.readTextFile(mdslFileName).toString());
		} else {
			return factory.createProtectedRegionContextForNewMDSLFile();
		}
	}
}

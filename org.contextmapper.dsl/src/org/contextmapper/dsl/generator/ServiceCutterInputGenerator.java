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

import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.generator.servicecutter.input.converter.ContextMappingModelToServiceCutterERDConverter;
import org.contextmapper.dsl.generator.servicecutter.input.model.EntityRelationshipDiagram;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ServiceCutterInputGenerator extends AbstractContextMapGenerator {

	@Override
	protected void generateFromContextMap(ContextMap contextmap, IFileSystemAccess2 fsa, URI inputFileURI) {
		String modelName = inputFileURI.trimFileExtension().lastSegment();
		ContextMappingModelToServiceCutterERDConverter converter = new ContextMappingModelToServiceCutterERDConverter();
		EntityRelationshipDiagram erd = converter.convert(modelName, contextmap);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			fsa.generateFile(modelName + ".json", objectMapper.writeValueAsString(erd));
		} catch (JsonProcessingException e) {
			throw new RuntimeException("JSON conversion error occured!", e);
		}
	}

}

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

import java.util.List;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.servicecutter.input.converter.ContextMappingModelToServiceCutterERDConverter;
import org.contextmapper.dsl.generator.servicecutter.input.model.EntityRelationshipDiagram;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Iterators;

public class ServiceCutterInputGenerator extends AbstractGenerator {

	@Override
	public void doGenerate(final Resource resource, final IFileSystemAccess2 fsa, final IGeneratorContext context) {
		List<ContextMappingModel> contextMappingModels = IteratorExtensions.<ContextMappingModel>toList(
				Iterators.<ContextMappingModel>filter(resource.getAllContents(), ContextMappingModel.class));

		if (contextMappingModels.size() > 0) {
			ContextMappingModel model = contextMappingModels.get(0);
			String modelName = resource.getURI().trimFileExtension().lastSegment();
			ContextMappingModelToServiceCutterERDConverter converter = new ContextMappingModelToServiceCutterERDConverter();
			EntityRelationshipDiagram erd = converter.convert(modelName, model.getMap());
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			try {
				fsa.generateFile(modelName + ".json", objectMapper.writeValueAsString(erd));
			} catch (JsonProcessingException e) {
				throw new RuntimeException("JSON conversion error occured!", e);
			}
		}
	}

}

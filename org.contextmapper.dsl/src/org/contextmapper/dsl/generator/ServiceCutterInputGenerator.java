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
import org.contextmapper.dsl.generator.servicecutter.input.converter.ContextMappingModelToServiceCutterERDConverter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ch.hsr.servicecutter.api.model.EntityRelation;
import ch.hsr.servicecutter.api.model.EntityRelationDiagram;

public class ServiceCutterInputGenerator extends AbstractContextMappingModelGenerator {

	@Override
	protected void generateFromContextMappingModel(ContextMappingModel model, IFileSystemAccess2 fsa, URI inputFileURI) {
		String modelName = inputFileURI.trimFileExtension().lastSegment();
		ContextMappingModelToServiceCutterERDConverter converter = new ContextMappingModelToServiceCutterERDConverter();
		EntityRelationDiagram erd = converter.convert(modelName, model);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

		SimpleModule customEntityRelationSerializerModule = new SimpleModule("CustomEntityRelationSerializer", new Version(1, 0, 0, null, null, null));
		customEntityRelationSerializerModule.addSerializer(new CustomEntityRelationSerializer(EntityRelation.class));
		objectMapper.registerModule(customEntityRelationSerializerModule);

		try {
			fsa.generateFile(modelName + ".json", objectMapper.writeValueAsString(erd));
		} catch (JsonProcessingException e) {
			throw new RuntimeException("JSON conversion error occured!", e);
		}
	}

	private class CustomEntityRelationSerializer extends StdSerializer<EntityRelation> {

		protected CustomEntityRelationSerializer(Class<EntityRelation> t) {
			super(t);
		}

		@Override
		public void serialize(EntityRelation value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("origin", value.getOrigin().getName());
			jsonGenerator.writeStringField("destination", value.getDestination().getName());
			jsonGenerator.writeStringField("type", value.getType().toString());
			jsonGenerator.writeEndObject();
		}

	}

}

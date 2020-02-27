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
package org.contextmapper.dsl.generator.freemarker;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.CustomerSupplierRelationship;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.Partnership;
import org.contextmapper.dsl.contextMappingDSL.SharedKernel;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.contextMappingDSL.SymmetricRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.tactic.dsl.tacticdsl.BasicType;
import org.contextmapper.tactic.dsl.tacticdsl.CommandEvent;
import org.contextmapper.tactic.dsl.tacticdsl.DataTransferObject;
import org.contextmapper.tactic.dsl.tacticdsl.DomainEvent;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Enum;
import org.contextmapper.tactic.dsl.tacticdsl.Event;
import org.contextmapper.tactic.dsl.tacticdsl.Trait;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * Generates a string from a CML Context Map based on a Freemarker template.
 * 
 * @author Stefan Kapferer
 *
 */
public class FreemarkerTextGenerator {

	private File templateFile;

	public FreemarkerTextGenerator(File templateFile) {
		this.templateFile = templateFile;
	}

	public String generate(ContextMappingModel contextMappingModel) {
		String result = "";
		try {
			Configuration configuration = configureFreemarker();
			Template template = configuration.getTemplate(templateFile.getName());

			StringWriter writer = new StringWriter();
			template.process(prepareModelData(contextMappingModel), writer);
			result = writer.toString();
		} catch (Exception e) {
			throw new ContextMapperApplicationException("Freemarker template exception: " + e.getMessage(), e);
		}
		return result;
	}

	private Map<String, Object> prepareModelData(ContextMappingModel contextMappingModel) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("contextMap", contextMappingModel.getMap());
		dataMap.put("boundedContexts", contextMappingModel.getBoundedContexts());
		dataMap.put("domains", contextMappingModel.getDomains());
		dataMap.put("imports", contextMappingModel.getImports());
		dataMap.put("useCases", contextMappingModel.getUseCases());
		dataMap.put("timestamp", new SimpleDateFormat("dd.MM.YYYY HH:mm:ss z").format(new Date()));
		dataMap.put("filename", contextMappingModel.eResource().getURI().lastSegment().toString());

		dataMap.putAll(createTemplatingHelperMethods());
		dataMap.putAll(createClassMap());

		return dataMap;
	}

	private Map<String, Object> createTemplatingHelperMethods() {
		Map<String, Object> methodsMap = new HashMap<>();
		methodsMap.put("instanceOf", new InstanceOfMethod());
		return methodsMap;
	}

	private Map<String, Object> createClassMap() {
		Map<String, Object> classMap = new HashMap<>();
		classMap.put("SymmetricRelationship", SymmetricRelationship.class);
		classMap.put("Partnership", Partnership.class);
		classMap.put("SharedKernel", SharedKernel.class);
		classMap.put("UpstreamDownstreamRelationship", UpstreamDownstreamRelationship.class);
		classMap.put("CustomerSupplierRelationship", CustomerSupplierRelationship.class);
		classMap.put("Domain", Domain.class);
		classMap.put("Subdomain", Subdomain.class);
		classMap.put("BasicType", BasicType.class);
		classMap.put("DataTransferObject", DataTransferObject.class);
		classMap.put("DomainObject", DomainObject.class);
		classMap.put("Enum", Enum.class);
		classMap.put("Trait", Trait.class);
		classMap.put("Entity", Entity.class);
		classMap.put("Event", Event.class);
		classMap.put("CommandEvent", CommandEvent.class);
		classMap.put("DomainEvent", DomainEvent.class);
		classMap.put("ValueObject", ValueObject.class);
		return classMap;
	}

	private Configuration configureFreemarker() throws IOException {
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
		configuration.setDirectoryForTemplateLoading(templateFile.getParentFile());
		configuration.setDefaultEncoding("UTF-8");
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		return configuration;
	}

}

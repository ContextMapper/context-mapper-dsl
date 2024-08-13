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

import org.contextmapper.dsl.contextMappingDSL.CommandInvokation;
import org.contextmapper.dsl.contextMappingDSL.CommandInvokationStep;
import org.contextmapper.dsl.contextMappingDSL.ConcurrentCommandInvokation;
import org.contextmapper.dsl.contextMappingDSL.ConcurrentOperationInvokation;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.CustomerSupplierRelationship;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.DomainEventProductionStep;
import org.contextmapper.dsl.contextMappingDSL.EitherCommandOrOperation;
import org.contextmapper.dsl.contextMappingDSL.EitherCommandOrOperationInvokation;
import org.contextmapper.dsl.contextMappingDSL.ExclusiveAlternativeCommandInvokation;
import org.contextmapper.dsl.contextMappingDSL.ExclusiveAlternativeEventProduction;
import org.contextmapper.dsl.contextMappingDSL.ExclusiveAlternativeOperationInvokation;
import org.contextmapper.dsl.contextMappingDSL.InclusiveAlternativeCommandInvokation;
import org.contextmapper.dsl.contextMappingDSL.InclusiveAlternativeEventProduction;
import org.contextmapper.dsl.contextMappingDSL.InclusiveAlternativeOperationInvokation;
import org.contextmapper.dsl.contextMappingDSL.MultipleEventProduction;
import org.contextmapper.dsl.contextMappingDSL.OperationInvokation;
import org.contextmapper.dsl.contextMappingDSL.Partnership;
import org.contextmapper.dsl.contextMappingDSL.SharedKernel;
import org.contextmapper.dsl.contextMappingDSL.SingleCommandInvokation;
import org.contextmapper.dsl.contextMappingDSL.SingleEventProduction;
import org.contextmapper.dsl.contextMappingDSL.SingleOperationInvokation;
import org.contextmapper.dsl.contextMappingDSL.Stakeholder;
import org.contextmapper.dsl.contextMappingDSL.StakeholderGroup;
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
import org.contextmapper.tactic.dsl.tacticdsl.ExclusiveAlternativeStateTransitionTarget;
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
	private Map<String, Object> dataMap;

	public FreemarkerTextGenerator(File templateFile) {
		this.templateFile = templateFile;
		dataMap = new HashMap<>();
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

	public void registerCustomModelProperty(String propertyName, Object object) {
		dataMap.put(propertyName, object);
	}

	private Map<String, Object> prepareModelData(ContextMappingModel contextMappingModel) {
		dataMap.put("contextMap", contextMappingModel.getMap());
		dataMap.put("boundedContexts", contextMappingModel.getBoundedContexts());
		dataMap.put("domains", contextMappingModel.getDomains());
		dataMap.put("imports", contextMappingModel.getImports());
		dataMap.put("useCases", contextMappingModel.getUserRequirements());
		dataMap.put("userStories", contextMappingModel.getUserRequirements());
		dataMap.put("stakeholders", contextMappingModel.getStakeholders());
		dataMap.put("valueRegisters", contextMappingModel.getValueRegisters());
		String timeStamp = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss z").format(new Date());
		dataMap.put("timestamp", timeStamp); // for backwards compatibility
		dataMap.put("timeStamp", timeStamp);
		String fileName = contextMappingModel.eResource().getURI().lastSegment().toString();
		dataMap.put("filename", fileName); // for backwards compatibility
		dataMap.put("fileName", fileName);
		String userName = System.getProperty("user.name");
		dataMap.put("username", userName); // for backwards compatibility
		dataMap.put("userName", userName);

		dataMap.putAll(createTemplatingHelperMethods());
		dataMap.putAll(createClassMap());

		return dataMap;
	}

	private Map<String, Object> createTemplatingHelperMethods() {
		Map<String, Object> methodsMap = new HashMap<>();
		methodsMap.put("instanceOf", new InstanceOfMethod());
		methodsMap.put("getType", new GetTypeOfComplexTypeMethod());
		methodsMap.put("filterBoundedContexts", new BoundedContextsFilterMethod()); // for backwards compatibility
		methodsMap.put("filterStructuralBoundedContexts", new BoundedContextsFilterMethod());
		methodsMap.put("filterTeams", new TeamsFilterMethod());
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
		classMap.put("DomainEventProductionStep", DomainEventProductionStep.class);
		classMap.put("CommandInvokationStep", CommandInvokationStep.class);
		classMap.put("EitherCommandOrOperation", EitherCommandOrOperation.class);
		classMap.put("SingleEventProduction", SingleEventProduction.class);
		classMap.put("MultipleEventProduction", MultipleEventProduction.class);
		classMap.put("ExclusiveAlternativeEventProduction", ExclusiveAlternativeEventProduction.class);
		classMap.put("InclusiveAlternativeEventProduction", InclusiveAlternativeEventProduction.class);
		classMap.put("EitherCommandOrOperationInvokation", EitherCommandOrOperationInvokation.class);
		classMap.put("SingleCommandInvokation", SingleCommandInvokation.class);
		classMap.put("ConcurrentCommandInvokation", ConcurrentCommandInvokation.class);
		classMap.put("ExclusiveAlternativeCommandInvokation", ExclusiveAlternativeCommandInvokation.class);
		classMap.put("InclusiveAlternativeCommandInvokation", InclusiveAlternativeCommandInvokation.class);
		classMap.put("SingleOperationInvokation", SingleOperationInvokation.class);
		classMap.put("ConcurrentOperationInvokation", ConcurrentOperationInvokation.class);
		classMap.put("ExclusiveAlternativeOperationInvokation", ExclusiveAlternativeOperationInvokation.class);
		classMap.put("InclusiveAlternativeOperationInvokation", InclusiveAlternativeOperationInvokation.class);
		classMap.put("CommandInvokation", CommandInvokation.class);
		classMap.put("OperationInvokation", OperationInvokation.class);
		classMap.put("ExclusiveAlternativeStateTransitionTarget", ExclusiveAlternativeStateTransitionTarget.class);
		classMap.put("StakeholderGroup", StakeholderGroup.class);
		classMap.put("Stakeholder", Stakeholder.class);
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

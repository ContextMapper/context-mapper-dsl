/*
 * Copyright 2019-2021 The Context Mapper Project Team
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
package org.contextmapper.dsl.generator.mdsl;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.Application;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.CommandInvokationStep;
import org.contextmapper.dsl.contextMappingDSL.ConcurrentCommandInvokation;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.DomainEventProductionStep;
import org.contextmapper.dsl.contextMappingDSL.EitherCommandOrOperation;
import org.contextmapper.dsl.contextMappingDSL.EitherCommandOrOperationInvokation;
import org.contextmapper.dsl.contextMappingDSL.EventProduction;
import org.contextmapper.dsl.contextMappingDSL.ExclusiveAlternativeCommandInvokation;
import org.contextmapper.dsl.contextMappingDSL.ExclusiveAlternativeEventProduction;
import org.contextmapper.dsl.contextMappingDSL.Flow;
import org.contextmapper.dsl.contextMappingDSL.FlowStep;
import org.contextmapper.dsl.contextMappingDSL.InclusiveAlternativeCommandInvokation;
import org.contextmapper.dsl.contextMappingDSL.InclusiveAlternativeEventProduction;
import org.contextmapper.dsl.contextMappingDSL.MultipleEventProduction;
import org.contextmapper.dsl.contextMappingDSL.SingleCommandInvokation;
import org.contextmapper.dsl.contextMappingDSL.SingleEventProduction;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamRole;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.contextmapper.dsl.generator.exception.InputNotYetSupportedException;
import org.contextmapper.dsl.generator.mdsl.generatorcontext.DownstreamContext;
import org.contextmapper.dsl.generator.mdsl.generatorcontext.UpstreamAPIContext;
import org.contextmapper.dsl.generator.mdsl.model.APIUsageContext;
import org.contextmapper.dsl.generator.mdsl.model.DataType;
import org.contextmapper.dsl.generator.mdsl.model.EndpointClient;
import org.contextmapper.dsl.generator.mdsl.model.EndpointContract;
import org.contextmapper.dsl.generator.mdsl.model.EndpointOffer;
import org.contextmapper.dsl.generator.mdsl.model.EndpointOperation;
import org.contextmapper.dsl.generator.mdsl.model.EndpointProvider;
import org.contextmapper.dsl.generator.mdsl.model.OrchestrationFlow;
import org.contextmapper.dsl.generator.mdsl.model.ServiceSpecification;
import org.contextmapper.tactic.dsl.tacticdsl.CollectionType;
import org.contextmapper.tactic.dsl.tacticdsl.CommandEvent;
import org.contextmapper.tactic.dsl.tacticdsl.ComplexType;
import org.contextmapper.tactic.dsl.tacticdsl.DomainEvent;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObjectOperation;
import org.contextmapper.tactic.dsl.tacticdsl.Parameter;
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.contextmapper.tactic.dsl.tacticdsl.Visibility;
import org.eclipse.emf.common.util.EList;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MDSLModelCreator {

	private static final String API_NAME_EXTENSION = "API";
	private static final String PROVIDER_NAME_EXTENSION = "Provider";
	private static final String CLIENT_NAME_EXTENSION = "Client";
	private static final String MDSL_VOID_RETURN_TYPE = "D<void>";
	private static final String ENDPOINT_LOCATION = "http://localhost:";
	private static final String PROTOCOL_STRING_IF_NOT_DEFINED = "tbd";
	private static final String PROTOCOL_NOT_DEFINED_COMMENT = "The protocol is generated if you specify the implementation technology in CML";

	private ContextMappingModel model;
	private int initialPort = 8000;
	private MDSLDataTypeCreator dataTypeCreator;
	private MDSLNameEncoder mdslEncoder;

	public MDSLModelCreator(ContextMappingModel model) {
		this.model = model;
		this.dataTypeCreator = new MDSLDataTypeCreator();
		this.mdslEncoder = new MDSLNameEncoder();
	}

	public List<ServiceSpecification> createServiceSpecifications() {
		checkPreconditions();

		List<ServiceSpecification> specs = Lists.newArrayList();
		Map<String, UpstreamAPIContext> upstreamContexts = collectUpstreamContexts();
		for (String apiName : upstreamContexts.keySet()) {
			UpstreamAPIContext context = upstreamContexts.get(apiName);
			specs.add(createServiceSpecification(context.getApiName(), context));
		}

		return specs;
	}

	private ServiceSpecification createServiceSpecification(String apiName, UpstreamAPIContext context) {
				
		ServiceSpecification specification = new ServiceSpecification();
		specification.setName(mdslEncoder.encodeName(apiName));

		/*
		// TODO see https://github.com/ContextMapper/context-mapper-dsl/issues/301 and https://github.com/ContextMapper/context-mapper-dsl/issues/303
		*/ 
		
		if (context.getUpstreamRoles().contains(UpstreamRole.OPEN_HOST_SERVICE) && context.getUpstreamRoles().contains(UpstreamRole.PUBLISHED_LANGUAGE)) {
			specification.setUsageContext(APIUsageContext.PUBLIC_API);
		} else if (context.getUpstreamRoles().contains(UpstreamRole.OPEN_HOST_SERVICE)) {
			specification.setUsageContext(APIUsageContext.COMMUNITY_API);
		}

		if (context.getApplicationLayer() != null) {
			specification.addEndpoint(createEndpoint(context.getApplicationLayer(), specification));			
			for(DomainEvent de:context.getApplicationLayer().getEvents()) {
				specification.addEventType(de.getName()); 
				// TODO map data structure, not just name (if present)
			}
			// add event types for all domain events in all exposed aggregates
			for(Aggregate exposedAggregate:context.getExposedAggregates()) {
				for(SimpleDomainObject objectInAggregate:exposedAggregate.getDomainObjects()) {
					// check type of domain object (entity? event?...?):
					if(objectInAggregate instanceof DomainEvent) {
						DomainEvent de = (DomainEvent) objectInAggregate;
						// TODO make sure names are unique/do not add duplicates
						specification.addEventType(de.getName());
					}
				} 
			}
			
			for(CommandEvent ce:context.getApplicationLayer().getCommands()) {
				specification.addCommandType(ce.getName());
			}
			EList<Flow> flows = context.getApplicationLayer().getFlows();
			for(Flow cmlFlow : flows) {
				OrchestrationFlow mdslFlow = new OrchestrationFlow();
				mdslFlow.setName(cmlFlow.getName());
				for(FlowStep step: cmlFlow.getSteps() ) {
					mapFlowStep(mdslFlow, step);
				}
				specification.addFlow(mdslFlow);
			}
		}
		for (Aggregate aggregate : context.getExposedAggregates()) {
			specification.addEndpoint(createEndpoint(aggregate, specification));
		}
		for (DataType dataType : dataTypeCreator.getAllDataTypes()) {
			specification.addDataType(dataType);
		}
		specification.addProvider(createProvider(context, specification.getEndpoints()));
		for (DownstreamContext downstreamContext : context.getDownstreamContexts()) {
			specification.addClient(createClient(downstreamContext));
		}
		return specification;
	}

	private void mapFlowStep(OrchestrationFlow mdslFlow, FlowStep step) {
		if(step.getClass() == org.contextmapper.dsl.contextMappingDSL.impl.CommandInvokationStepImpl.class) {
			CommandInvokationStep cis = (CommandInvokationStep) step;
			EitherCommandOrOperationInvokation ecooi = cis.getAction();
			EList<DomainEvent> events = cis.getEvents();
			if(ecooi instanceof SingleCommandInvokation) {
				SingleCommandInvokation ci = (SingleCommandInvokation) ecooi;
				boolean first;
				String andEvents = combineEvents(events, " + ");
				String commands = "";
				first=true;
				for(CommandEvent ce : ci.getCommands()) {
					// we can only have one entry, but still checking (could also validate size and go to index 0 directly)
					if(!first) {
						commands += "-";
						first=false;
					}
					commands += ce.getName();
				}
				mdslFlow.addCommandInvocationStep(andEvents, commands); 
			} else if(ecooi instanceof ConcurrentCommandInvokation) {
				ConcurrentCommandInvokation cci = (ConcurrentCommandInvokation) ecooi; 
				EList<CommandEvent> commands = cci.getCommands();
				String andEvents = combineEvents(events, " + ");
				String andCommands = commands.get(0).getName();
				for(int i=1; i<commands.size();i++) {
					andCommands += " + " + commands.get(i).getName();
				}
				mdslFlow.addCommandInvocationStep(andEvents, andCommands); 
			} else if(ecooi instanceof ExclusiveAlternativeCommandInvokation){
				ExclusiveAlternativeCommandInvokation eaci = (ExclusiveAlternativeCommandInvokation) ecooi; 
				EList<CommandEvent> commands =eaci.getCommands();
				String xorEvents = combineEvents(events, " + ");
				String xorCommands = commands.get(0).getName();
				for(int i=1; i<commands.size();i++) {
					xorCommands += " x " + commands.get(i).getName();
				}
				mdslFlow.addCommandInvocationStep(xorEvents, xorCommands);
			} else if(ecooi instanceof InclusiveAlternativeCommandInvokation){
				InclusiveAlternativeCommandInvokation eaci = (InclusiveAlternativeCommandInvokation) ecooi; 
				EList<CommandEvent> commands =eaci.getCommands();
				String orEvents = combineEvents(events, " + "); 
				String orCommands = commands.get(0).getName();
				for(int i=1; i<commands.size();i++) {
					orCommands += " o " + commands.get(i).getName();
				}
				mdslFlow.addCommandInvocationStep(orEvents, orCommands);
			} else {
				throw new GeneratorInputException("Not yet implemented: support for " + ecooi.getClass());
			}
		}
		else if(step.getClass() == org.contextmapper.dsl.contextMappingDSL.impl.DomainEventProductionStepImpl.class) {
			DomainEventProductionStep depStep = (DomainEventProductionStep) step;
			EitherCommandOrOperation action = depStep.getAction();
			EventProduction ep = depStep.getEventProduction();
			
			if(action.getCommand() == null && action.getOperation() != null)
				throw new InputNotYetSupportedException("Operations are not yet supported in the MDSL generator. Please use commands instead.");
			
			if(ep instanceof SingleEventProduction) {
				EList<DomainEvent> events = ep.getEvents();
				// we can only have one entry, so just in case: 
				if(events.size()!=1) 
					throw new InvalidParameterException("Single event production must not list more than one event.");
				mdslFlow.addEventProductionStep(action.getCommand().getName(), events.get(0).getName()); 
			} else if(ep instanceof MultipleEventProduction) {
				String andEvents = mapEvents(action, ep, " + ");
				mdslFlow.addEventProductionStep(action.getCommand().getName(), andEvents); 
			} else if(ep instanceof InclusiveAlternativeEventProduction) {
				String orEvents = mapEvents(action, ep, " o ");
				mdslFlow.addEventProductionStep(action.getCommand().getName(), orEvents); 
			} else if(ep instanceof ExclusiveAlternativeEventProduction) {
				String xorEvents = mapEvents(action, ep, " x ");
				mdslFlow.addEventProductionStep(action.getCommand().getName(), xorEvents); 
			} else {
				throw new GeneratorInputException("Not yet implemented: support for " + ep.getClass());
			}
		}
	}
	
	// note: not mapping state transitions, actor delegations, operations (no direct support in MDSL 5.4)

	private String mapEvents(EitherCommandOrOperation action, EventProduction ep, String operator) {
		EList<DomainEvent> events = ep.getEvents();
		// all four event production options (line 260 in grammar) refer to at least one event
		String combinedEvents = events.get(0).getName();  
		for(int i=1; i<events.size();i++) {
			if(action.getCommand()!=null) {
				combinedEvents += operator + events.get(i).getName();
			} else {
				throw new GeneratorInputException("Operations are not supported in MDSL");
			}
		}
		return combinedEvents;
	}

	private String combineEvents(EList<DomainEvent> events, String operator) {
		String combinedEvent = "";
		boolean isFirstElementInList=true;
		for(DomainEvent event : events) {
			if (isFirstElementInList) {
				isFirstElementInList=false;
			} else { // must be false
				combinedEvent += operator; 
			}
			combinedEvent += event.getName();
		}
		return combinedEvent;
	}

	private EndpointContract createEndpoint(Aggregate aggregate, ServiceSpecification specification) {
		EndpointContract endpoint = new EndpointContract();
		String endpointName = mdslEncoder.encodeName(aggregate.getName());
		endpoint.setName(endpointName);
		Optional<DomainObject> aggregateRoot = aggregate.getDomainObjects().stream().filter(o -> o instanceof DomainObject).map(o -> (DomainObject) o)
				.filter(o -> o.isAggregateRoot()).findFirst();
		if (aggregateRoot.isPresent()) {
			for (DomainObjectOperation operation : aggregateRoot.get().getOperations()) {
				if (operation.getVisibility().equals(Visibility.PUBLIC))
					endpoint.addOperation(createOperation(operation, specification));
			}
		}
		List<ServiceOperation> serviceOperations = aggregate.getServices().stream().flatMap(s -> s.getOperations().stream()).collect(Collectors.toList());
		for (ServiceOperation serviceOperation : serviceOperations) {
			if (serviceOperation.getVisibility().equals(Visibility.PUBLIC))
				endpoint.addOperation(createOperation(serviceOperation, specification));
		}
		setEndpointServesAsString(endpoint, aggregate.getDoc());
		return endpoint;
	}

	private EndpointContract createEndpoint(Application application, ServiceSpecification specification) {
		EndpointContract endpoint = new EndpointContract();
		String endpointName = StringUtils.isNoneEmpty(application.getName()) ? mdslEncoder.encodeName(application.getName()) : mdslEncoder.encodeName("Application");
		endpoint.setName(endpointName);
		List<ServiceOperation> serviceOperations = application.getServices().stream().flatMap(s -> s.getOperations().stream()).collect(Collectors.toList());
		for (ServiceOperation serviceOperation : serviceOperations) {
			if (serviceOperation.getVisibility().equals(Visibility.PUBLIC))
				endpoint.addOperation(createOperation(serviceOperation, specification));
		}
		for (CommandEvent command : application.getCommands()) {
			endpoint.addOperation(createOperation(command, specification));
		}
		return endpoint;
	}

	private void setEndpointServesAsString(EndpointContract endpoint, String docString) {
		if (docString == null || "".equals(docString))
			return;
		String pattern = new MDSLPatternMatcher().matchPatterns(MDSLPatternMatcher.ENDPOINT_SERVES_AS_PATTERNS, docString);
		if (!"".equals(pattern)) {
			endpoint.setServesAsPatternMatched(true);
			endpoint.setServesAs(pattern);
		} else {
			endpoint.setServesAs(docString);
		}
	}

	private EndpointOperation createOperation(DomainObjectOperation domainObjectOperation, ServiceSpecification specification) {
		return createOperation(domainObjectOperation.getName(), domainObjectOperation.getParameters(), domainObjectOperation.getReturnType(), specification,
				domainObjectOperation.getDoc());
	}

	private EndpointOperation createOperation(ServiceOperation serviceOperation, ServiceSpecification specification) {
		return createOperation(serviceOperation.getName(), serviceOperation.getParameters(), serviceOperation.getReturnType(), specification, serviceOperation.getDoc());
	}

	private EndpointOperation createOperation(CommandEvent command, ServiceSpecification specification) {
		String name = command.getName();
		Parameter parameter = TacticdslFactory.eINSTANCE.createParameter();
		parameter.setName(name + "Parameter");
		ComplexType type = TacticdslFactory.eINSTANCE.createComplexType();
		if (!command.getName().endsWith("Command"))
			command.setName(command.getName() + "Command");
		type.setDomainObjectType(command);
		parameter.setParameterType(type);
		List<Parameter> parameters = Lists.newArrayList();
		parameters.add(parameter);
		return createOperation(name, parameters, null, specification, "");
	}

	private EndpointOperation createOperation(String operationName, List<Parameter> parameters, ComplexType returnType, ServiceSpecification specification, String docString) {
		EndpointOperation operation = new EndpointOperation();
		operation.setName(mdslEncoder.encodeName(operationName));

		if (parameters.isEmpty()) {
			operation.setExpectingPayload(createVoidReturnType());
		} else if (parameters.size() == 1) {
			Parameter parameter = parameters.get(0);
			operation.setExpectingPayload(dataTypeCreator.createMDSLDataType(parameter.getParameterType()));
			operation.setExpectingCollection(parameter.getParameterType().getCollectionType() != CollectionType.NONE);
		} else {
			operation.setExpectingPayload(dataTypeCreator.createMDSLDataType4ParameterList(operationName, parameters));
		}
		if (returnType != null) {
			operation.setDeliveringPayload(dataTypeCreator.createMDSLDataType(returnType));
			operation.setDeliveringCollection(returnType.getCollectionType() != CollectionType.NONE);
		}
		setOperationResponsibility(operation, docString);
		return operation;
	}

	private void setOperationResponsibility(EndpointOperation operation, String docString) {
		if (docString == null || "".equals(docString))
			return;
		String pattern = new MDSLPatternMatcher().matchPatterns(MDSLPatternMatcher.OPERATION_RESPONSIBILITY_PATTERNS, docString);
		if (!"".equals(pattern)) {
			operation.setEndpointResponsibilityPatternMatched(true);
			operation.setEndpointResponsibility(pattern);
		} else {
			operation.setEndpointResponsibility(docString);
		}
	}

	private DataType createVoidReturnType() {
		DataType voidType = new DataType();
		voidType.setIsPrimitiveType(true);
		voidType.setName(MDSL_VOID_RETURN_TYPE);
		return voidType;
	}

	private EndpointProvider createProvider(UpstreamAPIContext context, List<EndpointContract> endpointContracts) {
		EndpointProvider provider = new EndpointProvider();
		String implementationTechnology = context.getJoinedImplementationTechnologies();
		provider.setName(mdslEncoder.encodeName(context.getUpstreamContext().getName() + PROVIDER_NAME_EXTENSION));
		for (EndpointContract contract : endpointContracts) {
			EndpointOffer offer = new EndpointOffer();
			offer.setOfferedEndpoint(contract);
			offer.setLocation(ENDPOINT_LOCATION + (initialPort++));
			offer.setProtocol(!"".equals(implementationTechnology) ? implementationTechnology : PROTOCOL_STRING_IF_NOT_DEFINED);
			offer.setProtocolComment(!"".equals(implementationTechnology) ? "" : PROTOCOL_NOT_DEFINED_COMMENT);
			provider.addEndpointOffer(offer);
		}
		if (!context.getUpstreamRoles().isEmpty()) {
			String roles = String.join(" and ", context.getUpstreamRoles().stream().map(ur -> ur.getName() + " (" + ur.getLiteral() + ")").collect(Collectors.toList()));
			provider.addComment("Generated from DDD upstream Bounded Context '" + context.getUpstreamContext().getName() + "' implementing " + roles + ".");
		}
		if (context.getUpstreamContext().getDomainVisionStatement() != null && !"".equals(context.getUpstreamContext().getDomainVisionStatement()))
			provider.setDomainVisionStatement(context.getUpstreamContext().getDomainVisionStatement());
		return provider;
	}

	private EndpointClient createClient(DownstreamContext downstreamContext) {
		EndpointClient client = new EndpointClient();
		client.setName(mdslEncoder.encodeName(downstreamContext.getDownstreamName() + CLIENT_NAME_EXTENSION));
		List<String> endpoints = downstreamContext.getConsumedAggregates().stream().map(agg -> agg.getName()).collect(Collectors.toList());
		for (String offer : endpoints) {
			client.addConsumedOffer(offer);
		}
		if (!downstreamContext.getDownstreamRoles().isEmpty()) {
			String roles = String.join(" and ",
					downstreamContext.getDownstreamRoles().stream().map(ur -> ur.getName() + " (" + ur.getLiteral() + ")").collect(Collectors.toList()));
			client.addComment("Generated from DDD downstream Bounded Context '" + downstreamContext.getDownstreamName() + "' implementing " + roles + ".");
		}
		if (downstreamContext.getDomainVisionStatement() != null && !"".equals(downstreamContext.getDomainVisionStatement()))
			client.setDomainVisionStatement(downstreamContext.getDomainVisionStatement());
		return client;
	}

	private Map<String, UpstreamAPIContext> collectUpstreamContexts() {
		Map<String, UpstreamAPIContext> upstreamContextMap = Maps.newHashMap();
		List<UpstreamDownstreamRelationship> upstreamDownstreamRelationships = Lists.newLinkedList();
		if (model.getMap() != null)
			upstreamDownstreamRelationships = model.getMap().getRelationships().stream().filter(rel -> rel instanceof UpstreamDownstreamRelationship)
					.map(rel -> (UpstreamDownstreamRelationship) rel).collect(Collectors.toList());
		for (UpstreamDownstreamRelationship relationship : upstreamDownstreamRelationships) {
			if (relationship.getUpstreamExposedAggregates().isEmpty())
				continue;

			String upstreamAPIName = relationship.getUpstream().getName() + API_NAME_EXTENSION;
			UpstreamAPIContext context = null;
			if (upstreamContextMap.containsKey(upstreamAPIName)) {
				context = upstreamContextMap.get(upstreamAPIName);
			} else {
				context = new UpstreamAPIContext();
				context.setApiName(mdslEncoder.encodeName(upstreamAPIName));
				context.setUpstreamContext(relationship.getUpstream());
				upstreamContextMap.put(upstreamAPIName, context);
			}
			context.getUpstreamRoles().addAll(relationship.getUpstreamRoles());
			for (Aggregate exposedAggregate : relationship.getUpstreamExposedAggregates()) {
				if (!context.getExposedAggregates().stream().map(agg -> agg.getName()).collect(Collectors.toList()).contains(exposedAggregate.getName()))
					context.getExposedAggregates().add(exposedAggregate);
			}
			if (relationship.getUpstream().getApplication() != null)
				context.setApplicationLayer(relationship.getUpstream().getApplication());
			context.addDownstreamContext4Relationship(relationship);
			if (relationship.getImplementationTechnology() != null && !"".equals(relationship.getImplementationTechnology()))
				context.getImplementationTechnologies().add(relationship.getImplementationTechnology());
		}
		// add all contexts that are not upstream in an upstream-downstream relationship
		for (BoundedContext bc : model.getBoundedContexts()) {
			String apiName = bc.getName() + API_NAME_EXTENSION;
			if (upstreamContextMap.containsKey(apiName) || (bc.getAggregates().isEmpty() && bc.getApplication() == null))
				continue;

			UpstreamAPIContext context = new UpstreamAPIContext();
			context.setApiName(apiName);
			context.setUpstreamContext(bc);
			context.getExposedAggregates().addAll(bc.getAggregates());
			context.setApplicationLayer(bc.getApplication());
			upstreamContextMap.put(apiName, context);
		}
		return upstreamContextMap;
	}

	private void checkPreconditions() {
		Map<String, UpstreamAPIContext> upstreamContexts = collectUpstreamContexts();

		List<Aggregate> exposedAggregates = Lists.newArrayList();
		List<Application> applications = Lists.newArrayList();
		for (UpstreamAPIContext context : upstreamContexts.values()) {
			exposedAggregates.addAll(context.getExposedAggregates());
			if (context.getApplicationLayer() != null)
				applications.add(context.getApplicationLayer());
		}

		if (exposedAggregates.isEmpty() && applications.isEmpty())
			throw new GeneratorInputException(
					"None of your upstream-downstream relationships exposes any Aggregates or application layers. Therefore there is nothing to generate. Use the 'exposedAggregates' attribute on your upstream-downstream relationships to specify which Aggregates are exposed by the upstream or model an 'Application' in your upstream.");

		boolean atLeastOneAggregateWithAnOperation = false;
		for (Aggregate exposedAggregate : exposedAggregates) {
			Optional<DomainObject> aggregateRoot = exposedAggregate.getDomainObjects().stream().filter(o -> o instanceof DomainObject).map(o -> (DomainObject) o)
					.filter(o -> o.isAggregateRoot()).findFirst();
			if (aggregateRoot.isPresent() && !aggregateRoot.get().getOperations().isEmpty()) {
				atLeastOneAggregateWithAnOperation = true;
				break;
			}
			List<ServiceOperation> serviceOperations = exposedAggregate.getServices().stream().flatMap(s -> s.getOperations().stream()).collect(Collectors.toList());
			if (!serviceOperations.isEmpty()) {
				atLeastOneAggregateWithAnOperation = true;
				break;
			}
		}
		for (Application application : applications) {
			if (!application.getCommands().isEmpty()) {
				atLeastOneAggregateWithAnOperation = true;
				break;
			}
			List<ServiceOperation> serviceOperations = application.getServices().stream().flatMap(s -> s.getOperations().stream()).collect(Collectors.toList());
			if (!serviceOperations.isEmpty()) {
				atLeastOneAggregateWithAnOperation = true;
				break;
			}
		}
		if (!atLeastOneAggregateWithAnOperation)
			throw new GeneratorInputException(
					"None of your exposed Aggregates contains either Service or 'Aggregate Root' operations/methods. Therefore there is nothing to generate. Add at least one operation/method to the 'Aggregate Root' or to a Service in one of your exposed Aggregates to get a result.");
	}

}

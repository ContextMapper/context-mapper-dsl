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
package org.contextmapper.dsl.generator.mdsl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.contextmapper.dsl.generator.mdsl.model.DataType;
import org.contextmapper.dsl.generator.mdsl.model.EndpointClient;
import org.contextmapper.dsl.generator.mdsl.model.EndpointContract;
import org.contextmapper.dsl.generator.mdsl.model.EndpointOffer;
import org.contextmapper.dsl.generator.mdsl.model.EndpointOperation;
import org.contextmapper.dsl.generator.mdsl.model.EndpointProvider;
import org.contextmapper.dsl.generator.mdsl.model.ServiceSpecification;
import org.contextmapper.tactic.dsl.tacticdsl.ComplexType;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObjectOperation;
import org.contextmapper.tactic.dsl.tacticdsl.Parameter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MDSLModelCreator {

	private ContextMap contextMap;
	private Map<String, DataType> dataTypeMapping;
	private int initialPort = 80;

	public MDSLModelCreator(ContextMap contextMap) {
		this.contextMap = contextMap;
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
		specification.setName(apiName);
		dataTypeMapping = Maps.newTreeMap();
		for (Aggregate aggregate : context.getExposedAggregates()) {
			specification.addEndpoint(createEndpoint(aggregate, specification));
		}
		for (DataType dataType : dataTypeMapping.values()) {
			specification.addDataType(dataType);
		}
		specification.addProvider(createProvider(context.getUpstreamContext().getName(), context.getJoinedImplementationTechnologies(), specification.getEndpoints()));
		for (String downstreamName : context.getConsumedAggregatesByDownstreamContext().keySet()) {
			specification.addClient(createClient(downstreamName,
					context.getConsumedAggregatesByDownstreamContext().get(downstreamName).stream().map(agg -> agg.getName()).collect(Collectors.toList())));
		}
		return specification;
	}

	private EndpointContract createEndpoint(Aggregate aggregate, ServiceSpecification specification) {
		EndpointContract endpoint = new EndpointContract();
		endpoint.setName(aggregate.getName());
		Optional<DomainObject> aggregateRoot = aggregate.getDomainObjects().stream().filter(o -> o instanceof DomainObject).map(o -> (DomainObject) o).findFirst();
		if (aggregateRoot.isPresent()) {
			for (DomainObjectOperation operation : aggregateRoot.get().getOperations()) {
				endpoint.addOperation(createOperation(operation, specification));
			}
		}
		return endpoint;
	}

	private EndpointOperation createOperation(DomainObjectOperation domainObjectOperation, ServiceSpecification specification) {
		EndpointOperation operation = new EndpointOperation();
		operation.setName(domainObjectOperation.getName());

		if (domainObjectOperation.getParameters().size() == 1) {
			Parameter parameter = domainObjectOperation.getParameters().get(0);
			operation.setExpectingPayload(createOrGetDataType(getSculptorComplexTypeName(parameter.getParameterType())));
		} else if (domainObjectOperation.getParameters().size() > 1) {
			List<String> allParamTypes = Lists.newArrayList();
			for (Parameter parameter : domainObjectOperation.getParameters()) {
				String type = getSculptorComplexTypeName(parameter.getParameterType());
				allParamTypes.add(type);
				createOrGetDataType(type);
			}
			DataType paramTuple = createOrGetDataType(domainObjectOperation.getName() + "Parameter");
			paramTuple.setTupleList(allParamTypes);
			operation.setExpectingPayload(paramTuple);
		}
		if (domainObjectOperation.getReturnType() != null && !"".equals(domainObjectOperation.getReturnType().getType())
				&& !"void".equals(domainObjectOperation.getReturnType().getType())) {
			String type = getSculptorComplexTypeName(domainObjectOperation.getReturnType());
			DataType returnType = createOrGetDataType(type);
			operation.setDeliveringPayload(returnType);
		}
		return operation;
	}

	private DataType createOrGetDataType(String dataTypeName) {
		if (dataTypeMapping.containsKey(dataTypeName)) {
			return dataTypeMapping.get(dataTypeName);
		} else {
			DataType dataType = new DataType();
			dataType.setName(dataTypeName);
			dataTypeMapping.put(dataTypeName, dataType);
			return dataType;
		}
	}

	private String getSculptorComplexTypeName(ComplexType type) {
		if (type.getType() != null) {
			return type.getType();
		} else if (type.getDomainObjectType() != null) {
			return type.getDomainObjectType().getName();
		}
		return "Object";
	}

	private EndpointProvider createProvider(String providerName, String implementationTechnology, List<EndpointContract> endpointContracts) {
		EndpointProvider provider = new EndpointProvider();
		provider.setName(providerName);
		for (EndpointContract contract : endpointContracts) {
			EndpointOffer offer = new EndpointOffer();
			offer.setOfferedEndpoint(contract);
			offer.setLocation("http://localhost:" + (initialPort++) + "80");
			offer.setProtocol(implementationTechnology);
			provider.addEndpointOffer(offer);
		}
		return provider;
	}

	private EndpointClient createClient(String clientName, List<String> endpointNames) {
		EndpointClient client = new EndpointClient();
		client.setName(clientName);
		for (String offer : endpointNames) {
			client.addConsumedOffer(offer);
		}
		return client;
	}

	private Map<String, UpstreamAPIContext> collectUpstreamContexts() {
		Map<String, UpstreamAPIContext> upstreamContextMap = Maps.newHashMap();
		List<UpstreamDownstreamRelationship> upstreamDownstreamRelationships = contextMap.getRelationships().stream().filter(rel -> rel instanceof UpstreamDownstreamRelationship)
				.map(rel -> (UpstreamDownstreamRelationship) rel).collect(Collectors.toList());
		for (UpstreamDownstreamRelationship relationship : upstreamDownstreamRelationships) {
			if (relationship.getUpstreamExposedAggregates().isEmpty())
				continue;

			String upstreamAPIName = relationship.getUpstream().getName() + "API";
			UpstreamAPIContext context = null;
			if (upstreamContextMap.containsKey(upstreamAPIName)) {
				context = upstreamContextMap.get(upstreamAPIName);
			} else {
				context = new UpstreamAPIContext();
				context.setApiName(upstreamAPIName);
				context.setUpstreamContext(relationship.getUpstream());
				upstreamContextMap.put(upstreamAPIName, context);
			}
			for (Aggregate exposedAggregate : relationship.getUpstreamExposedAggregates()) {
				if (!context.getExposedAggregates().stream().map(agg -> agg.getName()).collect(Collectors.toList()).contains(exposedAggregate.getName()))
					context.getExposedAggregates().add(exposedAggregate);
			}
			if (!context.getDownstreamContexts().stream().map(bc -> bc.getName()).collect(Collectors.toList()).contains(relationship.getDownstream().getName()))
				context.getDownstreamContexts().add(relationship.getDownstream());
			context.addDownstreamConsumations(relationship.getDownstream().getName(), relationship.getUpstreamExposedAggregates());
			if (relationship.getImplementationTechnology() != null && !"".equals(relationship.getImplementationTechnology()))
				context.getImplementationTechnologies().add(relationship.getImplementationTechnology());
		}
		return upstreamContextMap;
	}

	private void checkPreconditions() {
		List<UpstreamDownstreamRelationship> upstreamDownstreamRelationships = contextMap.getRelationships().stream().filter(rel -> rel instanceof UpstreamDownstreamRelationship)
				.map(rel -> (UpstreamDownstreamRelationship) rel).collect(Collectors.toList());

		if (upstreamDownstreamRelationships.isEmpty())
			throw new GeneratorInputException("Your model does not contain any upstream-downstream relationships. Therefore we have nothing to generate...");

		List<Aggregate> exposedAggregates = Lists.newArrayList();
		for (UpstreamDownstreamRelationship rel : upstreamDownstreamRelationships) {
			exposedAggregates.addAll(rel.getUpstreamExposedAggregates());
		}

		if (exposedAggregates.isEmpty())
			throw new GeneratorInputException("None of your upstream-downstream relationships exposes any aggregates. Therefore we have nothing to generate...");

		List<DomainObject> aggregateRoots = Lists.newArrayList();
		for (Aggregate exposedAggregate : exposedAggregates) {
			Optional<DomainObject> aggregateRoot = exposedAggregate.getDomainObjects().stream().filter(o -> o instanceof DomainObject).map(o -> (DomainObject) o)
					.filter(o -> o.isAggregateRoot()).findFirst();
			if (aggregateRoot.isPresent())
				aggregateRoots.add(aggregateRoot.get());
		}
		if (aggregateRoots.isEmpty())
			throw new GeneratorInputException("None of your exposed aggregates contains an aggregate root. Therefore we have nothing to generate...");

		List<DomainObjectOperation> operations = Lists.newArrayList();
		for (DomainObject obj : aggregateRoots) {
			operations.addAll(obj.getOperations());
		}
		if (operations.isEmpty())
			throw new GeneratorInputException("None of your aggregate roots contains an operation/method. Therefore we have nothing to generate...");
	}

	private class UpstreamAPIContext {
		private String apiName;
		private BoundedContext upstreamContext;
		private List<Aggregate> exposedAggregates = Lists.newArrayList();
		private List<BoundedContext> downstreamContexts = Lists.newArrayList();
		private List<String> implementationTechnologies = Lists.newArrayList();
		private Map<String, List<Aggregate>> consumedAggregatesByDownstreamContext = Maps.newHashMap();

		public void setApiName(String apiName) {
			this.apiName = apiName;
		}

		public String getApiName() {
			return apiName;
		}

		public void setUpstreamContext(BoundedContext upstreamContext) {
			this.upstreamContext = upstreamContext;
		}

		public BoundedContext getUpstreamContext() {
			return upstreamContext;
		}

		public List<Aggregate> getExposedAggregates() {
			return exposedAggregates;
		}

		public List<BoundedContext> getDownstreamContexts() {
			return downstreamContexts;
		}

		public List<String> getImplementationTechnologies() {
			return implementationTechnologies;
		}

		public String getJoinedImplementationTechnologies() {
			return String.join(", ", new HashSet<>(implementationTechnologies));
		}

		public void addDownstreamConsumations(String downstreamName, List<Aggregate> consumedAggregates) {
			if (!this.consumedAggregatesByDownstreamContext.containsKey(downstreamName)) {
				this.consumedAggregatesByDownstreamContext.put(downstreamName, Lists.newArrayList());
			}
			for (Aggregate aggregate : consumedAggregates) {
				if (!this.consumedAggregatesByDownstreamContext.get(downstreamName).stream().map(agg -> agg.getName()).collect(Collectors.toList()).contains(aggregate.getName()))
					this.consumedAggregatesByDownstreamContext.get(downstreamName).add(aggregate);
			}
		}

		public Map<String, List<Aggregate>> getConsumedAggregatesByDownstreamContext() {
			return consumedAggregatesByDownstreamContext;
		}

	}

}

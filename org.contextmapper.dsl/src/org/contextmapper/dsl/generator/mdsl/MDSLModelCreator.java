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
import java.util.Stack;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.contextmapper.dsl.generator.mdsl.model.DataType;
import org.contextmapper.dsl.generator.mdsl.model.DataTypeAttribute;
import org.contextmapper.dsl.generator.mdsl.model.EndpointClient;
import org.contextmapper.dsl.generator.mdsl.model.EndpointContract;
import org.contextmapper.dsl.generator.mdsl.model.EndpointOffer;
import org.contextmapper.dsl.generator.mdsl.model.EndpointOperation;
import org.contextmapper.dsl.generator.mdsl.model.EndpointProvider;
import org.contextmapper.dsl.generator.mdsl.model.ServiceSpecification;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.CollectionType;
import org.contextmapper.tactic.dsl.tacticdsl.ComplexType;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObjectOperation;
import org.contextmapper.tactic.dsl.tacticdsl.Parameter;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.Service;
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MDSLModelCreator {

	private static final String AGGREGATE_NAME_EXTENSION = "Aggregate";
	private static final String PARAMETER_NAME_EXTENSION = "Parameter";
	private static final String API_NAME_EXTENSION = "API";
	private static final String PROVIDER_NAME_EXTENSION = "Provider";
	private static final String CLIENT_NAME_EXTENSION = "Client";
	private static final String BASE_TYPE = "Object";
	private static final String CML_VOID_RETURN_TYPE = "void";
	private static final String MDSL_VOID_RETURN_TYPE = "V<void>";
	private static final String ENDPOINT_LOCATION = "http://localhost:";
	private static final String PROTOCOL_STRING_IF_NOT_DEFINED = "tbd";
	private static final String PROTOCOL_NOT_DEFINED_COMMENT = "The protocol is generated if you specify the implementation technology in CML";

	private ContextMap contextMap;
	private Map<String, DataType> dataTypeMapping;
	private int initialPort = 8000;
	private Stack<String> recursiveAttributeResolutionStack;

	public MDSLModelCreator(ContextMap contextMap) {
		this.contextMap = contextMap;
		this.recursiveAttributeResolutionStack = new Stack<>();
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
		specification.addProvider(
				createProvider(context.getUpstreamContext().getName() + PROVIDER_NAME_EXTENSION, context.getJoinedImplementationTechnologies(), specification.getEndpoints()));
		for (String downstreamName : context.getConsumedAggregatesByDownstreamContext().keySet()) {
			specification.addClient(createClient(downstreamName + CLIENT_NAME_EXTENSION, context.getConsumedAggregatesByDownstreamContext().get(downstreamName).stream()
					.map(agg -> agg.getName() + AGGREGATE_NAME_EXTENSION).collect(Collectors.toList())));
		}
		return specification;
	}

	private EndpointContract createEndpoint(Aggregate aggregate, ServiceSpecification specification) {
		EndpointContract endpoint = new EndpointContract();
		String endpointName = aggregate.getName().endsWith(AGGREGATE_NAME_EXTENSION) ? aggregate.getName() : aggregate.getName() + AGGREGATE_NAME_EXTENSION;
		endpoint.setName(endpointName);
		Optional<DomainObject> aggregateRoot = aggregate.getDomainObjects().stream().filter(o -> o instanceof DomainObject).map(o -> (DomainObject) o).findFirst();
		if (aggregateRoot.isPresent()) {
			for (DomainObjectOperation operation : aggregateRoot.get().getOperations()) {
				endpoint.addOperation(createOperation(operation, specification));
			}
		}
		List<ServiceOperation> serviceOperations = aggregate.getServices().stream().flatMap(s -> s.getOperations().stream()).collect(Collectors.toList());
		for (ServiceOperation serviceOperation : serviceOperations) {
			endpoint.addOperation(createOperation(serviceOperation, specification));
		}
		return endpoint;
	}

	private EndpointOperation createOperation(DomainObjectOperation domainObjectOperation, ServiceSpecification specification) {
		return createOperation(domainObjectOperation.getName(), domainObjectOperation.getParameters(), domainObjectOperation.getReturnType(), specification);
	}

	private EndpointOperation createOperation(ServiceOperation serviceOperation, ServiceSpecification specification) {
		return createOperation(serviceOperation.getName(), serviceOperation.getParameters(), serviceOperation.getReturnType(), specification);
	}

	private EndpointOperation createOperation(String operationName, List<Parameter> parameters, ComplexType returnType, ServiceSpecification specification) {
		EndpointOperation operation = new EndpointOperation();
		operation.setName(operationName);

		if (parameters.isEmpty()) {
			operation.setExpectingPayload(createVoidReturnType());
		} else if (parameters.size() == 1) {
			Parameter parameter = parameters.get(0);
			operation.setExpectingPayload(getDataType4ComplexType(parameter.getParameterType()));
			operation.setExpectingCollection(parameter.getParameterType().getCollectionType() != CollectionType.NONE);
		} else {
			operation.setExpectingPayload(constructDataType4ParameterList(operationName, parameters));
		}
		if (returnType != null && !"".equals(returnType.getType()) && !CML_VOID_RETURN_TYPE.equals(returnType.getType())) {
			operation.setDeliveringPayload(getDataType4ComplexType(returnType));
			operation.setDeliveringCollection(returnType.getCollectionType() != CollectionType.NONE);
		}
		return operation;
	}

	private DataType createVoidReturnType() {
		DataType voidType = new DataType();
		voidType.setIsPrimitiveType(true);
		voidType.setName(MDSL_VOID_RETURN_TYPE);
		return voidType;
	}

	private DataType constructDataType4ParameterList(String methodName, List<Parameter> parameters) {
		String dataTypeName = methodName + PARAMETER_NAME_EXTENSION;
		if (dataTypeMapping.containsKey(dataTypeName)) {
			return dataTypeMapping.get(dataTypeName);
		} else {
			DataType dataType = new DataType();
			dataType.setName(dataTypeName);
			List<DataTypeAttribute> attributes = Lists.newArrayList();
			for (Parameter parameter : parameters) {
				ComplexType type = parameter.getParameterType();
				if (type.getDomainObjectType() != null) {
					attributes.add(getDataTypeAttribute4DomainObject(dataType, parameter.getName(), type.getDomainObjectType(), type.getCollectionType() != CollectionType.NONE));
				} else {
					attributes.add(createSimpleDataTypeAttributeWithoutChildren(parameter.getName(), mapAbstractDataType(type.getType()),
							type.getCollectionType() != CollectionType.NONE));
				}
			}
			dataType.addAttributes(attributes);
			dataTypeMapping.put(dataTypeName, dataType);
			return dataType;
		}
	}

	private DataType getDataType4ComplexType(ComplexType type) {
		// get data type name
		String dataTypeName = type.getType();
		if (type.getDomainObjectType() != null) {
			dataTypeName = type.getDomainObjectType().getName();
		}

		// check if its a primitive type and return it if its primitive
		String primitiveType = getMDSLPrimitiveType(dataTypeName);
		if (!BASE_TYPE.equals(primitiveType)) {
			DataType primitiveDataType = new DataType();
			primitiveDataType.setIsPrimitiveType(true);
			primitiveDataType.setName(primitiveType);
			return primitiveDataType;
		}

		// create complex data type
		if (dataTypeMapping.containsKey(dataTypeName)) {
			return dataTypeMapping.get(dataTypeName);
		} else {
			DataType dataType = new DataType();
			dataType.setName(dataTypeName);
			dataTypeMapping.put(dataTypeName, dataType);
			if (type.getDomainObjectType() != null && type.getDomainObjectType() instanceof DomainObject) {
				DomainObject object = (DomainObject) type.getDomainObjectType();
				dataType.addAttributes(getMDSLAttributesForAttributeList(object.getAttributes()));
				List<DataTypeAttribute> refAttributes = Lists.newArrayList();
				for (Reference reference : object.getReferences()) {
					refAttributes.add(getDataTypeAttribute4DomainObject(dataType, reference.getName(), reference.getDomainObjectType(),
							reference.getCollectionType() != CollectionType.NONE));
				}
				dataType.addAttributes(refAttributes);
			}
			return dataType;
		}
	}

	private DataTypeAttribute getDataTypeAttribute4DomainObject(DataType dataType, String attributeName, SimpleDomainObject simpleDomainObject, boolean isCollection) {
		this.recursiveAttributeResolutionStack.push(simpleDomainObject.getName());
		DataTypeAttribute mdslAttribute = new DataTypeAttribute();
		mdslAttribute.setName(attributeName);
		mdslAttribute.setIsCollection(isCollection);
		if (simpleDomainObject instanceof DomainObject) {
			DomainObject object = (DomainObject) simpleDomainObject;
			mdslAttribute.addChildren(getMDSLAttributesForAttributeList(object.getAttributes()));
			List<DataTypeAttribute> refAttributes = Lists.newArrayList();
			for (Reference reference : object.getReferences()) {
				// recursive attribute resolution, if it is no cyclic reference
				if (!this.recursiveAttributeResolutionStack.contains(reference.getDomainObjectType().getName())) {
					refAttributes.add(getDataTypeAttribute4DomainObject(dataType, reference.getName(), reference.getDomainObjectType(),
							reference.getCollectionType() != CollectionType.NONE));
				} else {
					dataType.addComment("You declared a cyclic reference! We had to break the cycle at " + reference.getDomainObjectType().getName());
					refAttributes.add(createSimpleDataTypeAttributeWithoutChildren(reference.getName(), mapAbstractDataType(reference.getDomainObjectType().getName()),
							reference.getCollectionType() != CollectionType.NONE));
				}
			}
			mdslAttribute.addChildren(refAttributes);
		} else {
			mdslAttribute.setType(mapAbstractDataType(simpleDomainObject.getName()));
		}
		this.recursiveAttributeResolutionStack.pop();
		return mdslAttribute;
	}

	private List<DataTypeAttribute> getMDSLAttributesForAttributeList(List<Attribute> attributes) {
		List<DataTypeAttribute> mdslAttributes = Lists.newArrayList();
		for (Attribute attribute : attributes) {
			mdslAttributes.add(createSimpleDataTypeAttributeWithoutChildren(attribute.getName(), mapAbstractDataType(attribute.getType()),
					attribute.getCollectionType() != CollectionType.NONE));
		}
		return mdslAttributes;
	}

	private DataTypeAttribute createSimpleDataTypeAttributeWithoutChildren(String attributeName, String attributeType, boolean isCollection) {
		DataTypeAttribute attribute = new DataTypeAttribute();
		attribute.setName(attributeName);
		attribute.setType(attributeType);
		attribute.setIsCollection(isCollection);
		return attribute;
	}

	private String mapAbstractDataType(String dataTypeName) {
		String primitiveType = getMDSLPrimitiveType(dataTypeName);
		if (!BASE_TYPE.equals(primitiveType))
			return primitiveType;

		// create data type, since it's not a primitive type
		if (dataTypeMapping.containsKey(dataTypeName))
			return dataTypeMapping.get(dataTypeName).getName();
		DataType newDataType = new DataType();
		newDataType.setName(dataTypeName);
		dataTypeMapping.put(dataTypeName, newDataType);
		return dataTypeName;
	}

	private String getMDSLPrimitiveType(String dataTypeName) {
		if ("boolean".equals(dataTypeName.toLowerCase())) {
			return "V<bool>";
		} else if ("String".equals(dataTypeName)) {
			return "V<string>";
		} else if ("int".equals(dataTypeName) || "Integer".equals(dataTypeName)) {
			return "V<int>";
		} else if ("long".equals(dataTypeName.toLowerCase())) {
			return "V<long>";
		} else if ("double".equals(dataTypeName.toLowerCase())) {
			return "V<double>";
		} else if ("Blob".equals(dataTypeName)) {
			return "V<blob>";
		} else if ("Date".equals(dataTypeName)) {
			return "V<string>";
		}
		return BASE_TYPE; // default case: we have to define a data type
	}

	private EndpointProvider createProvider(String providerName, String implementationTechnology, List<EndpointContract> endpointContracts) {
		EndpointProvider provider = new EndpointProvider();
		provider.setName(providerName);
		for (EndpointContract contract : endpointContracts) {
			EndpointOffer offer = new EndpointOffer();
			offer.setOfferedEndpoint(contract);
			offer.setLocation(ENDPOINT_LOCATION + (initialPort++));
			offer.setProtocol(!"".equals(implementationTechnology) ? implementationTechnology : PROTOCOL_STRING_IF_NOT_DEFINED);
			offer.setProtocolComment(!"".equals(implementationTechnology) ? "" : PROTOCOL_NOT_DEFINED_COMMENT);
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

			String upstreamAPIName = relationship.getUpstream().getName() + API_NAME_EXTENSION;
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
			throw new GeneratorInputException(
					"Your model does not contain any upstream-downstream relationships. Therefore there is nothing to generate. Create an upstream-downstream relationship in your Context Map to get a result.");

		List<Aggregate> exposedAggregates = Lists.newArrayList();
		for (UpstreamDownstreamRelationship rel : upstreamDownstreamRelationships) {
			exposedAggregates.addAll(rel.getUpstreamExposedAggregates());
		}

		if (exposedAggregates.isEmpty())
			throw new GeneratorInputException(
					"None of your upstream-downstream relationships exposes any Aggregates. Therefore there is nothing to generate. Use the 'exposedAggregates' attribute on your upstream-downstream relationships to specify which Aggregates are exposed by the upstream.");

		List<DomainObject> aggregateRoots = Lists.newArrayList();
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
		if (!atLeastOneAggregateWithAnOperation)
			throw new GeneratorInputException(
					"None of your exposed Aggregates contains either Service or 'Aggregate Root' operations/methods. Therefore there is nothing to generate. Add at least one operation/method to the 'Aggregate Root' or to a Service in one of your exposed Aggregates to get a result.");
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

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamRole;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.contextmapper.dsl.generator.mdsl.generatorcontext.DownstreamContext;
import org.contextmapper.dsl.generator.mdsl.generatorcontext.UpstreamAPIContext;
import org.contextmapper.dsl.generator.mdsl.model.APIUsageContext;
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
import org.contextmapper.tactic.dsl.tacticdsl.CommandEvent;
import org.contextmapper.tactic.dsl.tacticdsl.ComplexType;
import org.contextmapper.tactic.dsl.tacticdsl.DomainEvent;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObjectOperation;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Enum;
import org.contextmapper.tactic.dsl.tacticdsl.EnumValue;
import org.contextmapper.tactic.dsl.tacticdsl.Parameter;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;
import org.contextmapper.tactic.dsl.tacticdsl.Visibility;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MDSLModelCreator {

	private static final String AGGREGATE_NAME_EXTENSION = "Aggregate";
	private static final String PARAMETER_NAME_EXTENSION = "Parameter";
	private static final String API_NAME_EXTENSION = "API";
	private static final String PROVIDER_NAME_EXTENSION = "Provider";
	private static final String CLIENT_NAME_EXTENSION = "Client";
	private static final String BASE_TYPE = "Object";
	private static final String MDSL_VOID_RETURN_TYPE = "V<void>";
	private static final String ENDPOINT_LOCATION = "http://localhost:";
	private static final String PROTOCOL_STRING_IF_NOT_DEFINED = "tbd";
	private static final String PROTOCOL_NOT_DEFINED_COMMENT = "The protocol is generated if you specify the implementation technology in CML";

	private static final String[] MDSL_KEYWORDS = { "API", "description", "data", "type", "P", "endpoint", "type", "exposes", "operation", "expecting", "delivering", "payload",
			"Link", "provider", "client", "consumes", "offers", "at", "location", "via", "protocol", "IPA" };

	private ContextMap contextMap;
	private Map<String, DataType> dataTypeMapping;
	private int initialPort = 8000;
	private Stack<String> recursiveAttributeResolutionStack;
	private Set<String> mdslKeywords;

	public MDSLModelCreator(ContextMap contextMap) {
		this.contextMap = contextMap;
		this.recursiveAttributeResolutionStack = new Stack<>();
		this.mdslKeywords = new HashSet<>(Arrays.asList(MDSL_KEYWORDS));
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
		specification.setName(encodeName(apiName));

		if (context.getUpstreamRoles().contains(UpstreamRole.OPEN_HOST_SERVICE) && context.getUpstreamRoles().contains(UpstreamRole.PUBLISHED_LANGUAGE)) {
			specification.setUsageContext(APIUsageContext.PUBLIC_API);
		} else if (context.getUpstreamRoles().contains(UpstreamRole.OPEN_HOST_SERVICE)) {
			specification.setUsageContext(APIUsageContext.COMMUNITY_API);
		}

		dataTypeMapping = Maps.newTreeMap();
		for (Aggregate aggregate : context.getExposedAggregates()) {
			specification.addEndpoint(createEndpoint(aggregate, specification));
		}
		for (DataType dataType : dataTypeMapping.values()) {
			specification.addDataType(dataType);
		}
		specification.addProvider(createProvider(context, specification.getEndpoints()));
		for (DownstreamContext downstreamContext : context.getDownstreamContexts()) {
			specification.addClient(createClient(downstreamContext));
		}
		return specification;
	}

	private EndpointContract createEndpoint(Aggregate aggregate, ServiceSpecification specification) {
		EndpointContract endpoint = new EndpointContract();
		String endpointName = encodeName(aggregate.getName().endsWith(AGGREGATE_NAME_EXTENSION) ? aggregate.getName() : aggregate.getName() + AGGREGATE_NAME_EXTENSION);
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

	private EndpointOperation createOperation(String operationName, List<Parameter> parameters, ComplexType returnType, ServiceSpecification specification, String docString) {
		EndpointOperation operation = new EndpointOperation();
		operation.setName(encodeName(operationName));

		if (parameters.isEmpty()) {
			operation.setExpectingPayload(createVoidReturnType());
		} else if (parameters.size() == 1) {
			Parameter parameter = parameters.get(0);
			operation.setExpectingPayload(getDataType4ComplexType(parameter.getParameterType()));
			operation.setExpectingCollection(parameter.getParameterType().getCollectionType() != CollectionType.NONE);
		} else {
			operation.setExpectingPayload(constructDataType4ParameterList(operationName, parameters));
		}
		if (returnType != null) {
			operation.setDeliveringPayload(getDataType4ComplexType(returnType));
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

	private DataType constructDataType4ParameterList(String methodName, List<Parameter> parameters) {
		String dataTypeName = encodeName(methodName + PARAMETER_NAME_EXTENSION);
		if (dataTypeMapping.containsKey(dataTypeName)) {
			return dataTypeMapping.get(dataTypeName);
		} else {
			DataType dataType = new DataType();
			dataType.setName(dataTypeName);
			List<DataTypeAttribute> attributes = Lists.newArrayList();
			for (Parameter parameter : parameters) {
				ComplexType type = parameter.getParameterType();
				if (type.getDomainObjectType() != null) {
					attributes.add(
							getDataTypeAttribute4DomainObject(dataType, parameter.getName(), type.getDomainObjectType(), type.getCollectionType() != CollectionType.NONE, false));
				} else {
					attributes.add(createSimpleDataTypeAttributeWithoutChildren(parameter.getName(), mapAbstractDataType(type.getType()),
							type.getCollectionType() != CollectionType.NONE, false));
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
		dataTypeName = encodeName(dataTypeName);
		if (dataTypeMapping.containsKey(dataTypeName)) {
			return dataTypeMapping.get(dataTypeName);
		} else {
			DataType dataType = new DataType();
			dataType.setName(dataTypeName);
			dataTypeMapping.put(dataTypeName, dataType);
			if (type.getDomainObjectType() != null && type.getDomainObjectType() instanceof DomainObject) {
				DomainObject object = (DomainObject) type.getDomainObjectType();
				dataType.addAttributes(getMDSLAttributesForAttributeList(getDomainObjectAttributes(object)));
				List<DataTypeAttribute> refAttributes = Lists.newArrayList();
				for (Reference reference : getDomainObjectReferences(object)) {
					refAttributes.add(getDataTypeAttribute4DomainObject(dataType, reference.getName(), reference.getDomainObjectType(),
							reference.getCollectionType() != CollectionType.NONE, reference.isNullable()));
				}
				dataType.addAttributes(refAttributes);
			} else if (type.getDomainObjectType() != null && type.getDomainObjectType() instanceof Enum) {
				Enum enumm = (Enum) type.getDomainObjectType();
				dataType.setIsEnumType(true);
				dataType.addAttributes(createAttributesForEnum(enumm));
			}
			return dataType;
		}
	}

	private List<DataTypeAttribute> createAttributesForEnum(Enum enumm) {
		List<DataTypeAttribute> attributes = new ArrayList<>();
		for (EnumValue value : enumm.getValues()) {
			DataTypeAttribute attribute = new DataTypeAttribute();
			attribute.setName(encodeName(value.getName()));
			attribute.setType(encodeName(enumm.getName()));
			attributes.add(attribute);
		}
		return attributes;
	}

	private DataType createEnumDataType(Enum enumm) {
		if (dataTypeMapping.containsKey(enumm.getName()))
			return dataTypeMapping.get(enumm.getName());
		DataType dataType = new DataType();
		dataType.setName(encodeName(enumm.getName()));
		dataType.setIsEnumType(true);
		dataType.addAttributes(createAttributesForEnum(enumm));
		dataTypeMapping.put(enumm.getName(), dataType);
		return dataType;
	}

	private DataTypeAttribute getDataTypeAttribute4DomainObject(DataType dataType, String attributeName, SimpleDomainObject simpleDomainObject, boolean isCollection,
			boolean isNullable) {
		this.recursiveAttributeResolutionStack.push(simpleDomainObject.getName());
		DataTypeAttribute mdslAttribute = new DataTypeAttribute();
		mdslAttribute.setName(encodeName(attributeName));
		mdslAttribute.setIsCollection(isCollection);
		mdslAttribute.setIsNullable(isNullable);
		if (simpleDomainObject instanceof DomainObject) {
			DomainObject object = (DomainObject) simpleDomainObject;
			mdslAttribute.addChildren(getMDSLAttributesForAttributeList(getDomainObjectAttributes(object)));
			List<DataTypeAttribute> refAttributes = Lists.newArrayList();
			for (Reference reference : getDomainObjectReferences(object)) {
				// recursive attribute resolution, if it is no cyclic reference
				if (!this.recursiveAttributeResolutionStack.contains(reference.getDomainObjectType().getName())) {
					refAttributes.add(getDataTypeAttribute4DomainObject(dataType, reference.getName(), reference.getDomainObjectType(),
							reference.getCollectionType() != CollectionType.NONE, reference.isNullable()));
				} else {
					dataType.addComment("You declared a cyclic reference! We had to break the cycle at " + reference.getDomainObjectType().getName());
					refAttributes.add(createSimpleDataTypeAttributeWithoutChildren(reference.getName(), mapAbstractDataType(reference.getDomainObjectType().getName()),
							reference.getCollectionType() != CollectionType.NONE, reference.isNullable()));
				}
			}
			mdslAttribute.addChildren(refAttributes);
		} else if (simpleDomainObject instanceof Enum) {
			mdslAttribute.setType(createEnumDataType((Enum) simpleDomainObject).getName());
		} else {
			mdslAttribute.setType(mapAbstractDataType(simpleDomainObject.getName()));
		}
		this.recursiveAttributeResolutionStack.pop();
		return mdslAttribute;
	}

	private List<Attribute> getDomainObjectAttributes(DomainObject domainObject) {
		List<Attribute> attributes = new ArrayList<>();

		DomainObject extendsType = getExtendsType(domainObject);
		while (extendsType != null) {
			attributes.addAll(extendsType.getAttributes());
			extendsType = getExtendsType(extendsType);
		}
		attributes.addAll(domainObject.getAttributes());

		return attributes;
	}

	private List<Reference> getDomainObjectReferences(DomainObject domainObject) {
		List<Reference> references = new ArrayList<>();

		DomainObject extendsType = getExtendsType(domainObject);
		while (extendsType != null) {
			references.addAll(extendsType.getReferences());
			extendsType = getExtendsType(extendsType);
		}
		references.addAll(domainObject.getReferences());

		return references;
	}

	private DomainObject getExtendsType(DomainObject domainObject) {
		if (domainObject instanceof Entity) {
			return ((Entity) domainObject).getExtends();
		} else if (domainObject instanceof CommandEvent) {
			return ((CommandEvent) domainObject).getExtends();
		} else if (domainObject instanceof DomainEvent) {
			return ((DomainEvent) domainObject).getExtends();
		} else if (domainObject instanceof ValueObject) {
			return ((ValueObject) domainObject).getExtends();
		} else {
			return null;
		}
	}

	private List<DataTypeAttribute> getMDSLAttributesForAttributeList(List<Attribute> attributes) {
		List<DataTypeAttribute> mdslAttributes = Lists.newArrayList();
		for (Attribute attribute : attributes) {
			mdslAttributes.add(createSimpleDataTypeAttributeWithoutChildren(attribute.getName(), mapAbstractDataType(attribute.getType()),
					attribute.getCollectionType() != CollectionType.NONE, attribute.isNullable()));
		}
		return mdslAttributes;
	}

	private DataTypeAttribute createSimpleDataTypeAttributeWithoutChildren(String attributeName, String attributeType, boolean isCollection, boolean isNullable) {
		DataTypeAttribute attribute = new DataTypeAttribute();
		attribute.setName(encodeName(attributeName));
		attribute.setType(encodeName(attributeType));
		attribute.setIsCollection(isCollection);
		attribute.setIsNullable(isNullable);
		return attribute;
	}

	private String mapAbstractDataType(String dataTypeName) {
		String primitiveType = getMDSLPrimitiveType(dataTypeName);
		if (!BASE_TYPE.equals(primitiveType))
			return primitiveType;

		// create data type, since it's not a primitive type
		String encodedDataTypeName = encodeName(dataTypeName);
		if (dataTypeMapping.containsKey(encodedDataTypeName))
			return dataTypeMapping.get(encodedDataTypeName).getName();
		DataType newDataType = new DataType();
		newDataType.setName(encodeName(encodedDataTypeName));
		dataTypeMapping.put(encodedDataTypeName, newDataType);
		return encodedDataTypeName;
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

	private EndpointProvider createProvider(UpstreamAPIContext context, List<EndpointContract> endpointContracts) {
		EndpointProvider provider = new EndpointProvider();
		String implementationTechnology = context.getJoinedImplementationTechnologies();
		provider.setName(encodeName(context.getUpstreamContext().getName() + PROVIDER_NAME_EXTENSION));
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
		client.setName(encodeName(downstreamContext.getDownstreamName() + CLIENT_NAME_EXTENSION));
		List<String> endpoints = downstreamContext.getConsumedAggregates().stream().map(agg -> agg.getName() + AGGREGATE_NAME_EXTENSION).collect(Collectors.toList());
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

	private String encodeName(String name) {
		if (this.mdslKeywords.contains(name))
			return "^" + name;
		return name;
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
				context.setApiName(encodeName(upstreamAPIName));
				context.setUpstreamContext(relationship.getUpstream());
				upstreamContextMap.put(upstreamAPIName, context);
			}
			context.getUpstreamRoles().addAll(relationship.getUpstreamRoles());
			// context.getDownstreamRoles().addAll(relationship.getDownstreamRoles());
			for (Aggregate exposedAggregate : relationship.getUpstreamExposedAggregates()) {
				if (!context.getExposedAggregates().stream().map(agg -> agg.getName()).collect(Collectors.toList()).contains(exposedAggregate.getName()))
					context.getExposedAggregates().add(exposedAggregate);
			}
			context.addDownstreamContext4Relationship(relationship);
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

}

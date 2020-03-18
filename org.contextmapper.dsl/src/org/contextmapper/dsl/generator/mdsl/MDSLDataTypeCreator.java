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
package org.contextmapper.dsl.generator.mdsl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.contextmapper.dsl.generator.mdsl.model.DataType;
import org.contextmapper.dsl.generator.mdsl.model.DataTypeAttribute;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.CollectionType;
import org.contextmapper.tactic.dsl.tacticdsl.CommandEvent;
import org.contextmapper.tactic.dsl.tacticdsl.ComplexType;
import org.contextmapper.tactic.dsl.tacticdsl.DomainEvent;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Enum;
import org.contextmapper.tactic.dsl.tacticdsl.EnumValue;
import org.contextmapper.tactic.dsl.tacticdsl.Parameter;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MDSLDataTypeCreator {

	private static final String BASE_TYPE = "Object";
	private static final String PARAMETER_NAME_EXTENSION = "Parameter";

	private Map<String, DataType> dataTypeMapping;
	private MDSLNameEncoder mdslNameEncoder;

	public MDSLDataTypeCreator() {
		dataTypeMapping = Maps.newTreeMap();
		mdslNameEncoder = new MDSLNameEncoder();
	}

	public DataType createMDSLDataType(ComplexType type) {
		String dataTypeName = getDataTypeName(type);
		String mdslTypeName = getMDSLDataTypeName(dataTypeName);
		if (isPrimitiveType(mdslTypeName)) {
			return createPrimitiveDataType(mdslTypeName);
		} else {
			return createComplexType(mdslNameEncoder.encodeName(dataTypeName), type);
		}
	}

	public DataType createMDSLDataType4ParameterList(String methodName, List<Parameter> parameters) {
		String dataTypeName = mdslNameEncoder.encodeName(methodName + PARAMETER_NAME_EXTENSION);
		if (dataTypeMapping.containsKey(dataTypeName))
			return dataTypeMapping.get(dataTypeName);

		DataType dataType = new DataType();
		dataType.setName(dataTypeName);
		dataType.addAttributes(createAttributes4ParameterList(parameters));
		dataTypeMapping.put(dataTypeName, dataType);
		return dataType;
	}

	public Collection<DataType> getAllDataTypes() {
		return this.dataTypeMapping.values();
	}

	private DataType createPrimitiveDataType(String primitiveTypeName) {
		DataType primitiveDataType = new DataType();
		primitiveDataType.setIsPrimitiveType(true);
		primitiveDataType.setName(primitiveTypeName);
		return primitiveDataType;
	}

	private DataType createComplexType(String complexTypeName, ComplexType type) {
		// do not create new type if a type with the given name already exists
		if (dataTypeMapping.containsKey(complexTypeName) && !(dataTypeMapping.get(complexTypeName).isAbstractDataType()))
			return dataTypeMapping.get(complexTypeName);

		DataType dataType = new DataType();
		dataType.setName(complexTypeName);
		dataTypeMapping.put(complexTypeName, dataType);

		// add attributes if available
		if (type.getDomainObjectType() != null && type.getDomainObjectType() instanceof Enum) {
			dataType.setIsEnumType(true);
			dataType.addAttributes(createAttributesForEnum((Enum) type.getDomainObjectType()));
		} else if (type.getDomainObjectType() != null && type.getDomainObjectType() instanceof DomainObject) {
			DomainObject object = (DomainObject) type.getDomainObjectType();
			dataType.addAttributes(createAttributes4AttributeList(getDomainObjectAttributes(object)));
			dataType.addAttributes(createAttributes4ReferencesList(getDomainObjectReferences(object)));
		}
		return dataType;
	}

	private String getDataTypeName(ComplexType type) {
		String dataTypeName = type.getType();
		if (type.getDomainObjectType() != null) {
			dataTypeName = type.getDomainObjectType().getName();
		}
		return dataTypeName;
	}

	private boolean isPrimitiveType(String dataTypeName) {
		return !BASE_TYPE.equals(dataTypeName);
	}

	private String getMDSLDataTypeName(String dataTypeName) {
		if ("boolean".equals(dataTypeName.toLowerCase())) {
			return "D<bool>";
		} else if ("String".equals(dataTypeName)) {
			return "D<string>";
		} else if ("int".equals(dataTypeName) || "Integer".equals(dataTypeName)) {
			return "D<int>";
		} else if ("long".equals(dataTypeName.toLowerCase())) {
			return "D<long>";
		} else if ("double".equals(dataTypeName.toLowerCase())) {
			return "D<double>";
		} else if ("Blob".equals(dataTypeName)) {
			return "D<raw>";
		} else if ("Date".equals(dataTypeName)) {
			return "D<string>";
		}
		return BASE_TYPE; // default case: we have to define a data type
	}

	private List<DataTypeAttribute> createAttributesForEnum(Enum enumm) {
		List<DataTypeAttribute> attributes = new ArrayList<>();
		for (EnumValue value : enumm.getValues()) {
			DataTypeAttribute attribute = new DataTypeAttribute();
			attribute.setName(mdslNameEncoder.encodeName(value.getName()));
			attribute.setType(mdslNameEncoder.encodeName(enumm.getName()));
			attributes.add(attribute);
		}
		return attributes;
	}

	private List<DataTypeAttribute> createAttributes4AttributeList(List<Attribute> attributes) {
		List<DataTypeAttribute> mdslAttributes = Lists.newArrayList();
		for (Attribute attribute : attributes) {
			mdslAttributes.add(createAttribute(attribute.getName(), mapAbstractDataType(attribute.getType()), isCollection(attribute.getCollectionType()), attribute.isNullable()));
		}
		return mdslAttributes;
	}

	private List<DataTypeAttribute> createAttributes4ReferencesList(List<Reference> references) {
		List<DataTypeAttribute> mdslAttributes = Lists.newArrayList();
		for (Reference reference : references) {
			DataType referencedType = createMDSLDataType(createComplexTypeForReference(reference));
			mdslAttributes.add(createAttribute(reference.getName(), referencedType.getName(), isCollection(reference.getCollectionType()), reference.isNullable()));
		}
		return mdslAttributes;
	}

	private List<DataTypeAttribute> createAttributes4ParameterList(List<Parameter> parameters) {
		List<DataTypeAttribute> mdslAttributes = Lists.newArrayList();
		for (Parameter parameter : parameters) {
			DataType referencedType = createMDSLDataType(parameter.getParameterType());
			mdslAttributes.add(createAttribute(parameter.getName(), referencedType.getName(), isCollection(parameter.getParameterType().getCollectionType()), false));
		}
		return mdslAttributes;
	}

	private DataTypeAttribute createAttribute(String attributeName, String attributeType, boolean isCollection, boolean isNullable) {
		DataTypeAttribute attribute = new DataTypeAttribute();
		attribute.setName(mdslNameEncoder.encodeName(attributeName));
		attribute.setType(mdslNameEncoder.encodeName(attributeType));
		attribute.setIsCollection(isCollection);
		attribute.setIsNullable(isNullable);
		return attribute;
	}

	private String mapAbstractDataType(String dataTypeName) {
		String mdslTypeName = getMDSLDataTypeName(dataTypeName);
		if (isPrimitiveType(mdslTypeName))
			return mdslTypeName;

		// create data type, since it's not a primitive type
		String encodedDataTypeName = mdslNameEncoder.encodeName(dataTypeName);
		if (dataTypeMapping.containsKey(encodedDataTypeName))
			return dataTypeMapping.get(encodedDataTypeName).getName();
		DataType newDataType = new DataType();
		newDataType.setName(mdslNameEncoder.encodeName(encodedDataTypeName));
		dataTypeMapping.put(encodedDataTypeName, newDataType);
		return encodedDataTypeName;
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

	private ComplexType createComplexTypeForReference(Reference reference) {
		ComplexType type = TacticdslFactory.eINSTANCE.createComplexType();
		type.setCollectionType(reference.getCollectionType());
		type.setDomainObjectType(reference.getDomainObjectType());
		return type;
	}

	private boolean isCollection(CollectionType collectionType) {
		return collectionType != CollectionType.NONE;
	}

}

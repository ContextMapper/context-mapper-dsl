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
package org.contextmapper.dsl.generator.plantuml;

import java.util.List;

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
import org.contextmapper.tactic.dsl.tacticdsl.Event;
import org.contextmapper.tactic.dsl.tacticdsl.Parameter;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;
import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Lists;

abstract public class AbstractPlantUMLClassDiagramCreator<T extends EObject> extends AbstractPlantUMLDiagramCreator<T> {

	protected List<UMLRelationship> relationships;
	protected List<UMLRelationship> extensions;
	protected List<SimpleDomainObject> domainObjects;

	protected void printDomainObject(SimpleDomainObject domainObject, int indentation) {
		if (domainObject instanceof Enum)
			printEnum((Enum) domainObject, indentation);
		else if (domainObject instanceof Entity)
			printEntity((Entity) domainObject, indentation);
		else if (domainObject instanceof Event)
			printEvent((Event) domainObject, indentation);
		else if (domainObject instanceof ValueObject)
			printValueObject((ValueObject) domainObject, indentation);
	}

	private void printEnum(Enum theEnum, int indentation) {
		printIndentation(indentation);
		sb.append("enum").append(" ").append(theEnum.getName()).append(" {");
		linebreak();
		for (EnumValue value : theEnum.getValues()) {
			printIndentation(indentation + 1);
			sb.append(value.getName());
			linebreak();
		}
		printIndentation(indentation);
		sb.append("}");
		linebreak();
	}

	private void printEntity(Entity entity, int indentation) {
		printStereotypedClass("(E,DarkSeaGreen) Entity", entity, indentation);
	}

	private void printValueObject(ValueObject valueObject, int indentation) {
		printStereotypedClass("(V,DarkSeaGreen) Value Object", valueObject, indentation);
	}

	private void printEvent(Event event, int indentation) {
		if (event instanceof CommandEvent)
			printStereotypedClass("(C,#3bc5e9) Command", event, indentation);
		else if (event instanceof DomainEvent)
			printStereotypedClass("(E,#ff9f4b) Domain Event", event, indentation);
	}

	private void printStereotypedClass(String stereotype, DomainObject object, int indentation) {
		printIndentation(indentation);
		sb.append("class").append(" ").append(object.getName());
		if (object.isAggregateRoot())
			sb.append(" <<(A,#fffab8) Aggregate Root>> ");
		else
			sb.append(" <<" + stereotype + ">> ");
		sb.append("{");
		linebreak();
		printAttributes(object.getAttributes(), indentation + 1);
		printReferenceAttributes(object.getReferences(), indentation + 1);
		
		addReferences2List(object, object.getReferences());
		
		printDomainObjectOperations(object.getName(), object.getOperations(), indentation + 1);
		printIndentation(indentation);
		sb.append("}");
		linebreak();
		if (object instanceof Entity && ((Entity) object).getExtends() != null)
			addExtensionToList(object.getName(), ((Entity) object).getExtends());
		if (object instanceof CommandEvent && ((CommandEvent) object).getExtends() != null)
			addExtensionToList(object.getName(), ((CommandEvent) object).getExtends());
		if (object instanceof DomainEvent && ((DomainEvent) object).getExtends() != null)
			addExtensionToList(object.getName(), ((DomainEvent) object).getExtends());
		if (object instanceof ValueObject && ((ValueObject) object).getExtends() != null)
			addExtensionToList(object.getName(), ((ValueObject) object).getExtends());
	}

	private void addReferences2List(SimpleDomainObject sourceDomainObject, List<Reference> references) {
		for (Reference reference : references) {
			addDomainObjectReference2List(sourceDomainObject.getName(), reference.getDomainObjectType(), reference.getName());
		}
	}

	private void addDomainObjectReference2List(String sourceDomainObject, SimpleDomainObject targetDomainObject, String label) {
		if (this.domainObjects.contains(targetDomainObject)) {
			UMLRelationship relationship = new UMLRelationship(sourceDomainObject, targetDomainObject.getName(), label);
			if (!this.relationships.contains(relationship))
				this.relationships.add(relationship);
		}
	}

	private void addExtensionToList(String sourceDomainObject, SimpleDomainObject extendedDomainObject) {
		if (this.domainObjects.contains(extendedDomainObject)) {
			UMLRelationship relationship = new UMLRelationship(sourceDomainObject, extendedDomainObject.getName(), "");
			if (!this.extensions.contains(relationship))
				this.extensions.add(relationship);
		}
	}

	private void printAttributes(List<Attribute> attributes, int indentation) {
		for (Attribute attribute : attributes) {
			printIndentation(indentation);
			sb.append(getAttributeTypeAsString(attribute));
			sb.append(" ").append(attribute.getName());
			linebreak();
		}
	}

	private void printDomainObjectOperations(String objectName, List<DomainObjectOperation> operations, int indentation) {
		for (DomainObjectOperation operation : operations) {
			printOperation(objectName, operation.getName(), operation.getReturnType(), operation.getParameters(), indentation);
		}
	}

	private String getAttributeTypeAsString(Attribute attribute) {
		String type = attribute.getType();
		if (attribute.getCollectionType() != CollectionType.NONE)
			type = attribute.getCollectionType() + "<" + attribute.getType() + ">";
		if (attribute.isNullable())
			type = type + "[0..1]";
		return type;
	}

	private void printReferenceAttributes(List<Reference> references, int indentation) {
		for (Reference reference : references) {
			printIndentation(indentation);
			sb.append(getReferenceTypeAsString(reference));
			if (reference.isNullable())
				sb.append("[0..1]");
			sb.append(" ").append(reference.getName());
			linebreak();
		}
	}

	protected void printOperation(String objectName, String operationName, ComplexType returnType, List<Parameter> parameters, int indentation) {
		printIndentation(indentation);
		String returnTypeAsString;
		if (returnType == null)
			returnTypeAsString = "void";
		else
			returnTypeAsString = getComplexMethodTypeAsString(returnType, objectName, operationName);
		sb.append(returnTypeAsString).append(" ").append(operationName).append("(");
		List<String> parameterStrings = Lists.newArrayList();
		for (Parameter parameter : parameters) {
			String parameterType = getComplexMethodTypeAsString(parameter.getParameterType(), objectName, operationName);
			parameterStrings.add(parameterType + " " + parameter.getName());
		}
		if (!parameterStrings.isEmpty())
			sb.append(String.join(", ", parameterStrings));
		sb.append(")");
		linebreak();
	}

	private String getComplexMethodTypeAsString(ComplexType type, String containingObjectName4References, String methodName) {
		String genericType = "Object";
		if (type.getDomainObjectType() != null) {
			genericType = type.getDomainObjectType().getName();
			addDomainObjectReference2List(containingObjectName4References, type.getDomainObjectType(), methodName);
		} else {
			genericType = type.getType();
		}
		if (type.getCollectionType() != CollectionType.NONE) {
			return type.getCollectionType().getName() + "<" + genericType + ">";
		}
		return genericType;
	}

	private String getReferenceTypeAsString(Reference reference) {
		if (reference.getCollectionType() != CollectionType.NONE)
			return reference.getCollectionType().getName() + "<" + reference.getDomainObjectType().getName() + ">";
		return reference.getDomainObjectType().getName();
	}

	protected void printReferences(int indentation) {
		for (UMLRelationship reference : relationships) {
			printIndentation(indentation);
			sb.append(reference.getSource()).append(" --> ").append(reference.getTarget());
			if (!"".equals(reference.getLabel()))
				sb.append(" : ").append(reference.getLabel());
			linebreak();
		}
		for (UMLRelationship extension : extensions) {
			printIndentation(indentation);
			sb.append(extension.getSource()).append(" --|> ").append(extension.getTarget());
			linebreak();
		}
	}

	protected void printIndentation(int amount) {
		for (int i = 0; i < amount; i++) {
			sb.append("\t");
		}
	}
}

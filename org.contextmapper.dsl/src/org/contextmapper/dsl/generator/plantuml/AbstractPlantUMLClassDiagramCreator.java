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
package org.contextmapper.dsl.generator.plantuml;

import java.util.List;
import java.util.Map;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.tactic.dsl.tacticdsl.Association;
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
import org.contextmapper.tactic.dsl.tacticdsl.Service;
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;
import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Lists;

abstract public class AbstractPlantUMLClassDiagramCreator<T extends EObject> extends AbstractPlantUMLDiagramCreator<T> {

	protected Map<AssociationLink, AssociationInfo> associationInfos;
	protected List<UMLRelationship> extensions;
	protected List<SimpleDomainObject> domainObjects;

	protected void printDomainObject(SimpleDomainObject domainObject, int indentation) {
		printDomainObject(null, domainObject, indentation);
	}

	protected void printDomainObject(Aggregate aggregate, SimpleDomainObject domainObject, int indentation) {
		if (domainObject instanceof Enum)
			printEnum((Enum) domainObject, indentation);
		else if (domainObject instanceof Entity)
			printEntity(aggregate, (Entity) domainObject, indentation);
		else if (domainObject instanceof Event)
			printEvent(aggregate, (Event) domainObject, indentation);
		else if (domainObject instanceof ValueObject)
			printValueObject(aggregate, (ValueObject) domainObject, indentation);
	}

	protected void printModule(SculptorModule module) {
		sb.append("package ");
		if (module.getBasePackage() != null && !"".equals(module.getBasePackage()))
			sb.append(module.getBasePackage()).append(".").append(module.getName());
		else
			sb.append(module.getName());
		sb.append(" {");
		linebreak();
		for (Aggregate aggregate : module.getAggregates()) {
			printAggregate(aggregate, 1);
		}
		for (SimpleDomainObject simpleDomainObject : module.getDomainObjects()) {
			printDomainObject(simpleDomainObject, 1);
		}
		for (Service service : module.getServices()) {
			printService(service, 1);
		}
		sb.append("}");
		linebreak();
	}

	protected void printAggregate(Aggregate aggregate, int indentation) {
		printIndentation(indentation);
		sb.append("package ").append("\"'").append(aggregate.getName()).append("' ").append("Aggregate\"").append(" <<Rectangle>> ").append("{");
		linebreak();
		for (SimpleDomainObject domainObject : aggregate.getDomainObjects()) {
			printDomainObject(aggregate, domainObject, indentation + 1);
		}
		for (Service service : aggregate.getServices()) {
			printService(aggregate, service, indentation + 1);
		}
		printIndentation(indentation);
		sb.append("}");
		linebreak();
	}
	
	protected void printService(Service service, int indentation) {
		printService(null, service, indentation);
	}

	protected void printService(Aggregate aggregate, Service service, int indentation) {
		printIndentation(indentation);
		sb.append("class").append(" ").append(service.getName());
		sb.append(" <<(S,DarkSeaGreen) Service>> ");
		sb.append("{");
		linebreak();
		printServiceOperations(service.getName(), service.getOperations(), indentation + 1);
		processAssociations(aggregate, service.getName(), service.getAssociations());
		printIndentation(indentation);
		sb.append("}");
		linebreak();
	}

	private void printServiceOperations(String objectName, List<ServiceOperation> operations, int indentation) {
		for (ServiceOperation operation : operations) {
			printOperation(objectName, operation.getName(), operation.getReturnType(), operation.getParameters(), indentation);
		}
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

	private void printEntity(Aggregate aggregate, Entity entity, int indentation) {
		printStereotypedClass(aggregate, "(E,DarkSeaGreen) Entity", entity, indentation);
	}

	private void printValueObject(Aggregate aggregate, ValueObject valueObject, int indentation) {
		printStereotypedClass(aggregate, "(V,DarkSeaGreen) Value Object", valueObject, indentation);
	}

	private void printEvent(Aggregate aggregate, Event event, int indentation) {
		if (event instanceof CommandEvent)
			printStereotypedClass(aggregate, "(C,#3bc5e9) Command", event, indentation);
		else if (event instanceof DomainEvent)
			printStereotypedClass(aggregate, "(E,#ff9f4b) Domain Event", event, indentation);
	}

	private void printStereotypedClass(Aggregate aggregate, String stereotype, DomainObject object, int indentation) {
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

		processReferencesAsAssociations(aggregate, object, object.getReferences());

		printDomainObjectOperations(object.getName(), object.getOperations(), indentation + 1);
		printIndentation(indentation);
		sb.append("}");
		linebreak();

		processAssociations(aggregate, object.getName(), object.getAssociations());

		if (object.getExtendsName() != null && !"".equals(object.getExtendsName()))
			addExtensionToList(object.getName(), object.getExtendsName());
		else if (object instanceof Entity && ((Entity) object).getExtends() != null)
			addExtensionToList(object.getName(), ((Entity) object).getExtends());
		else if (object instanceof CommandEvent && ((CommandEvent) object).getExtends() != null)
			addExtensionToList(object.getName(), ((CommandEvent) object).getExtends());
		else if (object instanceof DomainEvent && ((DomainEvent) object).getExtends() != null)
			addExtensionToList(object.getName(), ((DomainEvent) object).getExtends());
		else if (object instanceof ValueObject && ((ValueObject) object).getExtends() != null)
			addExtensionToList(object.getName(), ((ValueObject) object).getExtends());
	}

	private void processAssociations(Aggregate aggregate, String source, List<Association> associations) {
		for (Association association : associations) {
			addAssociationToList(aggregate, source, association.getDomainObjectType(), association.getDescription());
		}
	}

	private void processReferencesAsAssociations(Aggregate aggregate, SimpleDomainObject sourceDomainObject, List<Reference> references) {
		for (Reference reference : references) {
			Multiplicity multiplicityTarget = getMultiplicityReference(reference);
			addNavigableAssociationToList(aggregate, sourceDomainObject.getName(), reference.getDomainObjectType(), getLabel(reference.getAssociationLabel(), reference.getName()), multiplicityTarget);
		}
	}

	private Multiplicity getMultiplicityReference(Reference reference) {
		Multiplicity result = new Multiplicity(1, 1);

		if (reference.getCollectionType() != null && reference.getCollectionType() != CollectionType.NONE)
			result = new Multiplicity(0, Multiplicity.STAR);
		
		if (reference.getSize() != null && !reference.getSize().equals("")) {
			String size = reference.getSize();
			String[] parts = size.split(",");
			for (String part : parts) {
				if (part.startsWith("min=")) {
					Integer min = parseIntLax(part.substring(4));
					if (min != null)
						result = result.withMin(min);
				}
				if (part.startsWith("max=")) {
					Integer max = parseIntLax(part.substring(4));
					if (max != null)
						result = result.withMax(max);
				}
			}
		}
		if (reference.isNullable())
			result = result.withMin(0);

		return result;
	}

	private Integer parseIntLax(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private String getLabel(String label, String name) {
		if (label != null && !"".equals(label))
			return label;
		else
			return name;
	}

	private void addNavigableAssociationToList(Aggregate aggregate, String source, SimpleDomainObject targetDomainObject, String label, Multiplicity multiplicityTarget) {
		if (this.domainObjects.contains(targetDomainObject)) {
			String target = targetDomainObject.getName();
			AssociationInfo info = createOrGetAssociationInList(aggregate, source, target, label);
			info.setNavigability(source, target);
			if (info.getMultiplicity(source) == null)
				info.setMultiplicity(source, new Multiplicity(1, 1));
			if (info.getMultiplicity(target) == null || info.getMultiplicity(target).getMin() == 1)
				info.setMultiplicity(target, multiplicityTarget);
		}
	}

	private void addAssociationToList(Aggregate aggregate, String source, SimpleDomainObject targetDomainObject, String label) {
		if (this.domainObjects.contains(targetDomainObject)) {
			String target = targetDomainObject.getName();
			createOrGetAssociationInList(aggregate, source, target, label);
		}
	}

	private AssociationInfo createOrGetAssociationInList(Aggregate aggregate, String source, String target, String label) {
		AssociationLink link = new AssociationLink(source, target, label);
		if (!associationInfos.containsKey(link)) {
			AssociationInfo info = new AssociationInfo(link);
			if (aggregateContainsDomainObject(aggregate, source))
				info.setAggregateSource(aggregate.getName());
			if (aggregateContainsDomainObject(aggregate, target))
				info.setAggregateTarget(aggregate.getName());
			associationInfos.put(link, info);				 
		}
		return associationInfos.get(link);
	}

	private boolean aggregateContainsDomainObject(Aggregate aggregate, String source) {
		if (aggregate != null) {
			return aggregate.getDomainObjects()
				.stream()
				.filter((obj) -> obj.getName().equals(source))
				.findAny()
				.isPresent();
		}

		return false;
	}

	private void addExtensionToList(String sourceDomainObject, SimpleDomainObject extendedDomainObject) {
		if (this.domainObjects.contains(extendedDomainObject))
			addExtensionToList(sourceDomainObject, extendedDomainObject.getName());
	}

	private void addExtensionToList(String sourceDomainObject, String extendedDomainObject) {
		UMLRelationship relationship = new UMLRelationship(sourceDomainObject, extendedDomainObject, "", ClassRelationType.EXTENSION);
		if (!this.extensions.contains(relationship))
			this.extensions.add(relationship);
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
			returnTypeAsString = getComplexMethodTypeAsString(returnType);
		sb.append(returnTypeAsString).append(" ").append(operationName).append("(");
		List<String> parameterStrings = Lists.newArrayList();
		for (Parameter parameter : parameters) {
			String parameterType = getComplexMethodTypeAsString(parameter.getParameterType());
			parameterStrings.add(parameterType + " " + parameter.getName());
		}
		if (!parameterStrings.isEmpty())
			sb.append(String.join(", ", parameterStrings));
		sb.append(")");
		linebreak();
	}

	private String getComplexMethodTypeAsString(ComplexType type) {
		String genericType = "Object";
		if (type.getDomainObjectType() != null) {
			genericType = type.getDomainObjectType().getName();
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
		associationInfos.forEach((link, info) -> {
			boolean suppressMultiplicity = 
				info.getMultiplicityFirstParticipant() != null &&
				info.getMultiplicityFirstParticipant().isConstant(1) && 
				info.getMultiplicitySecondParticipant() != null &&
				info.getMultiplicitySecondParticipant().isConstant(1);

			printIndentation(indentation);

			// e.g.: first
			sb.append(link.getFirstParticipant()).append(" ");			

			// e.g.: first "1"
			if (info.getMultiplicityFirstParticipant() != null && !suppressMultiplicity)
				sb.append("\"").append(printMultiplicity(info.getMultiplicityFirstParticipant())).append("\"").append(" ");	

			// e.g.: first "1" *-->
			printLink(info);

			// e.g.: first "1" *--> "*"
			if (info.getMultiplicitySecondParticipant() != null && !suppressMultiplicity)
				sb.append("\"").append(printMultiplicity(info.getMultiplicitySecondParticipant())).append("\"").append(" ");

			// e.g.: first "1" *--> "*" second
			sb.append(link.getSecondParticipant());

			// e.g.: first "1" *--> "*" second : something
			if (!"".equals(link.getLabel()))
		 		sb.append(" : ").append(link.getLabel());
		 	linebreak();
		});

		for (UMLRelationship extension : extensions) {
			printIndentation(indentation);
			sb.append(extension.getSource()).append(" ").append(extension.getSymbol()).append(" ").append(extension.getTarget());
			linebreak();
		}
	}

	private void printLink(AssociationInfo info) {
		boolean multFirstIsMany = info.getMultiplicityFirstParticipant() != null && info.getMultiplicityFirstParticipant().getMax() > 1,
				multSecondIsMany = info.getMultiplicitySecondParticipant() != null && info.getMultiplicitySecondParticipant().getMax() > 1;
		String aggregationSymbol = (info.getAggregateSource() != null && info.getAggregateSource().equals(info.getAggregateTarget())) ? "*" : "o";

		if (multSecondIsMany && !multFirstIsMany)
			sb.append(aggregationSymbol);
		else if (info.getIsFirstNavigableFromSecond())
			sb.append("<");

		sb.append("--");

		if (multFirstIsMany && !multSecondIsMany) {
			sb.append(aggregationSymbol); 
		} else if (info.getIsSecondNavigableFromFirst())
			sb.append(">");							
		sb.append(" ");
	}

	private String printMultiplicity(Multiplicity multiplicity) {
		int min = multiplicity.getMin(), max = multiplicity.getMax();

		if (min == max)
			return Integer.toString(min);
		
		if (min == 0 && max == Multiplicity.STAR)
			return "*";

		return Integer.toString(min) + ".." + Integer.toString(max);
	}

	protected void printIndentation(int amount) {
		for (int i = 0; i < amount; i++) {
			sb.append("\t");
		}
	}
}

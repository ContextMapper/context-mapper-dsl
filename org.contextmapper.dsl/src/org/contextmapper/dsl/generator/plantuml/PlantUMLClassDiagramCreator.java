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
package org.contextmapper.dsl.generator.plantuml;

import java.util.List;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.Module;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.validation.ValidationMessages;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.CollectionType;
import org.contextmapper.tactic.dsl.tacticdsl.CommandEvent;
import org.contextmapper.tactic.dsl.tacticdsl.DomainEvent;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Enum;
import org.contextmapper.tactic.dsl.tacticdsl.EnumValue;
import org.contextmapper.tactic.dsl.tacticdsl.Event;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Lists;

public class PlantUMLClassDiagramCreator extends AbstractPlantUMLDiagramCreator<BoundedContext> implements PlantUMLDiagramCreator<BoundedContext> {

	private List<UMLRelationship> relationships;
	private List<SimpleDomainObject> boundedContextsDomainObjects;

	@Override
	protected void printDiagramContent(BoundedContext boundedContext) {
		this.relationships = Lists.newArrayList();
		this.boundedContextsDomainObjects = EcoreUtil2.<SimpleDomainObject>getAllContentsOfType(boundedContext, SimpleDomainObject.class);
		if (this.boundedContextsDomainObjects.size() <= 0) {
			printEmptyDiagramNote();
			return;
		}
		for (Module module : boundedContext.getModules()) {
			printModule(module);
		}
		for (Aggregate aggregate : boundedContext.getAggregates()) {
			printAggregate(aggregate, 0);
		}
		printReferences(0);
		printSubdomainLegend(boundedContext.getImplementedSubdomains());
	}

	private void printSubdomainLegend(List<Subdomain> subdomains) {
		if (subdomains.isEmpty())
			return;
		sb.append("legend left");
		linebreak();
		for (Subdomain subdomain : subdomains) {
			if (subdomain.getEntities().isEmpty()) {
				sb.append("  ").append("This bounded context implements the subdomain '" + subdomain.getName() + "'.");
			} else {
				sb.append("  ").append("This bounded context implements the subdomain '" + subdomain.getName() + "', which contains the following entities:");
			}
			linebreak();
			for (Entity entity : subdomain.getEntities()) {
				sb.append("  ").append(" - ").append(entity.getName());
				linebreak();
			}
		}
		sb.append("endlegend");
		linebreak();
	}

	private void printEmptyDiagramNote() {
		sb.append("note").append(" ").append("\"").append(ValidationMessages.EMPTY_UML_CLASS_DIAGRAM_MESSAGE).append("\"").append(" as EmptyDiagramError");
		linebreak();
	}

	private void printModule(Module module) {
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
		sb.append("}");
		linebreak();
	}

	private void printAggregate(Aggregate aggregate, int indentation) {
		printIndentation(indentation);
		sb.append("package ").append("\"'").append(aggregate.getName()).append("' ").append("Aggregate\"").append(" <<Rectangle>> ").append("{");
		linebreak();
		for (SimpleDomainObject domainObject : aggregate.getDomainObjects()) {
			printDomainObject(domainObject, indentation + 1);
		}
		printIndentation(indentation);
		sb.append("}");
		linebreak();
	}

	private void printDomainObject(SimpleDomainObject domainObject, int indentation) {
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
		printStereotypedClass("Entity", entity, indentation);
	}

	private void printValueObject(ValueObject valueObject, int indentation) {
		printStereotypedClass("Value Object", valueObject, indentation);
	}

	private void printEvent(Event event, int indentation) {
		if (event instanceof CommandEvent)
			printStereotypedClass("Command Event", event, indentation);
		else if (event instanceof DomainEvent)
			printStereotypedClass("Domain Event", event, indentation);
	}

	private void printStereotypedClass(String stereotype, DomainObject object, int indentation) {
		printIndentation(indentation);
		sb.append("class").append(" ").append(object.getName());
		if (object.isAggregateRoot())
			sb.append(" <<Aggregate Root>> ");
		else
			sb.append(" <<" + stereotype + ">> ");
		sb.append("{");
		linebreak();
		printAttributes(object.getAttributes(), indentation + 1);
		printReferenceAttributes(object.getReferences(), indentation + 1);
		printIndentation(indentation);
		sb.append("}");
		linebreak();
		addReferences2List(object, object.getReferences());
	}

	private void addReferences2List(SimpleDomainObject sourceDomainObject, List<Reference> references) {
		for (Reference reference : references) {
			if (this.boundedContextsDomainObjects.contains(reference.getDomainObjectType()))
				this.relationships.add(new UMLRelationship(sourceDomainObject.getName(), reference.getDomainObjectType().getName()));
		}
	}

	private void printAttributes(List<Attribute> attributes, int indentation) {
		for (Attribute attribute : attributes) {
			printIndentation(indentation);
			if (attribute.getCollectionType() != CollectionType.NONE)
				sb.append(attribute.getCollectionType()).append("<").append(attribute.getType()).append(">");
			else
				sb.append(attribute.getType());
			sb.append(" ").append(attribute.getName());
			linebreak();
		}
	}

	private void printReferenceAttributes(List<Reference> references, int indentation) {
		for (Reference reference : references) {
			printIndentation(indentation);
			if (reference.getCollectionType() != CollectionType.NONE)
				sb.append(reference.getCollectionType()).append("<").append(reference.getDomainObjectType().getName()).append(">");
			else
				sb.append(reference.getDomainObjectType().getName());
			sb.append(" ").append(reference.getName());
			linebreak();
		}
	}

	private void printReferences(int indentation) {
		for (UMLRelationship reference : relationships) {
			printIndentation(indentation);
			sb.append(reference.getSource()).append(" --> ").append(reference.getTarget());
			linebreak();
		}
	}

	private void printIndentation(int amount) {
		for (int i = 0; i < amount; i++) {
			sb.append("\t");
		}
	}

	private class UMLRelationship {
		private String source;
		private String target;

		public UMLRelationship(String source, String target) {
			this.source = source;
			this.target = target;
		}

		public String getSource() {
			return source;
		}

		public String getTarget() {
			return target;
		}
	}
}

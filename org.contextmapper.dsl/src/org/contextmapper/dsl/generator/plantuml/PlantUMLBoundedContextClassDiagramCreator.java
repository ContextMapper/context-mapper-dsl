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

import org.apache.commons.lang3.StringUtils;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.Application;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.DomainPart;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.validation.ValidationMessages;
import org.contextmapper.tactic.dsl.tacticdsl.CommandEvent;
import org.contextmapper.tactic.dsl.tacticdsl.DomainEvent;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Service;
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Lists;

public class PlantUMLBoundedContextClassDiagramCreator extends AbstractPlantUMLClassDiagramCreator<BoundedContext> implements PlantUMLDiagramCreator<BoundedContext> {

	@Override
	protected void printDiagramContent(BoundedContext boundedContext) {
		this.relationships = Lists.newArrayList();
		this.extensions = Lists.newArrayList();
		this.domainObjects = EcoreUtil2.<SimpleDomainObject>getAllContentsOfType(boundedContext, SimpleDomainObject.class);
		if (this.domainObjects.size() <= 0) {
			printEmptyDiagramNote();
			return;
		}
		for (SculptorModule module : boundedContext.getModules()) {
			printModule(module);
		}
		for (Aggregate aggregate : boundedContext.getAggregates()) {
			printAggregate(aggregate, 0);
		}
		if (boundedContext.getApplication() != null)
			printApplication(boundedContext.getApplication(), 0);
		printReferences(0);
		printLegend(boundedContext);
	}

	private void printLegend(BoundedContext boundedContext) {
		List<Subdomain> subdomains = getSubdomains(boundedContext.getImplementedDomainParts());
		if (subdomains.isEmpty() && boundedContext.getRefinedBoundedContext() == null)
			return;
		sb.append("legend left");
		linebreak();
		if (boundedContext.getRefinedBoundedContext() != null) {
			sb.append("  ").append("This Bounded Context '").append(boundedContext.getName()).append("' refines the '").append(boundedContext.getRefinedBoundedContext().getName())
					.append("' Bounded Context.");
			linebreak();
		}
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
		sb.append("end legend");
		linebreak();
	}

	private void printEmptyDiagramNote() {
		sb.append("note").append(" ").append("\"").append(ValidationMessages.EMPTY_UML_CLASS_DIAGRAM_MESSAGE).append("\"").append(" as EmptyDiagramError");
		linebreak();
	}

	private void printModule(SculptorModule module) {
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

	private void printAggregate(Aggregate aggregate, int indentation) {
		printIndentation(indentation);
		sb.append("package ").append("\"'").append(aggregate.getName()).append("' ").append("Aggregate\"").append(" <<Rectangle>> ").append("{");
		linebreak();
		for (SimpleDomainObject domainObject : aggregate.getDomainObjects()) {
			printDomainObject(domainObject, indentation + 1);
		}
		for (Service service : aggregate.getServices()) {
			printService(service, indentation + 1);
		}
		printIndentation(indentation);
		sb.append("}");
		linebreak();
	}

	private void printApplication(Application application, int indentation) {
		printIndentation(indentation);
		String name = StringUtils.isNotEmpty(application.getName()) ? application.getName() : "Application";
		sb.append("package ").append("\"'").append(name).append("'").append("\"").append(" <<Rectangle>> ").append("{");
		linebreak();
		if (!application.getFlows().isEmpty()) {
			printIndentation(indentation + 1);
			sb.append("legend left");
			linebreak();
			printIndentation(indentation + 2);
			sb.append("This application layer contains flow definitions (visualization available via BPMN Sketch Miner).");
			linebreak();
			printIndentation(indentation + 1);
			sb.append("end legend");
			linebreak();
		}
		for (DomainEvent event : application.getEvents()) {
			printDomainObject(event, indentation + 1);
		}
		for (CommandEvent command : application.getCommands()) {
			printDomainObject(command, indentation + 1);
		}
		for (Service service : application.getServices()) {
			printService(service, indentation + 1);
		}
		printIndentation(indentation);
		sb.append("}");
		linebreak();
	}

	private void printService(Service service, int indentation) {
		printIndentation(indentation);
		sb.append("class").append(" ").append(service.getName());
		sb.append(" <<(S,DarkSeaGreen) Service>> ");
		sb.append("{");
		linebreak();
		printServiceOperations(service.getName(), service.getOperations(), indentation + 1);
		printIndentation(indentation);
		sb.append("}");
		linebreak();
	}

	private void printServiceOperations(String objectName, List<ServiceOperation> operations, int indentation) {
		for (ServiceOperation operation : operations) {
			printOperation(objectName, operation.getName(), operation.getReturnType(), operation.getParameters(), indentation);
		}
	}

	private List<Subdomain> getSubdomains(List<DomainPart> domainParts) {
		List<Subdomain> subdomains = Lists.newArrayList();
		domainParts.forEach(domainPart -> {
			if (domainPart instanceof Domain) {
				subdomains.addAll(((Domain) domainPart).getSubdomains());
			} else {
				subdomains.add((Subdomain) domainPart);
			}
		});
		return subdomains;
	}

}

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
package org.contextmapper.dsl.refactoring;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.DomainPart;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.ComplexType;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Parameter;
import org.contextmapper.tactic.dsl.tacticdsl.Service;
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DeriveBoundedContextFromSubdomains extends AbstractRefactoring implements Refactoring {

	private Set<String> subdomainIds = Sets.newHashSet();
	private String boundedContextName;

	public DeriveBoundedContextFromSubdomains(String boundedContextName, Set<String> subdomainIds) {
		this.boundedContextName = boundedContextName;
		this.subdomainIds = subdomainIds;
	}

	@Override
	protected void doRefactor() {
		Set<Subdomain> selectedSubdomains = collectSubdomains();
		if (selectedSubdomains.isEmpty())
			throw new RefactoringInputException("Please provide at least one subdomain name that can be found in the given CML model.");

		BoundedContext newBC = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		newBC.setName(getUniqueBoundedContextName(boundedContextName));
		newBC.setDomainVisionStatement(
				"This Bounded Context realizes the following subdomains: " + String.join(", ", selectedSubdomains.stream().map(sd -> sd.getName()).collect(Collectors.toList())));
		newBC.setType(BoundedContextType.FEATURE);
		for (Subdomain subdomain : selectedSubdomains) {
			addElementToEList(newBC.getImplementedDomainParts(), (DomainPart) subdomain);
			createAggregate4Subdomain(subdomain, newBC);
		}

		addElementToEList(rootResource.getContextMappingModel().getBoundedContexts(), newBC);
		markResourceChanged(rootResource);

		saveResources();
	}

	private void createAggregate4Subdomain(Subdomain subdomain, BoundedContext newBC) {
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName(subdomain.getName() + "Aggregate");
		aggregate.setComment("/* This Aggregate contains the entities and services of the '" + subdomain.getName() + "' subdomain." + System.lineSeparator()
				+ "	 * You can now refactor the Aggregate, for example by using the 'Split Aggregate by Entities' architectural refactoring. */");

		createEntities(subdomain, aggregate);
		createServices(subdomain, aggregate);

		addElementToEList(newBC.getAggregates(), aggregate);
	}

	private void createEntities(Subdomain subdomain, Aggregate aggregate) {
		for (Entity entity : subdomain.getEntities()) {
			Entity newEntity = TacticdslFactory.eINSTANCE.createEntity();
			newEntity.setName(entity.getName());
			newEntity.setAggregateRoot(false);
			newEntity.setComment("/* TODO: Add attributes, references, and operations to entity (see examples below)." + System.lineSeparator()
					+ "		 * attribute example: String attribute" + System.lineSeparator() + "		 * reference example: - OtherObject reference" + System.lineSeparator()
					+ "		 * operation example: def ReturnType doSomething(Parameter parameter);" + System.lineSeparator() + "		 */");

			Attribute idAttribute = TacticdslFactory.eINSTANCE.createAttribute();
			idAttribute.setType(entity.getName() + "ID");
			idAttribute.setName(entity.getName().toLowerCase() + "Id");

			addElementToEList(newEntity.getAttributes(), idAttribute);
			addElementToEList(aggregate.getDomainObjects(), newEntity);
		}
	}

	private void createServices(Subdomain subdomain, Aggregate aggregate) {
		for (Service service : subdomain.getServices()) {
			Service newService = TacticdslFactory.eINSTANCE.createService();
			newService.setName(service.getName());
			newService.setDoc(service.getDoc());
			newService.setHint(service.getHint());
			newService.setComment("/* TODO: Add operations to service." + System.lineSeparator()
					+ "		 * example: ReturnType exampleServiceOperation(String param1, @Object param2);" + System.lineSeparator() + "		 */");

			addElementsToEList(newService.getOperations(), copyAndEnhanceOperations(service));
			addElementToEList(aggregate.getServices(), newService);
		}
	}

	private List<ServiceOperation> copyAndEnhanceOperations(Service service) {
		List<ServiceOperation> operations = Lists.newLinkedList();
		for (ServiceOperation operation : service.getOperations()) {
			ServiceOperation copiedOperation = TacticdslFactory.eINSTANCE.createServiceOperation();
			copiedOperation.setName(operation.getName());
			copiedOperation.setDelegateHolder(operation.getDelegateHolder());
			copiedOperation.setHint(operation.getHint());
			copiedOperation.setDoc(operation.getDoc());
			copiedOperation.setPublish(operation.getPublish());
			copiedOperation.setThrows(operation.getThrows());
			copiedOperation.setVisibility(operation.getVisibility());
			operations.add(copiedOperation);

			if (operation.getReturnType() == null)
				copiedOperation.setReturnType(createComplexType(service.getName() + "Output"));

			if (operation.getParameters().isEmpty())
				addElementToEList(copiedOperation.getParameters(), createParameter("input", createComplexType(service.getName() + "Input")));

		}
		return operations;
	}

	private ComplexType createComplexType(String type) {
		ComplexType complexType = TacticdslFactory.eINSTANCE.createComplexType();
		complexType.setType(type);
		return complexType;
	}

	private Parameter createParameter(String name, ComplexType type) {
		Parameter parameter = TacticdslFactory.eINSTANCE.createParameter();
		parameter.setName(name);
		parameter.setParameterType(type);
		return parameter;
	}

	private String getUniqueBoundedContextName(String initialName) {
		String bcName = initialName;
		Set<String> allBCNames = getAllBoundedContexts().stream().map(bc -> bc.getName()).collect(Collectors.toSet());
		int counter = 2;
		while (allBCNames.contains(bcName)) {
			bcName = initialName + "_" + counter;
			counter++;
		}
		return bcName;
	}

	private Set<Subdomain> collectSubdomains() {
		Set<Subdomain> allSubdomains = Sets.newHashSet();
		for (Domain domain : getAllDomains()) {
			allSubdomains.addAll(domain.getSubdomains());
		}
		Set<Subdomain> subdomains = Sets.newHashSet();
		for (String subdomainId : subdomainIds) {
			Optional<Subdomain> optSubdomain = allSubdomains.stream().filter(sd -> subdomainId.equals(sd.getName())).findFirst();
			if (optSubdomain.isPresent())
				subdomains.add(optSubdomain.get());
		}
		return subdomains;
	}

}

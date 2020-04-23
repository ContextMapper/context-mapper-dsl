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
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.Service;
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.eclipse.emf.ecore.util.EcoreUtil;

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

		BoundedContext bc = createOrGetBoundedContext(boundedContextName);
		bc.setDomainVisionStatement(
				"This Bounded Context realizes the following subdomains: " + String.join(", ", selectedSubdomains.stream().map(sd -> sd.getName()).collect(Collectors.toList())));
		bc.setType(BoundedContextType.FEATURE);
		for (Subdomain subdomain : selectedSubdomains) {
			addElementToEList(bc.getImplementedDomainParts(), (DomainPart) subdomain);
			createAggregate4Subdomain(subdomain, bc);
		}

		if (getResource(bc) != null)
			markResourceChanged(bc);

		saveResources();
	}

	private void createAggregate4Subdomain(Subdomain subdomain, BoundedContext bc) {
		Aggregate aggregate = createOrGetAggregate(bc, getAggregateName(subdomain.getName() + "Aggregate", bc));
		aggregate.setComment("/* This Aggregate contains the entities and services of the '" + subdomain.getName() + "' subdomain." + System.lineSeparator()
				+ "	 * TODO: You can now refactor the Aggregate, for example by using the 'Split Aggregate by Entities' architectural refactoring." + System.lineSeparator()
				+ "	 * TODO: Add attributes and operations to the entities." + System.lineSeparator() + "	 * TODO: Add operations to the services." + System.lineSeparator()
				+ "	 * Find examples and further instructions on our website: https://contextmapper.org/docs/rapid-ooad/ */");

		createEntities(subdomain, aggregate);
		createServices(subdomain, aggregate);
	}

	private String getAggregateName(String initialName, BoundedContext bc) {
		Set<String> currentBCAggregates = bc.getAggregates().stream().map(agg -> agg.getName()).collect(Collectors.toSet());
		if (currentBCAggregates.contains(initialName))
			return initialName;

		String contextName = initialName;
		Set<String> allAggregateNames = collectAllAggregateNames();
		allAggregateNames.removeAll(currentBCAggregates);
		int counter = 2;
		while (allAggregateNames.contains(contextName)) {
			contextName = initialName + "_" + counter;
			counter++;
		}
		return contextName;
	}

	private Set<String> collectAllAggregateNames() {
		Set<String> aggregateNames = Sets.newHashSet();
		for (BoundedContext bc : getAllBoundedContexts()) {
			aggregateNames.addAll(bc.getAggregates().stream().map(agg -> agg.getName()).collect(Collectors.toSet()));
		}
		return aggregateNames;
	}

	private void createEntities(Subdomain subdomain, Aggregate aggregate) {
		for (Entity sdEntity : subdomain.getEntities()) {
			Entity bcEntity = createOrGetEntity(aggregate, sdEntity.getName());
			bcEntity.setAggregateRoot(false);

			copyAttributes(sdEntity, bcEntity);

			String idAttributeName = sdEntity.getName().toLowerCase() + "Id";
			if (!bcEntity.getAttributes().stream().filter(a -> idAttributeName.equals(a.getName())).findFirst().isPresent()) {
				Attribute idAttribute = TacticdslFactory.eINSTANCE.createAttribute();
				idAttribute.setType(sdEntity.getName() + "ID");
				idAttribute.setName(idAttributeName);
				addElementToEList(bcEntity.getAttributes(), idAttribute);
			}
		}
	}

	private void copyAttributes(Entity source, Entity target) {
		Set<String> existingAttrs = target.getAttributes().stream().map(attr -> attr.getName()).collect(Collectors.toSet());
		Set<String> existingRefs = target.getReferences().stream().map(ref -> ref.getName()).collect(Collectors.toSet());
		for (Attribute sourceAttr : source.getAttributes()) {
			if (existingAttrs.contains(sourceAttr.getName()))
				continue;
			addElementToEList(target.getAttributes(), EcoreUtil.copy(sourceAttr));
		}
		for (Reference sourceRef : source.getReferences()) {
			if (existingRefs.contains(sourceRef.getName()))
				continue;
			addElementToEList(target.getReferences(), EcoreUtil.copy(sourceRef));
		}
	}

	private void createServices(Subdomain subdomain, Aggregate aggregate) {
		for (Service sdService : subdomain.getServices()) {
			Service bcService = createOrGetService(aggregate, sdService.getName());
			bcService.setDoc(sdService.getDoc());
			bcService.setHint(sdService.getHint());

			copyAndEnhanceOperations(sdService, bcService);
		}
	}

	private void copyAndEnhanceOperations(Service source, Service target) {
		Set<String> existingOperations = target.getOperations().stream().map(o -> o.getName()).collect(Collectors.toSet());
		for (ServiceOperation sourceOperation : source.getOperations()) {
			if (existingOperations.contains(sourceOperation.getName()))
				continue;

			ServiceOperation targetOperation = TacticdslFactory.eINSTANCE.createServiceOperation();
			targetOperation.setName(sourceOperation.getName());
			targetOperation.setDelegateHolder(sourceOperation.getDelegateHolder());
			targetOperation.setHint(sourceOperation.getHint());
			targetOperation.setDoc(sourceOperation.getDoc());
			targetOperation.setPublish(sourceOperation.getPublish());
			targetOperation.setThrows(sourceOperation.getThrows());
			targetOperation.setVisibility(sourceOperation.getVisibility());

			if (sourceOperation.getReturnType() == null)
				targetOperation.setReturnType(createComplexType(sourceOperation.getName().substring(0, 1).toUpperCase() + sourceOperation.getName().substring(1) + "Output"));

			if (sourceOperation.getParameters().isEmpty())
				addElementToEList(targetOperation.getParameters(),
						createParameter("input", createComplexType(sourceOperation.getName().substring(0, 1).toUpperCase() + sourceOperation.getName().substring(1) + "Input")));

			addElementToEList(target.getOperations(), targetOperation);
		}
	}

	private BoundedContext createOrGetBoundedContext(String boundedContextName) {
		Optional<BoundedContext> optContext = getAllBoundedContexts().stream().filter(bc -> boundedContextName.equals(bc.getName())).findFirst();
		if (optContext.isPresent())
			return optContext.get();
		BoundedContext newBC = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		newBC.setName(boundedContextName);
		addElementToEList(rootResource.getContextMappingModel().getBoundedContexts(), newBC);
		markResourceChanged(rootResource);
		return newBC;
	}

	private Aggregate createOrGetAggregate(BoundedContext bc, String aggregateName) {
		Optional<Aggregate> optAggregate = bc.getAggregates().stream().filter(agg -> aggregateName.equals(agg.getName())).findFirst();
		if (optAggregate.isPresent())
			return optAggregate.get();

		Aggregate newAggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		newAggregate.setName(aggregateName);
		addElementToEList(bc.getAggregates(), newAggregate);
		return newAggregate;
	}

	private Entity createOrGetEntity(Aggregate aggregate, String entityName) {
		Optional<Entity> optEntity = aggregate.getDomainObjects().stream().filter(o -> o instanceof Entity && entityName.equals(o.getName())).map(o -> (Entity) o).findFirst();
		if (optEntity.isPresent())
			return optEntity.get();

		Entity newEntity = TacticdslFactory.eINSTANCE.createEntity();
		newEntity.setName(entityName);
		addElementToEList(aggregate.getDomainObjects(), newEntity);
		return newEntity;
	}

	private Service createOrGetService(Aggregate aggregate, String serviceName) {
		Optional<Service> optService = aggregate.getServices().stream().filter(s -> serviceName.equals(s.getName())).findFirst();
		if (optService.isPresent())
			return optService.get();

		Service newService = TacticdslFactory.eINSTANCE.createService();
		newService.setName(serviceName);
		addElementToEList(aggregate.getServices(), newService);
		return newService;
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

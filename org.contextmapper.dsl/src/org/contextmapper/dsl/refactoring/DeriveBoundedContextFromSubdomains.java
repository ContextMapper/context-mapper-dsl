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

import org.contextmapper.dsl.cml.CMLModelObjectsResolvingHelper;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.DomainPart;
import org.contextmapper.dsl.contextMappingDSL.Feature;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.CollectionType;
import org.contextmapper.tactic.dsl.tacticdsl.ComplexType;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Parameter;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.Service;
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import com.google.common.collect.Sets;

public class DeriveBoundedContextFromSubdomains extends AbstractRefactoring implements SemanticCMLRefactoring {

	private CMLModelObjectsResolvingHelper objectResolver;

	private Set<String> subdomainIds = Sets.newHashSet();
	private String boundedContextName;

	public DeriveBoundedContextFromSubdomains(String boundedContextName, Set<String> subdomainIds) {
		this.boundedContextName = boundedContextName;
		this.subdomainIds = subdomainIds;
		this.objectResolver = new CMLModelObjectsResolvingHelper(model);
	}

	@Override
	protected void doRefactor() {
		Set<Subdomain> selectedSubdomains = collectSubdomains();
		if (selectedSubdomains.isEmpty())
			throw new RefactoringInputException(
					"Please provide at least one subdomain name that can be found in the given CML model.");

		BoundedContext bc = createOrGetBoundedContext(boundedContextName);
		bc.setDomainVisionStatement("This Bounded Context realizes the following subdomains: "
				+ String.join(", ", selectedSubdomains.stream().map(sd -> sd.getName()).collect(Collectors.toList())));
		bc.setType(BoundedContextType.FEATURE);
		for (Subdomain subdomain : selectedSubdomains) {
			addElementToEList(bc.getImplementedDomainParts(), (DomainPart) subdomain);
			createAggregate4Subdomain(subdomain, bc);
		}
	}

	private void createAggregate4Subdomain(Subdomain subdomain, BoundedContext bc) {
		Aggregate aggregate = createOrGetAggregate(bc, getAggregateName(subdomain.getName() + "Aggregate", bc));
		aggregate.setComment("/* This Aggregate contains the entities and services of the '" + subdomain.getName()
				+ "' subdomain." + System.lineSeparator()
				+ "	 * TODO: You can now refactor the Aggregate, for example by using the 'Split Aggregate by Entities' architectural refactoring."
				+ System.lineSeparator() + "	 * TODO: Add attributes and operations to the entities."
				+ System.lineSeparator() + "	 * TODO: Add operations to the services." + System.lineSeparator()
				+ "	 * Find examples and further instructions on our website: https://contextmapper.org/docs/rapid-ooad/ */");

		createEntities(subdomain, aggregate);
		createServices(subdomain, aggregate);
	}

	private String getAggregateName(String initialName, BoundedContext bc) {
		Set<String> currentBCAggregates = bc.getAggregates().stream().map(agg -> agg.getName())
				.collect(Collectors.toSet());
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
			if (entityAlreadyExistsInOtherContext(sdEntity.getName()))
				throw new ContextMapperApplicationException(
						"Cannot derive Bounded Context. Another context with an Entity of the name \""
								+ sdEntity.getName() + "\" already exists.");

			Entity bcEntity = createOrGetEntity(aggregate, sdEntity.getName());
			bcEntity.setAggregateRoot(false);

			copyAttributes(sdEntity, bcEntity);

			String idAttributeName = sdEntity.getName().toLowerCase() + "Id";
			if (!bcEntity.getAttributes().stream().filter(a -> idAttributeName.equals(a.getName())).findFirst()
					.isPresent()) {
				Attribute idAttribute = TacticdslFactory.eINSTANCE.createAttribute();
				idAttribute.setName(idAttributeName);
				idAttribute.setType("String");
				idAttribute.setKey(true);
				addElementToEList(bcEntity.getAttributes(), idAttribute);
			}
		}
		for (Entity sdEntity : subdomain.getEntities()) {
			Entity bcEntity = createOrGetEntity(aggregate, sdEntity.getName());
			copyReferences(sdEntity, bcEntity, aggregate.getDomainObjects());
		}
	}

	private boolean entityAlreadyExistsInOtherContext(String entityName) {
		Set<String> existingEntities = Sets.newHashSet();
		Set<BoundedContext> boundedContexts = getAllBoundedContexts().stream()
				.filter(bc -> !bc.getName().equals(boundedContextName)).collect(Collectors.toSet());
		for (BoundedContext bc : boundedContexts) {
			existingEntities.addAll(Sets.newHashSet(IteratorExtensions.filter(EcoreUtil2.eAll(bc), Entity.class))
					.stream().map(e -> e.getName()).collect(Collectors.toSet()));
		}
		if (existingEntities.contains(entityName))
			return true;
		return false;
	}

	private void copyAttributes(Entity source, Entity target) {
		Set<String> existingAttrs = target.getAttributes().stream().map(attr -> attr.getName())
				.collect(Collectors.toSet());
		for (Attribute sourceAttr : source.getAttributes()) {
			if (existingAttrs.contains(sourceAttr.getName()))
				continue;
			addElementToEList(target.getAttributes(), EcoreUtil.copy(sourceAttr));
		}
	}

	private void copyReferences(Entity source, Entity target, List<SimpleDomainObject> referenceableObjects) {
		Set<String> existingRefs = target.getReferences().stream().map(ref -> ref.getName())
				.collect(Collectors.toSet());
		for (Reference sourceRef : source.getReferences()) {
			if (existingRefs.contains(sourceRef.getName()))
				continue;
			Reference newReference = TacticdslFactory.eINSTANCE.createReference();
			newReference.setName(sourceRef.getName());
			newReference.setCollectionType(sourceRef.getCollectionType());
			newReference.setDomainObjectType(referenceableObjects.stream()
					.filter(o -> o.getName().equals(sourceRef.getDomainObjectType().getName())).findFirst().get());
			addElementToEList(target.getReferences(), newReference);
		}
	}

	private void createServices(Subdomain subdomain, Aggregate aggregate) {
		for (Service sdService : subdomain.getServices()) {
			Service bcService = createOrGetService(aggregate, sdService.getName());
			bcService.setDoc(sdService.getDoc());
			bcService.setHint(sdService.getHint());

			copyAndEnhanceOperations(aggregate, sdService, bcService);
		}
	}

	private void copyAndEnhanceOperations(Aggregate aggregate, Service source, Service target) {
		Set<String> existingOperations = target.getOperations().stream().map(o -> o.getName())
				.collect(Collectors.toSet());
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
				targetOperation.setReturnType(getReturnType4Operation(aggregate, sourceOperation.getName()));

			if (sourceOperation.getParameters().isEmpty()) {
				ComplexType parameterType = getParameterType4Operation(aggregate, sourceOperation.getName());
				addElementToEList(targetOperation.getParameters(),
						createParameter(getParameterName4ComplexType(parameterType), parameterType));
			}
			addElementToEList(target.getOperations(), targetOperation);
		}
	}

	private BoundedContext createOrGetBoundedContext(String boundedContextName) {
		Optional<BoundedContext> optContext = getAllBoundedContexts().stream()
				.filter(bc -> boundedContextName.equals(bc.getName())).findFirst();
		if (optContext.isPresent())
			return optContext.get();
		BoundedContext newBC = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		newBC.setName(boundedContextName);
		addElementToEList(rootResource.getContextMappingModel().getBoundedContexts(), newBC);
		return newBC;
	}

	private Aggregate createOrGetAggregate(BoundedContext bc, String aggregateName) {
		// try to find existing Aggregate
		Optional<Aggregate> optAggregate = bc.getAggregates().stream()
				.filter(agg -> aggregateName.equals(agg.getName())).findFirst();
		if (optAggregate.isPresent())
			return optAggregate.get();

		// try to find existing Aggregate in modules
		for (SculptorModule module : bc.getModules()) {
			optAggregate = module.getAggregates().stream().filter(agg -> aggregateName.equals(agg.getName()))
					.findFirst();
			if (optAggregate.isPresent())
				return optAggregate.get();
		}

		Aggregate newAggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		newAggregate.setName(aggregateName);
		addElementToEList(bc.getAggregates(), newAggregate);
		return newAggregate;
	}

	private Entity createOrGetEntity(Aggregate aggregate, String entityName) {
		Optional<Entity> optEntity = aggregate.getDomainObjects().stream()
				.filter(o -> o instanceof Entity && entityName.equals(o.getName())).map(o -> (Entity) o).findFirst();
		if (optEntity.isPresent())
			return optEntity.get();

		Entity newEntity = TacticdslFactory.eINSTANCE.createEntity();
		newEntity.setName(entityName);
		addElementToEList(aggregate.getDomainObjects(), newEntity);
		return newEntity;
	}

	private Service createOrGetService(Aggregate aggregate, String serviceName) {
		Optional<Service> optService = aggregate.getServices().stream().filter(s -> serviceName.equals(s.getName()))
				.findFirst();
		if (optService.isPresent())
			return optService.get();

		Service newService = TacticdslFactory.eINSTANCE.createService();
		newService.setName(serviceName);
		addElementToEList(aggregate.getServices(), newService);
		return newService;
	}

	private ComplexType getReturnType4Operation(Aggregate aggregate, String operationName) {
		Entity correspondingEntity = resolveEntity4OperationByFeatures(aggregate, operationName);
		if (correspondingEntity != null && operationName.startsWith("create")) {
			return createComplexType("String");
		} else if (correspondingEntity != null
				&& (operationName.startsWith("read") || operationName.startsWith("get"))) {
			return createComplexType(correspondingEntity, CollectionType.SET);
		} else if (correspondingEntity != null && operationName.startsWith("update")) {
			return createComplexType("String");
		}
		return createComplexType("boolean");
	}

	private ComplexType getParameterType4Operation(Aggregate aggregate, String operationName) {
		Entity correspondingEntity = resolveEntity4OperationByFeatures(aggregate, operationName);
		if (correspondingEntity != null && (operationName.startsWith("read") || operationName.startsWith("get"))) {
			return createComplexType("String", CollectionType.SET);
		} else if (correspondingEntity != null && (operationName.startsWith("delete"))) {
				return createComplexType("String");
		} else if (correspondingEntity != null) {
			return createComplexType(correspondingEntity);
		}
		return createComplexType(operationName.substring(0, 1).toUpperCase() + operationName.substring(1) + "Input");
	}

	private ComplexType createComplexType(String type) {
		ComplexType complexType = TacticdslFactory.eINSTANCE.createComplexType();
		complexType.setType(type);
		return complexType;
	}
	
	private ComplexType createComplexType(String type, CollectionType collectionType) {
		ComplexType complexType = TacticdslFactory.eINSTANCE.createComplexType();
		complexType.setType(type);
		complexType.setCollectionType(collectionType);
		return complexType;
	}

	private ComplexType createComplexType(SimpleDomainObject object) {
		ComplexType complexType = TacticdslFactory.eINSTANCE.createComplexType();
		complexType.setDomainObjectType(object);
		return complexType;
	}

	private ComplexType createComplexType(SimpleDomainObject object, CollectionType collectionType) {
		ComplexType complexType = createComplexType(object);
		complexType.setCollectionType(collectionType);
		return complexType;
	}

	private String getParameterName4ComplexType(ComplexType complexType) {
		String parameterName = "input";
		if (complexType.getDomainObjectType() != null)
			parameterName = complexType.getDomainObjectType().getName();
		else if (complexType.getType() != null && "String".equals(complexType.getType()))
			parameterName = "id";
		else if (complexType.getType() != null && !"".equals(complexType.getType()))
			parameterName = complexType.getType();
		return parameterName.substring(0, 1).toLowerCase() + parameterName.substring(1);
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
			Optional<Subdomain> optSubdomain = allSubdomains.stream().filter(sd -> subdomainId.equals(sd.getName()))
					.findFirst();
			if (optSubdomain.isPresent())
				subdomains.add(optSubdomain.get());
		}
		return subdomains;
	}

	private Entity resolveEntity4OperationByFeatures(Aggregate aggregate2Search, String operationName) {
		BoundedContext bc = objectResolver.resolveBoundedContext(aggregate2Search);
		Set<Feature> features = objectResolver.resolveFeatures(bc);
		for (Feature feature : features) {
			if (operationName.equals(feature.getVerb() + feature.getEntity())
					|| operationName.endsWith(feature.getEntity())) {
				return aggregate2Search.getDomainObjects().stream().filter(o -> o instanceof Entity)
						.map(o -> (Entity) o).filter(o -> o.getName().equals(feature.getEntity())).findFirst().get();
			}
		}
		return null;
	}

}

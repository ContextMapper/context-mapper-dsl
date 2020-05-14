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

import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.Feature;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.contextMappingDSL.UserRequirement;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.CollectionType;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.Service;
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DeriveSubdomainFromUserRequirements extends AbstractRefactoring implements SemanticCMLRefactoring {

	private static final String BENEFIT_SEPARATOR_STRING = "; ";

	private Set<String> userRequiremendIds = Sets.newHashSet();
	private String domainName;
	private String subdomainName;

	public DeriveSubdomainFromUserRequirements(String domainName, String subdomainName, Set<String> userRequirements) {
		this.domainName = domainName;
		this.subdomainName = subdomainName;
		this.userRequiremendIds = userRequirements;
	}

	@Override
	protected void doRefactor() {
		Set<UserRequirement> selectedUserRequirements = collectUserRequirements();

		// if there are no user stories or use cases selected, we do nothing
		if (selectedUserRequirements.isEmpty())
			throw new RefactoringInputException("Please provide at least one user story or use case that exists in the CML model.");

		Domain domain = getOrCreateDomain();
		Subdomain subdomain = getOrCreateSubdomain(domain);
		List<String> benefits = Lists.newLinkedList();
		if (subdomain.getDomainVisionStatement() != null && !"".equals(subdomain.getDomainVisionStatement()))
			benefits.addAll(List.of(subdomain.getDomainVisionStatement().split(BENEFIT_SEPARATOR_STRING)));

		for (UserRequirement ur : selectedUserRequirements) {
			if (!doesContainAtLeastOneEntity(ur))
				continue;

			benefits.add("Aims at promoting the following benefit for a " + ur.getRole() + ": " + ur.getBenefit());
			deriveSubdomainEntities4Features(subdomain, ur.getName(), ur.getFeatures());
		}

		for (UserRequirement ur : selectedUserRequirements) {
			deriveSubdomainEntityReferences(subdomain, ur.getFeatures());
		}

		subdomain.setDomainVisionStatement(String.join(BENEFIT_SEPARATOR_STRING, benefits));

		markResourceChanged(domain);
		saveResources();
	}

	private boolean doesContainAtLeastOneEntity(UserRequirement ur) {
		if (ur.getFeatures().isEmpty())
			return false;

		Set<String> entityNames = ur.getFeatures().stream().map(f -> f.getEntity()).collect(Collectors.toSet());
		if (entityNames.size() == 1 && "".equals(entityNames.iterator().next()))
			return false;

		return true;
	}

	private void deriveSubdomainEntities4Features(Subdomain subdomain, String urName, List<Feature> features) {
		for (Feature feature : features) {
			if (feature.getEntity() == null || "".equals(feature.getEntity()))
				continue;

			// create the entity
			String entityName = createEntityIfNotExisting(feature.getEntity(), subdomain, feature.getEntityAttributes());

			// create the service
			String serviceName = urName.substring(0, 1).toUpperCase() + urName.substring(1) + "Service";
			Optional<Service> alreadyExistingService = subdomain.getServices().stream().filter(s -> serviceName.equals(s.getName())).findFirst();
			Service service;
			if (!alreadyExistingService.isPresent()) {
				service = createService(serviceName, entityName, feature.getVerb());
				addElementToEList(subdomain.getServices(), service);
			} else {
				service = alreadyExistingService.get();
			}

			String operationName = feature.getVerb().replace(" ", "_") + entityName;
			Optional<ServiceOperation> alreadyExistingServiceOperation = service.getOperations().stream().filter(o -> operationName.equals(o.getName())).findFirst();
			if (!alreadyExistingServiceOperation.isPresent())
				addElementToEList(service.getOperations(), createServiceOperation(operationName));

			// create the entity that contains the one created above
			if (feature.getContainerEntity() != null && !"".equals(feature.getContainerEntity()))
				createEntityIfNotExisting(feature.getContainerEntity(), subdomain, Lists.newLinkedList());
		}
	}

	private void deriveSubdomainEntityReferences(Subdomain subdomain, List<Feature> features) {
		for (Feature feature : features) {
			if (feature.getContainerEntity() == null || "".equals(feature.getContainerEntity()))
				continue;

			String containerEntityName = mapEntityName(feature.getContainerEntity());
			String referencedEntityName = mapEntityName(feature.getEntity());
			Entity containerEntity = subdomain.getEntities().stream().filter(e -> e.getName().equals(containerEntityName)).findFirst().get();
			Entity referencedEntity = subdomain.getEntities().stream().filter(e -> e.getName().equals(referencedEntityName)).findFirst().get();
			String refName = referencedEntity.getName().toLowerCase() + "List";

			if (containerEntity.getReferences().stream().filter(r -> r.getName().equals(refName)).findAny().isPresent())
				continue;

			Reference reference = TacticdslFactory.eINSTANCE.createReference();
			reference.setName(refName);
			reference.setCollectionType(CollectionType.LIST);
			reference.setDomainObjectType(referencedEntity);
			addElementToEList(containerEntity.getReferences(), reference);
		}
	}

	private String createEntityIfNotExisting(String entity, Subdomain subdomain, List<String> attributes) {
		String entityName = mapEntityName(entity);
		Optional<Entity> alreadyExistingEntity = subdomain.getEntities().stream().filter(e -> entityName.equals(e.getName())).findFirst();
		if (!alreadyExistingEntity.isPresent()) {
			Entity newEntity = TacticdslFactory.eINSTANCE.createEntity();
			newEntity.setName(entityName);
			createEntityAttributes(newEntity, attributes);
			addElementToEList(subdomain.getEntities(), newEntity);
		} else {
			createEntityAttributes(alreadyExistingEntity.get(), attributes);
		}
		return entityName;
	}

	private String mapEntityName(String entityName) {
		return entityName.trim().replace(" ", "_");
	}

	private void createEntityAttributes(Entity entity, List<String> attributeNames) {
		Set<String> existingAttrNames = entity.getAttributes().stream().map(a -> a.getName()).collect(Collectors.toSet());
		for (String attributeName : attributeNames) {
			if ("".equals(attributeName.trim()))
				continue;

			String attributeNameEncoded = encodeAttrName(attributeName);
			if (existingAttrNames.contains(attributeNameEncoded))
				continue;

			Attribute attribute = TacticdslFactory.eINSTANCE.createAttribute();
			attribute.setName(attributeNameEncoded);
			attribute.setType("String");
			addElementToEList(entity.getAttributes(), attribute);
		}
	}

	private String encodeAttrName(String originalName) {
		String name = originalName.trim();
		name = name.substring(0, 1).toLowerCase() + name.substring(1);
		return name;
	}

	private Service createService(String serviceName, String entityName, String verb) {
		Service service = TacticdslFactory.eINSTANCE.createService();
		service.setName(serviceName);
		return service;
	}

	private ServiceOperation createServiceOperation(String operationName) {
		ServiceOperation operation = TacticdslFactory.eINSTANCE.createServiceOperation();
		operation.setName(operationName);
		return operation;
	}

	private Domain getOrCreateDomain() {
		if (domainName == null || "".equals(domainName))
			throw new RefactoringInputException("Please provide a name for the domain where the new subdomain shall be added.");

		Optional<Domain> optDomain = getAllDomains().stream().filter(d -> domainName.equals(d.getName())).findFirst();
		if (optDomain.isPresent())
			return optDomain.get();

		Domain newDomain = ContextMappingDSLFactory.eINSTANCE.createDomain();
		newDomain.setName(domainName);
		addElementToEList(model.getDomains(), newDomain);
		return newDomain;
	}

	private Subdomain getOrCreateSubdomain(Domain domain) {
		if (subdomainName == null || "".equals(subdomainName))
			throw new RefactoringInputException("Please provide a name for the subdomain that shall be created or existing subdomain that shall contain the new entities.");

		Optional<Subdomain> optSubdomain = domain.getSubdomains().stream().filter(sd -> subdomainName.equals(sd.getName())).findFirst();
		if (optSubdomain.isPresent())
			return optSubdomain.get();

		Subdomain newSubdomain = ContextMappingDSLFactory.eINSTANCE.createSubdomain();
		newSubdomain.setName(subdomainName);
		addElementToEList(domain.getSubdomains(), newSubdomain);
		return newSubdomain;
	}

	private Set<UserRequirement> collectUserRequirements() {
		Set<UserRequirement> userRequirements = Sets.newHashSet();
		Set<UserRequirement> allUserRequirements = getAllUserRequirements();
		for (String urName : this.userRequiremendIds) {
			Optional<UserRequirement> optUR = allUserRequirements.stream().filter(ur -> urName.equals(ur.getName())).findFirst();
			if (optUR.isPresent())
				userRequirements.add(optUR.get());
		}
		return userRequirements;
	}

}

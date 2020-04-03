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
import org.contextmapper.tactic.dsl.tacticdsl.DomainObjectOperation;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Parameter;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
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
		newBC.setName(boundedContextName);
		newBC.setDomainVisionStatement(
				"This Bounded Context realizes the following subdomains: " + String.join(", ", selectedSubdomains.stream().map(sd -> sd.getName()).collect(Collectors.toList())));
		newBC.setType(BoundedContextType.FEATURE);
		for (Subdomain subdomain : selectedSubdomains) {
			addElementToEList(newBC.getImplementedDomainParts(), (DomainPart) subdomain);
			createAggregates4Entities(subdomain, newBC);
		}

		addElementToEList(rootResource.getContextMappingModel().getBoundedContexts(), newBC);
		markResourceChanged(rootResource);

		saveResources();
	}

	private void createAggregates4Entities(Subdomain subdomain, BoundedContext newBC) {
		for (Entity entity : subdomain.getEntities()) {
			Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
			aggregate.setName(getUniqueAggregateName(newBC, entity.getName() + "Aggregate"));
			aggregate.setComment("// TODO: please add other domain objects (entities, value objects, domain events) and their attributes and operations.");

			Entity rootEntity = TacticdslFactory.eINSTANCE.createEntity();
			rootEntity.setName(entity.getName());
			rootEntity.setAggregateRoot(true);

			Attribute globalIdAttribute = TacticdslFactory.eINSTANCE.createAttribute();
			globalIdAttribute.setType("GlobalID");
			globalIdAttribute.setName("globalId");

			addElementToEList(rootEntity.getAttributes(), globalIdAttribute);
			addElementToEList(aggregate.getDomainObjects(), rootEntity);
			addElementToEList(newBC.getAggregates(), aggregate);
		}
	}

	private ComplexType createComplexType(String type) {
		ComplexType complexType = TacticdslFactory.eINSTANCE.createComplexType();
		complexType.setType(type);
		return complexType;
	}

	private ComplexType createComplexType(SimpleDomainObject type) {
		ComplexType complexType = TacticdslFactory.eINSTANCE.createComplexType();
		complexType.setDomainObjectType(type);
		return complexType;
	}

	private Parameter createParameter(String name, ComplexType type) {
		Parameter parameter = TacticdslFactory.eINSTANCE.createParameter();
		parameter.setName(name);
		parameter.setParameterType(type);
		return parameter;
	}

	private String getUniqueAggregateName(BoundedContext bc, String initialAggregateName) {
		Set<String> allAggregateNames = bc.getAggregates().stream().map(agg -> agg.getName()).collect(Collectors.toSet());
		String aggregateName = initialAggregateName;
		int counter = 2;
		while (allAggregateNames.contains(aggregateName)) {
			aggregateName = initialAggregateName + "_" + counter;
			counter++;
		}
		return aggregateName;
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

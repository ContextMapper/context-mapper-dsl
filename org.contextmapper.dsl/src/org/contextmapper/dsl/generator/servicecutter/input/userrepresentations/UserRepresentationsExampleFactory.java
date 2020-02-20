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
package org.contextmapper.dsl.generator.servicecutter.input.userrepresentations;

import java.util.List;
import java.util.Random;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.Aggregate;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.AvailabilityCriticality;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.Compatibilities;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ConsistencyCriticality;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ContentVolatility;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.PredefinedService;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.SecurityAccessGroup;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.SecurityCriticality;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.SeparatedSecurityZone;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterConfigurationDSLFactory;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterUserRepresentationsModel;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.SharedOwnerGroup;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.StorageSimilarity;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.StructuralVolatility;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.UseCase;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.DomainEvent;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Lists;

/**
 * Creates an example model for the ServiceCutter User Representations. Note
 * that the model does not must make any sense! It is just an example to start
 * and adjust.
 * 
 * @author Stefan Kapferer
 *
 */
public class UserRepresentationsExampleFactory {

	private static final int MAX_ENTITY_USECASE_EXAMPLES = 4;

	private ServiceCutterConfigurationDSLFactory factory = ServiceCutterConfigurationDSLFactory.eINSTANCE;
	private ContextMappingModel contextMappingModel;
	private List<Attribute> allModelAttributes;

	public ServiceCutterUserRepresentationsModel createExampleModel(ContextMappingModel contextMappingModel) {
		ServiceCutterUserRepresentationsModel model = factory.createServiceCutterUserRepresentationsModel();
		this.contextMappingModel = contextMappingModel;
		this.allModelAttributes = collectAttributes(contextMappingModel);

		// give the user a hint, if no data available to generate something useful
		if (allModelAttributes.size() <= 0) {
			UseCase dummyUseCase = factory.createUseCase();
			dummyUseCase.setName("NoDataFound");
			dummyUseCase.setDoc(
					"/* Your context map should at least contain one bounded context with aggregates, entities and some attributes. \n"
							+ " * Otherwise it is not possible to generate user representation examples. */");
			model.getUseCases().add(dummyUseCase);
			return model;
		}

		List<UseCase> useCases = createSampleUseCases();
		if (useCases.size() > 0)
			useCases.get(0).setDoc(
					"/* The following usecases are just examples to give you a hint how to specify them! You may want to change or remove them. */");
		model.getUseCases().addAll(useCases);
		model.setCompatibilities(createSampleCompatibilities());
		model.getAggregates().addAll(createSampleAggregates());
		model.getEntities().addAll(createSampleEntities());
		model.getPredefinedServices().addAll(createSamplePredefinedServices());
		model.getSecurityAccessGroups().addAll(createSampleSecurityAccessGroups());
		model.getSeparatedSecurityZones().addAll(createSampleSeparatedSecurityZones());
		model.getSharedOwnerGroups().addAll(createSampleSharedOwnerGroups());

		return model;
	}

	private List<UseCase> createSampleUseCases() {
		List<UseCase> useCases = Lists.newArrayList();
		List<DomainObject> domainObjects = collectDomainObjects();
		for (DomainObject domainObject : domainObjects) {
			if (useCases.size() >= MAX_ENTITY_USECASE_EXAMPLES)
				break;
			useCases.addAll(createEntityUseCases(domainObject));
		}
		return useCases;
	}

	private List<UseCase> createEntityUseCases(DomainObject domainObject) {
		List<UseCase> useCases = Lists.newArrayList();

		// READ use case example
		UseCase viewUseCase = factory.createUseCase();
		viewUseCase.setName("View" + domainObject.getName());
		viewUseCase.getNanoentitiesRead().addAll(collectNanoEntities(domainObject));
		useCases.add(viewUseCase);

		// WRITE use case example
		UseCase updateUseCase = factory.createUseCase();
		updateUseCase.setName("Update" + domainObject.getName());
		updateUseCase.getNanoentitiesWritten().addAll(collectNanoEntities(domainObject));
		useCases.add(updateUseCase);

		return useCases;
	}

	private List<String> collectNanoEntities(DomainObject domainObject) {
		List<String> nanoEntities = Lists.newArrayList();
		for (Attribute attribute : domainObject.getAttributes()) {
			nanoEntities.add(domainObject.getName() + "." + attribute.getName());
		}
		for (Reference reference : domainObject.getReferences()) {
			nanoEntities.add(reference.getDomainObjectType().getName() + "." + reference.getName());
		}
		return nanoEntities;
	}

	private Compatibilities createSampleCompatibilities() {
		Compatibilities compatibilities = factory.createCompatibilities();
		compatibilities.setDoc("/* Compatibilities can be used to import all coupling criteria of type Compatability.\n"
				+ " * https://github.com/ServiceCutter/ServiceCutter/wiki/Compatibilities\n"
				+ " * The following compatibilities are just examples to provide you a template! You have to specify or remove them. "
				+ " */");

		// difficult to create examples, if there are no attributes in the model
		if (allModelAttributes.size() <= 0)
			return compatibilities;

		ContentVolatility exampleContentVolatility = factory.createContentVolatility();
		exampleContentVolatility
				.setDoc("/* See: https://github.com/ServiceCutter/ServiceCutter/wiki/CC-8-Content-Volatility */");
		exampleContentVolatility.setCharacteristic("Often");
		exampleContentVolatility.setCharacteristicDoc("// Allowed characteristics: Often, Regularly, Rarely");
		exampleContentVolatility.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
		exampleContentVolatility.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
		compatibilities.getContentVolatility().add(exampleContentVolatility);

		StructuralVolatility exampleStructuralVolatility = factory.createStructuralVolatility();
		exampleStructuralVolatility
				.setDoc("/* See: https://github.com/ServiceCutter/ServiceCutter/wiki/CC-4-Structural-Volatility */");
		exampleStructuralVolatility.setCharacteristic("Normal");
		exampleStructuralVolatility.setCharacteristicDoc("// Allowed characteristics: Often, Normal, Rarely");
		exampleStructuralVolatility.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
		exampleStructuralVolatility.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
		compatibilities.getStructuralVolatility().add(exampleStructuralVolatility);

		AvailabilityCriticality exampleAvailabilityCriticality = factory.createAvailabilityCriticality();
		exampleAvailabilityCriticality
				.setDoc("/* See: https://github.com/ServiceCutter/ServiceCutter/wiki/CC-7-Availability-Criticality */");
		exampleAvailabilityCriticality.setCharacteristic("Normal");
		exampleAvailabilityCriticality.setCharacteristicDoc("// Allowed characteristics: Critical, Normal, Low");
		exampleAvailabilityCriticality.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
		exampleAvailabilityCriticality.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
		compatibilities.getAvailabilityCriticality().add(exampleAvailabilityCriticality);

		ConsistencyCriticality exampleConsistencyCriticality = factory.createConsistencyCriticality();
		exampleConsistencyCriticality
				.setDoc("/* See: https://github.com/ServiceCutter/ServiceCutter/wiki/CC-6-Consistency-Criticality */");
		exampleConsistencyCriticality.setCharacteristic("Eventually");
		exampleConsistencyCriticality.setCharacteristicDoc("// Allowed characteristics: High, Eventually, Weak");
		exampleConsistencyCriticality.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
		exampleConsistencyCriticality.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
		compatibilities.getConsistencyCriticality().add(exampleConsistencyCriticality);

		StorageSimilarity exampleStorageSimilarity = factory.createStorageSimilarity();
		exampleStorageSimilarity
				.setDoc("/* See: https://github.com/ServiceCutter/ServiceCutter/wiki/CC-11-Storage-Similarity */");
		exampleStorageSimilarity.setCharacteristic("Normal");
		exampleStorageSimilarity.setCharacteristicDoc("// Allowed characteristics: Tiny, Normal, Huge");
		exampleStorageSimilarity.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
		exampleStorageSimilarity.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
		compatibilities.getStorageSimilarity().add(exampleStorageSimilarity);

		SecurityCriticality exampleSecurityCriticality = factory.createSecurityCriticality();
		exampleSecurityCriticality
				.setDoc("/* See: https://github.com/ServiceCutter/ServiceCutter/wiki/CC-15-Security-Criticality */");
		exampleSecurityCriticality.setCharacteristic("Internal");
		exampleSecurityCriticality.setCharacteristicDoc("// Allowed characteristics: Critical, Internal, Public");
		exampleSecurityCriticality.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
		exampleSecurityCriticality.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
		compatibilities.getSecurityCriticality().add(exampleSecurityCriticality);

		return compatibilities;
	}

	private List<Aggregate> createSampleAggregates() {
		List<org.contextmapper.dsl.contextMappingDSL.Aggregate> existingAggregates = collectAggregates();
		List<Aggregate> aggregates = Lists.newArrayList();
		if (existingAggregates.size() > 0) {
			for (org.contextmapper.dsl.contextMappingDSL.Aggregate aggregate : existingAggregates) {
				List<Attribute> aggregateAttributes = collectAttributes(aggregate);
				Aggregate scAggregate = factory.createAggregate();
				scAggregate.setName(aggregate.getName());
				scAggregate
						.setDoc("/* This aggregate was generated by your CML model. You do not have to change it. */");
				scAggregate.getNanoentities().addAll(convertAttributesToNanoEntites(aggregateAttributes));
				aggregates.add(scAggregate);
			}
		} else if (allModelAttributes.size() > 0) {
			Aggregate sampleAggregate = factory.createAggregate();
			sampleAggregate.setDoc("/* This is just a sample aggregate (template). \n"
					+ "* Note that if your CML model had Aggregates within the Bounded Contexts, they would have been generated at this place! */");
			sampleAggregate.setName("SampleAggregate");
			sampleAggregate.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
			sampleAggregate.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
			aggregates.add(sampleAggregate);
		}
		return aggregates;
	}

	private List<org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.Entity> createSampleEntities() {
		// Does not seem to make sense to me to use them here... basically all our
		// objects are entities.
		return Lists.newArrayList();
	}

	private List<PredefinedService> createSamplePredefinedServices() {
		List<PredefinedService> predefinedServices = Lists.newArrayList();
		for (BoundedContext boundedContext : this.contextMappingModel.getMap().getBoundedContexts()) {
			List<Attribute> attributes = collectAttributes(boundedContext);
			PredefinedService predefinedService = factory.createPredefinedService();
			predefinedService.setName(boundedContext.getName());
			predefinedService.setDoc("/* This predefined service is based on your '" + boundedContext.getName()
					+ "' BoundedContext. You do not have to change it. */");
			predefinedService.getNanoentities().addAll(convertAttributesToNanoEntites(attributes));
			predefinedServices.add(predefinedService);
		}
		return predefinedServices;
	}

	private List<SecurityAccessGroup> createSampleSecurityAccessGroups() {
		List<SecurityAccessGroup> securityAccessGroups = Lists.newArrayList();
		if (allModelAttributes.size() > 0) {
			SecurityAccessGroup exampleSecurityAccessGroup = factory.createSecurityAccessGroup();
			exampleSecurityAccessGroup.setName("SecurityAccessGroupTemplate");
			exampleSecurityAccessGroup.setDoc("/* Security Accress Groups cannot be derived from ContextMap. \n "
					+ "* This is a template/example how you can define them. If you do not want to specify any, remove this block. */");
			exampleSecurityAccessGroup.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
			exampleSecurityAccessGroup.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
			securityAccessGroups.add(exampleSecurityAccessGroup);
		}
		return securityAccessGroups;
	}

	private List<SeparatedSecurityZone> createSampleSeparatedSecurityZones() {
		List<SeparatedSecurityZone> separatedSecurityZones = Lists.newArrayList();
		if (allModelAttributes.size() > 0) {
			SeparatedSecurityZone exampleSeparatedSecurityZone = factory.createSeparatedSecurityZone();
			exampleSeparatedSecurityZone.setName("SeparatedSecurityZoneTemplate");
			exampleSeparatedSecurityZone.setDoc("/* Separated Security Zones cannot be derived from ContextMap. \n "
					+ "* This is a template/example how you can define them. If you do not want to specify any, remove this block. */");
			exampleSeparatedSecurityZone.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
			exampleSeparatedSecurityZone.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
			separatedSecurityZones.add(exampleSeparatedSecurityZone);
		}
		return separatedSecurityZones;
	}

	private List<SharedOwnerGroup> createSampleSharedOwnerGroups() {
		List<SharedOwnerGroup> sharedOwnerGroups = Lists.newArrayList();
		if (allModelAttributes.size() > 0) {
			SharedOwnerGroup exampleSharedOwnerGroup = factory.createSharedOwnerGroup();
			exampleSharedOwnerGroup.setName("SharedOwnerGroupTemplate");
			exampleSharedOwnerGroup.setDoc("/* Shared Owner Groups cannot be derived from ContextMap. \n "
					+ "* This is a template/example how you can define them. If you do not want to specify any, remove this block. */");
			exampleSharedOwnerGroup.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
			exampleSharedOwnerGroup.getNanoentities().add(randomlyPickAttribute(allModelAttributes));
			sharedOwnerGroups.add(exampleSharedOwnerGroup);
		}
		return sharedOwnerGroups;
	}

	private List<org.contextmapper.dsl.contextMappingDSL.Aggregate> collectAggregates() {
		return EcoreUtil2.getAllContentsOfType(contextMappingModel,
				org.contextmapper.dsl.contextMappingDSL.Aggregate.class);
	}

	private List<DomainObject> collectDomainObjects() {
		List<DomainObject> list = Lists.newArrayList();
		list.addAll(EcoreUtil2.getAllContentsOfType(contextMappingModel, Entity.class));
		list.addAll(EcoreUtil2.getAllContentsOfType(contextMappingModel, ValueObject.class));
		list.addAll(EcoreUtil2.getAllContentsOfType(contextMappingModel, DomainEvent.class));
		return list;
	}

	private List<Attribute> collectAttributes(ContextMappingModel rootModel) {
		List<Attribute> attributes = Lists.newArrayList();
		
		if(rootModel.getMap() == null)
			return attributes;
		
		for(BoundedContext bc : rootModel.getMap().getBoundedContexts()) {
			attributes.addAll(EcoreUtil2.getAllContentsOfType(bc, Attribute.class));
		}
		return attributes;
	}
	
	private List<Attribute> collectAttributes(EObject root) {
		return EcoreUtil2.getAllContentsOfType(root, Attribute.class);
	}

	private String randomlyPickAttribute(List<Attribute> attributes) {
		Random random = new Random();
		if (attributes.size() > 0)
			return createNanoEntityString(attributes.get(random.nextInt(attributes.size())));
		else
			return "";
	}

	private String createNanoEntityString(Attribute attribute) {
		DomainObject domainObject = (DomainObject) attribute.eContainer();
		return domainObject.getName() + "." + attribute.getName();
	}

	private List<String> convertAttributesToNanoEntites(List<Attribute> attributes) {
		List<String> nanoEntities = Lists.newArrayList();
		for (Attribute attribute : attributes) {
			nanoEntities.add(createNanoEntityString(attribute));
		}
		return nanoEntities;
	}

}

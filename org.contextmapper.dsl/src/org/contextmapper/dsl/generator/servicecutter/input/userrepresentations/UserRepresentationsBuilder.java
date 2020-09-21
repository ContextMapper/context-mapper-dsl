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
package org.contextmapper.dsl.generator.servicecutter.input.userrepresentations;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLModelObjectsResolvingHelper;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.UserRequirement;
import org.contextmapper.dsl.generator.servicecutter.input.nanoentities.NanoentityResolver;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.PredefinedService;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.SecurityAccessGroup;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.SeparatedSecurityZone;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterConfigurationDSLFactory;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterUserRepresentationsModel;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.SharedOwnerGroup;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.UseCase;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Creates an SCL file for the ServiceCutter user representations from a CML
 * model as input.
 * 
 * @author Stefan Kapferer
 *
 */
public class UserRepresentationsBuilder {

	private ServiceCutterConfigurationDSLFactory factory = ServiceCutterConfigurationDSLFactory.eINSTANCE;
	private ContextMappingModel contextMappingModel;
	private ServiceCutterUserRepresentationsModel model;
	private CMLModelObjectsResolvingHelper resolvingHelper;
	private NanoentityResolver nanoentityResolver;
	private CompatibilityBuilder compatibilityBuilder;

	public UserRepresentationsBuilder(ContextMappingModel cmlModel) {
		this.contextMappingModel = cmlModel;
		this.resolvingHelper = new CMLModelObjectsResolvingHelper(cmlModel);
		this.model = factory.createServiceCutterUserRepresentationsModel();
		this.nanoentityResolver = new NanoentityResolver();
		this.compatibilityBuilder = new CompatibilityBuilder(cmlModel);
	}

	/**
	 * Builds the user representations from the given CML file.
	 * 
	 * @return the built user representation model
	 */
	public ServiceCutterUserRepresentationsModel build() {
		buildUseCases();
		buildAggregates();
		buildEntities();
		buildPredefinedServices();
		buildSharedOwnerGroups();
		mapCompatibilities();
		buildSeparatedSecurityZones();
		buildSecurityAccessGroups();
		return model;
	}

	private void buildUseCases() {
		model.getUseCases().clear();
		for (UserRequirement ur : contextMappingModel.getUserRequirements()) {
			Set<String> nanoEntitiesRead = new NanoentityCollector().getNanoentitiesRead(ur);
			Set<String> nanoEntitiesWritten = new NanoentityCollector().getNanoentitiesWritten(ur);

			if (nanoEntitiesRead.isEmpty() && nanoEntitiesWritten.isEmpty())
				continue;

			// create one use case for each user requirement in CML (use case or user story
			// in CML)
			UseCase uc = factory.createUseCase();
			uc.setName(ur.getName());
			uc.getNanoentitiesRead().addAll(nanoEntitiesRead);
			uc.getNanoentitiesWritten().addAll(nanoEntitiesWritten);
			model.getUseCases().add(uc);
		}
	}

	private void buildAggregates() {
		model.getAggregates().clear();
		for (Aggregate aggregate : resolvingHelper.resolveAllAggregates()) {
			org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.Aggregate scAggregate = factory.createAggregate();
			scAggregate.setName(aggregate.getName());
			scAggregate.getNanoentities().addAll(nanoentityResolver.getAllNanoentities(aggregate));
			model.getAggregates().add(scAggregate);
		}
		if (!model.getAggregates().isEmpty())
			model.getAggregates().get(0).setDoc("/* These Aggregates are generated from the CML model. Note that they are overwritten each time you use the service cut generator! */");
	}

	private void buildEntities() {
		model.getEntities().clear();
		for (Entity entity : EcoreUtil2.eAllOfType(contextMappingModel, Entity.class)) {
			org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.Entity scEntity = factory.createEntity();
			scEntity.setName(entity.getName());
			scEntity.getNanoentities().addAll(nanoentityResolver.getAllNanoentities(entity));
			model.getEntities().add(scEntity);
		}
		if (!model.getEntities().isEmpty())
			model.getEntities().get(0).setDoc("/* These Entities are generated from the CML model. Note that they are overwritten each time you use the service cut generator! */");
	}

	private void buildPredefinedServices() {
		model.getPredefinedServices().clear();
		for (BoundedContext bc : contextMappingModel.getBoundedContexts()) {
			if (bc.getType().equals(BoundedContextType.TEAM))
				continue;

			PredefinedService predefinedService = factory.createPredefinedService();
			predefinedService.setName(bc.getName());
			predefinedService.getNanoentities().addAll(nanoentityResolver.getAllNanoentities(bc));
			model.getPredefinedServices().add(predefinedService);
		}
		if (!model.getPredefinedServices().isEmpty())
			model.getPredefinedServices().get(0)
					.setDoc("/* These predefined services are generated from the CML model. Note that they are overwritten each time you use the service cut generator! */");
	}

	private void buildSharedOwnerGroups() {
		model.getSharedOwnerGroups().clear();
		List<Aggregate> allAggregatesWithOwner = resolvingHelper.resolveAllAggregates().stream().filter(agg -> agg.getOwner() != null).collect(Collectors.toList());
		if (allAggregatesWithOwner.isEmpty())
			return;
		Map<String, Set<String>> nanoentitiesPerOwner = Maps.newHashMap();
		for (Aggregate aggregate : allAggregatesWithOwner) {
			if (!nanoentitiesPerOwner.containsKey(aggregate.getOwner().getName()))
				nanoentitiesPerOwner.put(aggregate.getOwner().getName(), Sets.newHashSet());

			nanoentitiesPerOwner.get(aggregate.getOwner().getName()).addAll(nanoentityResolver.getAllNanoentities(aggregate));
		}
		for (Entry<String, Set<String>> entry : nanoentitiesPerOwner.entrySet()) {
			SharedOwnerGroup ownerGroup = factory.createSharedOwnerGroup();
			ownerGroup.setName(entry.getKey());
			ownerGroup.getNanoentities().addAll(entry.getValue());
			model.getSharedOwnerGroups().add(ownerGroup);
		}
		if (!model.getSharedOwnerGroups().isEmpty())
			model.getSharedOwnerGroups().get(0).setDoc("/* These shared owner groups are generated from the CML model. Note that they are overwritten each time you use the service cut generator! */");
	}

	private void buildSeparatedSecurityZones() {
		model.getSeparatedSecurityZones().clear();
		List<Aggregate> allAggregatesWithSecurityZone = resolvingHelper.resolveAllAggregates().stream().filter(agg -> agg.getSecurityZone() != null && !"".equals(agg.getSecurityZone()))
				.collect(Collectors.toList());
		Map<String, List<Aggregate>> aggregatesPerSecurityZone = allAggregatesWithSecurityZone.stream().collect(Collectors.groupingBy(Aggregate::getSecurityZone));
		for (Entry<String, List<Aggregate>> entry : aggregatesPerSecurityZone.entrySet()) {
			SeparatedSecurityZone separatedSecurityZone = factory.createSeparatedSecurityZone();
			separatedSecurityZone.setName(entry.getKey());
			for (Aggregate aggregate : entry.getValue())
				separatedSecurityZone.getNanoentities().addAll(nanoentityResolver.getAllNanoentities(aggregate));
			model.getSeparatedSecurityZones().add(separatedSecurityZone);
		}
	}

	private void buildSecurityAccessGroups() {
		model.getSecurityAccessGroups().clear();
		List<Aggregate> allAggregatesWithSecurityAccessGroups = resolvingHelper.resolveAllAggregates().stream()
				.filter(agg -> agg.getSecurityAccessGroup() != null && !"".equals(agg.getSecurityAccessGroup())).collect(Collectors.toList());
		Map<String, List<Aggregate>> aggregatesPerSecurityAccessGroup = allAggregatesWithSecurityAccessGroups.stream().collect(Collectors.groupingBy(Aggregate::getSecurityAccessGroup));
		for (Entry<String, List<Aggregate>> entry : aggregatesPerSecurityAccessGroup.entrySet()) {
			SecurityAccessGroup securityAccessGroup = factory.createSecurityAccessGroup();
			securityAccessGroup.setName(entry.getKey());
			for (Aggregate aggregate : entry.getValue())
				securityAccessGroup.getNanoentities().addAll(nanoentityResolver.getAllNanoentities(aggregate));
			model.getSecurityAccessGroups().add(securityAccessGroup);
		}
	}

	private void mapCompatibilities() {
		model.setCompatibilities(compatibilityBuilder.buildCompatibilities());
	}

}

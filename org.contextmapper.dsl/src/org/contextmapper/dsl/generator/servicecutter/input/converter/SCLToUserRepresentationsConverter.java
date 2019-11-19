/*
 * Copyright 2019 The Context Mapper Project Team
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
package org.contextmapper.dsl.generator.servicecutter.input.converter;

import java.util.List;

import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterUserRepresentationsModel;

import com.google.common.collect.Lists;

import ch.hsr.servicecutter.api.model.Characteristic;
import ch.hsr.servicecutter.api.model.Compatibilities;
import ch.hsr.servicecutter.api.model.RelatedGroup;
import ch.hsr.servicecutter.api.model.UseCase;
import ch.hsr.servicecutter.api.model.UserRepresentationContainer;

/**
 * Converts the user representations provided by the SCL language to a Service
 * Cutter user representations container.
 * 
 * @author Stefan Kapferer
 */
public class SCLToUserRepresentationsConverter {

	public UserRepresentationContainer convert(ServiceCutterUserRepresentationsModel sclUserRepresentationsModel) {
		UserRepresentationContainer container = new UserRepresentationContainer();
		if (sclUserRepresentationsModel.getCompatibilities() != null)
			container.setCompatibilities(convert(sclUserRepresentationsModel.getCompatibilities()));
		container.setUseCases(convertUseCases(sclUserRepresentationsModel.getUseCases()));
		container.setAggregates(convertRelatedGroups(sclUserRepresentationsModel.getAggregates()));
		container.setEntities(convertRelatedGroups(sclUserRepresentationsModel.getEntities()));
		container.setPredefinedServices(convertRelatedGroups(sclUserRepresentationsModel.getPredefinedServices()));
		container.setSecurityAccessGroups(convertRelatedGroups(sclUserRepresentationsModel.getSecurityAccessGroups()));
		container.setSeparatedSecurityZones(convertRelatedGroups(sclUserRepresentationsModel.getSeparatedSecurityZones()));
		container.setSharedOwnerGroups(convertRelatedGroups(sclUserRepresentationsModel.getSharedOwnerGroups()));
		return container;
	}

	private Compatibilities convert(org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.Compatibilities sclCompatibilities) {
		Compatibilities compatibilities = new Compatibilities();
		compatibilities.setAvailabilityCriticality(convertCharacteristics(sclCompatibilities.getAvailabilityCriticality()));
		compatibilities.setConsistencyCriticality(convertCharacteristics(sclCompatibilities.getConsistencyCriticality()));
		compatibilities.setContentVolatility(convertCharacteristics(sclCompatibilities.getContentVolatility()));
		compatibilities.setSecurityCriticality(convertCharacteristics(sclCompatibilities.getSecurityCriticality()));
		compatibilities.setStorageSimilarity(convertCharacteristics(sclCompatibilities.getStorageSimilarity()));
		compatibilities.setStructuralVolatility(convertCharacteristics(sclCompatibilities.getStructuralVolatility()));
		return compatibilities;
	}

	private List<Characteristic> convertCharacteristics(List<? extends org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.Characteristic> sclCharacteristics) {
		List<Characteristic> characteristics = Lists.newArrayList();
		for (org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.Characteristic characteristic : sclCharacteristics) {
			characteristics.add(convert(characteristic));
		}
		return characteristics;
	}

	private Characteristic convert(org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.Characteristic sclCharacteristic) {
		Characteristic characteristic = new Characteristic();
		characteristic.setCharacteristic(sclCharacteristic.getCharacteristic());
		characteristic.setNanoentities(Lists.newArrayList(sclCharacteristic.getNanoentities()));
		return characteristic;
	}

	private List<UseCase> convertUseCases(List<org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.UseCase> sclUseCases) {
		List<UseCase> useCases = Lists.newArrayList();
		for (org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.UseCase useCase : sclUseCases) {
			useCases.add(convert(useCase));
		}
		return useCases;
	}

	private UseCase convert(org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.UseCase sclUseCase) {
		UseCase useCase = new UseCase();
		useCase.setName(sclUseCase.getName());
		useCase.setLatencyCritical(sclUseCase.isIsLatencyCritical());
		useCase.setNanoentitiesRead(Lists.newArrayList(sclUseCase.getNanoentitiesRead()));
		useCase.setNanoentitiesWritten(Lists.newArrayList(sclUseCase.getNanoentitiesWritten()));
		return useCase;
	}

	private List<RelatedGroup> convertRelatedGroups(List<? extends org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.RelatedGroup> sclRelatedGroups) {
		List<RelatedGroup> relatedGroups = Lists.newArrayList();
		for (org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.RelatedGroup relatedGroup : sclRelatedGroups) {
			relatedGroups.add(convert(relatedGroup));
		}
		return relatedGroups;
	}

	private RelatedGroup convert(org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.RelatedGroup sclRelatedGroup) {
		RelatedGroup relatedGroup = new RelatedGroup();
		relatedGroup.setName(sclRelatedGroup.getName());
		relatedGroup.setNanoentities(Lists.newArrayList(sclRelatedGroup.getNanoentities()));
		return relatedGroup;
	}

}

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

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLModelObjectsResolvingHelper;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Criticality;
import org.contextmapper.dsl.contextMappingDSL.Similarity;
import org.contextmapper.dsl.contextMappingDSL.Volatility;
import org.contextmapper.dsl.generator.servicecutter.input.nanoentities.NanoentityResolver;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.AvailabilityCriticality;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.Compatibilities;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ConsistencyCriticality;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ContentVolatility;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.SecurityCriticality;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterConfigurationDSLFactory;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.StorageSimilarity;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.StructuralVolatility;

import com.google.common.collect.Sets;

public class CompatibilityBuilder {

	private CMLModelObjectsResolvingHelper resolvingHelper;
	private NanoentityResolver nanoentityResolver;

	public CompatibilityBuilder(ContextMappingModel cmlModel) {
		this.resolvingHelper = new CMLModelObjectsResolvingHelper(cmlModel);
		this.nanoentityResolver = new NanoentityResolver();
	}

	public Compatibilities buildCompatibilities() {
		Compatibilities compatibilities = ServiceCutterConfigurationDSLFactory.eINSTANCE.createCompatibilities();
		buildStructuralVolatilitySets(compatibilities);
		buildContentVolatilitySets(compatibilities);
		buildAvailabilityCriticalitySets(compatibilities);
		buildConsistencyCriticalitySets(compatibilities);
		buildStorageSimilaritySets(compatibilities);
		buildSecurityCriticalitySets(compatibilities);
		return compatibilities;
	}

	private void buildStructuralVolatilitySets(Compatibilities compatibilities) {
		compatibilities.getStructuralVolatility().clear();

		Set<String> normalNanoEntities = getNanoentities4AggregatePredicate(agg -> agg.getLikelihoodForChange() == Volatility.NORMAL);
		Set<String> oftenNanoEntities = getNanoentities4AggregatePredicate(agg -> agg.getLikelihoodForChange() == Volatility.OFTEN);
		Set<String> rarelyNanoEntities = getNanoentities4AggregatePredicate(agg -> agg.getLikelihoodForChange() == Volatility.RARELY);

		if (!normalNanoEntities.isEmpty())
			compatibilities.getStructuralVolatility().add(createStructuralVolatility("Normal", normalNanoEntities));

		if (!oftenNanoEntities.isEmpty())
			compatibilities.getStructuralVolatility().add(createStructuralVolatility("Often", oftenNanoEntities));

		if (!rarelyNanoEntities.isEmpty())
			compatibilities.getStructuralVolatility().add(createStructuralVolatility("Rarely", rarelyNanoEntities));
	}

	private void buildContentVolatilitySets(Compatibilities compatibilities) {
		compatibilities.getContentVolatility().clear();

		Set<String> normalNanoEntities = getNanoentities4AggregatePredicate(agg -> agg.getContentVolatility() == Volatility.NORMAL);
		Set<String> oftenNanoEntities = getNanoentities4AggregatePredicate(agg -> agg.getContentVolatility() == Volatility.OFTEN);
		Set<String> rarelyNanoEntities = getNanoentities4AggregatePredicate(agg -> agg.getContentVolatility() == Volatility.RARELY);

		if (!normalNanoEntities.isEmpty())
			compatibilities.getContentVolatility().add(createContentVolatility("Regularly", normalNanoEntities));

		if (!oftenNanoEntities.isEmpty())
			compatibilities.getContentVolatility().add(createContentVolatility("Often", oftenNanoEntities));

		if (!rarelyNanoEntities.isEmpty())
			compatibilities.getContentVolatility().add(createContentVolatility("Rarely", rarelyNanoEntities));
	}

	private void buildAvailabilityCriticalitySets(Compatibilities compatibilities) {
		compatibilities.getAvailabilityCriticality().clear();

		Set<String> normalNanoEntities = getNanoentities4AggregatePredicate(agg -> agg.getAvailabilityCriticality() == Criticality.NORMAL);
		Set<String> highNanoEntities = getNanoentities4AggregatePredicate(agg -> agg.getAvailabilityCriticality() == Criticality.HIGH);
		Set<String> lowNanoEntities = getNanoentities4AggregatePredicate(agg -> agg.getAvailabilityCriticality() == Criticality.LOW);

		if (!normalNanoEntities.isEmpty())
			compatibilities.getAvailabilityCriticality().add(createAvailabilityCriticality("Normal", normalNanoEntities));

		if (!highNanoEntities.isEmpty())
			compatibilities.getAvailabilityCriticality().add(createAvailabilityCriticality("Critical", highNanoEntities));

		if (!lowNanoEntities.isEmpty())
			compatibilities.getAvailabilityCriticality().add(createAvailabilityCriticality("Low", lowNanoEntities));
	}

	private void buildConsistencyCriticalitySets(Compatibilities compatibilities) {
		compatibilities.getConsistencyCriticality().clear();

		Set<String> normalNanoEntities = getNanoentities4AggregatePredicate(agg -> agg.getConsistencyCriticality() == Criticality.NORMAL);
		Set<String> highNanoEntities = getNanoentities4AggregatePredicate(agg -> agg.getConsistencyCriticality() == Criticality.HIGH);
		Set<String> lowNanoEntities = getNanoentities4AggregatePredicate(agg -> agg.getConsistencyCriticality() == Criticality.LOW);

		if (!normalNanoEntities.isEmpty())
			compatibilities.getConsistencyCriticality().add(createConsistencyCriticality("Eventually", normalNanoEntities));

		if (!highNanoEntities.isEmpty())
			compatibilities.getConsistencyCriticality().add(createConsistencyCriticality("High", highNanoEntities));

		if (!lowNanoEntities.isEmpty())
			compatibilities.getConsistencyCriticality().add(createConsistencyCriticality("Weak", lowNanoEntities));
	}

	private void buildStorageSimilaritySets(Compatibilities compatibilities) {
		compatibilities.getStorageSimilarity().clear();

		Set<String> normalNanoEntities = getNanoentities4AggregatePredicate(agg -> agg.getStorageSimilarity() == Similarity.NORMAL);
		Set<String> highNanoEntities = getNanoentities4AggregatePredicate(agg -> agg.getStorageSimilarity() == Similarity.HUGE);
		Set<String> lowNanoEntities = getNanoentities4AggregatePredicate(agg -> agg.getStorageSimilarity() == Similarity.TINY);

		if (!normalNanoEntities.isEmpty())
			compatibilities.getStorageSimilarity().add(createStorageSimilarity("Normal", normalNanoEntities));

		if (!highNanoEntities.isEmpty())
			compatibilities.getStorageSimilarity().add(createStorageSimilarity("Huge", highNanoEntities));

		if (!lowNanoEntities.isEmpty())
			compatibilities.getStorageSimilarity().add(createStorageSimilarity("Tiny", lowNanoEntities));
	}

	private void buildSecurityCriticalitySets(Compatibilities compatibilities) {
		compatibilities.getSecurityCriticality().clear();

		Set<String> normalNanoEntities = getNanoentities4AggregatePredicate(agg -> agg.getSecurityCriticality() == Criticality.NORMAL);
		Set<String> highNanoEntities = getNanoentities4AggregatePredicate(agg -> agg.getSecurityCriticality() == Criticality.HIGH);
		Set<String> lowNanoEntities = getNanoentities4AggregatePredicate(agg -> agg.getSecurityCriticality() == Criticality.LOW);

		if (!normalNanoEntities.isEmpty())
			compatibilities.getSecurityCriticality().add(createSecurityCriticality("Internal", normalNanoEntities));

		if (!highNanoEntities.isEmpty())
			compatibilities.getSecurityCriticality().add(createSecurityCriticality("Critical", highNanoEntities));

		if (!lowNanoEntities.isEmpty())
			compatibilities.getSecurityCriticality().add(createSecurityCriticality("Public", lowNanoEntities));
	}

	private StructuralVolatility createStructuralVolatility(String characteristic, Set<String> nanoentities) {
		StructuralVolatility structuralVolatility = ServiceCutterConfigurationDSLFactory.eINSTANCE.createStructuralVolatility();
		structuralVolatility.setCharacteristic(characteristic);
		structuralVolatility.getNanoentities().addAll(nanoentities);
		return structuralVolatility;
	}

	private ContentVolatility createContentVolatility(String characteristic, Set<String> nanoentities) {
		ContentVolatility contentVolatility = ServiceCutterConfigurationDSLFactory.eINSTANCE.createContentVolatility();
		contentVolatility.setCharacteristic(characteristic);
		contentVolatility.getNanoentities().addAll(nanoentities);
		return contentVolatility;
	}

	private AvailabilityCriticality createAvailabilityCriticality(String characteristic, Set<String> nanoentities) {
		AvailabilityCriticality availabilityCriticality = ServiceCutterConfigurationDSLFactory.eINSTANCE.createAvailabilityCriticality();
		availabilityCriticality.setCharacteristic(characteristic);
		availabilityCriticality.getNanoentities().addAll(nanoentities);
		return availabilityCriticality;
	}

	private ConsistencyCriticality createConsistencyCriticality(String characteristic, Set<String> nanoentities) {
		ConsistencyCriticality consistencyCriticality = ServiceCutterConfigurationDSLFactory.eINSTANCE.createConsistencyCriticality();
		consistencyCriticality.setCharacteristic(characteristic);
		consistencyCriticality.getNanoentities().addAll(nanoentities);
		return consistencyCriticality;
	}

	private StorageSimilarity createStorageSimilarity(String characteristic, Set<String> nanoentities) {
		StorageSimilarity storageSimilarity = ServiceCutterConfigurationDSLFactory.eINSTANCE.createStorageSimilarity();
		storageSimilarity.setCharacteristic(characteristic);
		storageSimilarity.getNanoentities().addAll(nanoentities);
		return storageSimilarity;
	}

	private SecurityCriticality createSecurityCriticality(String characteristic, Set<String> nanoentities) {
		SecurityCriticality securityCriticality = ServiceCutterConfigurationDSLFactory.eINSTANCE.createSecurityCriticality();
		securityCriticality.setCharacteristic(characteristic);
		securityCriticality.getNanoentities().addAll(nanoentities);
		return securityCriticality;
	}

	private Set<String> getNanoentities4AggregatePredicate(Predicate<Aggregate> aggregatePredicate) {
		Set<String> nanoentities = Sets.newHashSet();
		for (Aggregate aggregate : resolvingHelper.resolveAllAggregates().stream().filter(aggregatePredicate).collect(Collectors.toList()))
			nanoentities.addAll(nanoentityResolver.getAllNanoentities(aggregate));
		return nanoentities;
	}

}

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
import org.contextmapper.dsl.contextMappingDSL.Volatility;
import org.contextmapper.dsl.generator.servicecutter.input.nanoentities.NanoentityResolver;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.Compatibilities;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ContentVolatility;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterConfigurationDSLFactory;
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

	private Set<String> getNanoentities4AggregatePredicate(Predicate<Aggregate> aggregatePredicate) {
		Set<String> nanoentities = Sets.newHashSet();
		for (Aggregate aggregate : resolvingHelper.resolveAllAggregates().stream().filter(aggregatePredicate).collect(Collectors.toList()))
			nanoentities.addAll(nanoentityResolver.getAllNanoentities(aggregate));
		return nanoentities;
	}

}

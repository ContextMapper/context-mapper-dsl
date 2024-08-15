/*
 * Copyright 2024 The Context Mapper Project Team
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
package org.contextmapper.dsl.generator.plantuml.value_impact_mapping;

import java.util.List;

import org.contextmapper.dsl.contextMappingDSL.Consequence;
import org.contextmapper.dsl.contextMappingDSL.ValueCluster;
import org.contextmapper.dsl.contextMappingDSL.ValueElicitation;
import org.contextmapper.dsl.contextMappingDSL.ValueRegister;
import org.contextmapper.dsl.generator.plantuml.value_impact_mapping.model.ConsequenceOnValue;
import org.contextmapper.dsl.generator.plantuml.value_impact_mapping.model.MitigationAction;
import org.contextmapper.dsl.generator.plantuml.value_impact_mapping.model.Stakeholder;
import org.contextmapper.dsl.generator.plantuml.value_impact_mapping.model.SystemOfInterest;
import org.contextmapper.dsl.generator.plantuml.value_impact_mapping.model.Value;

public class CML2ValueImpactModelMapper {

	private static final String DEFAULT_SOI_NAME = "System of Interest (SOI)";

	private SystemOfInterest soi;

	public SystemOfInterest map(final ValueRegister valueRegister) {
		soi = new SystemOfInterest(
				valueRegister.getContext() != null ? valueRegister.getContext().getName() : DEFAULT_SOI_NAME);

		if (valueRegister.getValueClusters().isEmpty() && valueRegister.getValues().isEmpty())
			return soi;

		for (ValueCluster valueCluster : valueRegister.getValueClusters()) {
			mapValueCluster(valueCluster);
		}
		for (org.contextmapper.dsl.contextMappingDSL.Value value : valueRegister.getValues()) {
			mapValue(value);
		}

		return soi;
	}

	private void mapValueCluster(final ValueCluster cluster) {
		for (ValueElicitation elicitation : cluster.getElicitations()) {
			mapStakeholderElicitedValue(
					cluster.getCoreValue7000() != null ? cluster.getCoreValue7000().toString() : cluster.getCoreValue(),
					cluster.getDemonstrators(), elicitation);
		}
		for (org.contextmapper.dsl.contextMappingDSL.Value value : cluster.getValues()) {
			mapValue(value);
		}
	}

	private void mapValue(final org.contextmapper.dsl.contextMappingDSL.Value value) {
		for (ValueElicitation elicitation : value.getElicitations()) {
			mapStakeholderElicitedValue(value.getName(), value.getDemonstrators(), elicitation);
		}
	}

	private void mapStakeholderElicitedValue(final String valueName, final List<String> demonstrators,
			final ValueElicitation elicitation) {
		String stakeholderDescription = elicitation
				.getStakeholder() instanceof org.contextmapper.dsl.contextMappingDSL.Stakeholder
						? ((org.contextmapper.dsl.contextMappingDSL.Stakeholder) elicitation.getStakeholder())
								.getDescription()
						: null;
		Stakeholder stakeholder = soi.getOrCreateStakeholder(elicitation.getStakeholder().getName(),
				stakeholderDescription);
		if (elicitation.getConsequences().isEmpty()) {
			Value value = createValueObject(valueName, demonstrators, elicitation);
			value.setConsequenceType(ConsequenceOnValue.NEUTRAL);
			stakeholder.addValue(value);
		}
		for (Consequence consequence : elicitation.getConsequences()) {
			Value value = createValueObject(valueName, demonstrators, elicitation);
			value.setConsequenceType(ConsequenceOnValue.valueOfLowerCase(consequence.getType()));
			if (consequence.getConsequence() != null && !"".equals(consequence.getConsequence()))
				value.setConsequence(consequence.getConsequence());
			if (consequence.getAction() != null)
				value.addMitigationAction(
						new MitigationAction(consequence.getAction().getType(), consequence.getAction().getAction()));
			stakeholder.addValue(value);
		}
	}

	private Value createValueObject(final String valueName, final List<String> demonstrators,
			final ValueElicitation elicitation) {
		Value value = new Value(valueName);
		value.setPriority(elicitation.getPriority());
		value.setImpact(elicitation.getImpact());
		value.addDemonstrators(demonstrators);
		return value;
	}

}

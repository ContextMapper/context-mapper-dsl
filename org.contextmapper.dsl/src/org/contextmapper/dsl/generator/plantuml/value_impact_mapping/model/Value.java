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
package org.contextmapper.dsl.generator.plantuml.value_impact_mapping.model;

import java.util.Collections;
import java.util.List;

import org.contextmapper.dsl.contextMappingDSL.IMPACT;
import org.contextmapper.dsl.contextMappingDSL.PRIORITY;

import com.google.common.collect.Lists;

public class Value {

	private final String name;
	private PRIORITY priority;
	private IMPACT impact;
	private final List<String> demonstrators;
	private ConsequenceOnValue consequenceType;
	private String consequence;
	private final List<MitigationAction> mitigationActions;

	public Value(final String name) {
		this.name = name;
		this.demonstrators = Lists.newArrayList();
		this.mitigationActions = Lists.newArrayList();
	}

	public String getName() {
		return name;
	}

	public void setImpact(IMPACT impact) {
		this.impact = impact;
	}

	public IMPACT getImpact() {
		return impact;
	}

	public void setPriority(PRIORITY priority) {
		this.priority = priority;
	}

	public PRIORITY getPriority() {
		return priority;
	}

	public List<String> getDemonstrators() {
		return Collections.unmodifiableList(demonstrators);
	}

	public String getConsequenceType() {
		return this.consequenceType.name();
	}

	public String getConsequence() {
		return consequence;
	}

	public void addDemonstrators(final List<String> demonstrators) {
		this.demonstrators.addAll(demonstrators);
	}

	public void setConsequence(String consequence) {
		this.consequence = consequence;
	}

	public void setConsequenceType(ConsequenceOnValue consequenceType) {
		this.consequenceType = consequenceType;
	}

	public List<MitigationAction> getMitigationActions() {
		return Collections.unmodifiableList(mitigationActions);
	}

	public void addMitigationAction(final MitigationAction action) {
		this.mitigationActions.add(action);
	}

}

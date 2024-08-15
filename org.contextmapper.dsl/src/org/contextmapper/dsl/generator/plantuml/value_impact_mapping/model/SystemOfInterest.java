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
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;

public class SystemOfInterest {

	private final String name;
	private final Set<Stakeholder> stakeholders;

	public SystemOfInterest(final String name) {
		this.name = name;
		this.stakeholders = Sets.newLinkedHashSet();
	}

	public String getName() {
		return name;
	}

	public Set<Stakeholder> getStakeholders() {
		return Collections.unmodifiableSet(stakeholders);
	}

	public Stakeholder getOrCreateStakeholder(final String name, final String description) {
		Optional<Stakeholder> optStakeholder = stakeholders.stream().filter(s -> s.getName().equals(name)).findFirst();
		if (optStakeholder.isPresent())
			return optStakeholder.get();

		Stakeholder stakeholder = new Stakeholder(name, description);
		this.stakeholders.add(stakeholder);
		return stakeholder;
	}

}

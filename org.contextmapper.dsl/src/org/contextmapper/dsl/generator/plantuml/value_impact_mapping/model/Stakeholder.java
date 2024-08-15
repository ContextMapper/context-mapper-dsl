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
import java.util.Objects;

import com.google.common.collect.Lists;

public class Stakeholder {

	private final String name;
	private final String description;
	private final List<Value> values;

	public Stakeholder(final String name, final String description) {
		this.name = name;
		this.description = description;
		this.values = Lists.newArrayList();
	}

	public String getName() {
		return name;
	}

	public List<Value> getValues() {
		return Collections.unmodifiableList(values);
	}

	public void addValue(final Value value) {
		this.values.add(value);
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Stakeholder other = (Stakeholder) obj;
		return Objects.equals(name, other.name);
	}

}

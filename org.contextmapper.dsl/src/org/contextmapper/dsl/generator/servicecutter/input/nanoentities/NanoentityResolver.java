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
package org.contextmapper.dsl.generator.servicecutter.input.nanoentities;

import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;

import com.google.common.collect.Sets;

public class NanoentityResolver {

	public Set<String> getAllNanoentities(BoundedContext bc) {
		Set<String> nanoentities = Sets.newHashSet();
		for (Aggregate aggregate : bc.getAggregates()) {
			nanoentities.addAll(getAllNanoentities(aggregate));
		}
		return nanoentities;
	}

	public Set<String> getAllNanoentities(Aggregate aggregate) {
		Set<String> nanoentities = Sets.newHashSet();
		for (DomainObject domainObject : aggregate.getDomainObjects().stream().filter(o -> o instanceof DomainObject).map(o -> (DomainObject) o).collect(Collectors.toSet())) {
			nanoentities.addAll(getAllNanoentities(domainObject));
		}
		return nanoentities;
	}

	public Set<String> getAllNanoentities(DomainObject domainObject) {
		Set<String> nanoentities = Sets.newHashSet();
		for (Attribute attr : domainObject.getAttributes()) {
			nanoentities.add(domainObject.getName() + "." + attr.getName());
		}
		return nanoentities;
	}

}

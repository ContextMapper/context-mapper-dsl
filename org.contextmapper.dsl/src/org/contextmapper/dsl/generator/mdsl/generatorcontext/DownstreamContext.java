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
package org.contextmapper.dsl.generator.mdsl.generatorcontext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.DownstreamRole;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DownstreamContext {
	private BoundedContext downstreamContext;
	private List<Aggregate> consumedAggregates = Lists.newArrayList();
	private Set<DownstreamRole> downstreamRoles = Sets.newHashSet();

	DownstreamContext(BoundedContext downstreamContext) {
		this.downstreamContext = downstreamContext;
	}

	public String getDownstreamName() {
		return downstreamContext.getName();
	}

	public void addConsumedAggregates(List<Aggregate> consumedAggregates) {
		for (Aggregate aggregate : consumedAggregates) {
			if (!this.consumedAggregates.stream().map(agg -> agg.getName()).collect(Collectors.toList()).contains(aggregate.getName()))
				this.consumedAggregates.add(aggregate);
		}
	}

	public List<Aggregate> getConsumedAggregates() {
		return consumedAggregates;
	}

	public void addDownstreamRoles(List<DownstreamRole> downstreamRoles) {
		this.downstreamRoles.addAll(downstreamRoles);
	}

	public Set<DownstreamRole> getDownstreamRoles() {
		return downstreamRoles;
	}

	public String getDomainVisionStatement() {
		return this.downstreamContext.getDomainVisionStatement();
	}

}
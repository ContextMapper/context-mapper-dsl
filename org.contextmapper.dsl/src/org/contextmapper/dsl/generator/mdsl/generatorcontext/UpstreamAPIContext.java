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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamRole;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class UpstreamAPIContext {
	private String apiName;
	private BoundedContext upstreamContext;
	private List<UpstreamRole> upstreamRoles = Lists.newArrayList();
	private List<Aggregate> exposedAggregates = Lists.newArrayList();
	private List<String> implementationTechnologies = Lists.newArrayList();
	private Map<String, DownstreamContext> downstreamContexts = Maps.newHashMap();

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public String getApiName() {
		return apiName;
	}

	public void setUpstreamContext(BoundedContext upstreamContext) {
		this.upstreamContext = upstreamContext;
	}

	public BoundedContext getUpstreamContext() {
		return upstreamContext;
	}
	
	public List<UpstreamRole> getUpstreamRoles() {
		return this.upstreamRoles;
	}

	public List<Aggregate> getExposedAggregates() {
		return exposedAggregates;
	}

	public List<String> getImplementationTechnologies() {
		return implementationTechnologies;
	}

	public String getJoinedImplementationTechnologies() {
		return String.join(", ", new HashSet<>(implementationTechnologies));
	}

	public void addDownstreamContext4Relationship(UpstreamDownstreamRelationship relationship) {
		String downStreamName = relationship.getDownstream().getName();
		if(!downstreamContexts.containsKey(downStreamName)) {
			this.downstreamContexts.put(downStreamName, new DownstreamContext(relationship.getDownstream()));
		}
		this.downstreamContexts.get(downStreamName).addConsumedAggregates(relationship.getUpstreamExposedAggregates());
		this.downstreamContexts.get(downStreamName).addDownstreamRoles(relationship.getDownstreamRoles());
	}
	
	public Collection<DownstreamContext> getDownstreamContexts() {
		return downstreamContexts.values();
	}

}
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
package org.contextmapper.dsl.generator.contextmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.contextmapper.contextmap.generator.model.AbstractRelationship;
import org.contextmapper.contextmap.generator.model.BoundedContext;
import org.contextmapper.contextmap.generator.model.BoundedContextType;
import org.contextmapper.contextmap.generator.model.ContextMap;
import org.contextmapper.contextmap.generator.model.DownstreamPatterns;
import org.contextmapper.contextmap.generator.model.Partnership;
import org.contextmapper.contextmap.generator.model.Relationship;
import org.contextmapper.contextmap.generator.model.SharedKernel;
import org.contextmapper.contextmap.generator.model.UpstreamDownstreamRelationship;
import org.contextmapper.contextmap.generator.model.UpstreamPatterns;
import org.contextmapper.dsl.contextMappingDSL.DownstreamRole;
import org.contextmapper.dsl.contextMappingDSL.UpstreamRole;

/**
 * Converts a CML Context Map into the Context Map Generator model.
 * 
 * @author Stefan Kapferer
 */
public class ContextMapModelConverter {

	private Map<String, BoundedContext> bcMap = new HashMap<>();
	private boolean useNameAndImplementationTechnologyLabels = true;

	/**
	 * Converts the CML Context Map into a Context Map Generator Context Map.
	 * 
	 * @param cmlContextMap the CML Context Map for which you want to generate a
	 *                      graphical Context Map
	 * @return the Context Map Generator Context Map
	 */
	public ContextMap convert(org.contextmapper.dsl.contextMappingDSL.ContextMap cmlContextMap) {
		ContextMap contextMap = new ContextMap();

		for (org.contextmapper.dsl.contextMappingDSL.BoundedContext cmlBC : cmlContextMap.getBoundedContexts()) {
			contextMap.addBoundedContext(convert(cmlBC));
		}

		for (org.contextmapper.dsl.contextMappingDSL.Relationship cmlRelationship : cmlContextMap.getRelationships()) {
			contextMap.addRelationship(convert(cmlRelationship));
		}

		for (org.contextmapper.dsl.contextMappingDSL.BoundedContext team : cmlContextMap.getBoundedContexts().stream()
				.filter(bc -> bc.getType() == org.contextmapper.dsl.contextMappingDSL.BoundedContextType.TEAM && bc.getRealizedBoundedContexts() != null && !bc.getRealizedBoundedContexts().isEmpty())
				.collect(Collectors.toList())) {
			setRealizedContexts4Team(team, team.getRealizedBoundedContexts());
		}

		return contextMap;
	}

	/**
	 * Converts the CML Context Map into a Context Map Generator Context Map.
	 * 
	 * @param cmlContextMap                            the CML Context Map for which
	 *                                                 you want to generate a
	 *                                                 graphical Context Map
	 * @param useNameAndImplementationTechnologyLabels defines whether name and
	 *                                                 implementation technology on
	 *                                                 relationships shall be mapped
	 *                                                 or not
	 * @return the Context Map Generator Context Map
	 */
	public ContextMap convert(org.contextmapper.dsl.contextMappingDSL.ContextMap cmlContextMap, boolean useNameAndImplementationTechnologyLabels) {
		this.useNameAndImplementationTechnologyLabels = useNameAndImplementationTechnologyLabels;
		return convert(cmlContextMap);
	}

	private BoundedContext convert(org.contextmapper.dsl.contextMappingDSL.BoundedContext cmlBoundedContext) {
		BoundedContextType type = BoundedContextType.GENERIC;
		if (cmlBoundedContext.getType().equals(org.contextmapper.dsl.contextMappingDSL.BoundedContextType.TEAM))
			type = BoundedContextType.TEAM;

		BoundedContext boundedContext = new BoundedContext(cmlBoundedContext.getName(), type);
		this.bcMap.put(cmlBoundedContext.getName(), boundedContext);
		return boundedContext;
	}

	private Relationship convert(org.contextmapper.dsl.contextMappingDSL.Relationship cmlRelationship) {
		AbstractRelationship relationship = null;

		if (cmlRelationship instanceof org.contextmapper.dsl.contextMappingDSL.Partnership) {
			org.contextmapper.dsl.contextMappingDSL.Partnership cmlPartnership = (org.contextmapper.dsl.contextMappingDSL.Partnership) cmlRelationship;
			relationship = new Partnership(bcMap.get(cmlPartnership.getParticipant1().getName()), bcMap.get(cmlPartnership.getParticipant2().getName()));
		} else if (cmlRelationship instanceof org.contextmapper.dsl.contextMappingDSL.SharedKernel) {
			org.contextmapper.dsl.contextMappingDSL.SharedKernel cmlSharedKernel = (org.contextmapper.dsl.contextMappingDSL.SharedKernel) cmlRelationship;
			relationship = new SharedKernel(bcMap.get(cmlSharedKernel.getParticipant1().getName()), bcMap.get(cmlSharedKernel.getParticipant2().getName()));
		} else if (cmlRelationship instanceof org.contextmapper.dsl.contextMappingDSL.CustomerSupplierRelationship) {
			org.contextmapper.dsl.contextMappingDSL.CustomerSupplierRelationship cmlCustomerSupplier = (org.contextmapper.dsl.contextMappingDSL.CustomerSupplierRelationship) cmlRelationship;
			relationship = new UpstreamDownstreamRelationship(bcMap.get(cmlCustomerSupplier.getUpstream().getName()), bcMap.get(cmlCustomerSupplier.getDownstream().getName()));
			((UpstreamDownstreamRelationship) relationship).setCustomerSupplier(true);
			((UpstreamDownstreamRelationship) relationship).setUpstreamPatterns(convertUpstreamRoles(cmlCustomerSupplier.getUpstreamRoles()));
			((UpstreamDownstreamRelationship) relationship).setDownstreamPatterns(convertDownstreamRoles(cmlCustomerSupplier.getDownstreamRoles()));
		} else {
			org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship cmlUpstreamDownstream = (org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship) cmlRelationship;
			relationship = new UpstreamDownstreamRelationship(bcMap.get(cmlUpstreamDownstream.getUpstream().getName()), bcMap.get(cmlUpstreamDownstream.getDownstream().getName()));
			((UpstreamDownstreamRelationship) relationship).setUpstreamPatterns(convertUpstreamRoles(cmlUpstreamDownstream.getUpstreamRoles()));
			((UpstreamDownstreamRelationship) relationship).setDownstreamPatterns(convertDownstreamRoles(cmlUpstreamDownstream.getDownstreamRoles()));
		}
		if (useNameAndImplementationTechnologyLabels && cmlRelationship.getName() != null)
			relationship.setName(cmlRelationship.getName());
		if (useNameAndImplementationTechnologyLabels && cmlRelationship.getImplementationTechnology() != null)
			relationship.setImplementationTechnology(cmlRelationship.getImplementationTechnology());

		return relationship;
	}

	private void setRealizedContexts4Team(org.contextmapper.dsl.contextMappingDSL.BoundedContext cmlTeam, List<org.contextmapper.dsl.contextMappingDSL.BoundedContext> cmlRealizedBoundedContexts) {
		BoundedContext team = bcMap.get(cmlTeam.getName());
		for (org.contextmapper.dsl.contextMappingDSL.BoundedContext realizedBC : cmlRealizedBoundedContexts) {
			team.realizing(bcMap.get(realizedBC.getName()));
		}
	}

	private UpstreamPatterns[] convertUpstreamRoles(List<UpstreamRole> upstreamRoles) {
		List<UpstreamPatterns> upstreamPatterns = new ArrayList<>();
		if (upstreamRoles.contains(UpstreamRole.OPEN_HOST_SERVICE))
			upstreamPatterns.add(UpstreamPatterns.OPEN_HOST_SERVICE);
		if (upstreamRoles.contains(UpstreamRole.PUBLISHED_LANGUAGE))
			upstreamPatterns.add(UpstreamPatterns.PUBLISHED_LANGUAGE);
		return upstreamPatterns.toArray(new UpstreamPatterns[upstreamPatterns.size()]);
	}

	private DownstreamPatterns[] convertDownstreamRoles(List<DownstreamRole> downstreamRoles) {
		List<DownstreamPatterns> downstreamPatterns = new ArrayList<>();
		if (downstreamRoles.contains(DownstreamRole.ANTICORRUPTION_LAYER))
			downstreamPatterns.add(DownstreamPatterns.ANTICORRUPTION_LAYER);
		if (downstreamRoles.contains(DownstreamRole.CONFORMIST))
			downstreamPatterns.add(DownstreamPatterns.CONFORMIST);
		return downstreamPatterns.toArray(new DownstreamPatterns[downstreamPatterns.size()]);
	}

}

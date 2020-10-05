/*
 * Copyright 2018 The Context Mapper Project Team
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
package org.contextmapper.dsl.validation;

import static org.contextmapper.dsl.contextMappingDSL.BoundedContextType.TEAM;
import static org.contextmapper.dsl.contextMappingDSL.ContextMapType.ORGANIZATIONAL;
import static org.contextmapper.dsl.contextMappingDSL.ContextMapType.SYSTEM_LANDSCAPE;
import static org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage.Literals.CONTEXT_MAP__BOUNDED_CONTEXTS;
import static org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage.Literals.CONTEXT_MAP__TYPE;
import static org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage.Literals.SYMMETRIC_RELATIONSHIP__PARTICIPANT1;
import static org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage.Literals.SYMMETRIC_RELATIONSHIP__PARTICIPANT2;
import static org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage.Literals.UPSTREAM_DOWNSTREAM_RELATIONSHIP__DOWNSTREAM;
import static org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage.Literals.UPSTREAM_DOWNSTREAM_RELATIONSHIP__UPSTREAM;
import static org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage.Literals.UPSTREAM_DOWNSTREAM_RELATIONSHIP__UPSTREAM_EXPOSED_AGGREGATES;
import static org.contextmapper.dsl.validation.ValidationMessages.EXPOSED_AGGREGATE_NOT_PART_OF_UPSTREAM_CONTEXT;
import static org.contextmapper.dsl.validation.ValidationMessages.ORGANIZATIONAL_MAP_DOES_NOT_CONTAIN_TEAM;
import static org.contextmapper.dsl.validation.ValidationMessages.RELATIONSHIP_CONTEXT_NOT_ON_MAP_ERROR_MESSAGE;
import static org.contextmapper.dsl.validation.ValidationMessages.SYSTEM_LANDSCAPE_MAP_CONTAINS_TEAM;

import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.contextMappingDSL.SymmetricRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

public class ContextMapSemanticsValidator extends AbstractDeclarativeValidator {

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}

	@Check
	public void validateThatRelationshipContextArePartOfMap(final ContextMap map) {
		for (Relationship relationship : map.getRelationships()) {
			BoundedContext context1 = null;
			BoundedContext context2 = null;
			EReference attributeRelContext1 = null;
			EReference attributeRelContext2 = null;
			if (relationship instanceof SymmetricRelationship) {
				context1 = ((SymmetricRelationship) relationship).getParticipant1();
				context2 = ((SymmetricRelationship) relationship).getParticipant2();
				attributeRelContext1 = SYMMETRIC_RELATIONSHIP__PARTICIPANT1;
				attributeRelContext2 = SYMMETRIC_RELATIONSHIP__PARTICIPANT2;
			} else if (relationship instanceof UpstreamDownstreamRelationship) {
				context1 = ((UpstreamDownstreamRelationship) relationship).getUpstream();
				context2 = ((UpstreamDownstreamRelationship) relationship).getDownstream();
				attributeRelContext1 = UPSTREAM_DOWNSTREAM_RELATIONSHIP__UPSTREAM;
				attributeRelContext2 = UPSTREAM_DOWNSTREAM_RELATIONSHIP__DOWNSTREAM;
			}

			if (context1 != null && !isContextPartOfMap(map, context1))
				error(String.format(RELATIONSHIP_CONTEXT_NOT_ON_MAP_ERROR_MESSAGE, context1.getName()), relationship, attributeRelContext1);
			if (context2 != null && !isContextPartOfMap(map, context2))
				error(String.format(RELATIONSHIP_CONTEXT_NOT_ON_MAP_ERROR_MESSAGE, context2.getName()), relationship, attributeRelContext2);
		}
	}

	@Check
	public void validateThatExposedAggregateIsPartOfUpstreamContext(final ContextMap map) {
		for (Relationship rel : map.getRelationships()) {
			if (rel instanceof UpstreamDownstreamRelationship) {
				UpstreamDownstreamRelationship relationship = (UpstreamDownstreamRelationship) rel;
				BoundedContext upstreamContext = ((UpstreamDownstreamRelationship) relationship).getUpstream();
				int aggregateRefIndex = 0;
				for (Aggregate aggregate : relationship.getUpstreamExposedAggregates()) {
					List<String> aggregates = upstreamContext.getAggregates().stream().map(a -> a.getName()).collect(Collectors.toList());
					for (SculptorModule module : upstreamContext.getModules()) {
						aggregates.addAll(module.getAggregates().stream().map(b -> b.getName()).collect(Collectors.toList()));
					}
					if (!aggregates.contains(aggregate.getName()))
						error(String.format(EXPOSED_AGGREGATE_NOT_PART_OF_UPSTREAM_CONTEXT, aggregate.getName(), upstreamContext.getName()), relationship,
								UPSTREAM_DOWNSTREAM_RELATIONSHIP__UPSTREAM_EXPOSED_AGGREGATES, aggregateRefIndex);
					aggregateRefIndex++;
				}
			}
		}
	}

	@Check
	public void validateBoundedContextTypes(final ContextMap map) {
		if (ORGANIZATIONAL.equals(map.getType())) {
			if (!map.getBoundedContexts().stream().anyMatch(bc -> bc.getType() == BoundedContextType.TEAM))
				warning(ORGANIZATIONAL_MAP_DOES_NOT_CONTAIN_TEAM, map, CONTEXT_MAP__TYPE);
		} else if (SYSTEM_LANDSCAPE.equals(map.getType())) {
			for (BoundedContext bc : map.getBoundedContexts()) {
				if (TEAM.equals(bc.getType()))
					error(String.format(SYSTEM_LANDSCAPE_MAP_CONTAINS_TEAM), map, CONTEXT_MAP__BOUNDED_CONTEXTS);
			}
		}
	}

	private boolean isContextPartOfMap(ContextMap map, BoundedContext context) {
		return map.getBoundedContexts().contains(context);
	}
}

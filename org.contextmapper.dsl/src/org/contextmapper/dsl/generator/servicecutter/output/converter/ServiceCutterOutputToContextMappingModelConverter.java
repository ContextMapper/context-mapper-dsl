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
package org.contextmapper.dsl.generator.servicecutter.output.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.contextMappingDSL.SharedKernel;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;

import com.google.common.collect.Lists;

import ch.hsr.servicecutter.api.model.Service;
import ch.hsr.servicecutter.api.model.ServiceRelation;
import ch.hsr.servicecutter.api.model.SolverResult;

/**
 * Converter to convert ServiceCutter Output to CML Model.
 * 
 * @author Stefan Kapferer
 *
 */
public class ServiceCutterOutputToContextMappingModelConverter {

	private ContextMappingDSLFactory contextMappingFactory = ContextMappingDSLFactory.eINSTANCE;
	private TacticdslFactory tacticDDDFactory = TacticdslFactory.eINSTANCE;
	private Map<String, Entity> entityMap;
	private Map<String, BoundedContext> boundedContextMap;

	public ContextMappingModel convert(SolverResult serviceCutterResult) {
		this.entityMap = new HashMap<>();
		this.boundedContextMap = new HashMap<>();
		initializeEntityMap(serviceCutterResult);
		ContextMappingModel contextMappingModel = contextMappingFactory.createContextMappingModel();
		ContextMap contextMap = contextMappingFactory.createContextMap();
		for (Service service : serviceCutterResult.getServices()) {
			BoundedContext bc = createOrGetBoundedContext(service.getName());

			Aggregate aggregate = contextMappingFactory.createAggregate();
			aggregate.setName("Aggregate_" + service.getId());
			aggregate.getDomainObjects().addAll(convertEntities(service.getId(), service.getNanoentities()));
			bc.getAggregates().add(aggregate);

			contextMappingModel.getBoundedContexts().add(bc);
			contextMap.getBoundedContexts().add(bc);
		}
		contextMap.getRelationships().addAll(convertRelationships(serviceCutterResult.getRelations()));
		contextMappingModel.setMap(contextMap);
		return contextMappingModel;
	}

	private List<Entity> convertEntities(char serviceId, List<String> nanoEntities) {
		List<Entity> entities = Lists.newArrayList();
		for (String nanoEntity : nanoEntities) {
			String entityName = serviceId + "_" + nanoEntity.split("\\.")[0];
			String nanoEntityName = nanoEntity.split("\\.")[1];
			Entity entity = this.entityMap.get(entityName);
			entities.add(entity);
			Attribute attribute = tacticDDDFactory.createAttribute();
			attribute.setType("Nanoentity");
			attribute.setName(nanoEntityName);
			entity.getAttributes().add(attribute);
		}
		return entities;
	}

	private List<Relationship> convertRelationships(List<ServiceRelation> serviceRelations) {
		List<Relationship> relationships = Lists.newArrayList();
		for (ServiceRelation relation : serviceRelations) {
			if ("OUTGOING".equals(relation.getDirection().toString())) {
				relationships
						.add(createUpstreamDownstreamRelationship(createOrGetBoundedContext(relation.getServiceA()),
								createOrGetBoundedContext(relation.getServiceB())));
			} else if ("INCOMING".equals(relation.getDirection().toString())) {
				relationships
						.add(createUpstreamDownstreamRelationship(createOrGetBoundedContext(relation.getServiceB()),
								createOrGetBoundedContext(relation.getServiceA())));
			} else if ("BIDIRECTIONAL".equals(relation.getDirection().toString())) {
				relationships.add(createSharedKernelRelationship(createOrGetBoundedContext(relation.getServiceA()),
						createOrGetBoundedContext(relation.getServiceB())));
			}
		}
		return relationships;
	}

	private UpstreamDownstreamRelationship createUpstreamDownstreamRelationship(BoundedContext source,
			BoundedContext target) {
		UpstreamDownstreamRelationship relationship = contextMappingFactory.createUpstreamDownstreamRelationship();
		relationship.setUpstream(target);
		relationship.setDownstream(source);
		return relationship;
	}

	private SharedKernel createSharedKernelRelationship(BoundedContext context1, BoundedContext context2) {
		SharedKernel sharedKernel = contextMappingFactory.createSharedKernel();
		sharedKernel.setParticipant1(context1);
		sharedKernel.setParticipant2(context2);
		return sharedKernel;
	}

	private void initializeEntityMap(SolverResult serviceCutterResult) {
		for (Service service : serviceCutterResult.getServices()) {
			initializeEntityMap(service);
		}
	}

	private void initializeEntityMap(Service service) {
		for (String nanoEntity : service.getNanoentities()) {
			String entityName = nanoEntity.split("\\.")[0];
			addEntityToEntityMapIfNotExisting(service.getId() + "_" + entityName);
		}
	}

	private void addEntityToEntityMapIfNotExisting(String entityName) {
		if (!this.entityMap.containsKey(entityName)) {
			Entity entity = tacticDDDFactory.createEntity();
			entity.setName(entityName);
			this.entityMap.put(entityName, entity);
		}
	}

	private BoundedContext createOrGetBoundedContext(String serviceName) {
		String boundedContextName = getBoundedContextName(serviceName);
		if (!this.boundedContextMap.containsKey(boundedContextName)) {
			BoundedContext bc = contextMappingFactory.createBoundedContext();
			bc.setName(boundedContextName);
			this.boundedContextMap.put(boundedContextName, bc);
		}
		return this.boundedContextMap.get(boundedContextName);
	}

	private String getBoundedContextName(String serviceName) {
		return serviceName.replace(" ", "_");
	}

}

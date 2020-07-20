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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLModelObjectsResolvingHelper;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.contextMappingDSL.SharedKernel;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
	private Map<String, BoundedContext> boundedContextMap;
	private Map<String, String> attributeTypes;
	private ContextMappingModel originalModelState;
	private ContextMappingModel model;

	private CMLModelObjectsResolvingHelper resolvingHelper;

	public ServiceCutterOutputToContextMappingModelConverter() {
		this.boundedContextMap = new HashMap<>();
		this.attributeTypes = new HashMap<>();

		this.resolvingHelper = new CMLModelObjectsResolvingHelper();
	}

	public ServiceCutterOutputToContextMappingModelConverter(ContextMappingModel originalModel) {
		this();
		this.originalModelState = originalModel;
		initializeTypeMapByOriginalCMLModel(originalModel);
	}

	public ContextMappingModel convert(SolverResult serviceCutterResult) {
		model = contextMappingFactory.createContextMappingModel();
		ContextMap contextMap = contextMappingFactory.createContextMap();
		for (Service service : serviceCutterResult.getServices()) {
			BoundedContext bc = createOrGetBoundedContext(service.getName());

			Aggregate aggregate = contextMappingFactory.createAggregate();
			aggregate.setName("Aggregate_" + service.getId());
			aggregate.getDomainObjects().addAll(convertEntities(service.getId(), service.getNanoentities()));
			bc.getAggregates().add(aggregate);

			model.getBoundedContexts().add(bc);
			contextMap.getBoundedContexts().add(bc);
		}
		contextMap.getRelationships().addAll(convertRelationships(serviceCutterResult.getRelations()));
		model.setMap(contextMap);
		reconstructReferencesIfPossible();
		return model;
	}

	private List<Entity> convertEntities(char serviceId, List<String> nanoEntities) {
		Map<String, Entity> entities = Maps.newHashMap();
		for (String nanoEntity : nanoEntities) {
			String entityName = nanoEntity.split("\\.")[0];
			String nanoEntityName = nanoEntity.split("\\.")[1];
			Entity entity = createOrGetEntity(entities, entityName);

			Attribute attribute = tacticDDDFactory.createAttribute();
			if (attributeTypes.containsKey(nanoEntity))
				attribute.setType(attributeTypes.get(nanoEntity));
			else
				attribute.setType("UnknownType");
			attribute.setName(nanoEntityName);

			entity.getAttributes().add(attribute);
		}
		return Lists.newLinkedList(entities.values());
	}

	private Entity createOrGetEntity(Map<String, Entity> entities, String entityName) {
		if (entities.containsKey(entityName))
			return entities.get(entityName);
		Entity entity = TacticdslFactory.eINSTANCE.createEntity();
		entity.setName(entityName);
		entities.put(entityName, entity);
		return entity;
	}

	private List<Relationship> convertRelationships(List<ServiceRelation> serviceRelations) {
		List<Relationship> relationships = Lists.newArrayList();
		for (ServiceRelation relation : serviceRelations) {
			if ("OUTGOING".equals(relation.getDirection().toString())) {
				relationships.add(createUpstreamDownstreamRelationship(createOrGetBoundedContext(relation.getServiceA()), createOrGetBoundedContext(relation.getServiceB())));
			} else if ("INCOMING".equals(relation.getDirection().toString())) {
				relationships.add(createUpstreamDownstreamRelationship(createOrGetBoundedContext(relation.getServiceB()), createOrGetBoundedContext(relation.getServiceA())));
			} else if ("BIDIRECTIONAL".equals(relation.getDirection().toString())) {
				relationships.add(createSharedKernelRelationship(createOrGetBoundedContext(relation.getServiceA()), createOrGetBoundedContext(relation.getServiceB())));
			}
		}
		return relationships;
	}

	private UpstreamDownstreamRelationship createUpstreamDownstreamRelationship(BoundedContext source, BoundedContext target) {
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

	private void initializeTypeMapByOriginalCMLModel(ContextMappingModel model) {
		List<DomainObject> domainObjects = EcoreUtil2.getAllContentsOfType(model, DomainObject.class);
		for (DomainObject domainObject : domainObjects) {
			domainObject.getAttributes().stream().forEach(a -> {
				this.attributeTypes.put(domainObject.getName() + "." + a.getName(), a.getType());
			});
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

	private void reconstructReferencesIfPossible() {
		if (originalModelState == null)
			return;

		List<Reference> originalReferences = EcoreUtil2.eAllOfType(originalModelState, Reference.class);
		for (Reference originalReference : originalReferences) {
			DomainObject sourceObject = (DomainObject) originalReference.eContainer();
			Set<DomainObject> matchingObjectsInNewModel = findDomainObjectByName(sourceObject.getName());
			if (matchingObjectsInNewModel.size() != 1)
				continue;

			reconstructReference(matchingObjectsInNewModel.iterator().next(), originalReference, originalReference.getDomainObjectType().getName());
		}
	}

	private void reconstructReference(DomainObject sourceObject, Reference originalReference, String targetTypeName) {
		BoundedContext parentBC = resolvingHelper.resolveBoundedContext(sourceObject);
		if (parentBC == null)
			return; // in case this source object is not part of a Bounded Context

		DomainObject targetType = null;

		// try to find type of reference inside Bounded Context
		Optional<DomainObject> optTargetType = EcoreUtil2.eAllOfType(parentBC, DomainObject.class).stream().filter(obj -> obj.getName().equals(targetTypeName)).findFirst();
		if (optTargetType.isPresent())
			targetType = optTargetType.get();

		// try to find type of reference in upstream contexts
		if (targetType == null) {
			for (BoundedContext bc : resolvingHelper.resolveSharedDomainModels(model.getMap(), parentBC)) {
				optTargetType = EcoreUtil2.eAllOfType(bc, DomainObject.class).stream().filter(obj -> obj.getName().equals(targetTypeName)).findFirst();
				if (optTargetType.isPresent())
					targetType = optTargetType.get();
			}
		}

		// create reference, in case we have found the target type
		if (targetType != null) {
			Reference reference = TacticdslFactory.eINSTANCE.createReference();
			reference.setName(originalReference.getName());
			reference.setDomainObjectType(targetType);
			reference.setCollectionType(originalReference.getCollectionType());
			reference.setDoc(originalReference.getDoc());
			sourceObject.getReferences().add(reference);
		} else {
			sourceObject.setComment("/* Service Cut generator: it was not possible to reconstruct the reference '" + originalReference.getName() + "' from "
					+ sourceObject.getName() + " to " + targetTypeName + ". Please re-create that reference manually. */");
		}
	}

	private Set<DomainObject> findDomainObjectByName(String domainObjectName) {
		List<DomainObject> domainObjects = EcoreUtil2.eAllOfType(model, DomainObject.class);
		return domainObjects.stream().filter(obj -> obj.getName().equals(domainObjectName)).collect(Collectors.toSet());
	}

}

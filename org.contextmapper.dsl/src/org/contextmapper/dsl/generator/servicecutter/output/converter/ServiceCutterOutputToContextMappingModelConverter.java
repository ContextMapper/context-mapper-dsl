/*
 * Copyright 2018-2020 The Context Mapper Project Team
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
import java.util.HashSet;
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
import org.contextmapper.dsl.contextMappingDSL.Domain;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.contextMappingDSL.SharedKernel;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.contextMappingDSL.UserRequirement;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ch.hsr.servicecutter.api.ServiceCutterContext;
import ch.hsr.servicecutter.api.model.Service;
import ch.hsr.servicecutter.api.model.ServiceRelation;
import ch.hsr.servicecutter.api.model.SolverResult;
import ch.hsr.servicecutter.solver.SolverAlgorithm;
import ch.hsr.servicecutter.solver.SolverConfiguration;
import ch.hsr.servicecutter.solver.SolverPriority;

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
	private Set<String> referenceNames;
	private Map<String, DomainObject> references2Reconstruct;
	private ContextMappingModel originalModelState;
	private ContextMappingModel model;
	private ServiceCutterContext serviceCutterContext;
	private URI sclURI;

	private CMLModelObjectsResolvingHelper resolvingHelper;

	public ServiceCutterOutputToContextMappingModelConverter() {
		this.boundedContextMap = new HashMap<>();
		this.attributeTypes = new HashMap<>();
		this.referenceNames = new HashSet<>();
		this.references2Reconstruct = new HashMap<>();

		this.resolvingHelper = new CMLModelObjectsResolvingHelper();
	}

	public ServiceCutterOutputToContextMappingModelConverter(ContextMappingModel originalModel, ServiceCutterContext serviceCutterContext) {
		this();
		this.originalModelState = originalModel;
		this.serviceCutterContext = serviceCutterContext;
		initializeTypeMapByOriginalCMLModel(originalModel);
	}

	public ServiceCutterOutputToContextMappingModelConverter(ContextMappingModel originalModel, ServiceCutterContext serviceCutterContext, URI sclUri) {
		this(originalModel, serviceCutterContext);
		this.sclURI = sclUri;
	}

	public ContextMappingModel convert(SolverResult serviceCutterResult) {
		model = contextMappingFactory.createContextMappingModel();
		ContextMap contextMap = contextMappingFactory.createContextMap();
		if (originalModelState != null && originalModelState.getMap() != null) {
			contextMap.setName(originalModelState.getMap().getName());
			contextMap.setState(originalModelState.getMap().getState());
			contextMap.setType(originalModelState.getMap().getType());
		}
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
		copyRootElementsNotAffected();
		model.setTopComment(generateTopComment());
		return model;
	}

	private String generateTopComment() {
		StringBuilder sb = new StringBuilder();
		sb.append("/* This CML model has been generated with Service Cutter.");
		if (this.originalModelState != null)
			sb.append(System.lineSeparator() + " * It decomposes the original CML model " + originalModelState.eResource().getURI().toString() + ".");
		if (this.sclURI != null)
			sb.append(System.lineSeparator() + " * The following user representations file was used for the decomposition: " + sclURI.toString() + ".");
		if (this.serviceCutterContext != null) {
			SolverConfiguration config = serviceCutterContext.getSolverConfiguration();
			sb.append(System.lineSeparator() + " * ");
			sb.append(System.lineSeparator() + " * The service cut was generated with the following input parameters (.servicecutter.yml):");
			sb.append(System.lineSeparator() + " * Algorithm: " + config.getAlgorithm().name());
			for (Map.Entry<String, Double> entry : getAlgorithmParameters4Algo(config.getAlgorithmParams(), config.getAlgorithm()).entrySet())
				sb.append(System.lineSeparator() + " * " + entry.getKey() + ": " + entry.getValue().toString());
			for (Map.Entry<String, SolverPriority> entry : config.getPriorities().entrySet())
				sb.append(System.lineSeparator() + " * " + entry.getKey() + ": " + entry.getValue().name());
		}
		sb.append(System.lineSeparator() + " */");
		return sb.toString();
	}

	private Map<String, Double> getAlgorithmParameters4Algo(Map<String, Double> allParams, SolverAlgorithm algo) {
		Map<String, Double> algoParams = Maps.newHashMap();
		String prefix = getAlgoPrefix(algo);
		for (String paramKey : allParams.keySet()) {
			if (paramKey.startsWith(prefix))
				algoParams.put(paramKey, allParams.get(paramKey));
		}
		return algoParams;
	}

	private String getAlgoPrefix(SolverAlgorithm algo) {
		Map<SolverAlgorithm, String> prefixTable = Maps.newHashMap();
		prefixTable.put(SolverAlgorithm.LEUNG, "leung");
		prefixTable.put(SolverAlgorithm.CHINESE_WHISPERS, "cw");
		prefixTable.put(SolverAlgorithm.MARKOV_CLUSTERING, "mcl");
		return prefixTable.get(algo);
	}

	private void copyRootElementsNotAffected() {
		if (originalModelState == null)
			return;

		model.getUserRequirements().addAll(EcoreUtil2.copyAll(originalModelState.getUserRequirements()));
		model.getImports().addAll(EcoreUtil2.copyAll(originalModelState.getImports()));
		model.getDomains().addAll(EcoreUtil2.copyAll(originalModelState.getDomains()));
		for (Domain domain : model.getDomains())
			reconstructSubdomainToFeatureReferences(domain);
	}

	private void reconstructSubdomainToFeatureReferences(Domain domain) {
		for (Subdomain subdomain : domain.getSubdomains())
			reconstructSubdomainToFeatureReferences(subdomain);
	}

	private void reconstructSubdomainToFeatureReferences(Subdomain subDomain) {
		List<UserRequirement> urReferences = Lists.newLinkedList();
		for (UserRequirement ur : subDomain.getSupportedFeatures())
			urReferences.add(model.getUserRequirements().stream().filter(u -> u.getName().equals(ur.getName())).findFirst().get());
		subDomain.getSupportedFeatures().clear();
		subDomain.getSupportedFeatures().addAll(urReferences);
	}

	private List<Entity> convertEntities(char serviceId, List<String> nanoEntities) {
		Map<String, Entity> entities = Maps.newHashMap();
		for (String nanoEntity : nanoEntities) {
			String entityName = nanoEntity.split("\\.")[0];
			String nanoEntityName = nanoEntity.split("\\.")[1];
			Entity entity = createOrGetEntity(entities, entityName);

			// don't create attribute in case this was a reference originally
			if (referenceNames.contains(nanoEntity)) {
				references2Reconstruct.put(nanoEntity, entity);
				continue;
			}

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
			domainObject.getReferences().stream().forEach(r -> {
				this.referenceNames.add(domainObject.getName() + "." + r.getName());
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

		for (String referenceNanoEntity : references2Reconstruct.keySet()) {
			Optional<Reference> originalReference = originalReferences.stream().filter(r -> (((DomainObject) r.eContainer()).getName() + "." + r.getName()).equals(referenceNanoEntity)).findFirst();
			if (originalReference.isEmpty())
				continue;

			reconstructReference(references2Reconstruct.get(referenceNanoEntity), originalReference.get(), originalReference.get().getDomainObjectType().getName());
		}
	}

	private void reconstructReference(DomainObject sourceObject, Reference originalReference, String targetTypeName) {
		BoundedContext parentBC = resolvingHelper.resolveBoundedContext(sourceObject);
		if (parentBC == null)
			return; // in case this source object is not part of a Bounded Context

		List<DomainObject> targetDomainObjects = EcoreUtil2.eAllOfType(model, DomainObject.class).stream().filter(obj -> obj.getName().equals(targetTypeName)).collect(Collectors.toList());
		if (targetDomainObjects.size() == 1) {
			Reference reference = TacticdslFactory.eINSTANCE.createReference();
			reference.setName(originalReference.getName());
			reference.setDomainObjectType(targetDomainObjects.get(0));
			reference.setCollectionType(originalReference.getCollectionType());
			reference.setDoc(originalReference.getDoc());
			sourceObject.getReferences().add(reference);
		} else {
			sourceObject.setComment("/* Service Cut generator: it was not possible to reconstruct the reference '" + originalReference.getName() + "' from " + sourceObject.getName() + " to "
					+ targetTypeName + ". Please re-create that reference manually. */");
		}
	}

}

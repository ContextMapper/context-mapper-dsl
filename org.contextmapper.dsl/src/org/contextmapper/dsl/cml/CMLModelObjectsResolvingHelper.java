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
package org.contextmapper.dsl.cml;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.DomainPart;
import org.contextmapper.dsl.contextMappingDSL.Feature;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.contextMappingDSL.SymmetricRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.contextMappingDSL.UserRequirement;
import org.contextmapper.tactic.dsl.tacticdsl.Enum;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class CMLModelObjectsResolvingHelper {

	private ContextMappingModel rootModel;

	public CMLModelObjectsResolvingHelper(ContextMappingModel rootModel) {
		this.rootModel = rootModel;
	}

	private Set<ContextMappingModel> resolveImportedModels() {
		Set<CMLResource> importedResources = new CMLImportResolver().resolveImportedResources(new CMLResource(rootModel.eResource()));
		return importedResources.stream().map(r -> r.getContextMappingModel()).collect(Collectors.toSet());
	}

	public <T extends EObject> Set<T> resolveAllObjectsOfType(Class<T> type) {
		Set<T> resultSet = Sets.newHashSet();
		resultSet.addAll(Sets.newHashSet(IteratorExtensions.filter(EcoreUtil2.eAll(rootModel), type)));
		for (ContextMappingModel model : resolveImportedModels()) {
			resultSet.addAll(Sets.newHashSet(IteratorExtensions.filter(EcoreUtil2.eAll(model), type)));
		}
		return resultSet;
	}

	public BoundedContext resolveBoundedContext(Aggregate aggregate) {
		if (aggregate.eContainer() != null && aggregate.eContainer() instanceof SculptorModule)
			return resolveBoundedContext((SculptorModule) aggregate.eContainer());
		else
			return (BoundedContext) aggregate.eContainer();
	}

	public BoundedContext resolveBoundedContext(SculptorModule module) {
		return (BoundedContext) module.eContainer();
	}

	public BoundedContext resolveBoundedContext(SimpleDomainObject domainObject) {
		if (domainObject.eContainer() != null && domainObject.eContainer() instanceof Aggregate)
			return resolveBoundedContext((Aggregate) domainObject.eContainer());
		else if (domainObject.eContainer() != null && domainObject.eContainer() instanceof SculptorModule)
			return resolveBoundedContext((SculptorModule) domainObject.eContainer());
		else
			return null; // can happen if domain object is not part of Bounded Context but Subdomain
	}

	public BoundedContext resolveBoundedContext(EObject anyCMLObject) {
		if (anyCMLObject instanceof BoundedContext)
			return (BoundedContext) anyCMLObject;
		EObject parent = anyCMLObject.eContainer();
		while (parent != null) {
			if (parent instanceof BoundedContext)
				return (BoundedContext) parent;
			parent = parent.eContainer();
		}
		return null;
	}

	public Aggregate resolveAggregate(EObject anyCMLObject) {
		if (anyCMLObject instanceof Aggregate)
			return (Aggregate) anyCMLObject;
		EObject parent = anyCMLObject.eContainer();
		while (parent != null) {
			if (parent instanceof Aggregate)
				return (Aggregate) parent;
			parent = parent.eContainer();
		}
		return null;
	}

	public List<Aggregate> resolveAllAccessibleAggregates(BoundedContext bc) {
		List<Aggregate> aggregates = Lists.newLinkedList();
		aggregates.addAll(EcoreUtil2.eAllOfType(bc, Aggregate.class));
		ContextMap contextMap = getContextMap(bc);
		if (contextMap != null) {
			for (Relationship rel : contextMap.getRelationships()) {
				if (isBCDownstreamInRelationship(rel, bc))
					aggregates.addAll(getExposedAggregates(rel));
			}
		}
		return aggregates;
	}

	public ContextMap getContextMap(BoundedContext bc) {
		if (EcoreUtil2.getRootContainer(bc) instanceof ContextMappingModel) {
			ContextMappingModel rootModel = (ContextMappingModel) EcoreUtil2.getRootContainer(bc);
			return rootModel.getMap();
		}
		return null;
	}

	public Set<UserRequirement> resolveUserRequirements(BoundedContext boundedContext) {
		Set<UserRequirement> requirements = Sets.newHashSet();
		for (DomainPart domainPart : boundedContext.getImplementedDomainParts()) {
			if (!(domainPart instanceof Subdomain))
				continue;

			Subdomain subdomain = (Subdomain) domainPart;
			requirements.addAll(subdomain.getSupportedFeatures());
		}
		return requirements;
	}

	public Set<Feature> resolveFeatures(BoundedContext boundedContext) {
		Set<Feature> features = Sets.newHashSet();
		resolveUserRequirements(boundedContext).forEach(ur -> {
			features.addAll(ur.getFeatures());
		});
		return features;
	}

	public List<Aggregate> resolveAllAggregates() {
		List<Aggregate> allAggregates = Lists.newLinkedList();
		for (BoundedContext bc : rootModel.getBoundedContexts()) {
			allAggregates.addAll(bc.getAggregates());
		}
		return allAggregates;
	}

	public Set<String> resolveAggregateStates(Aggregate aggregate) {
		Set<String> aggregateStates = Sets.newHashSet();

		Optional<org.contextmapper.tactic.dsl.tacticdsl.Enum> optStatesEnum = aggregate.getDomainObjects().stream().filter(o -> o instanceof Enum).map(o -> (Enum) o)
				.filter(o -> o.isDefinesAggregateLifecycle()).findFirst();
		if (optStatesEnum.isPresent())
			aggregateStates.addAll(optStatesEnum.get().getValues().stream().map(v -> v.getName()).collect(Collectors.toSet()));

		return aggregateStates;
	}

	private boolean isBCDownstreamInRelationship(Relationship relationship, BoundedContext bc) {
		if (relationship instanceof SymmetricRelationship) {
			SymmetricRelationship symRel = (SymmetricRelationship) relationship;
			return symRel.getParticipant1().getName().equals(bc.getName()) || symRel.getParticipant2().getName().equals(bc.getName());
		} else if (relationship instanceof UpstreamDownstreamRelationship) {
			UpstreamDownstreamRelationship upDownRel = (UpstreamDownstreamRelationship) relationship;
			return upDownRel.getDownstream().getName().equals(bc.getName());
		}
		return false;
	}

	private List<Aggregate> getExposedAggregates(Relationship relationship) {
		List<Aggregate> aggregates = Lists.newLinkedList();
		if (relationship instanceof SymmetricRelationship) {
			aggregates.addAll(EcoreUtil2.eAllOfType(((SymmetricRelationship) relationship).getParticipant1(), Aggregate.class));
			aggregates.addAll(EcoreUtil2.eAllOfType(((SymmetricRelationship) relationship).getParticipant2(), Aggregate.class));
		} else if (relationship instanceof UpstreamDownstreamRelationship) {
			UpstreamDownstreamRelationship upDownRel = (UpstreamDownstreamRelationship) relationship;
			if (upDownRel.getUpstreamExposedAggregates() != null && !upDownRel.getUpstreamExposedAggregates().isEmpty()) {
				aggregates.addAll(upDownRel.getUpstreamExposedAggregates());
			} else {
				aggregates.addAll(EcoreUtil2.eAllOfType(upDownRel.getUpstream(), Aggregate.class));
			}
		}
		return aggregates;
	}

}

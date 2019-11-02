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
package org.contextmapper.dsl.refactoring;

import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.Partnership;
import org.contextmapper.dsl.contextMappingDSL.SymmetricRelationship;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;

/**
 * 
 * This is the 'extract' mode of the 'Suspend Partnership' AR. It extracts a new
 * Bounded Context for the common model parts and establishes
 * upstream-downstream relationships between the new and the two existing
 * Bounded Contexts.
 * 
 * @author Stefan Kapferer
 *
 */
public class ExtractPartnershipRefactoring extends AbstractExtractSymmetricRelationshipRefactoring implements Refactoring {

	public ExtractPartnershipRefactoring(String boundedContext1, String boundedContext2) {
		super(boundedContext1, boundedContext2);
	}

	@Override
	List<SymmetricRelationship> getMatchingRelationships() {
		return model.getMap().getRelationships().stream().filter(rel -> rel instanceof Partnership).map(rel -> (Partnership) rel)
				.filter(p -> (p.getParticipant1().getName().equals(boundedContext1) && p.getParticipant2().getName().equals(boundedContext2))
						|| (p.getParticipant1().getName().equals(boundedContext2) && p.getParticipant2().getName().equals(boundedContext1)))
				.collect(Collectors.toList());
	}

	@Override
	String getRelationshipType() {
		return "Partnership";
	}
	
	@Override
	protected BoundedContext createBoundedContext() {
		BoundedContext newBC = super.createBoundedContext();
		newBC.setComment("// Extracted Bounded Context for common model parts. Please specify the common model here:");
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName("CommonModelAggregate");
		Entity entity = TacticdslFactory.eINSTANCE.createEntity();
		entity.setAggregateRoot(true);
		entity.setName("CommonModelPartRoot");
		aggregate.getDomainObjects().add(entity);
		newBC.getAggregates().add(aggregate);
		return newBC;
	}

}

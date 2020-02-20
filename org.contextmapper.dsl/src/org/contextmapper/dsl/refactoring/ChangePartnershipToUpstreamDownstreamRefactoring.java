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

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.Partnership;
import org.contextmapper.dsl.contextMappingDSL.SymmetricRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;

/**
 * 
 * Removes a Partnership relationship between two Bounded Contexts and creates
 * an Upstream-Downstream relationship instead.
 * 
 * @author Stefan Kapferer
 *
 */
public class ChangePartnershipToUpstreamDownstreamRefactoring extends AbstractRefactoring implements Refactoring {

	protected String upstreamContext;
	protected String downstreamContext;

	/**
	 * Pass the two Bounded Context names within the Partnership relationship. The
	 * first partner (first parameter) will become upstream.
	 * 
	 */
	public ChangePartnershipToUpstreamDownstreamRefactoring(String upstreamContext, String downstreamContext) {
		this.upstreamContext = upstreamContext;
		this.downstreamContext = downstreamContext;
	}

	@Override
	protected void doRefactor() {
		checkPreconditions();

		ContextMap contextMap = model.getMap();

		// remove existing relationship
		removeElementFromEList(contextMap.getRelationships(), getMatchingRelationship());

		// create new upstream-downstream relationship
		UpstreamDownstreamRelationship relationship = ContextMappingDSLFactory.eINSTANCE.createUpstreamDownstreamRelationship();
		relationship.setUpstream(getBoundedContext(upstreamContext));
		relationship.setDownstream(getBoundedContext(downstreamContext));
		addElementToEList(contextMap.getRelationships(), relationship);

		// save model
		saveResources();
	}

	List<SymmetricRelationship> getMatchingRelationships() {
		return model.getMap().getRelationships().stream().filter(rel -> rel instanceof Partnership).map(rel -> (Partnership) rel)
				.filter(p -> (p.getParticipant1().getName().equals(upstreamContext) && p.getParticipant2().getName().equals(downstreamContext))
						|| (p.getParticipant1().getName().equals(downstreamContext) && p.getParticipant2().getName().equals(upstreamContext)))
				.collect(Collectors.toList());
	}

	private void checkPreconditions() {
		if (upstreamContext == null || downstreamContext == null)
			throw new RefactoringInputException("Please provide two bounded context names (input parameter was null).");

		if (upstreamContext.equals(downstreamContext))
			throw new RefactoringInputException("Please provide two different bounded context names (input parameters contain same name).");

		List<SymmetricRelationship> matchingRelationships = getMatchingRelationships();

		if (matchingRelationships.size() > 1)
			throw new RefactoringInputException("There are multiple Partnership relationships between '" + upstreamContext + "' and '" + downstreamContext + "'.");

		if (matchingRelationships.isEmpty())
			throw new RefactoringInputException("Your Context Map does not contain a Partnership relationship between '" + upstreamContext + "' and '" + downstreamContext + "'.");
	}

	private BoundedContext getBoundedContext(String name) {
		return getAllBoundedContexts().stream().filter(bc -> bc.getName().equals(name)).findFirst().get();
	}

	private SymmetricRelationship getMatchingRelationship() {
		return getMatchingRelationships().get(0);
	}

}

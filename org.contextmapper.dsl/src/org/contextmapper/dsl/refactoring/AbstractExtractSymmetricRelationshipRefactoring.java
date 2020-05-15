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
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.SymmetricRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;

/**
 * 
 * Used for 'Extract Shared Kernel' AR and the 'extract' mode of the 'Suspend
 * Partnership' AR.
 * 
 * @author Stefan Kapferer
 *
 */
public abstract class AbstractExtractSymmetricRelationshipRefactoring extends AbstractRefactoring implements SemanticCMLRefactoring {

	protected String boundedContext1;
	protected String boundedContext2;
	protected ContextMap contextMap;

	public AbstractExtractSymmetricRelationshipRefactoring(String boundedContext1, String boundedContext2) {
		this.boundedContext1 = boundedContext1;
		this.boundedContext2 = boundedContext2;
	}

	@Override
	protected void doRefactor() {
		checkPreconditions();

		this.contextMap = model.getMap();

		// remove existing relationship
		contextMap.getRelationships().remove(getMatchingRelationship());

		// create new BC for Shared Kernel or Partnership
		BoundedContext newBC = createBoundedContext();
		model.getBoundedContexts().add(newBC);
		contextMap.getBoundedContexts().add(newBC);

		// create new upstream-downstream relationships
		UpstreamDownstreamRelationship relationship1 = ContextMappingDSLFactory.eINSTANCE.createUpstreamDownstreamRelationship();
		relationship1.setUpstream(newBC);
		relationship1.setDownstream(getBoundedContext(boundedContext1));
		UpstreamDownstreamRelationship relationship2 = ContextMappingDSLFactory.eINSTANCE.createUpstreamDownstreamRelationship();
		relationship2.setUpstream(newBC);
		relationship2.setDownstream(getBoundedContext(boundedContext2));
		contextMap.getRelationships().add(relationship1);
		contextMap.getRelationships().add(relationship2);
	}

	abstract List<SymmetricRelationship> getMatchingRelationships();

	abstract String getRelationshipType();

	protected BoundedContext createBoundedContext() {
		BoundedContext newBC = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		newBC.setName(getNewBoundedContextName());
		return newBC;
	}

	private void checkPreconditions() {
		if (model.getMap() == null)
			throw new RefactoringInputException("The selected model does not conain a Context Map. Please select a CML file containing a ContextMap element.");

		if (boundedContext1 == null || boundedContext2 == null)
			throw new RefactoringInputException("Please provide two bounded context names (input parameter was null).");

		if (boundedContext1.equals(boundedContext2))
			throw new RefactoringInputException("Please provide two different bounded context names (input parameters contain same name).");

		List<SymmetricRelationship> matchingRelationships = getMatchingRelationships();

		if (matchingRelationships.size() > 1)
			throw new RefactoringInputException("There are multiple " + getRelationshipType() + " relationships between '" + boundedContext1 + "' and '" + boundedContext2 + "'.");

		if (matchingRelationships.isEmpty())
			throw new RefactoringInputException(
					"Your Context Map does not contain a " + getRelationshipType() + " relationship between '" + boundedContext1 + "' and '" + boundedContext2 + "'.");
	}

	private BoundedContext getBoundedContext(String name) {
		return contextMap.getBoundedContexts().stream().filter(bc -> bc.getName().equals(name)).findFirst().get();
	}

	private SymmetricRelationship getMatchingRelationship() {
		return getMatchingRelationships().get(0);
	}

	private String getNewBoundedContextName() {
		Set<String> existingNames = model.getBoundedContexts().stream().map(bc -> bc.getName()).collect(Collectors.toSet());
		String name = boundedContext1 + "_" + boundedContext2 + "_" + getRelationshipType();
		if (existingNames.contains(name)) {
			int counter = 1;
			while (existingNames.contains(name)) {
				name = boundedContext1 + "_" + boundedContext2 + "_" + getRelationshipType() + "_" + counter;
				counter++;
			}
		}
		return name;
	}

}

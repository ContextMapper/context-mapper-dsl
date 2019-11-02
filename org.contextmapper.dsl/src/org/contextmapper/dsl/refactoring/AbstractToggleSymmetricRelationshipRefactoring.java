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

import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.SymmetricRelationship;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;

/**
 * 
 * Used for 'Change to Partnership' on Shared Kernel and 'Change to Shared
 * Kernel' on Partnership.
 * 
 * @author Stefan Kapferer
 *
 */
public abstract class AbstractToggleSymmetricRelationshipRefactoring extends AbstractRefactoring implements Refactoring {

	protected String boundedContext1;
	protected String boundedContext2;

	public AbstractToggleSymmetricRelationshipRefactoring(String boundedContext1, String boundedContext2) {
		this.boundedContext1 = boundedContext1;
		this.boundedContext2 = boundedContext2;
	}

	@Override
	protected void doRefactor() {
		checkPreconditions();

		ContextMap contextMap = model.getMap();
		SymmetricRelationship originalRelationship = getMatchingRelationship();
		SymmetricRelationship newRelationship;

		if ("SharedKernel".equals(getRelationshipType())) {
			newRelationship = ContextMappingDSLFactory.eINSTANCE.createPartnership();
		} else {
			newRelationship = ContextMappingDSLFactory.eINSTANCE.createSharedKernel();
		}

		newRelationship.setImplementationTechnology(originalRelationship.getImplementationTechnology());
		newRelationship.setName(originalRelationship.getName());
		newRelationship.setParticipant1(originalRelationship.getParticipant1());
		newRelationship.setParticipant2(originalRelationship.getParticipant2());

		contextMap.getRelationships().remove(originalRelationship);
		contextMap.getRelationships().add(newRelationship);

		// save model
		saveResource();
	}

	abstract String getRelationshipType();

	abstract List<SymmetricRelationship> getMatchingRelationships();

	private void checkPreconditions() {
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

	private SymmetricRelationship getMatchingRelationship() {
		return getMatchingRelationships().get(0);
	}

}

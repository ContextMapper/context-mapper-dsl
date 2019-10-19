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
import org.contextmapper.dsl.contextMappingDSL.SharedKernel;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;

public class ExtractSharedKernelRefactoring extends AbstractRefactoring implements Refactoring {

	private String boundedContext1;
	private String boundedContext2;

	public ExtractSharedKernelRefactoring(String boundedContext1, String boundedContext2) {
		this.boundedContext1 = boundedContext1;
		this.boundedContext2 = boundedContext2;
	}

	@Override
	protected void doRefactor() {
		checkPreconditions();

		ContextMap contextMap = model.getMap();

		// remove existing relationship
		contextMap.getRelationships().remove(getMatchingSharedKernel());

		// create new BC for Shared Kernel
		BoundedContext newBC = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		newBC.setName(boundedContext1 + "_" + boundedContext2 + "_SharedKernel");
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

		// save model
		saveResource();
	}

	private void checkPreconditions() {
		if (boundedContext1 == null || boundedContext2 == null)
			throw new RefactoringInputException("Please provide two bounded context names (input parameter was null).");

		if (boundedContext1.equals(boundedContext2))
			throw new RefactoringInputException("Please provide two different bounded context names (input parameters contain same name).");

		List<SharedKernel> matchingRelationships = getMatchingSharedKernels();

		if (matchingRelationships.size() > 1)
			throw new RefactoringInputException("There are multiple Shared Kernel relationships between '" + boundedContext1 + "' and '" + boundedContext2 + "'.");

		if (matchingRelationships.isEmpty())
			throw new RefactoringInputException("Your Context Map does not contain a Shared Kernel relationship between '" + boundedContext1 + "' and '" + boundedContext2 + "'.");
	}

	private BoundedContext getBoundedContext(String name) {
		return model.getBoundedContexts().stream().filter(bc -> bc.getName().equals(name)).findFirst().get();
	}

	private SharedKernel getMatchingSharedKernel() {
		return getMatchingSharedKernels().get(0);
	}

	private List<SharedKernel> getMatchingSharedKernels() {
		return model.getMap().getRelationships().stream().filter(rel -> rel instanceof SharedKernel).map(rel -> (SharedKernel) rel)
				.filter(sk -> sk.getParticipant1().getName().equals(boundedContext1) && sk.getParticipant2().getName().equals(boundedContext2)).collect(Collectors.toList());
	}

}

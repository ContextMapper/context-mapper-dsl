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
package org.contextmapper.dsl.generator;

import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Coordination;
import org.contextmapper.dsl.contextMappingDSL.Flow;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.contextmapper.dsl.generator.sketchminer.SketchMinerCoordinationModelCreator;
import org.contextmapper.dsl.generator.sketchminer.SketchMinerModelCreator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import com.google.common.collect.Sets;

public class SketchMinerGenerator extends AbstractContextMappingModelGenerator {

	private static final String SKETCH_MINER_FILE_EXT = "sketch_miner";

	@Override
	protected void generateFromContextMappingModel(ContextMappingModel model, IFileSystemAccess2 fsa, URI inputFileURI) {
		checkPreconditions();
		String fileName = inputFileURI.trimFileExtension().lastSegment();

		// generate sketch miner file for Bounded Contexts flows
		for (BoundedContext boundedContext : model.getBoundedContexts()) {
			for (Flow flow : getFlowsWithSteps(boundedContext)) {
				fsa.generateFile(fileName + "_BC_" + boundedContext.getName() + "_" + flow.getName() + "." + SKETCH_MINER_FILE_EXT, new SketchMinerModelCreator().createText(flow));
			}
			for (Coordination coordination : getCoordinationsWithSteps(boundedContext)) {
				fsa.generateFile("coordinations/" + fileName + "_BC_" + boundedContext.getName() + "_" + coordination.getName() + "." + SKETCH_MINER_FILE_EXT, new SketchMinerCoordinationModelCreator().createText(coordination));
			}
		}
	}

	private void checkPreconditions() {
		for (BoundedContext boundedContext : this.contextMappingModel.getBoundedContexts()) {
			if (boundedContext.getApplication() != null && (boundedContext.getApplication().getFlows() != null || boundedContext.getApplication().getCoordinations() != null)) {
				Set<Flow> nonEmptyFlows = boundedContext.getApplication().getFlows().stream().filter(f -> !f.getSteps().isEmpty()).collect(Collectors.toSet());
				Set<Coordination> nonEmptyCoordinations = boundedContext.getApplication().getCoordinations().stream().filter(c -> !c.getCoordinationSteps().isEmpty()).collect(Collectors.toSet());

				if (!nonEmptyFlows.isEmpty() || !nonEmptyCoordinations.isEmpty())
					return;
			}
		}
		throw new GeneratorInputException("Your model does not contain any Bounded Contexts with application layer and flow or coordination definition.");
	}

	private Set<Flow> getFlowsWithSteps(BoundedContext boundedContext) {
		if (boundedContext.getApplication() == null || boundedContext.getApplication().getFlows() == null)
			return Sets.newHashSet();
		return boundedContext.getApplication().getFlows().stream().filter(f -> !f.getSteps().isEmpty()).collect(Collectors.toSet());
	}
	
	private Set<Coordination> getCoordinationsWithSteps(BoundedContext boundedContext) {
		if (boundedContext.getApplication() == null || boundedContext.getApplication().getFlows() == null)
			return Sets.newHashSet();
		return boundedContext.getApplication().getCoordinations().stream().filter(c -> !c.getCoordinationSteps().isEmpty()).collect(Collectors.toSet());
	}

}

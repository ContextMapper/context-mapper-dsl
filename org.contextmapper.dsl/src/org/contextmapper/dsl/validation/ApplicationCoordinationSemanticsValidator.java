/*
 * Copyright 2023 The Context Mapper Project Team
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
package org.contextmapper.dsl.validation;

import static org.contextmapper.dsl.validation.ValidationMessages.COORDINATION_STEP_CONTEXT_NOT_REACHABLE;
import static org.contextmapper.dsl.validation.ValidationMessages.COORDINATION_STEP_SERVICE_NOT_ON_STEP_CONTEXT_APPLICATION;
import static org.contextmapper.dsl.validation.ValidationMessages.COORDINATION_STEP_OPERATION_NOT_ON_STEP_SERVICE;
import static org.contextmapper.dsl.validation.ValidationMessages.COORDINATION_STEP_OPERATION_IS_AMBIGUOUS;
import static org.contextmapper.dsl.validation.ValidationMessages.VISUALIZE_COORDINATION_WITH_SKETCH_MINER;

import java.util.List;

import org.contextmapper.dsl.cml.CMLModelObjectsResolvingHelper;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Coordination;
import org.contextmapper.dsl.contextMappingDSL.CoordinationStep;
import org.contextmapper.tactic.dsl.tacticdsl.Service;
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

public class ApplicationCoordinationSemanticsValidator extends AbstractDeclarativeValidator {
	
	public static final String SKETCH_MINER_INFO_ID = "open-coordination-in-sketch-miner";

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}
	
	@Check
	public void stepComponentsMustBeCorrectlyReferenced(final CoordinationStep coordinationStep) {
		CMLModelObjectsResolvingHelper helper = new CMLModelObjectsResolvingHelper((ContextMappingModel) EcoreUtil2.getRootContainer(coordinationStep));
		
		BoundedContext containerContext = helper.resolveBoundedContext(coordinationStep);
		BoundedContext stepContext = coordinationStep.getBoundedContext();
		if (containerContext == null || stepContext == null || isNullName(stepContext.getName())) {
			return; // BC is undefined
		}
		
		// Outer context in step must be reachable by relationship
		if (!helper.resolveAllUpstreamContexts(containerContext).contains(stepContext)) {
			error(String.format(COORDINATION_STEP_CONTEXT_NOT_REACHABLE, stepContext.getName()), 
					coordinationStep, ContextMappingDSLPackage.Literals.COORDINATION_STEP__BOUNDED_CONTEXT);
			return;
		}

		Service stepService = coordinationStep.getService();
		if (stepService == null || isNullName(stepService.getName())) {
			return; // Service is undefined or out of BC scope
		}
		
		// Service in step must be part of step context application
		if (helper.resolveApplicationServiceByName(stepContext.getApplication(), stepService.getName()) == null) {
			error(String.format(COORDINATION_STEP_SERVICE_NOT_ON_STEP_CONTEXT_APPLICATION, stepService.getName(), stepContext.getName()), 
					coordinationStep, ContextMappingDSLPackage.Literals.COORDINATION_STEP__SERVICE);
			return;
		}
		
		ServiceOperation stepOperation = coordinationStep.getOperation();
		if (stepOperation == null || isNullName(stepOperation.getName())) {
			return; // Operation is undefined or out of Service scope
		}
		
		// Operation in step must be part of step service
		List<ServiceOperation> operations = helper.resolveServiceOperationsByName(stepService, stepOperation.getName());
		if (operations.isEmpty()) {
			error(String.format(COORDINATION_STEP_OPERATION_NOT_ON_STEP_SERVICE, stepOperation.getName(), stepService.getName(), stepContext.getName()), 
					coordinationStep, ContextMappingDSLPackage.Literals.COORDINATION_STEP__OPERATION);
			return;
		}
		
		// Operation in step should be unique in step service
		if (operations.size() > 1) {
			warning(String.format(COORDINATION_STEP_OPERATION_IS_AMBIGUOUS, stepOperation.getName(), stepService.getName()), 
					coordinationStep, ContextMappingDSLPackage.Literals.COORDINATION_STEP__OPERATION);
		}
	}
	
	@Check
	public void sketchMinerLink(final Coordination coordination) {
		if (!coordination.getCoordinationSteps().isEmpty())
			info(VISUALIZE_COORDINATION_WITH_SKETCH_MINER, coordination, ContextMappingDSLPackage.Literals.COORDINATION__NAME, SKETCH_MINER_INFO_ID);
	}
	
	private boolean isNullName(String name) {
		return name == null || "".equals(name);
	}
	
}

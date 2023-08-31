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
package org.contextmapper.dsl.validation;

import static org.contextmapper.dsl.validation.ValidationMessages.FUNCTIONALITY_STEP_CONTEXT_NOT_ON_MAP;
import static org.contextmapper.dsl.validation.ValidationMessages.FUNCTIONALITY_STEP_SERVICE_NOT_ON_STEP_CONTEXT;
import static org.contextmapper.dsl.validation.ValidationMessages.FUNCTIONALITY_STEP_OPERATION_NOT_ON_STEP_SERVICE;
import static org.contextmapper.dsl.validation.ValidationMessages.FUNCTIONALITY_STEP_SERVICE_NOT_APPLICATION_SERVICE;

import org.contextmapper.dsl.cml.CMLModelObjectsResolvingHelper;
import org.contextmapper.dsl.contextMappingDSL.Application;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.FunctionalityStep;
import org.contextmapper.tactic.dsl.tacticdsl.Service;
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

public class ApplicationFunctionalitySemanticsValidator extends AbstractDeclarativeValidator {

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}

	@Check
	public void contextInStepIsPartOfMap(final FunctionalityStep functionalityStep) {
		CMLModelObjectsResolvingHelper helper = new CMLModelObjectsResolvingHelper((ContextMappingModel) EcoreUtil2.getRootContainer(functionalityStep));
		
		ContextMap map = helper.getContextMap(helper.resolveBoundedContext(functionalityStep));
		BoundedContext stepContext = functionalityStep.getBoundedContext();
		if (map == null || stepContext == null || !map.getBoundedContexts().contains(stepContext)) {
			error(String.format(FUNCTIONALITY_STEP_CONTEXT_NOT_ON_MAP, stepContext.getName()), 
					functionalityStep, ContextMappingDSLPackage.Literals.FUNCTIONALITY_STEP__BOUNDED_CONTEXT);
		}
	}

	@Check
	public void serviceInStepIsPartOfStepContext(final FunctionalityStep functionalityStep) {
		CMLModelObjectsResolvingHelper helper = new CMLModelObjectsResolvingHelper((ContextMappingModel) EcoreUtil2.getRootContainer(functionalityStep));

		BoundedContext stepContext = functionalityStep.getBoundedContext();
		Service stepService = functionalityStep.getService();
		if (stepContext == null || stepService == null) {
			return;
		}
		
		BoundedContext stepServiceContext = helper.resolveBoundedContext(stepService);
		if (stepServiceContext == null || !stepContext.getName().equals(stepServiceContext.getName())) {
			error(String.format(FUNCTIONALITY_STEP_SERVICE_NOT_ON_STEP_CONTEXT, stepService.getName(), stepContext.getName()), 
					functionalityStep, ContextMappingDSLPackage.Literals.FUNCTIONALITY_STEP__SERVICE);
		}
	}
	
	@Check
	public void serviceOperationInStepIsPartOfStepService(final FunctionalityStep functionalityStep) {
		Service stepService = functionalityStep.getService();
		ServiceOperation stepOperation = functionalityStep.getOperation();
		if (stepService == null || stepOperation == null) {
			return;
		}
		
		Service stepOperationService = (Service) stepOperation.eContainer();
		if (stepOperationService == null || !stepService.getName().equals(stepOperationService.getName())) {
			error(String.format(FUNCTIONALITY_STEP_OPERATION_NOT_ON_STEP_SERVICE, stepOperation.getName(), stepService.getName()), 
					functionalityStep, ContextMappingDSLPackage.Literals.FUNCTIONALITY_STEP__OPERATION);
		}
	}
	
	@Check
	public void serviceInStepIsApplicationService(final FunctionalityStep functionalityStep) {
		Service stepService = functionalityStep.getService();
		if (stepService.eContainer() == null || !(stepService.eContainer() instanceof Application)) {
			error(String.format(FUNCTIONALITY_STEP_SERVICE_NOT_APPLICATION_SERVICE, stepService.getName()), 
					functionalityStep, ContextMappingDSLPackage.Literals.FUNCTIONALITY_STEP__SERVICE);
		}
	}
}

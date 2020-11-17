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

import static org.contextmapper.dsl.validation.ValidationMessages.COMMAND_OR_OPERATION_IS_NOT_PART_OF_BOUNDED_CONTEXT;
import static org.contextmapper.dsl.validation.ValidationMessages.STATE_VALUE_DOES_NOT_BELONG_TO_AGGREGATE;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLModelObjectsResolvingHelper;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.CommandInvokation;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.DomainEventProductionStep;
import org.contextmapper.dsl.contextMappingDSL.EitherCommandOrOperation;
import org.contextmapper.dsl.contextMappingDSL.OperationInvokation;
import org.contextmapper.tactic.dsl.tacticdsl.CommandEvent;
import org.contextmapper.tactic.dsl.tacticdsl.Enum;
import org.contextmapper.tactic.dsl.tacticdsl.EnumValue;
import org.contextmapper.tactic.dsl.tacticdsl.ServiceOperation;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

import com.google.common.collect.Sets;

public class ApplicationFlowSemanticsValidator extends AbstractDeclarativeValidator {

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}

	@Check
	public void commandOrOperationMustBeDefinedInSameContext(final EitherCommandOrOperation commandOrOperation) {
		CMLModelObjectsResolvingHelper helper = new CMLModelObjectsResolvingHelper((ContextMappingModel) EcoreUtil2.getRootContainer(commandOrOperation));

		BoundedContext currentContext = helper.resolveBoundedContext(commandOrOperation);
		BoundedContext referencedContext = null;
		String name = null;
		if (commandOrOperation.getCommand() != null) {
			referencedContext = helper.resolveBoundedContext(commandOrOperation.getCommand());
			name = commandOrOperation.getCommand().getName();
			if (currentContext == null || referencedContext == null)
				return;

			if (!currentContext.getName().equals(referencedContext.getName()))
				error(String.format(COMMAND_OR_OPERATION_IS_NOT_PART_OF_BOUNDED_CONTEXT, name, currentContext.getName()), commandOrOperation,
						ContextMappingDSLPackage.Literals.EITHER_COMMAND_OR_OPERATION__COMMAND);
		} else {
			referencedContext = helper.resolveBoundedContext(commandOrOperation.getOperation());
			name = commandOrOperation.getOperation().getName();
			if (currentContext == null || referencedContext == null)
				return;

			if (!currentContext.getName().equals(referencedContext.getName()))
				error(String.format(COMMAND_OR_OPERATION_IS_NOT_PART_OF_BOUNDED_CONTEXT, name, currentContext.getName()), commandOrOperation,
						ContextMappingDSLPackage.Literals.EITHER_COMMAND_OR_OPERATION__OPERATION);
		}

	}

	@Check
	public void commandInvokationMustReferCommandInSameContext(final CommandInvokation commandInvokation) {
		CMLModelObjectsResolvingHelper helper = new CMLModelObjectsResolvingHelper((ContextMappingModel) EcoreUtil2.getRootContainer(commandInvokation));

		BoundedContext currentContext = helper.resolveBoundedContext(commandInvokation);

		for (CommandEvent commandEvent : commandInvokation.getCommands()) {
			BoundedContext commandContext = helper.resolveBoundedContext((EObject) commandEvent);
			if (commandContext == null)
				continue;
			if (!currentContext.getName().equals(commandContext.getName()))
				error(String.format(COMMAND_OR_OPERATION_IS_NOT_PART_OF_BOUNDED_CONTEXT, commandEvent.getName(), currentContext.getName()), commandInvokation,
						ContextMappingDSLPackage.Literals.COMMAND_INVOKATION__COMMANDS, commandInvokation.getCommands().indexOf(commandEvent));
		}
	}

	@Check
	public void operationInvokationMustReferCommandInSameContext(final OperationInvokation operationInvokation) {
		CMLModelObjectsResolvingHelper helper = new CMLModelObjectsResolvingHelper((ContextMappingModel) EcoreUtil2.getRootContainer(operationInvokation));

		BoundedContext currentContext = helper.resolveBoundedContext(operationInvokation);

		for (ServiceOperation operation : operationInvokation.getOperations()) {
			BoundedContext commandContext = helper.resolveBoundedContext(operation);
			if (commandContext == null)
				continue;
			if (!currentContext.getName().equals(commandContext.getName()))
				error(String.format(COMMAND_OR_OPERATION_IS_NOT_PART_OF_BOUNDED_CONTEXT, operation.getName(), currentContext.getName()), operationInvokation,
						ContextMappingDSLPackage.Literals.OPERATION_INVOKATION__OPERATIONS, operationInvokation.getOperations().indexOf(operation));
		}
	}

	@Check
	public void checkThatStateTransitionStatesBelongToAggregate(final DomainEventProductionStep step) {
		if (step.getAggregate() == null)
			return;
		if (step.getStateTransition() == null)
			return;

		Set<String> aggregateStates = resolveAggregateStates(step.getAggregate());
		for (EnumValue value : step.getStateTransition().getFrom()) {
			if (!aggregateStates.contains(value.getName()))
				error(String.format(STATE_VALUE_DOES_NOT_BELONG_TO_AGGREGATE, value.getName(), step.getAggregate().getName()), step.getStateTransition(),
						ContextMappingDSLPackage.Literals.STATE_TRANSITION__FROM, step.getStateTransition().getFrom().indexOf(value));
		}

		for (EnumValue value : step.getStateTransition().getTarget().getTo()) {
			if (!aggregateStates.contains(value.getName()))
				error(String.format(STATE_VALUE_DOES_NOT_BELONG_TO_AGGREGATE, value.getName(), step.getAggregate().getName()), step.getStateTransition().getTarget(),
						ContextMappingDSLPackage.Literals.STATE_TRANSITION_TARGET__TO, step.getStateTransition().getTarget().getTo().indexOf(value));
		}
	}

	private Set<String> resolveAggregateStates(Aggregate aggregate) {
		Set<String> aggregateStates = Sets.newHashSet();

		Optional<org.contextmapper.tactic.dsl.tacticdsl.Enum> optStatesEnum = aggregate.getDomainObjects().stream().filter(o -> o instanceof Enum).map(o -> (Enum) o)
				.filter(o -> o.isDefinesAggregateStates()).findFirst();
		if (optStatesEnum.isPresent())
			aggregateStates.addAll(optStatesEnum.get().getValues().stream().map(v -> v.getName()).collect(Collectors.toSet()));

		return aggregateStates;
	}

}

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
package org.contextmapper.dsl.generator.sketchminer.converter;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.contextmapper.dsl.cml.CMLModelObjectsResolvingHelper;
import org.contextmapper.dsl.contextMappingDSL.Application;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.CommandInvokation;
import org.contextmapper.dsl.contextMappingDSL.CommandInvokationStep;
import org.contextmapper.dsl.contextMappingDSL.ConcurrentCommandInvokation;
import org.contextmapper.dsl.contextMappingDSL.ConcurrentOperationInvokation;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.DomainEventProductionStep;
import org.contextmapper.dsl.contextMappingDSL.EitherCommandOrOperation;
import org.contextmapper.dsl.contextMappingDSL.Flow;
import org.contextmapper.dsl.contextMappingDSL.FlowStep;
import org.contextmapper.dsl.contextMappingDSL.InclusiveAlternativeCommandInvokation;
import org.contextmapper.dsl.contextMappingDSL.InclusiveAlternativeEventProduction;
import org.contextmapper.dsl.contextMappingDSL.InclusiveAlternativeOperationInvokation;
import org.contextmapper.dsl.contextMappingDSL.MultipleEventProduction;
import org.contextmapper.dsl.contextMappingDSL.OperationInvokation;
import org.contextmapper.dsl.generator.sketchminer.converter.SimplifiedFlowStep.ToType;
import org.contextmapper.dsl.generator.sketchminer.model.SketchMinerModel;
import org.contextmapper.dsl.generator.sketchminer.model.Task;
import org.contextmapper.dsl.generator.sketchminer.model.TaskSequence;
import org.contextmapper.dsl.generator.sketchminer.model.TaskType;
import org.contextmapper.tactic.dsl.tacticdsl.StateTransition;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class Flow2SketchMinerConverter {

	private Flow flow;
	private List<SimplifiedFlowStep> simplifiedSteps;
	private Map<String, Task> taskMap;
	private SketchMinerModel model;

	public Flow2SketchMinerConverter(Flow flow) {
		this.flow = flow;
		this.model = new SketchMinerModel(getDefaultActorName(flow));
		initIntermediateTypes();
	}

	public SketchMinerModel convert() {
		for (Task initialTask : getInitialTasks()) {
			TaskSequence seq = new TaskSequence(initialTask);
			finishSequence(seq);
			model.addSequence(seq);
		}
		model.cleanupDuplicateSequences();
		return model;
	}

	private void finishSequence(TaskSequence seq) {
		Task lastTask = seq.getLastTaskInSequence();
		List<SimplifiedFlowStep> nextSteps = getNextSteps(lastTask);
		if (!nextSteps.isEmpty()) {
			for (SimplifiedFlowStep nextStep : nextSteps) {
				if (nextStep.getTos().size() == 1) {
					if (seq.addTask(nextStep.getTos().iterator().next()))
						finishSequence(seq);
				} else if (nextStep.getToType().equals(ToType.AND)) {
					forkSequence(seq, createSetOfParallelTasks(nextStep.getTos()));
				} else {
					forkSequence(seq, nextStep.getTos());
				}
			}
		}
	}

	private void forkSequence(TaskSequence seq, Collection<Task> nextTasks) {
		Iterator<Task> it = nextTasks.iterator();
		Task firstTask = it.next();
		while (it.hasNext()) {
			Task nextTask = it.next();
			createNewSequenceWithTask(seq, nextTask);
		}
		if (seq.addTask(firstTask))
			finishSequence(seq);
	}

	private void createNewSequenceWithTask(TaskSequence seq, Task nextTask) {
		TaskSequence newSeq = seq.copy();
		if (newSeq.addTask(nextTask))
			finishSequence(newSeq);
		model.addSequence(newSeq);
	}

	private List<Task> createSetOfParallelTasks(Set<Task> allTasks) {
		List<Task> parallelTasks = Lists.newLinkedList();
		for (Task task : allTasks) {
			Set<Task> otherTasks = Sets.newHashSet(allTasks);
			otherTasks.remove(task);
			parallelTasks.add(new Task(task.getName(), task.getType(), otherTasks));
		}
		return parallelTasks;
	}

	private List<SimplifiedFlowStep> getNextSteps(Task lastTask) {
		List<SimplifiedFlowStep> nextSteps = Lists.newLinkedList();
		for (SimplifiedFlowStep step : simplifiedSteps) {
			for (Task task : step.getFroms()) {
				if (task.equalsOrContainsTask(lastTask))
					nextSteps.add(step);
			}
		}
		return nextSteps;
	}

	private void initIntermediateTypes() {
		this.simplifiedSteps = Lists.newLinkedList();
		this.taskMap = Maps.newHashMap();

		for (FlowStep step : flow.getSteps()) {
			this.simplifiedSteps.add(convert(step));
		}
	}

	private SimplifiedFlowStep convert(FlowStep step) {
		Set<Task> froms = Sets.newHashSet();
		Set<Task> tos = Sets.newHashSet();
		ToType toType = ToType.XOR;
		if (step instanceof CommandInvokationStep) {
			froms.addAll(((CommandInvokationStep) step).getEvents().stream().map(e -> getOrCreateTask(e.getName(), TaskType.EVENT)).collect(Collectors.toSet()));
			if (((CommandInvokationStep) step).getAction() instanceof CommandInvokation) {
				CommandInvokation commandInvokation = (CommandInvokation) ((CommandInvokationStep) step).getAction();
				tos.addAll(commandInvokation.getCommands().stream().map(c -> getOrCreateTask(c.getName(), TaskType.COMMAND)).collect(Collectors.toSet()));
				if (commandInvokation instanceof ConcurrentCommandInvokation)
					toType = ToType.AND;
				if (commandInvokation instanceof InclusiveAlternativeCommandInvokation)
					toType = ToType.OR;
			} else if (((CommandInvokationStep) step).getAction() instanceof OperationInvokation) {
				OperationInvokation operationInvokation = (OperationInvokation) ((CommandInvokationStep) step).getAction();
				tos.addAll(operationInvokation.getOperations().stream().map(o -> getOrCreateTask(o.getName(), TaskType.COMMAND)).collect(Collectors.toSet()));
				if (operationInvokation instanceof ConcurrentOperationInvokation)
					toType = ToType.AND;
				if (operationInvokation instanceof InclusiveAlternativeOperationInvokation)
					toType = ToType.OR;
			}
		} else if (step instanceof DomainEventProductionStep) {
			DomainEventProductionStep eventStep = (DomainEventProductionStep) step;
			froms.add(createTask4EventProduction(eventStep));
			tos.addAll(eventStep.getEventProduction().getEvents().stream().map(e -> getOrCreateTask(e.getName(), TaskType.EVENT)).collect(Collectors.toSet()));
			if (eventStep.getEventProduction() instanceof MultipleEventProduction)
				toType = ToType.AND;
			if (eventStep.getEventProduction() instanceof InclusiveAlternativeEventProduction)
				toType = ToType.OR;
		}
		return new SimplifiedFlowStep(froms, tos, toType);
	}

	private Task getOrCreateTask(String name, TaskType type) {
		if (taskMap.containsKey(name))
			return taskMap.get(name);
		Task task = new Task(name, type);
		taskMap.put(name, task);
		return task;
	}

	private Task createTask4EventProduction(DomainEventProductionStep eventStep) {
		String name = "UndefinedTask";
		if (eventStep.getAction().getCommand() != null) {
			name = eventStep.getAction().getCommand().getName();
		} else if (eventStep.getAction().getOperation() != null) {
			name = eventStep.getAction().getOperation().getName();
		}
		Task task = getOrCreateTask(name, TaskType.COMMAND);
		addStateTransitionIfAvailable(task, eventStep);
		addActorIfAvailable(task, eventStep.getAction());
		return task;
	}

	private void addStateTransitionIfAvailable(Task task, DomainEventProductionStep step) {
		if (step.getAggregate() == null || step.getStateTransition() == null)
			return;
		task.setComment(step.getAggregate().getName() + " [" + getStateTransitionAsString(step.getStateTransition()) + "]");
	}

	private void addActorIfAvailable(Task task, EitherCommandOrOperation action) {
		if (StringUtils.isNoneEmpty(action.getActor()))
			task.setActor(action.getActor().trim());
	}

	private String getStateTransitionAsString(StateTransition transition) {
		return String.join(", ", transition.getFrom().stream().map(v -> v.getName()).collect(Collectors.toList())) + " -> "
				+ String.join(" X ", transition.getTarget().getTo().stream().map(t -> t.getValue().getName()).collect(Collectors.toList()));
	}

	private List<Task> getInitialTasks() {
		List<Task> initialTasks = Lists.newLinkedList();
		for (Task task : this.taskMap.values()) {
			if (isInitialTask(task))
				initialTasks.add(task);
		}
		if (initialTasks.isEmpty()) { // just take the first mentioned task if we cannot find clear entry point
			SimplifiedFlowStep firstStep = this.simplifiedSteps.get(0);
			initialTasks.add(firstStep.getFroms().iterator().next());
		}
		return initialTasks;
	}

	private boolean isInitialTask(Task potentialInitTask) {
		for (SimplifiedFlowStep step : this.simplifiedSteps) {
			if (step.getTos().contains(potentialInitTask))
				return false;
		}
		return true;
	}

	private String getDefaultActorName(Flow flow) {
		if (flow.eContainer() instanceof Application && StringUtils.isNoneEmpty(((Application) flow.eContainer()).getName()))
			return ((Application) flow.eContainer()).getName();
		if (EcoreUtil2.getRootContainer(flow) instanceof ContextMappingModel)
			return new CMLModelObjectsResolvingHelper((ContextMappingModel) EcoreUtil2.getRootContainer(flow)).resolveBoundedContext(flow).getName() + " Application";
		return "Application";
	}

}

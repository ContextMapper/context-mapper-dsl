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
package org.contextmapper.dsl.generator.sketchminer.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.contextmapper.dsl.cml.CMLModelObjectsResolvingHelper;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Coordination;
import org.contextmapper.dsl.contextMappingDSL.CoordinationStep;
import org.contextmapper.dsl.generator.sketchminer.model.SketchMinerModel;
import org.contextmapper.dsl.generator.sketchminer.model.Task;
import org.contextmapper.dsl.generator.sketchminer.model.TaskSequence;
import org.contextmapper.dsl.generator.sketchminer.model.TaskType;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Lists;

public class Coordination2SketchMinerConverter {

	private Coordination coordination;
	private List<SimplifiedCoordinationStep> simplifiedSteps;
	private Map<String, Task> taskMap;
	private SketchMinerModel model;

	public Coordination2SketchMinerConverter(Coordination coordination) {
		this.coordination = coordination;
		this.model = new SketchMinerModel(getDefaultActorName(coordination));
		initIntermediateTypes();
	}

	public SketchMinerModel convert() {
		TaskSequence seq = new TaskSequence(simplifiedSteps.get(0).getOperation());
		model.addSequence(seq);
		finishSequence(seq);
		return model;
	}

	private void finishSequence(TaskSequence seq) {
		for (int i = 1; i < simplifiedSteps.size(); i++) {
			seq.addTask(simplifiedSteps.get(i).getOperation());
		}
	}

	private void initIntermediateTypes() {
		this.simplifiedSteps = Lists.newLinkedList();
		this.taskMap = new HashMap<>();

		for (CoordinationStep step : coordination.getCoordinationSteps()) {
			this.simplifiedSteps.add(convert(step));
		}
	}

	private SimplifiedCoordinationStep convert(CoordinationStep step) {
		Task operation = getOrCreateTask(step, TaskType.COMMAND);
		return new SimplifiedCoordinationStep(operation);
	}

	private Task getOrCreateTask(CoordinationStep step, TaskType type) {
		String taskName = step.getService().getName() + "." + step.getOperation().getName();
		String taskMapKey = step.getBoundedContext().getName() + "." + taskName;
		
		if (taskMap.containsKey(taskMapKey))
			return taskMap.get(taskMapKey);
		Task task = new Task(taskName, type);
		task.setActor(step.getBoundedContext().getName());
		taskMap.put(taskMapKey, task);
		return task;
	}

	private String getDefaultActorName(Coordination coordination) {
		if (EcoreUtil2.getRootContainer(coordination) instanceof ContextMappingModel)
			return new CMLModelObjectsResolvingHelper((ContextMappingModel) EcoreUtil2.getRootContainer(coordination)).resolveBoundedContext(coordination).getName();
		return "Application";
	}

}

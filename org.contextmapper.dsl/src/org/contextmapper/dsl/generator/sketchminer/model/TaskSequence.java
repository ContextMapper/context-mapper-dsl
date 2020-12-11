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
package org.contextmapper.dsl.generator.sketchminer.model;

import java.util.Collections;
import java.util.List;

import org.contextmapper.dsl.exception.ContextMapperApplicationException;

import com.google.common.collect.Lists;

public class TaskSequence {

	private List<Task> tasks;

	public TaskSequence(Task initialTask) {
		if (initialTask == null)
			throw new ContextMapperApplicationException("Initial task of a sequence must be defined!");
		this.tasks = Lists.newLinkedList();
		this.tasks.add(initialTask);
	}

	private TaskSequence(List<Task> tasks) {
		this.tasks = Lists.newLinkedList(tasks);
	}

	public boolean addTask(Task task2Add) {
		if (Collections.frequency(tasks, task2Add) <= 1) {
			tasks.add(task2Add);
			return true;
		}
		return false;
	}

	public List<Task> getTasks() {
		return Lists.newLinkedList(tasks);
	}

	public Task getLastTaskInSequence() {
		return tasks.get(tasks.size() - 1);
	}

	public TaskSequence copy() {
		return new TaskSequence(tasks);
	}

	public boolean isEqualToOtherSequence(TaskSequence otherSequence) {
		return tasks.equals(otherSequence.getTasks());
	}

}

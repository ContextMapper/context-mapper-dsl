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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.exception.ContextMapperApplicationException;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Task {

	private String name;
	private TaskType type;
	private List<Task> parallelTasks;
	private String comment;
	private String actor;

	public Task(String name, TaskType type) {
		if (name == null || "".equals(name))
			throw new ContextMapperApplicationException("No valid event or command name passed.");
		this.name = name;
		this.type = type;
	}

	public Task(String name, TaskType type, List<Task> parallelTasks) {
		this(name, type);
		this.parallelTasks = Lists.newLinkedList(parallelTasks);
	}

	public String getName() {
		return name;
	}

	public TaskType getType() {
		return type;
	}

	public Set<Task> getParallelTasks() {
		if (parallelTasks == null)
			return Sets.newHashSet();
		return Sets.newHashSet(parallelTasks);
	}

	public boolean equalsOrContainsTask(Task otherTask) {
		return otherTask.name.equals(this.name)
				|| (parallelTasks != null && parallelTasks.stream().map(t -> t.getName()).collect(Collectors.toList()).contains(otherTask.getName()));
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getActor() {
		return actor;
	}

	private Set<String> getAllTaskNames() {
		Set<String> names = Sets.newHashSet();
		names.add(name);
		if (parallelTasks != null)
			names.addAll(parallelTasks.stream().map(t -> t.getName()).collect(Collectors.toSet()));
		return names;
	}

	@Override
	public String toString() {
		if (parallelTasks == null || parallelTasks.isEmpty())
			return name;
		return String.join("|", getAllTaskNames().stream().sorted().collect(Collectors.toList()));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parallelTasks == null) ? 0 : parallelTasks.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		if (!getAllTaskNames().equals(other.getAllTaskNames()))
			return false;
		return true;
	}

}

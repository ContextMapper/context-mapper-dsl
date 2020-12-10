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
package org.contextmapper.dsl.generators.sketchminer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.generator.sketchminer.model.Task;
import org.contextmapper.dsl.generator.sketchminer.model.TaskType;
import org.junit.jupiter.api.Test;

public class TaskTest {

	@Test
	public void cannotCreateTaskWithoutName() {
		assertThrows(ContextMapperApplicationException.class, () -> {
			new Task(null, TaskType.COMMAND);
		});
	}

	@Test
	public void cannotCreateTaskWithEmptyString() {
		assertThrows(ContextMapperApplicationException.class, () -> {
			new Task("", TaskType.COMMAND);
		});
	}

	@Test
	public void canCreateNormalTask() {
		// given
		String name = "MyTask";
		TaskType type = TaskType.COMMAND;

		// when
		Task task = new Task(name, type);

		// then
		assertEquals("MyTask", task.getName());
		assertEquals("MyTask", task.toString());
		assertEquals(TaskType.COMMAND, task.getType());
	}

	@Test
	public void canCreateParallelTask() {
		// given
		String name = "MyTask";
		String[] parallalTaskNames = new String[] { "Par1", "Par2" };
		Set<Task> parallelTasks = Arrays.asList(parallalTaskNames).stream().map(s -> new Task(s, TaskType.COMMAND)).collect(Collectors.toSet());

		// when
		Task parTask = new Task(name, TaskType.COMMAND, parallelTasks);

		// then
		assertEquals("MyTask", parTask.getName());
		assertEquals("MyTask|Par1|Par2", parTask.toString());
	}

	@Test
	public void sameObjectIsEqual() {
		// given
		Task task = new Task("TestTask", TaskType.COMMAND);

		// when
		boolean equals = task.equals(task);

		// then
		assertTrue(equals);
	}

	@Test
	public void taskNotEqualToNull() {
		// given
		Task task = new Task("TestTask", TaskType.COMMAND);

		// when
		boolean equals = task.equals(null);

		// then
		assertFalse(equals);
	}

	@Test
	public void taskNotEqualToOtherType() {
		// given
		Task task = new Task("TestTask", TaskType.COMMAND);

		// when
		boolean equals = task.equals(new Object());

		// then
		assertFalse(equals);
	}

	@Test
	public void simpleTasksWithSameNameAreEqual() {
		// given
		Task task1 = new Task("TestTask", TaskType.COMMAND);
		Task task2 = new Task("TestTask", TaskType.COMMAND);

		// when
		boolean equals = task1.equals(task2);

		// then
		assertTrue(equals);
	}

	@Test
	public void parallelTaskWithSameSetAreEqual1() {
		// given
		String[] parallalTaskNames = new String[] { "Par1", "Par2" };
		Set<Task> parallelTasks = Arrays.asList(parallalTaskNames).stream().map(s -> new Task(s, TaskType.COMMAND)).collect(Collectors.toSet());
		Task task1 = new Task("TestTask", TaskType.COMMAND, parallelTasks);
		Task task2 = new Task("TestTask", TaskType.COMMAND, parallelTasks);

		// when
		boolean equals = task1.equals(task2);

		// then
		assertTrue(equals);
	}

	@Test
	public void parallelTaskWithSameSetAreEqual2() {
		// given
		String[] parallalTaskNames1 = new String[] { "Par1", "Par2" };
		Set<Task> parallelTasks1 = Arrays.asList(parallalTaskNames1).stream().map(s -> new Task(s, TaskType.COMMAND)).collect(Collectors.toSet());
		String[] parallalTaskNames2 = new String[] { "Par2", "Par3" };
		Set<Task> parallelTasks2 = Arrays.asList(parallalTaskNames2).stream().map(s -> new Task(s, TaskType.COMMAND)).collect(Collectors.toSet());
		Task task1 = new Task("Par3", TaskType.COMMAND, parallelTasks1);
		Task task2 = new Task("Par1", TaskType.COMMAND, parallelTasks2);

		// when
		boolean equals = task1.equals(task2);

		// then
		assertTrue(equals);
	}

	@Test
	public void otherTaskWithParTasksIsNotEqual() {
		// given
		String[] parallalTaskNames = new String[] { "Par1", "Par2" };
		Set<Task> parallelTasks = Arrays.asList(parallalTaskNames).stream().map(s -> new Task(s, TaskType.COMMAND)).collect(Collectors.toSet());
		Task task1 = new Task("TestTask", TaskType.COMMAND, parallelTasks);
		Task task2 = new Task("TestTask", TaskType.COMMAND);

		// when
		boolean equals = task1.equals(task2);

		// then
		assertFalse(equals);
	}

	@Test
	public void otherTaskWithoutParTasksIsNotEqual() {
		// given
		String[] parallalTaskNames = new String[] { "Par1", "Par2" };
		Set<Task> parallelTasks = Arrays.asList(parallalTaskNames).stream().map(s -> new Task(s, TaskType.COMMAND)).collect(Collectors.toSet());
		Task task1 = new Task("TestTask", TaskType.COMMAND);
		Task task2 = new Task("TestTask", TaskType.COMMAND, parallelTasks);

		// when
		boolean equals = task1.equals(task2);

		// then
		assertFalse(equals);
	}

}

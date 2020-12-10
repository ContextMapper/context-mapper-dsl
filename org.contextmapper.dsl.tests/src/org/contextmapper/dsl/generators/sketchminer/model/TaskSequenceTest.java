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

import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.generator.sketchminer.model.Task;
import org.contextmapper.dsl.generator.sketchminer.model.TaskSequence;
import org.contextmapper.dsl.generator.sketchminer.model.TaskType;
import org.junit.jupiter.api.Test;

public class TaskSequenceTest {

	@Test
	public void cannotCreateSequenceWithoutInitialTask() {
		assertThrows(ContextMapperApplicationException.class, () -> {
			new TaskSequence(null);
		});
	}

	@Test
	public void canCreateTaskSequence() {
		// given
		Task initTask = new Task("Start", TaskType.COMMAND);

		// when
		TaskSequence seq = new TaskSequence(initTask);

		// then
		assertEquals(initTask, seq.getLastTaskInSequence());
		assertEquals(1, seq.getTasks().size());
	}

	@Test
	public void canAddTask() {
		// given
		Task initTask = new Task("Start", TaskType.COMMAND);
		Task secondTask = new Task("SecondTask", TaskType.COMMAND);

		// when
		TaskSequence seq = new TaskSequence(initTask);
		seq.addTask(secondTask);

		// then
		assertEquals(secondTask, seq.getLastTaskInSequence());
		assertEquals(2, seq.getTasks().size());
	}

	@Test
	public void canCopySequence() {
		// given
		Task initTask = new Task("Start", TaskType.COMMAND);
		TaskSequence seq1 = new TaskSequence(initTask);

		// when
		TaskSequence seq2 = seq1.copy();

		// then
		assertEquals(1, seq1.getTasks().size());
		assertEquals(1, seq2.getTasks().size());
		assertEquals(initTask, seq1.getLastTaskInSequence());
		assertEquals(initTask, seq2.getLastTaskInSequence());
		assertFalse(seq1.equals(seq2));
		assertFalse(seq1 == seq2);
	}

}

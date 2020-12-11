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

import com.google.common.collect.Lists;

public class SketchMinerModel {

	private List<TaskSequence> sequences;
	private String defaultActorName;

	public SketchMinerModel(String defaultActorName) {
		this.sequences = Lists.newLinkedList();
		this.defaultActorName = defaultActorName;
	}

	public void addSequence(TaskSequence sequence) {
		this.sequences.add(sequence);
	}

	public String getDefaultActorName() {
		return defaultActorName;
	}

	public List<TaskSequence> getSequences() {
		return Lists.newLinkedList(sequences);
	}

	public void cleanupDuplicateSequences() {
		List<TaskSequence> oldSequences = sequences;
		List<TaskSequence> newSequences = Lists.newLinkedList();
		for (TaskSequence seq : oldSequences) {
			if (!containsSequence(newSequences, seq))
				newSequences.add(seq);
		}
		this.sequences = Lists.newLinkedList(newSequences);
	}

	private boolean containsSequence(List<TaskSequence> seqList, TaskSequence testSeq) {
		for (TaskSequence seq : seqList) {
			if (seq.isEqualToOtherSequence(testSeq))
				return true;
		}
		return false;
	}

}

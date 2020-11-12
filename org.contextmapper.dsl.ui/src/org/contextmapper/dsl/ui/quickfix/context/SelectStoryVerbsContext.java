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
package org.contextmapper.dsl.ui.quickfix.context;

import java.util.Set;

import com.google.common.collect.Sets;

public class SelectStoryVerbsContext {

	private Set<String> defaultVerbs;
	private Set<String> selectedVerbs;
	private boolean finishedWizard = false;

	public SelectStoryVerbsContext() {
		this.defaultVerbs = Sets.newHashSet();
		this.selectedVerbs = Sets.newHashSet();
		this.defaultVerbs.add("create");
		this.defaultVerbs.add("read");
		this.defaultVerbs.add("update");
		this.defaultVerbs.add("delete");
		this.defaultVerbs.add("search");
	}

	public Set<String> getDefaultVerbs() {
		return defaultVerbs;
	}

	public void setSelectedVerbs(Set<String> selectedVerbs) {
		this.selectedVerbs = Sets.newHashSet(selectedVerbs);
	}

	public Set<String> getSelectedVerbs() {
		return Sets.newHashSet(selectedVerbs);
	}

	public void setFinishedWizard(boolean finishedWizard) {
		this.finishedWizard = finishedWizard;
	}

	public boolean isFinishedWizard() {
		return finishedWizard;
	}

}

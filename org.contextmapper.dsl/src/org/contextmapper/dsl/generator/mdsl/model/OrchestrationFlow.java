/*
 * Copyright 2021 The Context Mapper Project Team
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
package org.contextmapper.dsl.generator.mdsl.model;

import java.util.List;
import com.google.common.collect.Lists;

public class OrchestrationFlow {
	
	private String name;
	private List<FlowStep> flowSteps = Lists.newArrayList();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public List<FlowStep> getSteps() {
		return flowSteps;
	}
	public void addEventProductionStep(String command, String event) {
		flowSteps.add(new FlowStep(command, event, true));
	}

	public void addCommandInvocationStep(String event, String command) {
		// note the different order:
		flowSteps.add(new FlowStep(command, event, false));
	}
}

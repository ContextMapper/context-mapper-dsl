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
package org.contextmapper.dsl.ui.handler.wizard;

import java.util.List;

public class MergeAggregatesContext {

	private String initialAggregate1;
	private String selectedAggregate1;
	private String selectedAggregate2;
	private List<String> allAggregates;

	public MergeAggregatesContext(String initialAggregate1, List<String> allAggregates) {
		this.initialAggregate1 = initialAggregate1;
		this.allAggregates = allAggregates;
	}

	public String getSelectedAggregate1() {
		return selectedAggregate1;
	}

	public String getSelectedAggregate2() {
		return selectedAggregate2;
	}

	public String getInitialAggregate1() {
		return initialAggregate1;
	}

	public void setSelectedAggregate1(String selectedAggregate1) {
		this.selectedAggregate1 = selectedAggregate1;
	}

	public void setSelectedAggregate2(String selectedAggregate2) {
		this.selectedAggregate2 = selectedAggregate2;
	}

	public List<String> getAllAggregates() {
		return allAggregates;
	}

}

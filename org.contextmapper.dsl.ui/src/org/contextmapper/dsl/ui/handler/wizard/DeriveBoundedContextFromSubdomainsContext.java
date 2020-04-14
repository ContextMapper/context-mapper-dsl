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
package org.contextmapper.dsl.ui.handler.wizard;

import java.util.Set;

public class DeriveBoundedContextFromSubdomainsContext {

	private String initialBoundedContextName;
	private String boundedContextName;
	private Set<String> allBoundedContextNames;

	public DeriveBoundedContextFromSubdomainsContext(String initialBoundedContextName, Set<String> allBoundedContextNames) {
		super();
		this.initialBoundedContextName = initialBoundedContextName;
		this.allBoundedContextNames = allBoundedContextNames;
	}

	public String getInitialBoundedContextName() {
		return initialBoundedContextName;
	}

	public void setInitialBoundedContextName(String initialBoundedContextName) {
		this.initialBoundedContextName = initialBoundedContextName;
	}

	public String getBoundedContextName() {
		return boundedContextName;
	}

	public void setBoundedContextName(String boundedContextName) {
		this.boundedContextName = boundedContextName;
	}

	public Set<String> getAllBoundedContextNames() {
		return allBoundedContextNames;
	}

	public void setAllBoundedContextNames(Set<String> allBoundedContextNames) {
		this.allBoundedContextNames = allBoundedContextNames;
	}

}

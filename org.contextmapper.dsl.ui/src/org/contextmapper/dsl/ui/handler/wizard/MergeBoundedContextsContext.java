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

public class MergeBoundedContextsContext {

	private String initialBoundedContext1;
	private String selectedBoundedContext1;
	private String selectedBoundedContext2;
	private boolean takeAttributesFromSecondBoundedContext;
	private List<String> allBoundedContexts;

	public MergeBoundedContextsContext(String initialBoundedContext1, List<String> allBoundedContexts) {
		this.initialBoundedContext1 = initialBoundedContext1;
		this.allBoundedContexts = allBoundedContexts;
	}

	public String getSelectedBoundedContext1() {
		return selectedBoundedContext1;
	}

	public String getSelectedBoundedContext2() {
		return selectedBoundedContext2;
	}

	public String getInitialBoundedContext1() {
		return initialBoundedContext1;
	}

	public void setSelectedBoundedContext1(String selectedBoundedContext1) {
		this.selectedBoundedContext1 = selectedBoundedContext1;
	}

	public void setSelectedBoundedContext2(String selectedBoundedContext2) {
		this.selectedBoundedContext2 = selectedBoundedContext2;
	}

	public void setTakeAttributesFromSecondBoundedContext(boolean takeAttributesFromSecondBoundedContext) {
		this.takeAttributesFromSecondBoundedContext = takeAttributesFromSecondBoundedContext;
	}

	public List<String> getAllBoundedContexts() {
		return allBoundedContexts;
	}

	public boolean takeAttributesFromSecondBoundedContext() {
		return takeAttributesFromSecondBoundedContext;
	}

}

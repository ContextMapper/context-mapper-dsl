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

import org.contextmapper.dsl.refactoring.SuspendPartnershipMode;

public class SuspendPartnershipContext {

	private String boundedContext1;
	private String boundedContext2;
	private SuspendPartnershipMode mode;
	private String replaceModeUpstreamBoundedContext;
	private String mergeModeSelectedBoundedContext1;
	private String mergeModeSelectedBoundedContext2;
	private boolean mergeModeTakeAttributesFromSecondBoundedContext;

	public SuspendPartnershipContext(String boundedContext1, String boundedContext2) {
		this.boundedContext1 = boundedContext1;
		this.boundedContext2 = boundedContext2;
	}

	public String getBoundedContext1() {
		return boundedContext1;
	}

	public String getBoundedContext2() {
		return boundedContext2;
	}

	public void setMode(SuspendPartnershipMode mode) {
		this.mode = mode;
	}

	public SuspendPartnershipMode getMode() {
		return mode;
	}

	public void setReplaceModeUpstreamBoundedContext(String replaceModeUpstreamBoundedContext) {
		this.replaceModeUpstreamBoundedContext = replaceModeUpstreamBoundedContext;
	}

	public String getReplaceModeUpstreamBoundedContext() {
		return replaceModeUpstreamBoundedContext;
	}

	public String getMergeModeSelectedBoundedContext1() {
		return mergeModeSelectedBoundedContext1;
	}

	public void setMergeModeSelectedBoundedContext1(String mergeModeSelectedBoundedContext1) {
		this.mergeModeSelectedBoundedContext1 = mergeModeSelectedBoundedContext1;
	}

	public String getMergeModeSelectedBoundedContext2() {
		return mergeModeSelectedBoundedContext2;
	}

	public void setMergeModeSelectedBoundedContext2(String mergeModeSelectedBoundedContext2) {
		this.mergeModeSelectedBoundedContext2 = mergeModeSelectedBoundedContext2;
	}
	
	public void setMergeModeTakeAttributesFromSecondBoundedContext(boolean mergeModeTakeAttributesFromSecondBoundedContext) {
		this.mergeModeTakeAttributesFromSecondBoundedContext = mergeModeTakeAttributesFromSecondBoundedContext;
	}
	
	public boolean isMergeModeTakeAttributesFromSecondBoundedContext() {
		return mergeModeTakeAttributesFromSecondBoundedContext;
	}
}

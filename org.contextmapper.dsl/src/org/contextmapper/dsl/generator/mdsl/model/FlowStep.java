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

public class FlowStep {

	private boolean isDepStep;
	private String command;
	private String event;
	
	public FlowStep(String command, String event, boolean isDepStep) {
		super();
		this.command = command;
		this.event = event;
		this.isDepStep = isDepStep;
	}
	
	public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public String getEvent() {
		return event;
	
	}
	
	public void setEvent(String event) {
		this.event = event;
	}
	
	public boolean isDepStep() {
		return this.isDepStep;
	}
}

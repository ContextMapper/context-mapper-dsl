/*
 * Copyright 2018 The Context Mapper Project Team
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
package org.contextmapper.dsl.generator.servicecutter.output.model;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Class representing the service object from ServiceCutter output. Mapped to
 * Bounded Context in CML.
 * 
 * @author Stefan Kapferer
 *
 */
public class Service {

	private String name;
	private char id;
	private List<String> nanoentities;

	public Service() {
		this.nanoentities = Lists.newArrayList();
	}

	public char getId() {
		return id;
	}

	public void setId(char id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getNanoentities() {
		return nanoentities;
	}

	public void setNanoentities(List<String> nanoentities) {
		this.nanoentities = nanoentities;
	}

}

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
package org.contextmapper.dsl.generator.servicecutter.input.model;

/**
 * ServiceCutter EnityRelation input object.
 * 
 * @author Stefan Kapferer
 *
 */
public class EntityRelation {

	private String origin;
	private String destination;
	private Relationtype type;

	public EntityRelation(String origin, String destination, Relationtype type) {
		this.origin = origin;
		this.destination = destination;
		this.type = type;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Relationtype getType() {
		return type;
	}

	public void setType(Relationtype type) {
		this.type = type;
	}

}

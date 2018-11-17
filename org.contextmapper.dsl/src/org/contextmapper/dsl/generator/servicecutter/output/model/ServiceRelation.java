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

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Class representing the service relation from ServiceCutter output. Mapped to
 * Bounded Context Relationship in CML.
 * 
 * @author Stefan Kapferer
 *
 */
public class ServiceRelation {

	private String serviceA;
	private String serviceB;
	private String direction;
	private Set<String> sharedEntities;

	public ServiceRelation() {
		this.sharedEntities = Sets.newHashSet();
	}

	public String getServiceA() {
		return serviceA;
	}

	public void setServiceA(String serviceA) {
		this.serviceA = serviceA;
	}

	public String getServiceB() {
		return serviceB;
	}

	public void setServiceB(String serviceB) {
		this.serviceB = serviceB;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public Set<String> getSharedEntities() {
		return sharedEntities;
	}

	public void setSharedEntities(Set<String> sharedEntities) {
		this.sharedEntities = sharedEntities;
	}

}

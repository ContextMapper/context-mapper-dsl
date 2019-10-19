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
package org.contextmapper.dsl.generator.mdsl.model;

import java.util.List;

import com.google.common.collect.Lists;

public class EndpointClient {

	private String name;
	private List<String> consumedOfferNames = Lists.newArrayList();
	private List<String> comments = Lists.newArrayList();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getConsumedOfferNames() {
		return consumedOfferNames;
	}

	public void addConsumedOffer(String offer) {
		this.consumedOfferNames.add(offer);
	}
	
	public void addComment(String comment) {
		this.comments.add(comment);
	}

	public List<String> getComments() {
		return comments;
	}

	public boolean hasComments() {
		return !this.comments.isEmpty();
	}

}

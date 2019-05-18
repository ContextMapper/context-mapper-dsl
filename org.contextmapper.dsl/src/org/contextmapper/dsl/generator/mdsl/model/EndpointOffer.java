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

public class EndpointOffer {

	private EndpointContract offeredEndpoint;
	private String protocol;
	private String location;
	private String protocolComment;

	public void setOfferedEndpoint(EndpointContract offeredEndpoint) {
		this.offeredEndpoint = offeredEndpoint;
	}

	public EndpointContract getOfferedEndpoint() {
		return offeredEndpoint;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public void setProtocolComment(String protocolComment) {
		this.protocolComment = protocolComment;
	}

	public String getProtocolComment() {
		return protocolComment;
	}

	public boolean hasProtocolComment() {
		return protocolComment != null && !"".equals(protocolComment);
	}

}

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
package org.contextmapper.dsl.generator.mdsl;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Stores identifiers of protected objects. Used by MDSLModelCreator to know
 * which element shouldn't be generated again. In addition contains the
 * protected region part to be added to newly generated file.
 * 
 * @author Stefan Kapferer
 *
 */
public class ProtectedRegionContext {

	private Set<String> dataTypeIdentifiers = Sets.newHashSet();
	private Set<String> endpointIdentifiers = Sets.newHashSet();
	private Set<String> providerIdentifiers = Sets.newHashSet();
	private Set<String> clientIdentifiers = Sets.newHashSet();

	private String protectedDataTypeRegion;
	private String protectedEndpointRegion;
	private String protectedProviderRegion;
	private String protectedClientRegion;

	public void addDataTypeIdentifiers(Set<String> identifier) {
		this.dataTypeIdentifiers.addAll(identifier);
	}

	public void addEndpointIdentifiers(Set<String> identifier) {
		this.endpointIdentifiers.addAll(identifier);
	}

	public void addProviderIdentifiers(Set<String> identifier) {
		this.providerIdentifiers.addAll(identifier);
	}

	public void addClientIdentifiers(Set<String> identifier) {
		this.clientIdentifiers.addAll(identifier);
	}

	public Set<String> getDataTypeIdentifiers() {
		return dataTypeIdentifiers;
	}

	public Set<String> getEndpointIdentifiers() {
		return endpointIdentifiers;
	}

	public Set<String> getProviderIdentifiers() {
		return providerIdentifiers;
	}

	public Set<String> getClientIdentifiers() {
		return clientIdentifiers;
	}

	public void setProtectedDataTypeRegion(String protectedDataTypeRegion) {
		this.protectedDataTypeRegion = protectedDataTypeRegion;
	}

	public void setProtectedEndpointRegion(String protectedEndpointRegion) {
		this.protectedEndpointRegion = protectedEndpointRegion;
	}

	public void setProtectedProviderRegion(String protectedProviderRegion) {
		this.protectedProviderRegion = protectedProviderRegion;
	}

	public void setProtectedClientRegion(String protectedClientRegion) {
		this.protectedClientRegion = protectedClientRegion;
	}

	public String getProtectedDataTypeRegion() {
		return protectedDataTypeRegion;
	}

	public String getProtectedEndpointRegion() {
		return protectedEndpointRegion;
	}

	public String getProtectedProviderRegion() {
		return protectedProviderRegion;
	}

	public String getProtectedClientRegion() {
		return protectedClientRegion;
	}
	
}

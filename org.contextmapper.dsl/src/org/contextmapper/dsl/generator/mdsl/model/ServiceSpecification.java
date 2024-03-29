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

public class ServiceSpecification {

	private String name;
	private List<DataType> dataTypes = Lists.newArrayList();
	private List<EndpointContract> endpoints = Lists.newArrayList();
	private List<EndpointProvider> providers = Lists.newArrayList();
	private List<EndpointClient> clients = Lists.newArrayList();
	
	private List<OrchestrationFlow> flows = Lists.newArrayList();
	private List<String> eventTypes = Lists.newArrayList();
	private List<String> commandTypes = Lists.newArrayList();
	private List<IntegrationScenario> storyScenarios = Lists.newArrayList();
	
	private APIUsageContext usageContext;
	private String dataTypeProtectedRegion;
	private String endpointProtectedRegion;
	private String providerProtectedRegion;
	private String clientProtectedRegion;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DataType> getDataTypes() {
		return dataTypes;
	}

	public List<EndpointContract> getEndpoints() {
		return endpoints;
	}

	public List<EndpointProvider> getProviders() {
		return providers;
	}

	public void addDataType(DataType dataType) {
		this.dataTypes.add(dataType);
	}

	public void addEndpoint(EndpointContract endpoint) {
		this.endpoints.add(endpoint);
	}

	public void addProvider(EndpointProvider provider) {
		this.providers.add(provider);
	}

	public List<EndpointClient> getClients() {
		return clients;
	}

	public void addClient(EndpointClient client) {
		this.clients.add(client);
	}
	
	public void setUsageContext(APIUsageContext usageContext) {
		this.usageContext = usageContext;
	}
	
	public APIUsageContext getUsageContext() {
		return usageContext;
	}

	public void setDataTypeProtectedRegion(String dataTypeProtectedRegion) {
		this.dataTypeProtectedRegion = dataTypeProtectedRegion;
	}
	
	public String getDataTypeProtectedRegion() {
		return dataTypeProtectedRegion;
	}
	
	public void setEndpointProtectedRegion(String endpointProtectedRegion) {
		this.endpointProtectedRegion = endpointProtectedRegion;
	}
	
	public String getEndpointProtectedRegion() {
		return endpointProtectedRegion;
	}
	
	public void setProviderProtectedRegion(String providerProtectedRegion) {
		this.providerProtectedRegion = providerProtectedRegion;
	}
	
	public String getProviderProtectedRegion() {
		return providerProtectedRegion;
	}
	
	public void setClientProtectedRegion(String clientProtectedRegion) {
		this.clientProtectedRegion = clientProtectedRegion;
	}
	
	public String getClientProtectedRegion() {
		return clientProtectedRegion;
	}
	
	public List<OrchestrationFlow> getFlows() {
		return this.flows;
	}

	public void addFlow(OrchestrationFlow flow) {
		this.flows.add(flow);
	}
	
	public List<String> getEventTypes() {
		return this.eventTypes;
	}

	public void addEventType(String command) {
		this.eventTypes.add(command);
	}

	public List<String> getCommandTypes() {
		return this.commandTypes;
	}

	public void addCommandType(String command) {
		this.commandTypes.add(command);
	}
	
	public List<IntegrationScenario> getScenarios() {
		return this.storyScenarios;
	}

	public void addScenario(IntegrationScenario scenario) {
		this.storyScenarios.add(scenario);
	}
}

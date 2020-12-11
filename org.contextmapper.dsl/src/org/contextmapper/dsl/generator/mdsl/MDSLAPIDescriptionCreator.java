/*
 * Copyright 2020 The Context Mapper Project Team
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.contextmapper.dsl.generator.AbstractFreemarkerTextCreator;
import org.contextmapper.dsl.generator.mdsl.model.DataType;
import org.contextmapper.dsl.generator.mdsl.model.EndpointClient;
import org.contextmapper.dsl.generator.mdsl.model.EndpointContract;
import org.contextmapper.dsl.generator.mdsl.model.EndpointProvider;
import org.contextmapper.dsl.generator.mdsl.model.ServiceSpecification;

public class MDSLAPIDescriptionCreator extends AbstractFreemarkerTextCreator<ServiceSpecification> {

	private static final String TEMPLATE_NAME = "mdsl-api-description.ftl";

	private ProtectedRegionContext protectedRegionContext;
	private String inputFileName;

	public MDSLAPIDescriptionCreator(ProtectedRegionContext protectedRegionContext, String inputFileName) {
		this.protectedRegionContext = protectedRegionContext;
	}

	@Override
	protected void preprocessing(ServiceSpecification modelObject) {
		updateServiceSpecification4ProtectedRegions(modelObject);
	}

	@Override
	protected void registerModelObjects(Map<String, Object> root, ServiceSpecification modelObject) {
		root.put("serviceSpecification", modelObject);
		root.put("timestampString", getTimestampString(inputFileName));
	}

	@Override
	protected String getTemplateName() {
		return TEMPLATE_NAME;
	}

	@Override
	protected Class<?> getTemplateClass() {
		return MDSLAPIDescriptionCreator.class;
	}

	protected String getTimestampString(String inputFileName) {
		return "Generated from DDD Context Map '" + inputFileName + "' at " + new SimpleDateFormat("dd.MM.YYYY HH:mm:ss z").format(new Date()) + ".";
	}

	private void updateServiceSpecification4ProtectedRegions(ServiceSpecification serviceSpecification) {
		serviceSpecification.setDataTypeProtectedRegion(protectedRegionContext.getProtectedDataTypeRegion());
		serviceSpecification.setEndpointProtectedRegion(protectedRegionContext.getProtectedEndpointRegion());
		serviceSpecification.setProviderProtectedRegion(protectedRegionContext.getProtectedProviderRegion());
		serviceSpecification.setClientProtectedRegion(protectedRegionContext.getProtectedClientRegion());

		// remove protected data types
		for (String protectedDataTye : this.protectedRegionContext.getDataTypeIdentifiers()) {
			Optional<DataType> dataType = serviceSpecification.getDataTypes().stream().filter(dt -> dt.getName().equals(protectedDataTye)).findAny();
			if (dataType.isPresent())
				serviceSpecification.getDataTypes().remove(dataType.get());
		}

		// remove protected endpoints
		for (String protectedEndpoint : this.protectedRegionContext.getEndpointIdentifiers()) {
			Optional<EndpointContract> endpoint = serviceSpecification.getEndpoints().stream().filter(ec -> ec.getName().equals(protectedEndpoint)).findAny();
			if (endpoint.isPresent())
				serviceSpecification.getEndpoints().remove(endpoint.get());
		}

		// remove protected providers
		for (String protectedProvider : this.protectedRegionContext.getProviderIdentifiers()) {
			Optional<EndpointProvider> provider = serviceSpecification.getProviders().stream().filter(p -> p.getName().equals(protectedProvider)).findAny();
			if (provider.isPresent())
				serviceSpecification.getProviders().remove(provider.get());
		}

		// remove protected clients
		for (String protectedClient : this.protectedRegionContext.getClientIdentifiers()) {
			Optional<EndpointClient> client = serviceSpecification.getClients().stream().filter(cl -> cl.getName().equals(protectedClient)).findAny();
			if (client.isPresent())
				serviceSpecification.getClients().remove(client.get());
		}
	}

}

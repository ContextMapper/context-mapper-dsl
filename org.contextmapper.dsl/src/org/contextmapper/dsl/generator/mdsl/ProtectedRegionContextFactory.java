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

public class ProtectedRegionContextFactory {

	public ProtectedRegionContext createProtectedRegionContextForNewMDSLFile() {
		ProtectedRegionContext context = new ProtectedRegionContext();
		context.setProtectedDataTypeRegion("// Hint: move data types which should not be overwritten by the generator into this section.\n");
		context.setProtectedEndpointRegion("// Hint: move endpoints which should not be overwritten by the generator into this section.\n");
		context.setProtectedProviderRegion("// Hint: move providers which should not be overwritten by the generator into this section.\n");
		context.setProtectedClientRegion("// Hint: move providers which should not be overwritten by the generator into this section.\n");
		return context;
	}

	public ProtectedRegionContext createProtectedRegionContextForExistingMDSLFile(String fileContent) {
		ProtectedRegionContext context = new ProtectedRegionContext();
		ProtectedRegionReader reader = new ProtectedRegionReader();

		String protectedSectionDataTypes = reader.getProtectedRegionContent(fileContent, ProtectedRegionIdentifier.DATA_TYPE_REGION);
		String protectedSectionEndpoints = reader.getProtectedRegionContent(fileContent, ProtectedRegionIdentifier.ENDPOINT_REGION);
		String protectedSectionProviders = reader.getProtectedRegionContent(fileContent, ProtectedRegionIdentifier.PROVIDER_REGION);
		String protectedSectionClient = reader.getProtectedRegionContent(fileContent, ProtectedRegionIdentifier.CLIENT_REGION);

		context.setProtectedDataTypeRegion(protectedSectionDataTypes);
		context.setProtectedEndpointRegion(protectedSectionEndpoints);
		context.setProtectedProviderRegion(protectedSectionProviders);
		context.setProtectedClientRegion(protectedSectionClient);

		if (protectedSectionDataTypes != null)
			context.addDataTypeIdentifiers(reader.getIdentifiersInProtectedRegion(protectedSectionDataTypes, ProtectedRegionIdentifier.DATA_TYPE_REGION));

		if (protectedSectionEndpoints != null)
			context.addEndpointIdentifiers(reader.getIdentifiersInProtectedRegion(protectedSectionEndpoints, ProtectedRegionIdentifier.ENDPOINT_REGION));

		if (protectedSectionProviders != null)
			context.addProviderIdentifiers(reader.getIdentifiersInProtectedRegion(protectedSectionProviders, ProtectedRegionIdentifier.PROVIDER_REGION));

		if (protectedSectionClient != null)
			context.addClientIdentifiers(reader.getIdentifiersInProtectedRegion(protectedSectionClient, ProtectedRegionIdentifier.CLIENT_REGION));

		return context;
	}

}

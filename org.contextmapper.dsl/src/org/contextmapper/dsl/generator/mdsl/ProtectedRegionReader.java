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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;

/**
 * Reads protected regions from existing MDSL file
 * 
 * @author Stefan Kapferer
 * 
 */
public class ProtectedRegionReader {

	public String getProtectedRegionContent(String mdslInput, ProtectedRegionIdentifier regionIdentifier) {
		String regionStartString = getRegionStartString(regionIdentifier);
		if (containsProtectedRegion(mdslInput, regionIdentifier))
			return mdslInput.substring(mdslInput.indexOf(regionStartString) + regionStartString.length() + 1, mdslInput.lastIndexOf(getRegionEndString(regionIdentifier)) - 1);
		return null;
	}

	public boolean containsProtectedRegion(String mdslInput, ProtectedRegionIdentifier regionIdentifier) {
		String regionStartString = getRegionStartString(regionIdentifier);
		return mdslInput.indexOf(regionStartString) > -1;
	}

	public Set<String> getIdentifiersInProtectedRegion(String protectedRegion, ProtectedRegionIdentifier regionIdentifier) {
		Set<String> ids = Sets.newHashSet();
		for (String line : protectedRegion.split("\\r?\\n")) {
			Pattern pattern = Pattern.compile("^[^\\/]?" + getIdentifierKeyWord(regionIdentifier) + " ([\\^]?[a-zA-Z_]{1}[a-zA-Z0-9_]*).*$");
			Matcher matcher = pattern.matcher(line);
			while (matcher.find()) {
				ids.add(matcher.group(1));
			}
		}
		return ids;
	}

	private String getRegionStartString(ProtectedRegionIdentifier regionIdentifier) {
		return "// ** BEGIN PROTECTED REGION for " + regionIdentifier.toString();
	}

	private String getRegionEndString(ProtectedRegionIdentifier regionIdentifier) {
		return "// ** END PROTECTED REGION for " + regionIdentifier.toString();
	}

	private String getIdentifierKeyWord(ProtectedRegionIdentifier regionIdentifier) {
		switch (regionIdentifier) {
		case DATA_TYPE_REGION:
			return "data type";
		case ENDPOINT_REGION:
			return "endpoint type";
		case PROVIDER_REGION:
			return "API provider";
		default:
			return "API client";
		}
	}

}

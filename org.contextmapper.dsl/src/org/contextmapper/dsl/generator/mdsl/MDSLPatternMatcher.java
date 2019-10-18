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

/**
 * Used to match MDSL patterns in CML doc strings. Returns empty string if
 * pattern cannot be matched.
 * 
 * @author Stefan Kapferer
 *
 */
public class MDSLPatternMatcher {

	public static final String[] ENDPOINT_SERVES_AS_PATTERNS = { "INFORMATION_HOLDER_RESOURCE", "PROCESSING_RESOURCE", "MASTER_DATA_HOLDER", "TRANSACTIONAL_DATA_HOLDER",
			"STATIC_DATA_HOLDER", "TRANSFER_RESOURCE", "CONNECTOR_RESOURCE", "LOOKUP_RESOURCE", "GATEWAY_RESOURCE", "GUARD_RESOURCE", "GROUND_RESOURCE" };

	public static final String[] OPERATION_RESPONSIBILITY_PATTERNS = { "COMPUTATION_FUNCTION", "RETRIEVAL_OPERATION", "EVENT_PROCESSOR", "BUSINESS_ACTIVITY_PROCESSOR" };

	public String matchPatterns(String[] patterns, String inputString) {
		for (String pattern : patterns) {
			if (inputString.contains(pattern))
				return pattern;
		}
		return "";
	}

}

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
package org.contextmapper.dsl.generator.servicecutter.input.userrepresentations;

import java.util.Set;

import org.contextmapper.dsl.contextMappingDSL.Feature;
import org.contextmapper.dsl.contextMappingDSL.UserRequirement;

import com.google.common.collect.Sets;

/**
 * Helper class used to collect nanoentities out of Use Case / User Story
 * definitions.
 */
public class NanoentityCollector {

	private static final String DEFAULT_VERB_CREATE = "create";
	private static final String DEFAULT_VERB_READ = "read";
	private static final String DEFAULT_VERB_UPDATE = "update";
	private static final String DEFAULT_VERB_DELETE = "delete";

	public Set<String> getNanoentitiesRead(UserRequirement ur) {
		Set<String> nanoEntities = Sets.newHashSet();
		for (Feature feature : ur.getFeatures())
			nanoEntities.addAll(getNanoentitiesRead(feature));
		return nanoEntities;
	}

	public Set<String> getNanoentitiesWritten(UserRequirement ur) {
		Set<String> nanoEntities = Sets.newHashSet();
		for (Feature feature : ur.getFeatures())
			nanoEntities.addAll(getNanoentitiesWritten(feature));
		return nanoEntities;
	}

	private Set<String> getNanoentitiesRead(Feature feature) {
		String verb = feature.getVerb().toLowerCase();
		if (verb.equals(DEFAULT_VERB_CREATE) || verb.equals(DEFAULT_VERB_UPDATE) || verb.equals(DEFAULT_VERB_DELETE))
			return Sets.newHashSet();

		return getAllNanoentities(feature);
	}

	private Set<String> getNanoentitiesWritten(Feature feature) {
		String verb = feature.getVerb().toLowerCase();
		if (verb.equals(DEFAULT_VERB_READ))
			return Sets.newHashSet();

		return getAllNanoentities(feature);
	}

	private Set<String> getAllNanoentities(Feature feature) {
		Set<String> nanoEntities = Sets.newHashSet();
		for (String attr : feature.getEntityAttributes())
			nanoEntities.add(feature.getEntity().trim() + "." + attr.trim());
		return nanoEntities;
	}

}

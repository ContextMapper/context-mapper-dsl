/*
 * Copyright 2024 The Context Mapper Project Team
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
package org.contextmapper.dsl.generator.plantuml.value_impact_mapping;

import java.util.Map;

import org.contextmapper.dsl.generator.AbstractFreemarkerTextCreator;
import org.contextmapper.dsl.generator.plantuml.value_impact_mapping.model.SystemOfInterest;

public class ValueImpactMapPumlTextCreator extends AbstractFreemarkerTextCreator<SystemOfInterest> {

	private static final String TEMPLATE_NAME = "value-impact-map-puml.ftl";

	@Override
	protected void preprocessing(SystemOfInterest soi) {
		// nothing to
	}

	@Override
	protected void registerModelObjects(Map<String, Object> root, SystemOfInterest soi) {
		root.put("soi", soi);
	}

	@Override
	protected String getTemplateName() {
		return TEMPLATE_NAME;
	}

	@Override
	protected Class<?> getTemplateClass() {
		return ValueImpactMapPumlTextCreator.class;
	}

}

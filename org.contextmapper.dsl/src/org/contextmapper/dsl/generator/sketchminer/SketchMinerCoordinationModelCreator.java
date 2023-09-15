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
package org.contextmapper.dsl.generator.sketchminer;

import java.util.Map;

import org.contextmapper.dsl.contextMappingDSL.Coordination;
import org.contextmapper.dsl.generator.AbstractFreemarkerTextCreator;
import org.contextmapper.dsl.generator.sketchminer.converter.Coordination2SketchMinerConverter;

public class SketchMinerCoordinationModelCreator extends AbstractFreemarkerTextCreator<Coordination> {

	private static final String TEMPLATE_NAME = "sketchminer.ftl";

	@Override
	protected void preprocessing(Coordination modelObject) {
		// nothing to do here
	}

	@Override
	protected void registerModelObjects(Map<String, Object> root, Coordination coordination) {
		Coordination2SketchMinerConverter converter = new Coordination2SketchMinerConverter(coordination);
		root.put("model", converter.convert());
	}

	@Override
	protected String getTemplateName() {
		return TEMPLATE_NAME;
	}

	@Override
	protected Class<?> getTemplateClass() {
		return SketchMinerCoordinationModelCreator.class;
	}

}

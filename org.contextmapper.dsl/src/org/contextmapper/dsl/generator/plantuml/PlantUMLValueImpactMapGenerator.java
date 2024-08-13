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
package org.contextmapper.dsl.generator.plantuml;

import org.contextmapper.dsl.contextMappingDSL.ValueRegister;
import org.contextmapper.dsl.generator.plantuml.value_impact_mapping.CML2ValueImpactModelMapper;
import org.contextmapper.dsl.generator.plantuml.value_impact_mapping.ValueImpactMapPumlTextCreator;

public class PlantUMLValueImpactMapGenerator implements PlantUMLDiagramCreator<ValueRegister> {

	@Override
	public String createDiagram(ValueRegister valueRegister) {
		return new ValueImpactMapPumlTextCreator().createText(new CML2ValueImpactModelMapper().map(valueRegister));
	}

}

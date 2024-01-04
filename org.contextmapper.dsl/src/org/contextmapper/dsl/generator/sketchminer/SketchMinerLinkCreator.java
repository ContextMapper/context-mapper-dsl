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

import org.contextmapper.dsl.contextMappingDSL.Coordination;
import org.contextmapper.dsl.contextMappingDSL.Flow;
import org.eclipse.emf.ecore.EObject;

/**
 * Generate a Sketch Miner (https://www.bpmn-sketch-miner.ai) link for a CML
 * flow definition.
 *
 */
public class SketchMinerLinkCreator {

	private static final String SKETCH_MINER_URL = "https://www.bpmn-sketch-miner.ai/index.html#";

	public String createSketchMinerLink(Flow flow) {
		String sketchMinerInput = new SketchMinerModelCreator().createText(flow);
		return SKETCH_MINER_URL + LZString.compressToEncodedURIComponent(
				"bpln:v1\n--\n" + sketchMinerInput);
	}
	
	public String createSketchMinerLink(Coordination coordination) {
		String sketchMinerInput = new SketchMinerCoordinationModelCreator().createText(coordination);
		return SKETCH_MINER_URL + LZString.compressToEncodedURIComponent(
				"bpln:v1\n--\n" + sketchMinerInput);
	}

	public String createSketchMinerLink(EObject object) {
		if (object instanceof Flow)
			return createSketchMinerLink((Flow) object);
		else if (object instanceof Coordination)
			return createSketchMinerLink((Coordination) object);
		return "";
	}

}

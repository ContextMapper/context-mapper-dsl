/*
 * Copyright 2018 The Context Mapper Project Team
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
package org.contextmapper.dsl.generator;

import java.io.File;
import java.io.IOException;

import ch.hsr.servicecutter.api.EntityRelationDiagramImporterJSON;
import ch.hsr.servicecutter.api.ResultSerializer;
import ch.hsr.servicecutter.api.ServiceCutter;
import ch.hsr.servicecutter.api.ServiceCutterContext;
import ch.hsr.servicecutter.api.ServiceCutterContextBuilder;
import ch.hsr.servicecutter.api.model.EntityRelationDiagram;
import ch.hsr.servicecutter.api.model.SolverResult;

public class ServiceCutterResultGenerator {

	public void doGenerate(final File inputFile) {
		try {
			EntityRelationDiagram erd = new EntityRelationDiagramImporterJSON().createERDFromJSONFile(inputFile);
			ServiceCutterContext context = new ServiceCutterContextBuilder(erd).build();
			SolverResult result = new ServiceCutter(context).generateDecomposition();
			
			ResultSerializer serializer = new ResultSerializer();
			serializer.serializeResult(result, new File(inputFile.getParent(), "service-cutter-result.json"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

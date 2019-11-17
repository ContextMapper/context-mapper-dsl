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
package org.contextmapper.dsl.generator.servicecutter.output.factory;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.hsr.servicecutter.api.model.SolverResult;

public class ServiceCutterOutputModelFactory {

	private ObjectMapper objectMapper;

	public ServiceCutterOutputModelFactory() {
		this.objectMapper = new ObjectMapper();
	}

	public SolverResult createFromJsonFile(File jsonFile) {
		try {
			return objectMapper.readValue(jsonFile, SolverResult.class);
		} catch (Exception e) {
			throw new ServiceCutterOutputModelReadingException(jsonFile.getAbsolutePath(), e);
		}
	}

}

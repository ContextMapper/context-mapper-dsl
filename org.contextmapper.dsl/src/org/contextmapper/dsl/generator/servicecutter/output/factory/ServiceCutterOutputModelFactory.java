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

import org.contextmapper.dsl.generator.servicecutter.output.model.ServiceCutterOutputModel;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceCutterOutputModelFactory {

	private ObjectMapper objectMapper;

	public ServiceCutterOutputModelFactory() {
		this.objectMapper = new ObjectMapper();
	}

	public ServiceCutterOutputModel createFromJsonFile(URI jsonFileURI) {
		try {
			URI resolvedFile = CommonPlugin.resolve(jsonFileURI);
			IFile jsonFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(resolvedFile.toFileString()));
			return createFromJsonFile(jsonFile.getFullPath().toFile());
		} catch (Exception e) {
			throw new ServiceCutterOutputModelReadingException(jsonFileURI.toPlatformString(true));
		}
	}

	public ServiceCutterOutputModel createFromJsonFile(File jsonFile) {
		try {
			return objectMapper.readValue(jsonFile, ServiceCutterOutputModel.class);
		} catch (Exception e) {
			throw new ServiceCutterOutputModelReadingException(jsonFile.getAbsolutePath());
		}
	}

}

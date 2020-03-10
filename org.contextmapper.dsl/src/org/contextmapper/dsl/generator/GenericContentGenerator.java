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
package org.contextmapper.dsl.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.contextmapper.dsl.generator.freemarker.FreemarkerTextGenerator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.generator.IFileSystemAccess2;

/**
 * Can generate arbitrary text files from a CML Context Map using a Freemarker
 * template.
 * 
 * @author Stefan Kapferer
 *
 */
public class GenericContentGenerator extends AbstractContextMappingModelGenerator {

	private File freemarkerTemplateFile;
	private String targetFileName;
	private Map<String, Object> customDataMap = new HashMap<>();;

	public void setFreemarkerTemplateFile(File freemarkerTemplateFile) {
		this.freemarkerTemplateFile = freemarkerTemplateFile;
	}

	public void setTargetFileName(String targetFileName) {
		this.targetFileName = targetFileName;
	}

	public void registerCustomModelProperty(String propertyName, Object object) {
		customDataMap.put(propertyName, object);
	}

	@Override
	protected void generateFromContextMappingModel(ContextMappingModel model, IFileSystemAccess2 fsa, URI inputFileURI) {
		if (freemarkerTemplateFile == null)
			throw new GeneratorInputException("The freemarker template has not been set!");
		if (!freemarkerTemplateFile.exists())
			throw new GeneratorInputException("The file '" + freemarkerTemplateFile.getAbsolutePath().toString() + "' does not exist!");
		if (targetFileName == null || "".equals(targetFileName))
			throw new GeneratorInputException("Please provide a name for the file that shall be generated.");

		FreemarkerTextGenerator generator = new FreemarkerTextGenerator(freemarkerTemplateFile);
		for (Map.Entry<String, Object> customDataEntry : customDataMap.entrySet()) {
			generator.registerCustomModelProperty(customDataEntry.getKey(), customDataEntry.getValue());
		}
		fsa.generateFile(targetFileName, generator.generate(model));
	}

}

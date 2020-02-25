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
package org.contextmapper.dsl.generator.freemarker;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * Generates a string from a CML Context Map based on a Freemarker template.
 * 
 * @author Stefan Kapferer
 *
 */
public class FreemarkerTextGenerator {

	private File templateFile;

	public FreemarkerTextGenerator(File templateFile) {
		this.templateFile = templateFile;
	}

	public String generate(ContextMap contextMap) {
		String result = "";
		try {
			Configuration configuration = configureFreemarker();
			Template template = configuration.getTemplate(templateFile.getName());

			StringWriter writer = new StringWriter();
			template.process(prepareModelData(contextMap), writer);
			result = writer.toString();
		} catch (Exception e) {
			throw new ContextMapperApplicationException("Freemarker template exception: " + e.getMessage(), e);
		}
		return result;
	}

	private Map<String, Object> prepareModelData(ContextMap contextMap) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("contextMap", contextMap);
		dataMap.put("timestampString", new SimpleDateFormat("dd.MM.YYYY HH:mm:ss z").format(new Date()));
		return dataMap;
	}

	private Configuration configureFreemarker() throws IOException {
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
		configuration.setDirectoryForTemplateLoading(templateFile.getParentFile());
		configuration.setDefaultEncoding("UTF-8");
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		return configuration;
	}

}

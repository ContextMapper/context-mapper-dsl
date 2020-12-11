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
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public abstract class AbstractFreemarkerTextCreator<T> {

	private Configuration freemarkerConfig;
	private Template freemarkerTemplate;

	public AbstractFreemarkerTextCreator() {
		loadFreemarkerTemplate();
	}

	public String createText(T modelObject) {
		preprocessing(modelObject);
		Map<String, Object> root = new HashMap<>();
		registerModelObjects(root, modelObject);
		StringWriter writer = new StringWriter();
		try {
			freemarkerTemplate.process(root, writer);
		} catch (Exception e) {
			throw new RuntimeException("Error in processing freemarker template.", e);
		}
		return writer.toString();
	}

	protected abstract void preprocessing(T modelObject);

	protected abstract void registerModelObjects(Map<String, Object> root, T modelObject);

	protected abstract String getTemplateName();

	protected abstract Class<?> getTemplateClass();

	private void loadFreemarkerTemplate() {
		URL url = getTemplateClass().getResource(getTemplateName());
		File templateDir = new File(System.getProperty("java.io.tmpdir"), "context-mapper-freemarker-templates-" + getPID());
		if (!templateDir.exists())
			templateDir.mkdir();
		try {
			FileUtils.copyURLToFile(url, new File(templateDir, getTemplateName()));

			freemarkerConfig = new Configuration(Configuration.VERSION_2_3_22);
			freemarkerConfig.setDirectoryForTemplateLoading(templateDir);
			freemarkerConfig.setDefaultEncoding("UTF-8");
			freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			freemarkerTemplate = freemarkerConfig.getTemplate(getTemplateName());
		} catch (Exception e) {
			throw new RuntimeException("Cannot load freemarker template!", e);
		}
	}

	private long getPID() {
		String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		return Long.parseLong(processName.split("@")[0]);
	}

}

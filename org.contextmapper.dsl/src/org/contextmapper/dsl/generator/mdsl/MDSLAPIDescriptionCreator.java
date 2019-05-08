package org.contextmapper.dsl.generator.mdsl;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.contextmapper.dsl.generator.mdsl.model.ServiceSpecification;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class MDSLAPIDescriptionCreator {

	private static final String TEMPLATE_NAME = "mdsl-api-description.ftl";

	private Configuration freemarkerConfig;
	private Template freemarkerTemplate;

	public MDSLAPIDescriptionCreator() {
		loadFreemarkerTemplate();
	}

	public String createAPIDescriptionText(ServiceSpecification serviceSpecification) {
		Map<String, Object> root = new HashMap<>();
		root.put("serviceSpecification", serviceSpecification);
		StringWriter writer = new StringWriter();
		try {
			freemarkerTemplate.process(root, writer);
		} catch (Exception e) {
			throw new RuntimeException("Error in processing freemarker template.", e);
		}
		return writer.toString();
	}

	private void loadFreemarkerTemplate() {
		URL url = getClass().getResource(TEMPLATE_NAME);
		File templateDir = new File(System.getProperty("java.io.tmpdir"), "context-mapper-freemarker-templates-" + getPID());
		if (!templateDir.exists())
			templateDir.mkdir();
		try {
			FileUtils.copyURLToFile(url, new File(templateDir, TEMPLATE_NAME));

			freemarkerConfig = new Configuration(Configuration.VERSION_2_3_22);
			freemarkerConfig.setDirectoryForTemplateLoading(templateDir);
			freemarkerConfig.setDefaultEncoding("UTF-8");
			freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			freemarkerTemplate = freemarkerConfig.getTemplate(TEMPLATE_NAME);
		} catch (Exception e) {
			throw new RuntimeException("Cannot load freemarker template!");
		}
	}

	private long getPID() {
		String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		return Long.parseLong(processName.split("@")[0]);
	}

}

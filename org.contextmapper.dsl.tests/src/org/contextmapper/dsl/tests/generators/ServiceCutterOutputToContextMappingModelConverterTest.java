package org.contextmapper.dsl.tests.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.file.Paths;

import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.servicecutter.output.converter.ServiceCutterOutputToContextMappingModelConverter;
import org.contextmapper.dsl.generator.servicecutter.output.factory.ServiceCutterOutputModelFactory;
import org.contextmapper.dsl.generator.servicecutter.output.model.ServiceCutterOutputModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceCutterOutputToContextMappingModelConverterTest {

	private ServiceCutterOutputToContextMappingModelConverter converter;
	private File sampleFile;

	@BeforeEach
	public void prepare() {
		this.converter = new ServiceCutterOutputToContextMappingModelConverter();
		this.sampleFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/ServiceCutter-Services.json");
	}

	@Test
	void testWithSampleFile() {
		// given
		ServiceCutterOutputModel model = new ServiceCutterOutputModelFactory().createFromJsonFile(sampleFile);

		// when
		ContextMappingModel contextMappingModel = this.converter.convert(model);
		ContextMap contextMap = contextMappingModel.getMap();
		
		// then
		assertEquals(3, contextMap.getBoundedContexts().size());
		assertEquals(2, contextMap.getRelationships().size());
	}

}

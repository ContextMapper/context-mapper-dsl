package org.contextmapper.dsl.tests.generators;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Paths;

import org.contextmapper.dsl.generator.CMLByServiceCutterOutputGenerator;
import org.contextmapper.dsl.generator.servicecutter.output.factory.ServiceCutterOutputModelFactory;
import org.eclipse.emf.common.util.URI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CMLByServiceCutterOutputGeneratorTest {

	private CMLByServiceCutterOutputGenerator generator;
	private File integTestFile;
	private String resultFilePath = "/integ-test-files/ServiceCutter-Services.cml";

	@BeforeEach
	public void prepare() {
		this.generator = new CMLByServiceCutterOutputGenerator();
		this.integTestFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/ServiceCutter-Services.json");
	}

	@Test
	void testGenerator() {
		// given
		URI uri = URI.createFileURI(this.integTestFile.getAbsolutePath());

		// when
		ServiceCutterOutputModelFactory modelFactory = new ServiceCutterOutputModelFactory();
		this.generator.doGenerate(uri, modelFactory.createFromJsonFile(this.integTestFile));

		// then
		File resultFile = new File(Paths.get("").toAbsolutePath().toString(), resultFilePath);
		assertTrue(resultFile.exists());
	}

	@AfterEach
	public void cleanup() {
		File resultFile = new File(Paths.get("").toAbsolutePath().toString(), resultFilePath);
		if (resultFile.exists())
			resultFile.delete();
	}

}

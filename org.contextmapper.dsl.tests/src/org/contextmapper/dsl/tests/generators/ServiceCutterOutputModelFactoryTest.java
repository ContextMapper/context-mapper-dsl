package org.contextmapper.dsl.tests.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.nio.file.Paths;

import org.contextmapper.dsl.generator.servicecutter.output.factory.ServiceCutterOutputModelFactory;
import org.contextmapper.dsl.generator.servicecutter.output.factory.ServiceCutterOutputModelReadingException;
import org.contextmapper.dsl.generator.servicecutter.output.model.ServiceCutterOutputModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceCutterOutputModelFactoryTest {

	private File integTestFile;
	private File integTestFileWrongInput;

	@BeforeEach
	public void prepare() {
		this.integTestFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/ServiceCutter-Services.json");
		this.integTestFileWrongInput = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/ServiceCutter-Output-Wrong.json");
	}

	@Test
	void canCreateFromFile() {
		// given
		ServiceCutterOutputModelFactory factory = new ServiceCutterOutputModelFactory();

		// when
		ServiceCutterOutputModel model = factory.createFromJsonFile(integTestFile);

		// then
		assertNotNull(model);
		assertEquals(3, model.getServices().size());
		assertEquals(2, model.getRelations().size());
		assertEquals(2, model.getUseCaseResponsibility().size());
	}

	@Test
	void throwExceptionForWrongFileInput() {
		ServiceCutterOutputModelFactory factory = new ServiceCutterOutputModelFactory();
		assertThrows(ServiceCutterOutputModelReadingException.class, () -> {
			factory.createFromJsonFile(integTestFileWrongInput);
		});
	}

}

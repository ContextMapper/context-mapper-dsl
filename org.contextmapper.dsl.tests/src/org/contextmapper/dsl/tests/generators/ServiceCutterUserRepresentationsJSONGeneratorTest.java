package org.contextmapper.dsl.tests.generators;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.contextmapper.dsl.generator.ServiceCutterUserRepresentationsJSONGenerator;
import org.contextmapper.dsl.tests.generators.mocks.IFileSystemAccess2Mock;
import org.contextmapper.dsl.tests.generators.mocks.IGeneratorContextMock;
import org.contextmapper.dsl.tests.generators.mocks.ServiceCutterConfigurationModelResourceMock;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterConfigurationDSLFactory;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterUserRepresentationsModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceCutterUserRepresentationsJSONGeneratorTest {

	private ServiceCutterUserRepresentationsJSONGenerator generator;

	@BeforeEach
	public void prepare() {
		this.generator = new ServiceCutterUserRepresentationsJSONGenerator();
	}

	@Test
	public void canCreateUserRepresentationsJSONFile() {
		// given
		ServiceCutterUserRepresentationsModel model = ServiceCutterConfigurationDSLFactory.eINSTANCE.createServiceCutterUserRepresentationsModel();

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(new ServiceCutterConfigurationModelResourceMock(model, "testmodel", "scl"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesMap().containsKey("testmodel.json"));
	}

}

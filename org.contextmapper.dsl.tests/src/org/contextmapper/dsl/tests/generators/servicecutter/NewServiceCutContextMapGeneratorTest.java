package org.contextmapper.dsl.tests.generators.servicecutter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.contextmapper.dsl.ContextMappingDSLStandaloneSetup;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.NewServiceCutContextMapGenerator;
import org.contextmapper.dsl.generator.servicecutter.input.converter.SCLToUserRepresentationsConverter;
import org.contextmapper.dsl.tests.AbstractCMLInputFileTest;
import org.contextmapper.dsl.tests.generators.mocks.ContextMappingModelResourceMock;
import org.contextmapper.dsl.tests.generators.mocks.IFileSystemAccess2Mock;
import org.contextmapper.dsl.tests.generators.mocks.IGeneratorContextMock;
import org.contextmapper.servicecutter.dsl.ServiceCutterConfigurationDSLStandaloneSetup;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterUserRepresentationsModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Iterators;

import ch.hsr.servicecutter.api.SolverConfigurationFactory;
import ch.hsr.servicecutter.solver.SolverConfiguration;

public class NewServiceCutContextMapGeneratorTest extends AbstractCMLInputFileTest {

	private static final String TEST_SCL_FILE = "/integ-test-files/servicecutter/DDD_Sample_ServiceCutter-User-Representations.scl";

	private NewServiceCutContextMapGenerator generator;

	@BeforeEach
	public void prepare() {
		super.prepare();
		this.generator = new NewServiceCutContextMapGenerator();
	}

	@Test
	void canGenerateNewContextMap() throws IOException {
		// given
		ContextMappingDSLStandaloneSetup.doSetup();
		Resource input = getResourceCopyOfTestCML("DDD_Sample_Input.cml");
		List<ContextMappingModel> models = IteratorExtensions.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(input.getAllContents(), ContextMappingModel.class));
		ContextMappingModel model = models.get(0);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml").setResourceSet(input.getResourceSet()), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_NewCut_1.cml"));
	}

	@Test
	void canGenerateUniqueFileName() throws IOException {
		// given
		ContextMappingDSLStandaloneSetup.doSetup();
		Resource input = getResourceCopyOfTestCML("DDD_Sample_Input.cml");
		List<ContextMappingModel> models = IteratorExtensions.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(input.getAllContents(), ContextMappingModel.class));
		ContextMappingModel model = models.get(0);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml").setResourceSet(input.getResourceSet()), filesystem, new IGeneratorContextMock());
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml").setResourceSet(input.getResourceSet()), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_NewCut_1.cml"));
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_NewCut_2.cml"));
	}

	@Test
	void canUseCustomSolverConfiguration() throws IOException {
		// given
		ContextMappingDSLStandaloneSetup.doSetup();
		Resource input = getResourceCopyOfTestCML("DDD_Sample_Input.cml");
		List<ContextMappingModel> models = IteratorExtensions.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(input.getAllContents(), ContextMappingModel.class));
		ContextMappingModel model = models.get(0);

		File configurationFile = new File(Paths.get("").toAbsolutePath().toString(), "/integ-test-files/servicecutter/solver-configuration.json");
		SolverConfiguration solverConfiguration = new SolverConfigurationFactory().createConfigurationWithJSONFile(configurationFile);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.setSolverConfiguration(solverConfiguration);
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml").setResourceSet(input.getResourceSet()), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_NewCut_1.cml"));
	}

	@Test
	void canUseUserRepresentations() throws IOException {
		// given
		ContextMappingDSLStandaloneSetup.doSetup();
		Resource input = getResourceCopyOfTestCML("DDD_Sample_Input.cml");
		List<ContextMappingModel> models = IteratorExtensions.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(input.getAllContents(), ContextMappingModel.class));
		ContextMappingModel model = models.get(0);

		ServiceCutterConfigurationDSLStandaloneSetup.doSetup();
		Resource resource = new ResourceSetImpl().getResource(URI.createURI(new File(Paths.get("").toAbsolutePath().toString(), TEST_SCL_FILE).getAbsolutePath()), true);
		ServiceCutterUserRepresentationsModel sclModel = (ServiceCutterUserRepresentationsModel) resource.getContents().get(0);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.setUserRepresentationContainer(new SCLToUserRepresentationsConverter().convert(sclModel));
		this.generator.doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml").setResourceSet(input.getResourceSet()), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesSet().contains("testmodel_NewCut_1.cml"));
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/servicecutter/";
	}

}

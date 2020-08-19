package org.contextmapper.dsl.generators.servicecutter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.generator.NewServiceCutContextMapGenerator;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.contextmapper.dsl.generators.mocks.ContextMappingModelResourceMock;
import org.contextmapper.dsl.generators.mocks.IFileSystemAccess2Mock;
import org.contextmapper.dsl.generators.mocks.IGeneratorContextMock;
import org.contextmapper.servicecutter.dsl.ServiceCutterConfigurationDSLStandaloneSetup;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.IGenerator2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NewServiceCutContextMapGeneratorTest extends AbstractCMLInputFileTest {

	@BeforeEach
	public void prepare() {
		super.prepare();
	}

	@Test
	void throwsExceptionIfModelIsEmpty() throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("CML_Model_without_attributes.cml");
		ContextMappingModel model = input.getContextMappingModel();
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();

		// when, then
		assertThrows(GeneratorInputException.class, () -> {
			new NewServiceCutContextMapGenerator().doGenerate(new ContextMappingModelResourceMock(model, "testmodel", "cml").setResourceSet(input.getResource().getResourceSet()), filesystem,
					new IGeneratorContextMock());
		});
	}

	@Test
	void canGenerateNewContextMap() throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("DDD_Sample_Input.cml");
		new ServiceCutterConfigurationDSLStandaloneSetup().createInjectorAndDoEMFRegistration();

		// when
		IGenerator2 generator = new NewServiceCutContextMapGenerator();
		generator.doGenerate(input.getResource(), getFileSystemAccess(), new GeneratorContext());
		File generatedFile = new File(
				input.getResource().getURI().trimFileExtension().trimSegments(1).appendSegment("DDD_Sample_Input_Markov_Clustering_Cut_1").appendFileExtension("cml").toFileString());

		// then
		assertTrue(generatedFile.exists());
	}

	@Test
	void canCopyOtherRootElements() throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("DDD_Sample_Input_Root-Elements.cml");
		new ServiceCutterConfigurationDSLStandaloneSetup().createInjectorAndDoEMFRegistration();

		// when
		IGenerator2 generator = new NewServiceCutContextMapGenerator();
		generator.doGenerate(input.getResource(), getFileSystemAccess(), new GeneratorContext());
		File generatedFile = new File(
				input.getResource().getURI().trimFileExtension().trimSegments(1).appendSegment("DDD_Sample_Input_Root-Elements_Markov_Clustering_Cut_1").appendFileExtension("cml").toFileString());

		// then
		assertTrue(generatedFile.exists());
		CMLResourceContainer output = new CMLResourceContainer(resourceSet.getResource(URI.createFileURI(generatedFile.getAbsolutePath()), true));
		ContextMappingModel outputModel = output.getContextMappingModel();
		assertEquals(1, outputModel.getDomains().size());
		assertEquals(2, outputModel.getDomains().get(0).getSubdomains().size());
		assertEquals(2, outputModel.getUserRequirements().size());
	}

	@Test
	void canGenerateUniqueFileName() throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("DDD_Sample_Input.cml");
		new ServiceCutterConfigurationDSLStandaloneSetup().createInjectorAndDoEMFRegistration();

		// when
		IGenerator2 generator = new NewServiceCutContextMapGenerator();
		generator.doGenerate(input.getResource(), getFileSystemAccess(), new GeneratorContext());
		generator.doGenerate(input.getResource(), getFileSystemAccess(), new GeneratorContext());

		// then
		assertTrue(new File(input.getResource().getURI().trimFileExtension().trimSegments(1).appendSegment("DDD_Sample_Input_Markov_Clustering_Cut_1").appendFileExtension("cml").toFileString())
				.exists());
		assertTrue(new File(input.getResource().getURI().trimFileExtension().trimSegments(1).appendSegment("DDD_Sample_Input_Markov_Clustering_Cut_2").appendFileExtension("cml").toFileString())
				.exists());
	}

	@Test
	void canCreateConfigFileIfProjectRootIsSet() throws IOException {
		// given
		CMLResourceContainer input = getResourceCopyOfTestCML("DDD_Sample_Input.cml");
		new ServiceCutterConfigurationDSLStandaloneSetup().createInjectorAndDoEMFRegistration();

		// when
		NewServiceCutContextMapGenerator generator = new NewServiceCutContextMapGenerator();
		generator.setProjectDirectory(testDir);
		generator.doGenerate(input.getResource(), getFileSystemAccess(), new GeneratorContext());

		// then
		assertTrue(new File(testDir, ".servicecutter.yml").exists());
	}

	@Test
	void cannotSetNotExistingProjectDir() throws IOException {
		// given
		NewServiceCutContextMapGenerator generator = new NewServiceCutContextMapGenerator();

		// when, then
		assertThrows(ContextMapperApplicationException.class, () -> {
			generator.setProjectDirectory(new File(testDir, "this-directory-does-not-exist"));
		});
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/servicecutter/";
	}

}

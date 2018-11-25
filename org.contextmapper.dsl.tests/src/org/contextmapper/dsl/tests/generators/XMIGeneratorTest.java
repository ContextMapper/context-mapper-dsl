package org.contextmapper.dsl.tests.generators;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Paths;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.XMIGenerator;
import org.contextmapper.dsl.tests.generators.mocks.ContextMappingModelResourceMock;
import org.contextmapper.dsl.tests.generators.mocks.IFileSystemAccess2Mock;
import org.contextmapper.dsl.tests.generators.mocks.IGeneratorContextMock;
import org.eclipse.emf.common.util.URI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class XMIGeneratorTest {

	private XMIGenerator generator;
	private String dummyInputFilePath = "/integ-test-files/model.cml";
	private String resultFiltPath = "/integ-test-files/model.xmi";

	@BeforeEach
	public void prepare() {
		this.generator = new XMIGenerator();
	}

	@Test
	void canCreateXMIModelFromCML() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
		BoundedContext boundedContext = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		boundedContext.setName("TestContext");
		contextMap.getBoundedContexts().add(boundedContext);
		model.getBoundedContexts().add(boundedContext);
		model.setMap(contextMap);

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(
				new ContextMappingModelResourceMock(model, URI.createFileURI(new File(Paths.get("").toAbsolutePath().toString(), dummyInputFilePath).getAbsolutePath())),
				filesystem, new IGeneratorContextMock());

		// then
		File resultFile = new File(Paths.get("").toAbsolutePath().toString(), resultFiltPath);
		assertTrue(resultFile.exists());
	}

	@AfterEach
	public void cleanup() {
		File resultFile = new File(Paths.get("").toAbsolutePath().toString(), resultFiltPath);
		if (resultFile.exists())
			resultFile.delete();
	}

}

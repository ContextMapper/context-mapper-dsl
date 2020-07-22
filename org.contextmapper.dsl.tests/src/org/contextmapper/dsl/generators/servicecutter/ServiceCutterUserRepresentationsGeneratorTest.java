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
package org.contextmapper.dsl.generators.servicecutter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.IOException;

import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.generator.ServiceCutterUserRepresentationsGenerator;
import org.contextmapper.servicecutter.dsl.ServiceCutterConfigurationDSLStandaloneSetup;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterUserRepresentationsModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.IGenerator2;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.parser.IEncodingProvider;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.junit.jupiter.api.Test;

import com.google.inject.Guice;

class ServiceCutterUserRepresentationsGeneratorTest extends AbstractCMLInputFileTest {

	@Test
	void canCreateUserRepresentationsFile() throws IOException {
		// given
		Resource inputResource = getResourceCopyOfTestCML("user-representations-generation-test-1.cml").getResource();
		new ServiceCutterConfigurationDSLStandaloneSetup().createInjectorAndDoEMFRegistration();

		// when
		IGenerator2 generator = new ServiceCutterUserRepresentationsGenerator();
		generator.doGenerate(inputResource, getFileSystemAccess(), new GeneratorContext());
		File sclFile = new File(new File(inputResource.getURI().toFileString()).getParentFile(), "user-representations-generation-test-1.scl");

		// then
		assertTrue(sclFile.exists());
		Resource sclResource = new ResourceSetImpl().getResource(URI.createFileURI(sclFile.getAbsolutePath()), true);
		ServiceCutterUserRepresentationsModel sclModel = (ServiceCutterUserRepresentationsModel) sclResource.getContents().get(0);
		assertNotNull(sclModel);
		assertEquals(1, sclModel.getUseCases().size());
	}

	@Test
	void canCreateUpdateUserRepresentationsFile() throws IOException {
		// given
		Resource inputResource = getResourceCopyOfTestCML("user-representations-generation-test-1.cml").getResource();
		Resource sclResource = getResourceCopyOfTestSCL("user-representations-generation-test-1.scl");

		// when
		IGenerator2 generator = new ServiceCutterUserRepresentationsGenerator();
		generator.doGenerate(inputResource, getFileSystemAccess(), new GeneratorContext());
		sclResource = new ResourceSetImpl()
				.getResource(URI.createFileURI(new File(new File(inputResource.getURI().toFileString()).getParentFile(), "user-representations-generation-test-1.scl").getAbsolutePath()), true);
		ServiceCutterUserRepresentationsModel sclModel = (ServiceCutterUserRepresentationsModel) sclResource.getContents().get(0);

		// then
		assertNotNull(sclModel);
		assertEquals(1, sclModel.getUseCases().size());
		assertNotNull(sclModel.getCompatibilities());
		assertFalse(sclModel.getCompatibilities().getAvailabilityCriticality().isEmpty());
		assertFalse(sclModel.getCompatibilities().getConsistencyCriticality().isEmpty());
		assertFalse(sclModel.getCompatibilities().getContentVolatility().isEmpty());
		assertFalse(sclModel.getCompatibilities().getSecurityCriticality().isEmpty());
		assertFalse(sclModel.getCompatibilities().getStorageSimilarity().isEmpty());
		assertFalse(sclModel.getCompatibilities().getStructuralVolatility().isEmpty());
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/servicecutter/";
	}
}

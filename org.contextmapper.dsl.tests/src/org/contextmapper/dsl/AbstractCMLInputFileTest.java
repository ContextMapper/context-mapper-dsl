/*
 * Copyright 2019 The Context Mapper Project Team
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
package org.contextmapper.dsl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.servicecutter.dsl.ServiceCutterConfigurationDSLStandaloneSetup;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.parser.IEncodingProvider;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.junit.jupiter.api.BeforeEach;

import com.google.inject.Guice;

public abstract class AbstractCMLInputFileTest extends AbstractDirectoryIntegrationTest {
	protected ResourceSet resourceSet;

	@BeforeEach
	public void prepare() {
		super.prepare();
		this.resourceSet = new ResourceSetImpl();
	}

	protected CMLResourceContainer getResourceCopyOfTestCML(String testCMLName) throws IOException {
		File testFile = new File(testDir, testCMLName);
		FileUtils.copyFile(getTestFile(testCMLName), testFile);
		new ContextMappingDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
		return new CMLResourceContainer(resourceSet.getResource(URI.createFileURI(testFile.getAbsolutePath()), true));
	}

	protected File getCopyOfTestInputFile(String testInputFileName) throws IOException {
		File testInputFile = new File(testDir, testInputFileName);
		FileUtils.copyFile(getTestFile(testInputFileName), testInputFile);
		return testInputFile;
	}

	/**
	 * In case multiple files are needed for the test (are copied to same directory)
	 */
	protected ResourceSet getResourceSetOfTestCMLFiles(String... testCMLFileNames) throws IOException {
		for (String testFileName : testCMLFileNames) {
			CMLResourceContainer cmlResource = this.getResourceCopyOfTestCML(testFileName);
			resourceSet.getResource(cmlResource.getResource().getURI(), true);
		}
		return resourceSet;
	}

	/**
	 * Only use this method if model is not changed!!
	 */
	protected CMLResourceContainer getOriginalResourceOfTestCML(String testCMLName) throws IOException {
		File testFile = getTestFile(testCMLName);
		new ContextMappingDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
		return new CMLResourceContainer(resourceSet.getResource(URI.createFileURI(testFile.getAbsolutePath()), true));
	}

	protected CMLResourceContainer getOriginalResourceOfTestCML(String testCMLName, boolean setupServiceCutterDSL) throws IOException {
		if (setupServiceCutterDSL)
			new ServiceCutterConfigurationDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
		return getOriginalResourceOfTestCML(testCMLName);
	}

	protected Resource getResourceCopyOfTestSCL(String testSCLName) throws IOException {
		File testFile = new File(testDir, testSCLName);
		FileUtils.copyFile(getTestFile(testSCLName), testFile);
		new ServiceCutterConfigurationDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
		ResourceSet rs = new ResourceSetImpl();
		return rs.getResource(URI.createFileURI(testFile.getAbsolutePath()), true);
	}

	protected CMLResourceContainer reloadResource(CMLResourceContainer resource) {
		ResourceSet rs = new ResourceSetImpl();
		return new CMLResourceContainer(rs.getResource(URI.createFileURI(resource.getResource().getURI().devicePath()), true));
	}

	protected File getTestFile(String testCMLName) {
		return new File(Paths.get("").toAbsolutePath().toString(), getTestFileDirectory() + testCMLName);
	}

	protected JavaIoFileSystemAccess getFileSystemAccess() {
		JavaIoFileSystemAccess fsa = new JavaIoFileSystemAccess();
		Guice.createInjector(new AbstractGenericModule() {
			public Class<? extends IEncodingProvider> bindIEncodingProvider() {
				return IEncodingProvider.Runtime.class;
			}
		}).injectMembers(fsa);
		return fsa;
	}

	/**
	 * Override this method to define test file directory. Example:
	 * "/integ-test-files/refactorings/"
	 */
	protected abstract String getTestFileDirectory();

}

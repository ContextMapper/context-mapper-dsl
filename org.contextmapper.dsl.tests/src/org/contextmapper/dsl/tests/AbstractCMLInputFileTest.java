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
package org.contextmapper.dsl.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.contextmapper.dsl.ContextMappingDSLStandaloneSetup;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractCMLInputFileTest {
	protected File testDir;

	@BeforeEach
	public void prepare() {
		String dirName = UUID.randomUUID().toString();
		this.testDir = new File(new File(System.getProperty("java.io.tmpdir")), dirName);
		this.testDir.mkdir();
	}

	protected Resource getResourceCopyOfTestCML(String testCMLName) throws IOException {
		File testFile = new File(testDir, testCMLName);
		FileUtils.copyFile(getTestFile(testCMLName), testFile);
		new ContextMappingDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
		ResourceSet rs = new ResourceSetImpl();
		return rs.getResource(URI.createFileURI(testFile.getAbsolutePath()), true);
	}

	protected Resource reloadResource(Resource resource) {
		ResourceSet rs = new ResourceSetImpl();
		return rs.getResource(URI.createFileURI(resource.getURI().devicePath()), true);
	}

	protected File getTestFile(String testCMLName) {
		return new File(Paths.get("").toAbsolutePath().toString(), getTestFileDirectory() + testCMLName);
	}

	/**
	 * Override this method to define test file directory. Example:
	 * "/integ-test-files/refactorings/"
	 */
	protected abstract String getTestFileDirectory();

	@AfterEach
	void cleanup() throws IOException {
		Path directory = Paths.get(this.testDir.getAbsolutePath());
		Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}
}

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
package org.contextmapper.dsl.standalone;

import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.parser.IEncodingProvider;
import org.eclipse.xtext.service.AbstractGenericModule;

import com.google.inject.Guice;

/**
 * Helper class for file system access.
 * 
 * @author Stefan Kapferer
 */
public class FileSystemHelper {

	/**
	 * Get file system access with specific output path.
	 * 
	 * @param outputPath the path where you want to persist generated stuff.
	 * @return the file system access object
	 */
	public static JavaIoFileSystemAccess getFileSystemAccess(String outputPath) {
		JavaIoFileSystemAccess fsa = new JavaIoFileSystemAccess();
		Guice.createInjector(new AbstractGenericModule() {
			public Class<? extends IEncodingProvider> bindIEncodingProvider() {
				return IEncodingProvider.Runtime.class;
			}
		}).injectMembers(fsa);
		fsa.setOutputPath(outputPath);
		return fsa;
	}

	/**
	 * Get the file system access with the default output path ("./src-gen").
	 * 
	 * @return the file system access object
	 */
	public static JavaIoFileSystemAccess getFileSystemAccess() {
		return getFileSystemAccess("./src-gen");
	}
}

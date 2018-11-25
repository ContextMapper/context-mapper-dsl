/*
 * Copyright 2018 The Context Mapper Project Team
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
package org.contextmapper.dsl.generator.servicecutter.output.factory;

public class ServiceCutterOutputModelReadingException extends RuntimeException {

	private static final long serialVersionUID = -1103030709732713303L;

	public ServiceCutterOutputModelReadingException(String filePath) {
		super("Error reading ServiceCutter output file. The file '" + filePath
				+ "' does not seam to have the proper format.");
	}
}

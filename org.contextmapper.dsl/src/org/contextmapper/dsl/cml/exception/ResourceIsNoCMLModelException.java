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
package org.contextmapper.dsl.cml.exception;

import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.eclipse.emf.common.util.URI;

public class ResourceIsNoCMLModelException extends ContextMapperApplicationException {

	public ResourceIsNoCMLModelException(URI uri) {
		super("The resource '" + uri.toString() + "' does not contain a CML model.");
	}

	public ResourceIsNoCMLModelException() {
		super("The given resource does not contain a CML model.");
	}

}

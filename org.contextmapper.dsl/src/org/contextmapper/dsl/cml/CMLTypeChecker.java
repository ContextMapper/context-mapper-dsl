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
package org.contextmapper.dsl.cml;

import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Helper class to check CML type properties.
 * 
 * @author Stefan Kapferer
 *
 */
public class CMLTypeChecker {

	private static final String[] SCULPTOR_PRIMITIVE_TYPES = { "String", "int", "Integer", "long", "Long", "boolean", "Boolean", "Date", "DateTime", "Timestamp", "BigDecimal",
			"BigInteger", "double", "Double", "float", "Float", "Key", "PagingParameter", "PagedResult", "Blob", "Clob", "Object[]" };

	/**
	 * Check whether a type is one of the Sculptor primitive types.
	 */
	public boolean isPrimitiveType(String type) {
		Set<String> primitiveTypes = Sets.newHashSet(Arrays.asList(SCULPTOR_PRIMITIVE_TYPES));
		return primitiveTypes.contains(type);
	}

}

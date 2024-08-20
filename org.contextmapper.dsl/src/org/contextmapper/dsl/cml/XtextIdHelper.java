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

public class XtextIdHelper {

	public String convertStringToXtextID(String text) {
		StringBuilder result = new StringBuilder(text);

		// ID is not supposed to contain non-letters, non-digits:
		for (int i = 0; i < text.length(); i++) {
			if (!Character.isLetterOrDigit(text.charAt(i))) {
				result.setCharAt(i, '_');
			}
		}

		// ID must not start with a number/digit:
		if (Character.isDigit(result.charAt(0))) {
			result.insert(0, "s_");
		}

		return result.toString();
	}

}

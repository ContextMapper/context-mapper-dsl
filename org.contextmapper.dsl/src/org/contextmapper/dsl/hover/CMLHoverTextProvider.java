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
package org.contextmapper.dsl.hover;

/**
 * Implementations of this interface provide hover texts for CML language
 * keywords.
 * 
 * @author Stefan Kapferer
 * 
 */
public interface CMLHoverTextProvider {

	/**
	 * Returns the hover text for a specific CML keyword.
	 * 
	 * @param keyword the keyword for which the hover text shall be returned
	 * @return the hover text for the given keyword
	 */
	public String getHoverText(String keyword);

	/**
	 * Returns whether the provider can give a hover text for a specific keyword or
	 * not.
	 * 
	 * @param keyword the keyword for which a hover text is asked
	 * @return whether the keyword is registered or not
	 */
	public boolean isKeywordRegistered(String keyword);

}

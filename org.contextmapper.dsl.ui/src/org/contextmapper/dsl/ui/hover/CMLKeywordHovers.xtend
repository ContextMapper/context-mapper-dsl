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
package org.contextmapper.dsl.ui.hover

import java.util.HashMap
import java.util.Map
import org.eclipse.xtext.Keyword

class CMLKeywordHovers {

	var Map<String, String> hoverTextMap;

	new() {
		initHoverMap();
	}

	def String hoverText(Keyword k) {
		if (hoverTextMap.containsKey(k.value)) {
			return hoverTextMap.get(k.value);
		}
		return "";
	}

	def initHoverMap() {
		hoverTextMap = new HashMap;

		hoverTextMap.put("BoundedContext", '''
		<strong>Bounded Context</strong>: A description of a boundary (typically a subsystem, or the work 
		of a particular team) within which a particular model is defined and applicable.
		<br/><br/>
		Find all DDD pattern descriptions in the DDD reference under 
		<a href="http://domainlanguage.com/ddd/reference/">http://domainlanguage.com/ddd/reference/</a>''');
	}

	def boolean hasKeywordHoverText(String keyword) {
		return hoverTextMap.containsKey(keyword);
	}

}

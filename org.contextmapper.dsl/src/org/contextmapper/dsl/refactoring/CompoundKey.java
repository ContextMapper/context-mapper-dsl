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
package org.contextmapper.dsl.refactoring;

import java.util.Objects;
import java.util.Set;

/**
 * 
 * Used in refactorings to map model elements to unique sets of keys. For
 * example: Map aggregates to unique set of use cases.
 *
 */
public class CompoundKey {
	Set<String> keyElements;

	CompoundKey(Set<String> keyElements) {
		this.keyElements = keyElements;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CompoundKey))
			return false;
		CompoundKey otherKey = (CompoundKey) obj;
		return (this.keyElements.size() == otherKey.keyElements.size()) && this.keyElements.containsAll(otherKey.keyElements);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.keyElements.toArray());
	}
}
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
package org.contextmapper.dsl.generator.mdsl.model;

public class DataTypeAttribute {

	private String attributeName;
	private String type;
	private boolean isCollection = false;
	private boolean isNullable = false;

	public String getName() {
		return attributeName;
	}

	public void setName(String name) {
		this.attributeName = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setIsCollection(boolean isCollection) {
		this.isCollection = isCollection;
	}

	public boolean isCollection() {
		return isCollection;
	}

	public void setIsNullable(boolean isNullable) {
		this.isNullable = isNullable;
	}

	public boolean isNullable() {
		return isNullable;
	}
}

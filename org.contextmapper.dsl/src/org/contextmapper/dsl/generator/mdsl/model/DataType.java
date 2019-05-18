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

import java.util.List;

import com.google.common.collect.Lists;

public class DataType {

	private String name;
	private boolean isAbstractType = true;
	private boolean isPrimitiveType = false;
	private List<DataTypeAttribute> children = Lists.newArrayList();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addAttributes(List<DataTypeAttribute> children) {
		this.isAbstractType = false;
		this.children.addAll(children);
	}

	public List<DataTypeAttribute> getChildren() {
		return children;
	}

	public boolean isAbstractDataType() {
		return isAbstractType;
	}

	public boolean isPrimitiveType() {
		return isPrimitiveType;
	}

	public void setIsPrimitiveType(boolean isPrimitiveType) {
		this.isPrimitiveType = isPrimitiveType;
	}
}

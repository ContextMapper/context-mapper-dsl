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
package org.contextmapper.dsl.hover.impl;

import java.util.Map;

import org.contextmapper.dsl.hover.CMLHoverTextProvider;

import com.google.common.collect.Maps;

public abstract class AbstractCMLHoverTextProvider implements CMLHoverTextProvider {

	private Map<String, String> registry;

	public AbstractCMLHoverTextProvider() {
		this.registry = Maps.newHashMap();
		registerHoverTexts();
	}

	@Override
	public String getHoverText(String keyword) {
		if (this.registry.containsKey(keyword))
			return this.registry.get(keyword);
		return "";
	}

	@Override
	public boolean isKeywordRegistered(String keyword) {
		return this.registry.containsKey(keyword);
	}

	/**
	 * Override this method to register hover texts.
	 */
	protected abstract void registerHoverTexts();

	protected void registerHoverText(String keyword, String hoverText) {
		this.registry.put(keyword, hoverText);
	}

}

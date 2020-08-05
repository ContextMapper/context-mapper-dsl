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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.contextmapper.dsl.hover.impl.HTMLHoverTextProvider4CML;
import org.contextmapper.dsl.hover.impl.MarkdownHoverTextProvider4CML;
import org.junit.jupiter.api.Test;

public class CMLHoverTextProviderTest {

	@Test
	public void canProvideHoverTextsAsHTML() {
		// given
		CMLHoverTextProvider textProvider = new HTMLHoverTextProvider4CML();

		// when
		String sampleHoverText = textProvider.getHoverText("ContextMap");

		// then
		assertNotNull(sampleHoverText);
		assertFalse("".equals(sampleHoverText));
	}

	@Test
	public void canProvideHoverTextsAsMarkdown() {
		// given
		CMLHoverTextProvider textProvider = new MarkdownHoverTextProvider4CML();

		// when
		String sampleHoverText = textProvider.getHoverText("ContextMap");

		// then
		assertNotNull(sampleHoverText);
		assertFalse("".equals(sampleHoverText));
	}

	@Test
	public void canReturnEmptyString4NonExistentKeyword() {
		// given
		CMLHoverTextProvider textProvider = new HTMLHoverTextProvider4CML();

		// when
		String sampleHoverText = textProvider.getHoverText("ThisKeywordDoesNotExist");

		// then
		assertNotNull(sampleHoverText);
		assertTrue("".equals(sampleHoverText));
	}

	@Test
	public void canTellIfKeywordExists() {
		// given
		CMLHoverTextProvider textProvider = new HTMLHoverTextProvider4CML();

		// when
		boolean existing = textProvider.isKeywordRegistered("ContextMap");
		boolean notExisting = textProvider.isKeywordRegistered("ThisKeywordDoesNotExist");

		// then
		assertTrue(existing);
		assertFalse(notExisting);
	}

}

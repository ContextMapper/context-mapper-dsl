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
package org.contextmapper.dsl.generators.freemarker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.contextmapper.dsl.generator.AbstractFreemarkerTextCreator;
import org.junit.jupiter.api.Test;

public class AbstractFreemarkerTextCreatorTest {

	@Test
	public void canGenerteSimpleTextWithFreemarkerTemplate() {
		// given
		String name = "Tester";

		// when
		String text = new SimpleTextCreator().createText(name);

		// then
		assertEquals("Hello Tester!", text);
	}

	@Test
	public void cannotGenerateTextIfClassIsWrong() {
		assertThrows(RuntimeException.class, () -> {
			new WrongClassTextCreator().createText("Tester");
		});
	}

	@Test
	public void cannotGenerateTextIfFTLTemplateHasErrors() {
		assertThrows(RuntimeException.class, () -> {
			new TemplateWithErrorCreator().createText("Tester");
		});
	}

	private class SimpleTextCreator extends AbstractFreemarkerTextCreator<String> {

		@Override
		protected void preprocessing(String name) {
			// do nothing here
		}

		@Override
		protected void registerModelObjects(Map root, String name) {
			root.put("name", name);
		}

		@Override
		protected String getTemplateName() {
			return "hello.ftl";
		}

		@Override
		protected Class getTemplateClass() {
			return AbstractFreemarkerTextCreatorTest.class;
		}

	}

	private class WrongClassTextCreator extends AbstractFreemarkerTextCreator<String> {

		@Override
		protected void preprocessing(String name) {
			// do nothing here
		}

		@Override
		protected void registerModelObjects(Map root, String name) {
			root.put("name", name);
		}

		@Override
		protected String getTemplateName() {
			return "hello.ftl";
		}

		@Override
		protected Class getTemplateClass() {
			return AbstractFreemarkerTextCreator.class; // wrong class to load template
		}

	}
	
	private class TemplateWithErrorCreator extends AbstractFreemarkerTextCreator<String> {

		@Override
		protected void preprocessing(String name) {
			// do nothing here
		}

		@Override
		protected void registerModelObjects(Map root, String name) {
			root.put("name", name);
		}

		@Override
		protected String getTemplateName() {
			return "hello_error.ftl";
		}

		@Override
		protected Class getTemplateClass() {
			return AbstractFreemarkerTextCreatorTest.class;
		}

	}

}

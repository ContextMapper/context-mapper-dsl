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
package org.contextmapper.dsl.ui.handler.wizard.pages.components;

import org.eclipse.swt.widgets.Composite;

/**
 * Component to choose a Freemarker template file.
 * 
 * @author Stefan Kapferer
 */
public class FreemarkerFileChooser extends FileByExtensionChooser {

	public FreemarkerFileChooser(Composite parent) {
		super(parent, "ftl");
	}

	@Override
	protected String getDialogTitle() {
		return "Choose Freemarker (*.ftl) file";
	}

	@Override
	protected String getDialogMessage() {
		return "Select the *.ftl file you want to use for generation:";
	}
}

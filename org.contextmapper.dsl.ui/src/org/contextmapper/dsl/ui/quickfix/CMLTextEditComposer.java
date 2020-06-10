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
package org.contextmapper.dsl.ui.quickfix;

import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.ui.editor.model.edit.DefaultTextEditComposer;

/**
 * Ensures that CML is formatted after applying quick fixes.
 * 
 * @author Stefan Kapferer
 *
 */
public class CMLTextEditComposer extends DefaultTextEditComposer {

	@Override
	protected SaveOptions getSaveOptions() {
		return SaveOptions.newBuilder().format().getOptions();
	}

}

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
package org.contextmapper.servicecutter.dsl.ui.editor;

import java.util.Arrays;
import java.util.Set;

import org.contextmapper.servicecutter.dsl.ui.actions.GeneratorsActionGroup;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.xtext.ui.editor.XtextEditor;

import com.google.common.collect.Sets;

public class SCLEditor extends XtextEditor {

	private ActionGroup generatorsGroup;

	private static final String SCL_EDITOR_KEYBINDING_SCOPE = "org.contextmapper.ui.scl.editor.SCLEditorScope";
	
	@Override
	protected void createActions() {
		super.createActions();

		generatorsGroup = new GeneratorsActionGroup(this);
	}

	@Override
	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);

		ActionContext context = new ActionContext(getSelectionProvider().getSelection());
		generatorsGroup.setContext(context);
		generatorsGroup.fillContextMenu(menu);
		generatorsGroup.setContext(null);
	}
	
	@Override
	protected void setKeyBindingScopes(String[] scopes) {
		Set<String> scopesSet = Sets.newHashSet();
		if (scopes != null)
			scopesSet.addAll(Arrays.asList(scopes));
		if (!scopesSet.contains(SCL_EDITOR_KEYBINDING_SCOPE))
			scopesSet.add(SCL_EDITOR_KEYBINDING_SCOPE);
		super.setKeyBindingScopes(scopesSet.toArray(new String[scopesSet.size()]));
	}

}

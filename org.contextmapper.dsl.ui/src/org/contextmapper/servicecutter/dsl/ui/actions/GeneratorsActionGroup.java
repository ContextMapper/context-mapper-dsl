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
package org.contextmapper.servicecutter.dsl.ui.actions;

import java.util.List;
import java.util.Optional;

import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.ui.editor.CMLQuickMenuCreator;
import org.contextmapper.dsl.ui.images.CMLImageDescriptionFactory;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.xtext.ui.editor.XtextEditor;

import com.google.common.collect.Lists;

public class GeneratorsActionGroup extends ActionGroup {

	private static final String QUICK_MENU_ID = "org.contextmapper.servicecutter.dsl.ui.edit.scl.generator.quickMenu";

	private IHandlerService handlerService;
	private ICommandService commandService;
	private List<Command> allCommands;

	public GeneratorsActionGroup(XtextEditor editor) {
		this.handlerService = editor.getEditorSite().getService(IHandlerService.class);
		this.commandService = editor.getEditorSite().getService(ICommandService.class);
		allCommands = Lists.newArrayList(commandService.getDefinedCommands());
		installQuickAccessAction();
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		addGeneratorSubmenu(menu);
	}

	private void installQuickAccessAction() {
		if (handlerService != null) {
			IHandler handler = new CMLQuickMenuCreator() {
				@Override
				protected void fillMenu(IMenuManager menu) {
					fillRefactorMenu(menu);
				}
			}.createHandler();
			handlerService.activateHandler(QUICK_MENU_ID, handler);
		}
	}

	private int fillRefactorMenu(IMenuManager generatorSubmenu) {
		int added = 0;
		added += addAction(generatorSubmenu, createAction("org.contextmapper.servicecutter.dsl.ui.handler.ServiceCutterUserRepresentationsJSONGenerationCommand"));
		return added;
	}

	private Action createAction(String commandId) {
		Optional<Command> optCommand = allCommands.stream().filter(c -> c.getId().equals(commandId)).findFirst();
		if (!optCommand.isPresent())
			return null;

		Command command = optCommand.get();
		Action action = new Action() {
			public void run() {
				try {
					handlerService.executeCommand(command.getId(), null);
				} catch (Exception e) {
					throw new ContextMapperApplicationException("Could not execute command with id '" + command.getId() + "'.", e);
				}
			};
		};
		try {
			action.setActionDefinitionId(command.getId());
			action.setText(command.getName());
			action.setEnabled(command.isEnabled());
		} catch (NotDefinedException e) {
			throw new ContextMapperApplicationException("The command with the id '" + command.getId() + "' is not properly defined!", e);
		}
		return action;
	}

	private void addGeneratorSubmenu(IMenuManager menu) {
		MenuManager contextMenu = new MenuManager("Context Mapper", "org.contextmapper.servicecutter.dsl.ui.generator.menu");
		contextMenu.setActionDefinitionId(QUICK_MENU_ID);
		contextMenu.setImageDescriptor(CMLImageDescriptionFactory.createContextMapperLogoDescriptor());
		if (fillRefactorMenu(contextMenu) > 0)
			menu.insertAfter("additions", contextMenu);
	}

	private int addAction(IMenuManager menu, Action action) {
		if (action != null && action.isEnabled()) {
			menu.add(action);
			return 1;
		}
		return 0;
	}

}

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
package org.contextmapper.dsl.ui.actions;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.ui.editor.CMLQuickMenuCreator;
import org.contextmapper.dsl.ui.images.CMLImageDescriptionFactory;
import org.contextmapper.dsl.ui.internal.DslActivator;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.osgi.framework.Bundle;

import com.google.common.collect.Lists;

public class RefactoringActionGroup extends ActionGroup {

	private static final String QUICK_MENU_ID = "org.contextmapper.ui.edit.cml.refactor.quickMenu";

	private IHandlerService handlerService;
	private ICommandService commandService;

	public RefactoringActionGroup(XtextEditor editor) {
		this.handlerService = editor.getEditorSite().getService(IHandlerService.class);
		this.commandService = editor.getEditorSite().getService(ICommandService.class);
		installQuickAccessAction();
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		addRefactorSubmenu(menu);
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

	private int fillRefactorMenu(IMenuManager refactorSubmenu) {
		int added = 0;
		List<Command> allCommands = Lists.newArrayList(commandService.getDefinedCommands());
		for (Command command : allCommands.stream().filter(c -> c.getId().startsWith("org.contextmapper") && c.getId().endsWith("RefactoringCommand"))
				.collect(Collectors.toList())) {
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

				added += addAction(refactorSubmenu, action);
			} catch (NotDefinedException e) {
				throw new ContextMapperApplicationException("The command with the id '" + command.getId() + "' is not properly defined!", e);
			}
		}
		return added;
	}

	private void addRefactorSubmenu(IMenuManager menu) {
		MenuManager contextMenu = new MenuManager("Context Mapper: Refactor", "org.contextmapper.dsl.ui.refactoring.menu");
		contextMenu.setActionDefinitionId(QUICK_MENU_ID);
		contextMenu.setImageDescriptor(CMLImageDescriptionFactory.createContextMapperLogoDescriptor());
		if (fillRefactorMenu(contextMenu) > 0)
			menu.insertAfter("group.edit", contextMenu);
	}

	private int addAction(IMenuManager menu, Action action) {
		if (action != null && action.isEnabled()) {
			menu.add(action);
			return 1;
		}
		return 0;
	}

}

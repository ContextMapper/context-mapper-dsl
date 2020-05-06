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
package org.contextmapper.dsl.ide.commands

import com.google.common.collect.Lists
import com.google.gson.JsonPrimitive
import com.google.inject.Inject
import org.apache.log4j.Logger
import org.contextmapper.dsl.cml.CMLResourceContainer
import org.eclipse.lsp4j.ExecuteCommandParams
import org.eclipse.xtext.ide.server.ILanguageServerAccess
import org.eclipse.xtext.ide.server.commands.IExecutableCommandService
import org.eclipse.xtext.util.CancelIndicator

class CMLCommandService implements IExecutableCommandService {

	static final Logger LOG = Logger.getLogger(CMLCommandService);

	@Inject CMLCommandRegistry commandRegistry

	override initialize() {
		return Lists.newLinkedList(commandRegistry.commandIds);
	}

	override execute(ExecuteCommandParams params, ILanguageServerAccess access, CancelIndicator cancelIndicator) {
		val command = commandRegistry.getCommand(params.command);

		if (command === null)
			return "Unknown command ID!"

		val uri = params.arguments.head as JsonPrimitive
		if (uri !== null) {
			return access.doRead(uri.asString) [
				LOG.info(
					"CML LSP command has been called: " + params.getCommand() + " (" + params.getArguments().get(0) +
						")");
				command.executeCommand(new CMLResourceContainer(resource), params);
				return "Command '" + params.command + "' executed."
			].get
		}
	}
}

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
package org.contextmapper.dsl.ide.actions

import com.google.inject.Inject
import java.util.stream.Collectors
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.xtext.ide.server.codeActions.ICodeActionService2
import com.google.common.collect.Lists
import org.contextmapper.dsl.cml.CMLResource

class CMLActionService implements ICodeActionService2 {

	@Inject SelectionContextResolver selectionResolver
	@Inject CMLActionRegistry actionRegistry

	override getCodeActions(Options options) {
		val params = options.codeActionParams;
		val currentSelectionRange = params.range;
		val startPosition = currentSelectionRange.start;
		val endPosition = currentSelectionRange.end;

		val resource = new CMLResource(options.resource);
		val selectedObjects = selectionResolver.resolveAllSelectedEObjects(resource,
			options.document.getOffSet(startPosition), options.document.getOffSet(endPosition));

		val allActions = Lists.newLinkedList

		// general actions that can be applied (such as refactorings)
		allActions.addAll(actionRegistry.getApplicableActionCommands(resource, selectedObjects).map [
			Either.forLeft(it)
		]);

		// quick fix commands (actions bound to validation message)
		if (!params.context.diagnostics.isEmpty) {
			for (d : params.context.diagnostics) {
				allActions.addAll(actionRegistry.getApplicableQuickfixes(d, options).stream.map [
					Either.forRight(it)
				].collect(Collectors.toList));
			}
		}
		return allActions;
	}

}

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
package org.contextmapper.dsl.ide.actions;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.ide.actions.impl.SplitBoundedContextByOwnerAction;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.lsp4j.Command;

import com.google.common.collect.Sets;

/**
 * Class to register all code actions (refactoring and transformation commands)
 * and to filter commands that can be applied for a given set of selected
 * EObjects.
 * 
 * @author Stefan Kapferer
 *
 */
public class CMLActionRegistry {

	private Set<CMLCodeAction> getAllActions(CMLResourceContainer resource, List<EObject> selectedObjects) {
		Set<CMLCodeAction> codeActions = Sets.newHashSet();

		// add new AR here:
		codeActions.add(new SplitBoundedContextByOwnerAction(resource, selectedObjects));

		return Sets.newHashSet(codeActions);
	}

	public List<? extends Command> getApplicableActionCommands(CMLResourceContainer resource, List<EObject> selectedObjects) {
		List<? extends Command> result = getAllActions(resource, selectedObjects).stream().filter(a -> a.isApplicable()).map(a -> a.getCommand())
				.collect(Collectors.toList());
		return result;
	}

}

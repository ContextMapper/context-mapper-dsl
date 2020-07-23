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
package org.contextmapper.dsl.ide.commands;

import java.util.Map;
import java.util.Set;

import org.contextmapper.dsl.ide.commands.impl.generation.ContextMapGenerationCommand;
import org.contextmapper.dsl.ide.commands.impl.generation.GenericTextFileGenerationCommand;
import org.contextmapper.dsl.ide.commands.impl.generation.MDSLGenerationCommand;
import org.contextmapper.dsl.ide.commands.impl.generation.NewServiceCutGenerationCommand;
import org.contextmapper.dsl.ide.commands.impl.generation.PlantUMLGenerationCommand;
import org.contextmapper.dsl.ide.commands.impl.generation.ServiceCutterInputGenerationCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.DeriveBoundedContextFromSubdomainsCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.DeriveSubdomainFromUserRequirementsCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.SplitAggregateByEntitiesRefactoringCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.SplitBoundedContextByFeaturesRefactoringCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.SplitBoundedContextByOwnerRefactoringCommand;
import org.contextmapper.dsl.ide.edit.WorkspaceEditRecorder;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Registers all commands that can be called on LSP command service.
 * 
 * @author Stefan Kapferer
 *
 */
public class CMLCommandRegistry {

	private Map<String, CMLResourceCommand> commandMap = Maps.newHashMap();
	private WorkspaceEditRecorder editRecorder;

	@Inject
	public CMLCommandRegistry(WorkspaceEditRecorder editRecorder) {
		this.editRecorder = editRecorder;
		registerCommands();
	}

	private void registerCommands() {
		commandMap.put("cml.generate.contextmap", new ContextMapGenerationCommand());
		commandMap.put("cml.generate.puml", new PlantUMLGenerationCommand());
		commandMap.put("cml.generate.mdsl", new MDSLGenerationCommand());
		commandMap.put("cml.generate.generic.text.file", new GenericTextFileGenerationCommand());
		commandMap.put("cml.generate.new.service.cut", new NewServiceCutGenerationCommand());
		commandMap.put("cml.generate.servicecutter.input", new ServiceCutterInputGenerationCommand());
		commandMap.put("cml.ar.splitBCByOwner", new SplitBoundedContextByOwnerRefactoringCommand(editRecorder));
		commandMap.put("cml.ar.splitBCByFeature", new SplitBoundedContextByFeaturesRefactoringCommand(editRecorder));
		commandMap.put("cml.ar.splitAggregateByEntities", new SplitAggregateByEntitiesRefactoringCommand(editRecorder));
		commandMap.put("cml.ar.deriveSubdomainFromURs", new DeriveSubdomainFromUserRequirementsCommand(editRecorder));
		commandMap.put("cml.ar.deriveBoundedContextFromSDs", new DeriveBoundedContextFromSubdomainsCommand(editRecorder));
	}

	public CMLResourceCommand getCommand(String commandId) {
		return this.commandMap.get(commandId);
	}

	public Set<String> getCommandIds() {
		return Sets.newHashSet(this.commandMap.keySet());
	}

}

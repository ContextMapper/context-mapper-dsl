/*
 * Copyright 2020-2024 The Context Mapper Project Team
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
import org.contextmapper.dsl.ide.commands.impl.generation.PlantUMLGenerationCommand;
import org.contextmapper.dsl.ide.commands.impl.generation.SketchMinerGenerationCommand;
import org.contextmapper.dsl.ide.commands.impl.quickfix.SplitStoryByVerbCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.AddEthicalValueAssessmentCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.CreateStakeholderForUserStoryRoleCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.CreateValue4StakeholderCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.CreateValueRegisterForBoundedContextCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.DeriveBoundedContextFromSubdomainsCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.DeriveFrontendAndBackendFromFeatureBCCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.DeriveSubdomainFromUserRequirementsCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.ExtractAggregatesByCohesionCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.ExtractAggregatesByVolatilityCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.ExtractSharedKernelCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.MergeAggregatesCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.MergeBoundedContextsCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.MoveStakeholderToGroupCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.SplitAggregateByEntitiesRefactoringCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.SplitBoundedContextByFeaturesRefactoringCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.SplitBoundedContextByOwnerRefactoringCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.SplitSystemContextIntoSubsystemsCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.SuspendPartnershipCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.SwitchFromPartnershipToSharedKernelCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.SwitchFromSharedKernelToPartnershipCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.WrapValueInClusterCommand;
import org.contextmapper.dsl.ide.edit.WorkspaceEditRecorder;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Registers all commands that can be called on LSP command service (for CML
 * language).
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
		commandMap.put("cml.generate.sketchminer", new SketchMinerGenerationCommand());
		commandMap.put("cml.generate.mdsl", new MDSLGenerationCommand());
		commandMap.put("cml.generate.generic.text.file", new GenericTextFileGenerationCommand());
		commandMap.put("cml.ar.splitBCByOwner", new SplitBoundedContextByOwnerRefactoringCommand(editRecorder));
		commandMap.put("cml.ar.splitBCByFeature", new SplitBoundedContextByFeaturesRefactoringCommand(editRecorder));
		commandMap.put("cml.ar.splitAggregateByEntities", new SplitAggregateByEntitiesRefactoringCommand(editRecorder));
		commandMap.put("cml.ar.deriveSubdomainFromURs", new DeriveSubdomainFromUserRequirementsCommand(editRecorder));
		commandMap.put("cml.ar.deriveBoundedContextFromSDs", new DeriveBoundedContextFromSubdomainsCommand(editRecorder));
		commandMap.put("cml.ar.deriveFrontendBackendSystemsFromFeatureBC", new DeriveFrontendAndBackendFromFeatureBCCommand(editRecorder));
		commandMap.put("cml.ar.splitSystemContextIntoSubsystems", new SplitSystemContextIntoSubsystemsCommand(editRecorder));
		commandMap.put("cml.ar.extractAggregatesByVolatility", new ExtractAggregatesByVolatilityCommand(editRecorder));
		commandMap.put("cml.ar.extractAggregatesByCohesion", new ExtractAggregatesByCohesionCommand(editRecorder));
		commandMap.put("cml.ar.mergeAggregates", new MergeAggregatesCommand(editRecorder));
		commandMap.put("cml.ar.mergeBoundedContexts", new MergeBoundedContextsCommand(editRecorder));
		commandMap.put("cml.ar.extractSharedKernel", new ExtractSharedKernelCommand(editRecorder));
		commandMap.put("cml.ar.suspendPartnership", new SuspendPartnershipCommand(editRecorder));
		commandMap.put("cml.ar.switchPartnershipToSharedKernel", new SwitchFromPartnershipToSharedKernelCommand(editRecorder));
		commandMap.put("cml.ar.switchSharedKernelToPartnership", new SwitchFromSharedKernelToPartnershipCommand(editRecorder));
		commandMap.put("cml.quickfix.command.splitStoryByVerb", new SplitStoryByVerbCommand(editRecorder));
		commandMap.put("cml.ar.moveStakeholderToGroup", new MoveStakeholderToGroupCommand(editRecorder));
		commandMap.put("cml.ar.createValueForStakeholder", new CreateValue4StakeholderCommand(editRecorder));
		commandMap.put("cml.ar.addEthicalValueAssessment", new AddEthicalValueAssessmentCommand(editRecorder));
		commandMap.put("cml.ar.wrapValueInCluster", new WrapValueInClusterCommand(editRecorder));
		commandMap.put("cml.ar.createStakeholderForUserStoryRole", new CreateStakeholderForUserStoryRoleCommand(editRecorder));
		commandMap.put("cml.ar.createValueRegisterForBoundedContext", new CreateValueRegisterForBoundedContextCommand(editRecorder));
	}

	public CMLResourceCommand getCommand(String commandId) {
		return this.commandMap.get(commandId);
	}

	public Set<String> getCommandIds() {
		return Sets.newHashSet(this.commandMap.keySet());
	}

}

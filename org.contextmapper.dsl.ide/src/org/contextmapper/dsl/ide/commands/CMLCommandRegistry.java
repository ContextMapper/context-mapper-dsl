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

import org.contextmapper.dsl.ide.commands.impl.generation.GenericTextFileGenerationCommand;
import org.contextmapper.dsl.ide.commands.impl.generation.MDSLGenerationCommand;
import org.contextmapper.dsl.ide.commands.impl.generation.PlantUMLGenerationCommand;
import org.contextmapper.dsl.ide.commands.impl.refactoring.SplitBoundedContextByOwnerRefactoringCommand;
import org.eclipse.xtext.ide.serializer.IChangeSerializer;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Registers all commands that can be called on LSP command service.
 * 
 * @author Stefan Kapferer
 *
 */
public class CMLCommandRegistry {

	private Map<String, CMLResourceCommand> commandMap = Maps.newHashMap();
	@SuppressWarnings("restriction")
	private Provider<IChangeSerializer> serializerProvider;

	@SuppressWarnings("restriction")
	@Inject
	public CMLCommandRegistry(Provider<IChangeSerializer> serializerProvider) {
		this.serializerProvider = serializerProvider;
		registerCommands();
	}

	private void registerCommands() {
		commandMap.put("cml.generate.puml", new PlantUMLGenerationCommand());
		commandMap.put("cml.generate.mdsl", new MDSLGenerationCommand());
		commandMap.put("cml.generate.generic.text.file", new GenericTextFileGenerationCommand());
		commandMap.put("cml.ar.splitBCByOwner", new SplitBoundedContextByOwnerRefactoringCommand(serializerProvider));
	}

	public CMLResourceCommand getCommand(String commandId) {
		return this.commandMap.get(commandId);
	}

	public Set<String> getCommandIds() {
		return Sets.newHashSet(this.commandMap.keySet());
	}

}

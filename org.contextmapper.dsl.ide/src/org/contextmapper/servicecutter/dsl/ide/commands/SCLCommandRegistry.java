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
package org.contextmapper.servicecutter.dsl.ide.commands;

import java.util.Map;
import java.util.Set;

import org.contextmapper.servicecutter.dsl.ide.commands.impl.generation.ServiceCutterUserRepresentationsJSONGenerationCommand;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Registers all commands that can be called on LSP command service (for SCL
 * language).
 * 
 * @author Stefan Kapferer
 *
 */
public class SCLCommandRegistry {

	private Map<String, SCLResourceCommand> commandMap = Maps.newHashMap();

	public SCLCommandRegistry() {
		registerCommands();
	}

	private void registerCommands() {
		commandMap.put("scl.generate.user.representations.json.file", new ServiceCutterUserRepresentationsJSONGenerationCommand());
	}

	public SCLResourceCommand getCommand(String commandId) {
		return this.commandMap.get(commandId);
	}

	public Set<String> getCommandIds() {
		return Sets.newHashSet(this.commandMap.keySet());
	}

}

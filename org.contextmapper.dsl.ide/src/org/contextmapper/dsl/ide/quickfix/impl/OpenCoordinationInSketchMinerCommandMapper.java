/*
 * Copyright 2023 The Context Mapper Project Team
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
package org.contextmapper.dsl.ide.quickfix.impl;

import java.util.Arrays;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.Coordination;
import org.contextmapper.dsl.generator.sketchminer.SketchMinerLinkCreator;
import org.contextmapper.dsl.ide.quickfix.QuickfixCommandMapper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Command;

import com.google.common.collect.Lists;

public class OpenCoordinationInSketchMinerCommandMapper implements QuickfixCommandMapper {

	@Override
	public CodeAction getCodeAction(CMLResource cmlResource, EObject selectedObject) {
		Coordination coordination = (Coordination) selectedObject;
		CodeAction action = new CodeAction("Open coordination in BPMN Sketch Miner");
		action.setKind(CodeActionKind.QuickFix);
		Command command = new Command("Open coordination in BPMN Sketch Miner", "cml.coordination.open.sketch.miner");
		command.setArguments(Lists.newLinkedList(Arrays.asList(new String[] { new SketchMinerLinkCreator().createSketchMinerLink(coordination) })));
		action.setCommand(command);
		return action;
	}

}

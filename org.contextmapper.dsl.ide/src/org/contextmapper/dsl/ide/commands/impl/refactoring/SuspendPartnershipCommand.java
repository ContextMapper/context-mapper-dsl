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
package org.contextmapper.dsl.ide.commands.impl.refactoring;

import org.contextmapper.dsl.ide.edit.WorkspaceEditRecorder;
import org.contextmapper.dsl.refactoring.ChangePartnershipToUpstreamDownstreamRefactoring;
import org.contextmapper.dsl.refactoring.ExtractPartnershipRefactoring;
import org.contextmapper.dsl.refactoring.SemanticCMLRefactoring;
import org.contextmapper.dsl.refactoring.SuspendPartnershipMode;
import org.eclipse.lsp4j.ExecuteCommandParams;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

public class SuspendPartnershipCommand extends AbstractRefactoringCommand {

	public SuspendPartnershipCommand(WorkspaceEditRecorder editRecorder) {
		super(editRecorder);
	}

	@Override
	protected SemanticCMLRefactoring getRefactoring(ExecuteCommandParams params) {
		JsonArray refactoringParams = (JsonArray) params.getArguments().get(1);
		
		JsonPrimitive participant1 = (JsonPrimitive) refactoringParams.get(0);
		JsonPrimitive participant2 = (JsonPrimitive) refactoringParams.get(1);
		JsonPrimitive modeAsString = (JsonPrimitive) refactoringParams.get(2);

		SuspendPartnershipMode mode = SuspendPartnershipMode.valueOf(modeAsString.getAsString());
		if (mode == SuspendPartnershipMode.EXTRACT_NEW_BOUNDED_CONTEXT) {
			return new ExtractPartnershipRefactoring(participant1.getAsString(), participant2.getAsString());
		
			
		// Unfortunately MERGE mode does currently not work because of this Xtext bug: 
		// https://github.com/eclipse/xtext-core/issues/1494	
			
		//} else if (mode == SuspendPartnershipMode.MERGE_BOUNDED_CONTEXTS) {
		//	return new MergeBoundedContextsRefactoring(participant1.getAsString(), participant2.getAsString());
		} else {
			String upstream = participant1.getAsString();
			String downstream = participant2.getAsString();

			JsonPrimitive upstreamContext = (JsonPrimitive) refactoringParams.get(3);
			if (upstreamContext.getAsString().equals(downstream)) {
				upstream = participant2.getAsString();
				downstream = participant1.getAsString();
			}
			return new ChangePartnershipToUpstreamDownstreamRefactoring(upstream, downstream);
		}
	}

}

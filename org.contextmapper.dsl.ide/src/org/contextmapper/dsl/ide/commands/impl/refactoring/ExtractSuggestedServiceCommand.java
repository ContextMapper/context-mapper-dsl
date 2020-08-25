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

import java.util.Optional;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.ide.edit.WorkspaceEditRecorder;
import org.contextmapper.dsl.refactoring.ExtractSuggestedService;
import org.contextmapper.dsl.refactoring.SemanticCMLRefactoring;
import org.eclipse.emf.common.util.URI;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.xtext.ide.server.Document;
import org.eclipse.xtext.ide.server.ILanguageServerAccess;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

public class ExtractSuggestedServiceCommand extends AbstractRefactoringCommand {

	private CMLResource cmlResource;

	public ExtractSuggestedServiceCommand(WorkspaceEditRecorder editRecorder) {
		super(editRecorder);
	}

	@Override
	public void executeCommand(CMLResource cmlResource, Document document, ILanguageServerAccess access, ExecuteCommandParams params) {
		this.cmlResource = cmlResource;
		super.executeCommand(cmlResource, document, access, params);
	}

	@Override
	protected SemanticCMLRefactoring getRefactoring(ExecuteCommandParams params) {
		JsonArray refactoringParams = (JsonArray) params.getArguments().get(1);

		JsonPrimitive serviceCutResourceURI = (JsonPrimitive) refactoringParams.get(0);
		JsonPrimitive serviceToExtractName = (JsonPrimitive) refactoringParams.get(1);
		JsonPrimitive newBoundedContextName = (JsonPrimitive) refactoringParams.get(2);

		URI serviceCutURI = URI.createURI(serviceCutResourceURI.getAsString());
		CMLResource serviceCutResource = new CMLResource(cmlResource.getResourceSet().getResource(serviceCutURI, true));
		Optional<BoundedContext> serviceToExtract = serviceCutResource.getContextMappingModel().getBoundedContexts().stream().filter(bc -> bc.getName().equals(serviceToExtractName.getAsString()))
				.findFirst();
		if (!serviceToExtract.isPresent())
			throw new ContextMapperApplicationException(
					"A service with the name '" + serviceToExtractName.getAsString() + "' does not exist in the CML model '" + serviceCutResourceURI.getAsString() + "'.");
		return new ExtractSuggestedService(serviceToExtract.get(), newBoundedContextName.getAsString());
	}

}

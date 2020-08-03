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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.ide.actions.impl.DeriveBoundedContextFromSubdomainsAction;
import org.contextmapper.dsl.ide.actions.impl.DeriveFrontendAndBackendFromFeatureBCAction;
import org.contextmapper.dsl.ide.actions.impl.DeriveSubdomainFromUserRequirementsAction;
import org.contextmapper.dsl.ide.actions.impl.SplitAggregateByEntitiesAction;
import org.contextmapper.dsl.ide.actions.impl.SplitBoundedContextByFeaturesAction;
import org.contextmapper.dsl.ide.actions.impl.SplitBoundedContextByOwnerAction;
import org.contextmapper.dsl.ide.actions.impl.SplitSystemIntoSubsystemsAction;
import org.contextmapper.dsl.ide.edit.WorkspaceEditRecorder;
import org.contextmapper.dsl.quickfixes.CMLQuickFix;
import org.contextmapper.dsl.quickfixes.tactic.ExtractIDValueObjectQuickFix;
import org.contextmapper.dsl.validation.DomainObjectValidator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.xtext.ide.server.codeActions.ICodeActionService2;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Class to register all code actions (refactoring and transformation commands)
 * and to filter commands that can be applied for a given set of selected
 * EObjects.
 * 
 * @author Stefan Kapferer
 *
 */
public class CMLActionRegistry {

	@Inject
	private WorkspaceEditRecorder editRecorder;
	@Inject
	private SelectionContextResolver selectionResolver;
	private Map<String, List<CMLQuickFix<? extends EObject>>> quickFixRegistry;

	public CMLActionRegistry() {
		this.quickFixRegistry = Maps.newHashMap();
		this.registerAllQuickFixes();
	}

	private Set<CMLCodeAction> getAllActions(CMLResourceContainer resource, List<EObject> selectedObjects) {
		Set<CMLCodeAction> codeActions = Sets.newHashSet();

		// add new AR here:
		codeActions.add(new SplitBoundedContextByOwnerAction(resource, selectedObjects));
		codeActions.add(new SplitBoundedContextByFeaturesAction(resource, selectedObjects));
		codeActions.add(new SplitAggregateByEntitiesAction(resource, selectedObjects));
		codeActions.add(new DeriveSubdomainFromUserRequirementsAction(resource, selectedObjects));
		codeActions.add(new DeriveBoundedContextFromSubdomainsAction(resource, selectedObjects));
		codeActions.add(new DeriveFrontendAndBackendFromFeatureBCAction(resource, selectedObjects));
		codeActions.add(new SplitSystemIntoSubsystemsAction(resource, selectedObjects));

		return Sets.newHashSet(codeActions);
	}

	private void registerAllQuickFixes() {
		// register quick fixes here:
		registerQuickFix(DomainObjectValidator.ID_IS_PRIMITIVE_CODE, new ExtractIDValueObjectQuickFix());
	}

	public List<? extends Command> getApplicableActionCommands(CMLResourceContainer resource, List<EObject> selectedObjects) {
		List<? extends Command> result = getAllActions(resource, selectedObjects).stream().filter(a -> a.isApplicable()).map(a -> a.getCommand()).collect(Collectors.toList());
		return result;
	}

	public List<? extends CodeAction> getApplicableQuickfixes(Diagnostic diagnostic, ICodeActionService2.Options options) {
		List<CodeAction> quickFixCodeActions = Lists.newLinkedList();

		if (diagnostic.getCode() == null || diagnostic.getCode().get() == null || !(diagnostic.getCode().get() instanceof String))
			return quickFixCodeActions;

		String key = (String) diagnostic.getCode().get();
		if (!quickFixRegistry.containsKey(key) || quickFixRegistry.get(key).isEmpty())
			return quickFixCodeActions;

		quickFixCodeActions.addAll(createQuickFixCodeActions4ValidationMessage(key, diagnostic, options));
		return quickFixCodeActions;
	}

	private List<CodeAction> createQuickFixCodeActions4ValidationMessage(String validationId, Diagnostic diagnostic, ICodeActionService2.Options options) {
		List<CodeAction> codeActions = Lists.newLinkedList();
		for (CMLQuickFix<? extends EObject> quickFix : quickFixRegistry.get(validationId)) {
			codeActions.add(createQuickFixCodeAction(quickFix, diagnostic, options));
		}
		return codeActions;
	}

	private CodeAction createQuickFixCodeAction(CMLQuickFix<? extends EObject> quickFix, Diagnostic diagnostic, ICodeActionService2.Options options) {
		CodeAction action = new CodeAction(quickFix.getName());
		action.setDiagnostics(Arrays.asList(new Diagnostic[] { diagnostic }));
		action.setKind(CodeActionKind.QuickFix);
		action.setEdit(editRecorder.recordWorkspaceEdit(options.getLanguageServerAccess(), options.getResource().getURI(), options.getDocument(), (Resource resource) -> {
			CMLResourceContainer cmlResource = new CMLResourceContainer(resource);
			List<EObject> objects = selectionResolver.resolveAllSelectedEObjects(cmlResource, options.getDocument().getOffSet(diagnostic.getRange().getStart()),
					options.getDocument().getOffSet(diagnostic.getRange().getEnd()));
			if (objects.isEmpty())
				throw new ContextMapperApplicationException("Selected object for quick fix could not be found.");

			// we can assume that there is only one object per validation message
			quickFix.applyQuickfix2EObject(EcoreUtil.resolve(objects.get(0), resource));
		}));
		return action;
	}

	private void registerQuickFix(String validationId, CMLQuickFix<? extends EObject> quickFix) {
		if (!quickFixRegistry.containsKey(validationId))
			quickFixRegistry.put(validationId, Lists.newLinkedList());
		quickFixRegistry.get(validationId).add(quickFix);
	}

}

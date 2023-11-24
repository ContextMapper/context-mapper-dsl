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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.ide.actions.impl.DeriveBoundedContextFromSubdomainsAction;
import org.contextmapper.dsl.ide.actions.impl.DeriveFrontendAndBackendFromFeatureBCAction;
import org.contextmapper.dsl.ide.actions.impl.DeriveSubdomainFromUserRequirementsAction;
import org.contextmapper.dsl.ide.actions.impl.ExtractAggregatesByCohesionAction;
import org.contextmapper.dsl.ide.actions.impl.ExtractAggregatesByVolatilityAction;
import org.contextmapper.dsl.ide.actions.impl.ExtractSharedKernelAction;
import org.contextmapper.dsl.ide.actions.impl.ExtractSuggestedServiceAction;
import org.contextmapper.dsl.ide.actions.impl.MergeAggregatesAction;
import org.contextmapper.dsl.ide.actions.impl.MergeBoundedContextsAction;
import org.contextmapper.dsl.ide.actions.impl.SplitAggregateByEntitiesAction;
import org.contextmapper.dsl.ide.actions.impl.SplitBoundedContextByFeaturesAction;
import org.contextmapper.dsl.ide.actions.impl.SplitBoundedContextByOwnerAction;
import org.contextmapper.dsl.ide.actions.impl.SplitSystemIntoSubsystemsAction;
import org.contextmapper.dsl.ide.actions.impl.SuspendPartnershipAction;
import org.contextmapper.dsl.ide.actions.impl.SwitchFromPartnershipToSharedKernelAction;
import org.contextmapper.dsl.ide.actions.impl.SwitchFromSharedKernelToPartnershipAction;
import org.contextmapper.dsl.ide.edit.WorkspaceEditRecorder;
import org.contextmapper.dsl.ide.quickfix.QuickfixCommandMapper;
import org.contextmapper.dsl.ide.quickfix.impl.OpenFlowInSketchMinerCommandMapper;
import org.contextmapper.dsl.ide.quickfix.impl.SplitStoryByVerbCommandMapper;
import org.contextmapper.dsl.quickfixes.CMLQuickFix;
import org.contextmapper.dsl.quickfixes.CreateMissingBoundedContextQuickFix;
import org.contextmapper.dsl.quickfixes.SplitStoryByVerb;
import org.contextmapper.dsl.quickfixes.tactic.ExtractIDValueObjectQuickFix;
import org.contextmapper.dsl.validation.ApplicationFlowSemanticsValidator;
import org.contextmapper.dsl.validation.DomainObjectValidator;
import org.contextmapper.dsl.validation.UserRequirementsValidator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.xtext.ide.server.codeActions.ICodeActionService2;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
	private Map<String, List<CMLQuickFix<? extends EObject>>> quickFixActionRegistry;
	private Map<String, List<QuickfixCommandMapper>> quickFixCommandRegistry;

	private static final String XTEXT_DIAGNOSTICS_PREFIX = "org.eclipse.xtext.diagnostics";

	public CMLActionRegistry() {
		this.quickFixActionRegistry = Maps.newHashMap();
		this.quickFixCommandRegistry = Maps.newHashMap();
		this.registerAllQuickFixes();
	}

	private List<CMLCodeAction> getAllActions(CMLResource resource, List<EObject> selectedObjects) {
		List<CMLCodeAction> codeActions = Lists.newLinkedList();

		// add new AR here:
		codeActions.add(new SplitBoundedContextByOwnerAction(resource, selectedObjects));
		codeActions.add(new SplitBoundedContextByFeaturesAction(resource, selectedObjects));
		codeActions.add(new SplitAggregateByEntitiesAction(resource, selectedObjects));
		codeActions.add(new DeriveSubdomainFromUserRequirementsAction(resource, selectedObjects));
		codeActions.add(new DeriveBoundedContextFromSubdomainsAction(resource, selectedObjects));
		codeActions.add(new DeriveFrontendAndBackendFromFeatureBCAction(resource, selectedObjects));
		codeActions.add(new SplitSystemIntoSubsystemsAction(resource, selectedObjects));
		codeActions.add(new ExtractAggregatesByVolatilityAction(resource, selectedObjects));
		codeActions.add(new ExtractAggregatesByCohesionAction(resource, selectedObjects));
		codeActions.add(new MergeAggregatesAction(resource, selectedObjects));
		codeActions.add(new MergeBoundedContextsAction(resource, selectedObjects));
		codeActions.add(new ExtractSharedKernelAction(resource, selectedObjects));
		codeActions.add(new SuspendPartnershipAction(resource, selectedObjects));
		codeActions.add(new SwitchFromPartnershipToSharedKernelAction(resource, selectedObjects));
		codeActions.add(new SwitchFromSharedKernelToPartnershipAction(resource, selectedObjects));
		codeActions.add(new ExtractSuggestedServiceAction(resource, selectedObjects));

		return Lists.newLinkedList(codeActions);
	}

	private void registerAllQuickFixes() {
		// register quick fixes here:
		registerActionQuickFix(DomainObjectValidator.ID_IS_PRIMITIVE_CODE, new ExtractIDValueObjectQuickFix());
		registerCommandQuickFix(UserRequirementsValidator.ID_SPLIT_FEATURE_BY_VERB_SUGGESTION, new SplitStoryByVerbCommandMapper(new SplitStoryByVerb()));
		registerCommandQuickFix(ApplicationFlowSemanticsValidator.SKETCH_MINER_INFO_ID, new OpenFlowInSketchMinerCommandMapper());
	}

	public List<? extends Command> getApplicableActionCommands(CMLResource resource, List<EObject> selectedObjects) {
		List<? extends Command> result = getAllActions(resource, selectedObjects).stream().filter(a -> a.isApplicable()).map(a -> a.getCommand()).collect(Collectors.toList());
		return result;
	}

	public List<Either<Command, CodeAction>> getApplicableQuickfixes(Diagnostic diagnostic, ICodeActionService2.Options options) {
		List<Either<Command, CodeAction>> quickFixCodeActions = Lists.newLinkedList();

		if (diagnostic.getCode() == null || diagnostic.getCode().get() == null || !(diagnostic.getCode().get() instanceof String))
			return quickFixCodeActions;

		String key = (String) diagnostic.getCode().get();
		if (key.startsWith(XTEXT_DIAGNOSTICS_PREFIX))
			quickFixCodeActions.addAll(createQuickFixes4XtextDiagnostics(diagnostic, options));

		quickFixCodeActions.addAll(createActionQuickFix4ValidationMessage(key, diagnostic, options));
		quickFixCodeActions.addAll(createCommandQuickFix4ValidationMessage(key, diagnostic, options));
		return quickFixCodeActions;
	}

	private List<Either<Command, CodeAction>> createActionQuickFix4ValidationMessage(String validationId, Diagnostic diagnostic, ICodeActionService2.Options options) {
		List<Either<Command, CodeAction>> codeActions = Lists.newLinkedList();
		if (quickFixActionRegistry.get(validationId) == null || quickFixActionRegistry.get(validationId).isEmpty())
			return codeActions;

		for (CMLQuickFix<? extends EObject> quickFix : quickFixActionRegistry.get(validationId)) {
			codeActions.add(Either.forRight(createQuickFixCodeAction(quickFix, diagnostic, options, false)));
		}
		return codeActions;
	}

	private List<Either<Command, CodeAction>> createCommandQuickFix4ValidationMessage(String validationId, Diagnostic diagnostic, ICodeActionService2.Options options) {
		List<Either<Command, CodeAction>> codeActions = Lists.newLinkedList();
		if (quickFixCommandRegistry.get(validationId) == null || quickFixCommandRegistry.get(validationId).isEmpty())
			return codeActions;

		CMLResource cmlResource = new CMLResource(options.getResource());
		List<EObject> objects = selectionResolver.resolveAllSelectedEObjects(cmlResource, options.getDocument().getOffSet(diagnostic.getRange().getStart()),
				options.getDocument().getOffSet(diagnostic.getRange().getEnd()));
		if (objects.isEmpty())
			throw new ContextMapperApplicationException("We could not find the selected object for the triggered quickfix.");
		for (QuickfixCommandMapper quickFixCommand : quickFixCommandRegistry.get(validationId)) {
			CodeAction action = quickFixCommand.getCodeAction(cmlResource, objects.get(0));
			List<Diagnostic> diagnostics = Lists.newLinkedList();
			diagnostics.add(diagnostic);
			action.setDiagnostics(diagnostics);
			codeActions.add(Either.forRight(action));
		}
		return codeActions;
	}

	private CodeAction createQuickFixCodeAction(CMLQuickFix<? extends EObject> quickFix, Diagnostic diagnostic, ICodeActionService2.Options options, boolean useRootContext) {
		CodeAction action = new CodeAction(quickFix.getName());
		action.setDiagnostics(Arrays.asList(new Diagnostic[] { diagnostic }));
		action.setKind(CodeActionKind.QuickFix);
		action.setEdit(editRecorder.recordWorkspaceEdit(options.getLanguageServerAccess(), options.getResource().getURI(), options.getDocument(), (Resource resource) -> {
			CMLResource cmlResource = new CMLResource(resource);
			if (useRootContext) {
				quickFix.applyQuickfix2EObject(cmlResource.getContextMappingModel());
			} else {
				List<EObject> objects = selectionResolver.resolveAllSelectedEObjects(cmlResource, options.getDocument().getOffSet(diagnostic.getRange().getStart()),
						options.getDocument().getOffSet(diagnostic.getRange().getEnd()));
				if (objects.isEmpty())
					throw new ContextMapperApplicationException("Selected object for quick fix could not be found.");

				// we can assume that there is only one object per validation message
				quickFix.applyQuickfix2EObject(EcoreUtil.resolve(objects.get(0), resource));
			}
		}));
		return action;
	}

	private List<Either<Command, CodeAction>> createQuickFixes4XtextDiagnostics(Diagnostic diagnostic, ICodeActionService2.Options options) {
		List<Either<Command, CodeAction>> quickFixCodeActions = Lists.newLinkedList();
		if (diagnostic.getCode().get().equals(org.eclipse.xtext.diagnostics.Diagnostic.LINKING_DIAGNOSTIC)
				&& diagnostic.getMessage().matches(String.format(CreateMissingBoundedContextQuickFix.LINK_DIAGNOSTIC_MESSAGE_PATTERN, "BoundedContext"))) {
			Pattern pattern = Pattern.compile(String.format(CreateMissingBoundedContextQuickFix.LINK_DIAGNOSTIC_MESSAGE_PATTERN, "BoundedContext"));
			Matcher matcher = pattern.matcher(diagnostic.getMessage());
			if (matcher.find()) {
				CreateMissingBoundedContextQuickFix quickFix = new CreateMissingBoundedContextQuickFix(matcher.group(1));
				quickFixCodeActions.add(Either.forRight(createQuickFixCodeAction(quickFix, diagnostic, options, true)));
			}
		}
		return quickFixCodeActions;
	}

	private void registerActionQuickFix(String validationId, CMLQuickFix<? extends EObject> quickFix) {
		if (!quickFixActionRegistry.containsKey(validationId))
			quickFixActionRegistry.put(validationId, Lists.newLinkedList());
		quickFixActionRegistry.get(validationId).add(quickFix);
	}

	private void registerCommandQuickFix(String validationId, QuickfixCommandMapper actionMapper) {
		if (!quickFixCommandRegistry.containsKey(validationId))
			quickFixCommandRegistry.put(validationId, Lists.newLinkedList());
		quickFixCommandRegistry.get(validationId).add(actionMapper);
	}

}

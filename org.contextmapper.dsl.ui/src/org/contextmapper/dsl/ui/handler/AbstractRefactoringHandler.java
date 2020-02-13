package org.contextmapper.dsl.ui.handler;

import java.util.List;
import java.util.Set;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Import;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.ui.internal.DslActivator;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.ui.resource.XtextLiveScopeResourceSetProvider;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public abstract class AbstractRefactoringHandler extends AbstractHandler implements IHandler {

	@Inject
	private EObjectAtOffsetHelper eObjectAtOffsetHelper;

	@Inject
	XtextLiveScopeResourceSetProvider resourceSetProvider;

	@Inject
	private ResourceDescriptionsProvider resourceDescriptionsProvider;

	protected Resource currentResource;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			currentResource = getCurrentResource();
			executeRefactoring(new CMLResourceContainer(currentResource), event);
		} catch (ContextMapperApplicationException e) {
			MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "Model Input", e.getMessage());
		} catch (Exception e) {
			String message = e.getMessage() != null && !"".equals(e.getMessage()) ? e.getMessage() : e.getClass().getName() + " occurred in " + this.getClass().getName();
			Status status = new Status(IStatus.ERROR, DslActivator.PLUGIN_ID, message, e);
			StatusManager.getManager().handle(status);
			ErrorDialog.openError(HandlerUtil.getActiveShell(event), "Error", "Exception occured during execution of command!", status);
		}
		return null;
	}

	private Resource getCurrentResource() {
		XtextEditor xEditor = EditorUtils.getActiveXtextEditor();
		IResource xResource = xEditor.getResource();

		URI uri = URI.createPlatformResourceURI(xResource.getFullPath().toString(), true);

		ResourceSet rs = resourceSetProvider.get(xResource.getProject());
		return rs.getResource(uri, true);
	}

	protected ContextMappingModel getCurrentContextMappingModel() {
		Resource resource = currentResource == null ? getCurrentResource() : currentResource;

		List<ContextMappingModel> contextMappingModels = IteratorExtensions
				.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(resource.getAllContents(), ContextMappingModel.class));

		if (contextMappingModels.size() > 0) {
			return contextMappingModels.get(0);
		}
		return null;
	}

	protected ResourceSet getOtherProjectResources() {
		IResourceDescriptions index = resourceDescriptionsProvider.createResourceDescriptions();
		XtextEditor xEditor = EditorUtils.getActiveXtextEditor();
		IResource xResource = xEditor.getResource();
		ResourceSet rs = resourceSetProvider.get(xResource.getProject());

		for (IResourceDescription resDesc : index.getAllResourceDescriptions()) {
			rs.getResource(resDesc.getURI(), true);
		}
		rs.getResources().remove(getCurrentResource());
		return rs;
	}

	protected Set<ContextMappingModel> getReferencedContextMappingModels(ContextMappingModel rootModel) {
		Set<ContextMappingModel> models = Sets.newHashSet();
		if (currentResource == null)
			return models;

		for (Import cmlImport : rootModel.getImports()) {
			ResourceSet rs = currentResource.getResourceSet();
			URI importURI = URI.createURI(cmlImport.getImportURI());
			Resource resource = rs.getResource(importURI.resolve(currentResource.getURI()), true);
			if (resource != null) {
				List<ContextMappingModel> contextMappingModels = IteratorExtensions
						.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(resource.getAllContents(), ContextMappingModel.class));
				models.addAll(contextMappingModels);
			}
		}
		return models;
	}

	/**
	 * Implement this method to initialize and execute the requested refactoring.
	 */
	protected abstract void executeRefactoring(CMLResourceContainer resource, ExecutionEvent event);

	/**
	 * Finds the selected element in the editor (where the user open the
	 * context-menu)
	 * 
	 * @return EObject of current context (editor selection)
	 */
	protected EObject getSelectedElement() {
		final XtextEditor editor = EditorUtils.getActiveXtextEditor();
		if (editor != null) {
			final ITextSelection selection = (ITextSelection) editor.getSelectionProvider().getSelection();
			ContextMapperRefactoringContext context = editor.getDocument().priorityReadOnly(new IUnitOfWork<ContextMapperRefactoringContext, XtextResource>() {
				@Override
				public ContextMapperRefactoringContext exec(XtextResource resource) throws Exception {
					EObject selectedElement = eObjectAtOffsetHelper.resolveElementAt(resource, selection.getOffset());
					if (selectedElement != null) {
						return new ContextMapperRefactoringContext(selectedElement);
					}
					return null;
				}

			});
			if (context != null) {
				return context.selectedObject;
			}
		}
		return null;
	}

	private boolean editorHasChanges() {
		final XtextEditor editor = EditorUtils.getActiveXtextEditor();
		if (editor != null) {
			return editor.isDirty();
		}
		return false;
	}

	@Override
	public boolean isEnabled() {
		return !editorHasChanges();
	}

	protected class ContextMapperRefactoringContext {
		private EObject selectedObject;

		public ContextMapperRefactoringContext(EObject selectedObject) {
			this.selectedObject = selectedObject;
		}
	}
}

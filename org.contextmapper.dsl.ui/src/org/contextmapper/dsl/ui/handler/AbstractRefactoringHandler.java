package org.contextmapper.dsl.ui.handler;

import java.util.List;
import java.util.Set;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Import;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.exception.RefactoringSerializationException;
import org.contextmapper.dsl.ui.editor.XtextEditorHelper;
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
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.ui.resource.XtextLiveScopeResourceSetProvider;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public abstract class AbstractRefactoringHandler extends AbstractHandler implements IHandler {

	@Inject
	XtextLiveScopeResourceSetProvider resourceSetProvider;

	@Inject
	private ResourceDescriptionsProvider resourceDescriptionsProvider;

	@Inject
	private XtextEditorHelper xtextEditorHelper;

	@Inject
	protected ISerializer serializer;

	protected Resource currentResource;
	protected ResourceSet currentResourceSet;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			XtextEditor xEditor = EditorUtils.getActiveXtextEditor();
			IResource xResource = xEditor.getResource();
			currentResourceSet = resourceSetProvider.get(xResource.getProject());
			currentResource = getCurrentResource();
			executeRefactoring(new CMLResource(currentResource), event);
		} catch (RefactoringSerializationException e) {
			String message = e.getMessage() != null && !"".equals(e.getMessage()) ? e.getMessage()
					: e.getClass().getName() + " occurred in " + this.getClass().getName();
			Status status = new Status(IStatus.ERROR, DslActivator.PLUGIN_ID, message, e);
			StatusManager.getManager().handle(status);
			MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "Model Input", e.getMessage());
		} catch (ContextMapperApplicationException e) {
			MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "Model Input", e.getMessage());
		} catch (Exception e) {
			String message = e.getMessage() != null && !"".equals(e.getMessage()) ? e.getMessage()
					: e.getClass().getName() + " occurred in " + this.getClass().getName();
			Status status = new Status(IStatus.ERROR, DslActivator.PLUGIN_ID, message, e);
			StatusManager.getManager().handle(status);
			ErrorDialog.openError(HandlerUtil.getActiveShell(event), "Error",
					"Exception occured during execution of command!", status);
		}
		return null;
	}

	protected Resource getCurrentResource() {
		XtextEditor xEditor = EditorUtils.getActiveXtextEditor();
		IResource xResource = xEditor.getResource();

		URI uri = URI.createPlatformResourceURI(xResource.getFullPath().toString(), true);

		return currentResourceSet.getResource(uri, true);
	}

	protected ContextMappingModel getCurrentContextMappingModel() {
		Resource resource = currentResource == null ? getCurrentResource() : currentResource;

		List<ContextMappingModel> contextMappingModels = IteratorExtensions.<ContextMappingModel>toList(
				Iterators.<ContextMappingModel>filter(resource.getAllContents(), ContextMappingModel.class));

		if (contextMappingModels.size() > 0) {
			return contextMappingModels.get(0);
		}
		return null;
	}

	protected ResourceSet getAllResources() {
		IResourceDescriptions index = resourceDescriptionsProvider.createResourceDescriptions();
		for (IResourceDescription resDesc : index.getAllResourceDescriptions()) {
			currentResourceSet.getResource(resDesc.getURI(), true);
		}
		return currentResourceSet;
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
				List<ContextMappingModel> contextMappingModels = IteratorExtensions.<ContextMappingModel>toList(
						Iterators.<ContextMappingModel>filter(resource.getAllContents(), ContextMappingModel.class));
				models.addAll(contextMappingModels);
			}
		}
		return models;
	}

	/**
	 * Implement this method to initialize and execute the requested refactoring.
	 */
	protected abstract void executeRefactoring(CMLResource resource, ExecutionEvent event);

	/**
	 * Finds the selected element in the editor (where the user open the
	 * context-menu)
	 * 
	 * @return EObject of current context (editor selection)
	 */
	protected EObject getSelectedElement() {
		return xtextEditorHelper.getFirstSelectedElement(EditorUtils.getActiveXtextEditor());
	}

	/**
	 * Finds all selected objects in the editor.
	 * 
	 * @return set with all EObject's of the current editor selection.
	 */
	protected Set<EObject> getAllSelectedElements() {
		return xtextEditorHelper.getAllSelectedElements(EditorUtils.getActiveXtextEditor());
	}

	protected boolean moreThenOneElementSelected() {
		return getAllSelectedElements().size() > 1;
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

}

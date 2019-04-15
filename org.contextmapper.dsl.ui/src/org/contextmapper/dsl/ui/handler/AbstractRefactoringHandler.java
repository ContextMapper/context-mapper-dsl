package org.contextmapper.dsl.ui.handler;

import org.contextmapper.dsl.refactoring.henshin.Refactoring;
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
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.inject.Inject;

public abstract class AbstractRefactoringHandler extends AbstractHandler implements IHandler {

	@Inject
	private EObjectAtOffsetHelper eObjectAtOffsetHelper;

	@Inject
	IResourceSetProvider resourceSetProvider;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			XtextEditor xEditor = EditorUtils.getActiveXtextEditor();
			IResource xResource = xEditor.getResource();

			URI uri = URI.createPlatformResourceURI(xResource.getFullPath().toString(), true);

			ResourceSet rs = resourceSetProvider.get(xResource.getProject());
			Resource resource = rs.getResource(uri, true);

			Refactoring refactoring = getRefactoring();
			refactoring.doRefactor(resource);
		} catch (Exception e) {
			String message = e.getMessage() != null && !"".equals(e.getMessage()) ? e.getMessage() : e.getClass().getName() + " occurred in " + this.getClass().getName();
			Status status = new Status(IStatus.ERROR, DslActivator.PLUGIN_ID, message, e);
			StatusManager.getManager().handle(status);
			ErrorDialog.openError(HandlerUtil.getActiveShell(event), "Error", "Exception occured during execution of command!", status);
		}
		return null;
	}

	/**
	 * Implement this method to initialize the requested refactoring.
	 * 
	 * @return The HenshinRefactoring instance.
	 */
	protected abstract Refactoring getRefactoring();

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

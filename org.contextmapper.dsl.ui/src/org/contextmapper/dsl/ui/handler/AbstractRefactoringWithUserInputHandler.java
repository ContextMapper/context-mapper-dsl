package org.contextmapper.dsl.ui.handler;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.exception.RefactoringSerializationException;
import org.contextmapper.dsl.refactoring.SemanticCMLRefactoring;
import org.contextmapper.dsl.ui.internal.DslActivator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

public abstract class AbstractRefactoringWithUserInputHandler extends AbstractRefactoringHandler {

	/**
	 * Use this method to finish refactoring after user input (on finish)
	 */
	protected boolean finishRefactoring(SemanticCMLRefactoring ar, CMLResource resource, ExecutionEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					ar.refactor(resource, getAllResources());
					ar.persistChanges(serializer);
				} catch (RefactoringSerializationException e) {
					String message = e.getMessage() != null && !"".equals(e.getMessage()) ? e.getMessage() : e.getClass().getName() + " occurred in " + this.getClass().getName();
					Status status = new Status(IStatus.ERROR, DslActivator.PLUGIN_ID, message, e);
					StatusManager.getManager().handle(status);
					MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "Model Input", e.getMessage());
				} catch (ContextMapperApplicationException e) {
					MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "Model Input", e.getMessage());
				}
			}
		});
		return true;
	}

}

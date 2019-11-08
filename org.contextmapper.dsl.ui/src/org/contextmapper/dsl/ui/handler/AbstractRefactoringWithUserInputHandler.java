package org.contextmapper.dsl.ui.handler;

import org.contextmapper.dsl.refactoring.Refactoring;
import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class AbstractRefactoringWithUserInputHandler extends AbstractRefactoringHandler {

	/**
	 * Use this method to finish refactoring after user input (on finish)
	 */
	protected boolean finishRefactoring(Refactoring ar, Resource resource, ExecutionEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					ar.doRefactor(resource);
				} catch (RefactoringInputException e) {
					MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "Model Input", e.getMessage());
				}
			}
		});
		return true;
	}

}

package org.contextmapper.dsl.ui.handler;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.refactoring.Refactoring;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class AbstractRefactoringWithUserInputHandler extends AbstractRefactoringHandler {

	/**
	 * Use this method to finish refactoring after user input (on finish)
	 */
	protected boolean finishRefactoring(Refactoring ar, CMLResourceContainer resource, ExecutionEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					ar.doRefactor(resource, getOtherProjectResources());
				} catch (ContextMapperApplicationException e) {
					MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "Model Input", e.getMessage());
				}
			}
		});
		return true;
	}

}

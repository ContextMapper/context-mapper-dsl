package org.contextmapper.dsl.ui.handler;

import org.contextmapper.dsl.refactoring.exception.RefactoringInputException;
import org.contextmapper.dsl.refactoring.henshin.Refactoring;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class AbstractRefactoringWithUserInputHandler extends AbstractRefactoringHandler {

	/**
	 * Use this method to finish refactoring after user input (on finish)
	 */
	protected boolean finishRefactoring(Refactoring ar, Resource resource, ExecutionEvent event) {
		try {
			ar.doRefactor(resource);
			return true;
		} catch (RefactoringInputException e) {
			MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "Model Input", e.getMessage());
		}
		return false;
	}

}

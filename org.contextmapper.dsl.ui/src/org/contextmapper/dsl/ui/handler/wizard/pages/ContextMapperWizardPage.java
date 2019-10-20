package org.contextmapper.dsl.ui.handler.wizard.pages;

import org.contextmapper.dsl.ui.internal.DslActivator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;

public abstract class ContextMapperWizardPage extends WizardPage {

	public ContextMapperWizardPage(String title) {
		super(title);
	}

	@Override
	public Image getImage() {
		return DslActivator.imageDescriptorFromPlugin(DslActivator.PLUGIN_ID, "icons/cml-dialog-image.png").createImage();
	}
	
	@Override
	public void performHelp() {
		Program.launch("https://contextmapper.org/docs/architectural-refactorings/");
	}
	
}

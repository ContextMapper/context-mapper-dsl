package org.contextmapper.dsl.ui.startup;

import org.contextmapper.dsl.refactoring.henshin.HenshinTransformationFileProvider;
import org.eclipse.ui.IStartup;

public class ContextMapperPluginStartup implements IStartup {

	private HenshinTransformationFileProvider refactoringFileProvider;

	public ContextMapperPluginStartup() {
		this.refactoringFileProvider = new HenshinTransformationFileProvider();
	}

	@Override
	public void earlyStartup() {
		this.refactoringFileProvider.prepareAllHenshinFiles();
	}

}

package org.contextmapper.dsl.tests.generators.mocks;

import org.eclipse.xtext.generator.IGeneratorContext;
import org.eclipse.xtext.util.CancelIndicator;

public class IGeneratorContextMock implements IGeneratorContext {

	@Override
	public CancelIndicator getCancelIndicator() {
		return null;
	}

}

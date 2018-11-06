package org.contextmapper.dsl.ui.handler;

import org.contextmapper.dsl.generator.ServiceCutterInputGenerator;
import org.eclipse.xtext.generator.IGenerator2;

import com.google.inject.Inject;

public class ServiceCutterGenerationHandler extends AbstractGenerationHandler {

	@Inject
	private ServiceCutterInputGenerator generator;

	@Override
	protected IGenerator2 getGenerator() {
		return generator;
	}

}

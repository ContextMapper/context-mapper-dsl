package org.contextmapper.dsl.ui.handler;

import org.contextmapper.dsl.generator.PlantUMLGenerator;
import org.eclipse.xtext.generator.IGenerator2;

import com.google.inject.Inject;

public class PlantUMLGenerationHandler extends AbstractGenerationHandler {

	@Inject
	private PlantUMLGenerator generator;

	@Override
	protected IGenerator2 getGenerator() {
		return generator;
	}

}

/*
 * Copyright 2018 The Context Mapper Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.contextmapper.dsl.refactoring.henshin;

import org.contextmapper.dsl.refactoring.AbstractRefactoring;
import org.contextmapper.dsl.refactoring.Refactoring;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.UnitApplication;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.interpreter.impl.UnitApplicationImpl;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

public abstract class AbstractHenshinRefactoring extends AbstractRefactoring implements Refactoring {

	@Override
	protected void doRefactor() {
		executeHenshinTransformation(rootResource.getResource());
	}

	private void executeHenshinTransformation(Resource resource) {
		// Create a resource set with a base directory:
		HenshinResourceSet resourceSet = new HenshinResourceSet();

		// Load the module
		String transformationFile = new HenshinTransformationFileProvider().getTransformationFilePath(getHenshinTransformationFilename());
		Module module = resourceSet.getModule(transformationFile, false);

		// Load the example model into an EGraph
		EGraph graph = new EGraphImpl(resource);

		// Create an engine and a rule application
		Engine engine = new EngineImpl();
		UnitApplication refactoringUnit = new UnitApplicationImpl(engine);
		refactoringUnit.setEGraph(graph);

		// Execute refactoring transformation
		refactoringUnit.setUnit(module.getUnit(getTransformationUnitName()));
		setUnitParameters(refactoringUnit);
		if (!refactoringUnit.execute(null))
			throwTransformationError();

		// Saving the result
		resourceSet.saveEObject(graph.getRoots().get(0), resource.getURI());

		// post-processing
		Resource newResource = resourceSet.getResource(resource.getURI(), false);
		postProcessing(newResource);

		saveResource(newResource);
	}

	/**
	 * Method has to be implemented to provide the name of the Henshin
	 * transformation file.
	 * 
	 * @return Name of the Henshin transformation file.
	 */
	protected abstract String getHenshinTransformationFilename();

	/**
	 * Method has to be implemented to provide the transformation unit name of the
	 * henshin transformation
	 * 
	 * @return The Henshin transformation unit name
	 */
	protected abstract String getTransformationUnitName();

	/**
	 * Override this method to provide the needed parameters for the given henshin
	 * transformation
	 * 
	 * @param refactoringUnit The Henshin transformation unit.
	 */
	protected void setUnitParameters(UnitApplication refactoringUnit) {
		// no parameters set
	}

	/**
	 * Override this method in case you want execute some post-processing after the
	 * Henshin transformation
	 * 
	 * @param resource The resource already transformed by Henshin.
	 */
	protected void postProcessing(Resource resource) {
		// nothing to do
	}

	/**
	 * Override this method to provide refactoring-specific error message.
	 */
	protected void throwTransformationError() {
		throw new RuntimeException("Error in Henshin transformation. Refactoring could not be executed ...");
	}

}

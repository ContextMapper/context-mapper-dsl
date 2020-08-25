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

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.refactoring.AbstractRefactoring;
import org.contextmapper.dsl.refactoring.SemanticCMLRefactoring;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.UnitApplication;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.interpreter.impl.UnitApplicationImpl;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

public abstract class AbstractHenshinRefactoring extends AbstractRefactoring implements SemanticCMLRefactoring {

	@Override
	protected void doRefactor() {
		executeHenshinTransformation();
	}

	private void executeHenshinTransformation() {
		// Create a resource set with a base directory:
		HenshinResourceSet resourceSet = new HenshinResourceSet();

		// Load the module
		String transformationFile = new HenshinTransformationFileProvider().getTransformationFilePath(getHenshinTransformationFilename());
		Module module = resourceSet.getModule(transformationFile, false);

		// Load the example model into an EGraph
		CMLResource transformationResource = getTransformationResource();
		EGraph graph = new EGraphImpl(transformationResource);

		// Create an engine and a rule application
		Engine engine = new EngineImpl();
		UnitApplication refactoringUnit = new UnitApplicationImpl(engine);
		refactoringUnit.setEGraph(graph);

		// Execute refactoring transformation
		refactoringUnit.setUnit(module.getUnit(getTransformationUnitName()));
		setUnitParameters(refactoringUnit);
		if (!refactoringUnit.execute(null))
			throwTransformationError();

		// replace transformed model in resource
		transformationResource.getContents().clear();
		transformationResource.getContents().add(graph.getRoots().get(0));

		// post-processing
		postProcessing(transformationResource);
	}

	/**
	 * The root resource on which the refactoring has been started might not be the
	 * resource that has to be transformed (reference). Implement this method to
	 * resolve the resource that has to be transformed by Henshin.
	 * 
	 * @return the CML resource that shall be transformed
	 */
	protected abstract CMLResource getTransformationResource();

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
	protected void postProcessing(CMLResource resource) {
		// nothing to do
	}

	/**
	 * Override this method to provide refactoring-specific error message.
	 */
	protected void throwTransformationError() {
		throw new RuntimeException("Error in Henshin transformation. Refactoring could not be executed ...");
	}

}

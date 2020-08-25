/*
 * Copyright 2020 The Context Mapper Project Team
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
package org.contextmapper.dsl.standalone;

import java.io.File;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.refactoring.SemanticCMLRefactoring;
import org.eclipse.xtext.generator.IGenerator2;

/**
 * Interface that eases the usage of Context Mapper in standalone (usage as
 * library) scenario.
 * 
 * @author Stefan Kapferer
 */
public interface StandaloneContextMapperAPI {

	/**
	 * Loads a CML model, given a *.cml file.
	 * 
	 * @param filepath the path to the *.cml file that shall be loaded
	 * @return the CMLResource loaded from the *.cml file
	 */
	CMLResource loadCML(String filepath);

	/**
	 * Loads a CML model, given a *.cml file.
	 * 
	 * @param cmlFile the *.cml file
	 * @return the CMLResource loaded from the *.cml file
	 */
	CMLResource loadCML(File cmlFile);

	/**
	 * Creates a new CML model.
	 * 
	 * @param filepath the filepath where the *.cml file will be stored, in case the
	 *                 resource is saved/persisted
	 * @return the new CMLResource (not yet persisted; you have to call save() on
	 *         the resource)
	 */
	CMLResource createCML(String filepath);

	/**
	 * Creates a new CML model.
	 * 
	 * @param cmlFile the file where the *.cml file will be stored, in case the
	 *                resource is saved/persisted
	 * @return the new CMLResource (not yet persisted; you have to call save() on
	 *         the resource)
	 */
	CMLResource createCML(File cmlFile);

	/**
	 * Calls a generator that produces output (Graphical Context Map, PlantUML,
	 * etc.) given a CML resource as input. This method generates all output files
	 * into the default directory "./src-gen".
	 * 
	 * @param cml       the CML resource for which the generator shall be called
	 * @param generator the generator that shall be called
	 */
	void callGenerator(CMLResource cml, IGenerator2 generator);

	/**
	 * Calls a generator that produces output (Graphical Context Map, PlantUML,
	 * etc.) given a CML resource as input. This method allows to define the
	 * directory into which the output shall be generated (start with "./" and
	 * provide a directory relative to your execution home).
	 * 
	 * @param cml       the CML resource for which the generator shall be called
	 * @param generator the generator that shall be called
	 * @param outputDir the directory in which you want to generate the output
	 *                  (start with "./", relative to your execution directory)
	 */
	void callGenerator(CMLResource cml, IGenerator2 generator, String outputDir);

	/**
	 * Applies a refactoring (must be of type SemanticCMLRefactoring) on a CML
	 * model.
	 * 
	 * @param cml         the model on which the refactoring shall be applied
	 * @param refactoring the refactoring that shall be applied
	 */
	void applyRefactoring(CMLResource cml, SemanticCMLRefactoring refactoring);
}

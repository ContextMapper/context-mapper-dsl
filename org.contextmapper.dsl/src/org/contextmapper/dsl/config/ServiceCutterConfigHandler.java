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
package org.contextmapper.dsl.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Tag;

import ch.hsr.servicecutter.api.SolverConfigurationFactory;
import ch.hsr.servicecutter.solver.SolverConfiguration;

/**
 * Used to create the .servicecutter.yml file in the root directory of the
 * project.
 * 
 * @author Stefan Kapferer
 *
 */
public class ServiceCutterConfigHandler {

	private File projectRoot;

	private static final String SC_CONFIG_FILENAME = ".servicecutter.yml";

	public ServiceCutterConfigHandler(File projectRoot) {
		if (!projectRoot.exists())
			throw new ContextMapperApplicationException("The project directory '" + projectRoot.getAbsolutePath() + "' does not exist!");
		this.projectRoot = projectRoot;
	}

	public ServiceCutterConfig createAndGetServiceCutterConfig() {
		createConfigFile();
		File configFile = new File(projectRoot, SC_CONFIG_FILENAME);
		Yaml yaml = new Yaml(new CustomClassLoaderConstructor(this.getClass().getClassLoader()));
		try {
			InputStream inputStream = new FileInputStream(configFile);
			return yaml.loadAs(inputStream, ServiceCutterConfig.class);
		} catch (FileNotFoundException e) {
			throw new ContextMapperApplicationException("Could not read '.servicecutter.yml' file!", e);
		} catch (YAMLException e) {
			throw new ContextMapperApplicationException("Could not parse '.servicecutter.yml' file. Allowed values for criteria priorities are: IGNORE, XS, S, M, L, XL, and XXL.", e);
		}
	}

	public SolverConfiguration getServiceCutterSolverConfiguration() {
		ServiceCutterConfig config = createAndGetServiceCutterConfig();
		SolverConfiguration solverConfig = new SolverConfiguration();
		solverConfig.setAlgorithm(config.getAlgorithm());
		solverConfig.getAlgorithmParams().putAll(config.getAlgorithmParams());
		solverConfig.getPriorities().putAll(config.getPriorities());
		return solverConfig;
	}

	/**
	 * Creates an initial .servicecutter.yml, in case it does not already exist.
	 */
	private void createConfigFile() {
		File configFile = new File(projectRoot, SC_CONFIG_FILENAME);
		if (configFile.exists())
			return;

		ServiceCutterConfig config = createInitialServiceCutterConfig();
		try {
			FileWriter writer = new FileWriter(configFile);
			writer.write(new Yaml().dumpAs(config, Tag.MAP, FlowStyle.BLOCK));
			writer.close();
		} catch (IOException e) {
			throw new ContextMapperApplicationException("Could not create '.servicecutter.yml' file!", e);
		}
	}

	private ServiceCutterConfig createInitialServiceCutterConfig() {
		ServiceCutterConfig config = new ServiceCutterConfig();
		SolverConfiguration defaultSolverConfig = new SolverConfigurationFactory().createDefaultConfiguration();
		config.setAlgorithm(defaultSolverConfig.getAlgorithm());
		config.setAlgorithmParams(defaultSolverConfig.getAlgorithmParams());
		config.setPriorities(defaultSolverConfig.getPriorities());
		return config;
	}

}

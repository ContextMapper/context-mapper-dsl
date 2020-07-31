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

import java.util.HashMap;
import java.util.Map;

import ch.hsr.servicecutter.solver.SolverAlgorithm;
import ch.hsr.servicecutter.solver.SolverPriority;

public class ServiceCutterConfig {

	private Map<String, Double> algorithmParams = new HashMap<>();
	private Map<String, SolverPriority> priorities = new HashMap<>();
	private SolverAlgorithm algorithm = SolverAlgorithm.MARKOV_CLUSTERING;

	public Map<String, Double> getAlgorithmParams() {
		return algorithmParams;
	}

	public void setAlgorithmParams(Map<String, Double> algorithmParams) {
		this.algorithmParams = algorithmParams;
	}

	public Map<String, SolverPriority> getPriorities() {
		return priorities;
	}

	public void setPriorities(Map<String, SolverPriority> priorities) {
		this.priorities = priorities;
	}

	public SolverAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(SolverAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

}

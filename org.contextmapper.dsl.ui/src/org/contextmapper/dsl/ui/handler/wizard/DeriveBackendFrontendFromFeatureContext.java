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
package org.contextmapper.dsl.ui.handler.wizard;

import java.util.Set;

import org.contextmapper.dsl.refactoring.DeriveFrontendAndBackendSystemsFromFeatureBoundedContext;
import org.contextmapper.dsl.refactoring.DeriveFrontendAndBackendSystemsFromFeatureBoundedContext.FrontendBackendRelationshipType;

public class DeriveBackendFrontendFromFeatureContext {

	private String frontendName;
	private String backendName;
	private Set<String> allBoundedContextNames;
	private String featureBoundedContextName;
	private boolean deriveViewModelInFrontend = true;
	private DeriveFrontendAndBackendSystemsFromFeatureBoundedContext.FrontendBackendRelationshipType relationshipType;
	private String frontendImplementationTechnology;
	private String backendImplementationTechnology;
	private String relationshipImplementationTechnology;

	public DeriveBackendFrontendFromFeatureContext(String featureBoundedContextName, Set<String> allBoundedContextNames) {
		this.featureBoundedContextName = featureBoundedContextName;
		this.allBoundedContextNames = allBoundedContextNames;
		this.frontendName = featureBoundedContextName + "Frontend";
		this.backendName = featureBoundedContextName + "Backend";
		this.relationshipType = FrontendBackendRelationshipType.CONFORMIST;
	}

	public String getFrontendName() {
		return frontendName;
	}

	public void setFrontendName(String frontendName) {
		this.frontendName = frontendName;
	}

	public String getBackendName() {
		return backendName;
	}

	public void setBackendName(String backendName) {
		this.backendName = backendName;
	}

	public Set<String> getAllBoundedContextNames() {
		return allBoundedContextNames;
	}

	public void setAllBoundedContextNames(Set<String> allBoundedContextNames) {
		this.allBoundedContextNames = allBoundedContextNames;
	}

	public String getFeatureBoundedContextName() {
		return featureBoundedContextName;
	}

	public void setFeatureBoundedContextName(String featureBoundedContextName) {
		this.featureBoundedContextName = featureBoundedContextName;
	}

	public DeriveFrontendAndBackendSystemsFromFeatureBoundedContext.FrontendBackendRelationshipType getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(DeriveFrontendAndBackendSystemsFromFeatureBoundedContext.FrontendBackendRelationshipType relationshipType) {
		this.relationshipType = relationshipType;
	}

	public boolean deriveViewModelInFrontend() {
		return deriveViewModelInFrontend;
	}

	public void deriveViewModelInFrontend(boolean deriveViewModelInFrontend) {
		this.deriveViewModelInFrontend = deriveViewModelInFrontend;
	}

	public String getFrontendImplementationTechnology() {
		return frontendImplementationTechnology;
	}

	public void setFrontendImplementationTechnology(String frontendImplementationTechnology) {
		this.frontendImplementationTechnology = frontendImplementationTechnology;
	}

	public String getBackendImplementationTechnology() {
		return backendImplementationTechnology;
	}

	public void setBackendImplementationTechnology(String backendImplementationTechnology) {
		this.backendImplementationTechnology = backendImplementationTechnology;
	}

	public String getRelationshipImplementationTechnology() {
		return relationshipImplementationTechnology;
	}

	public void setRelationshipImplementationTechnology(String relationshipImplementationTechnology) {
		this.relationshipImplementationTechnology = relationshipImplementationTechnology;
	}

}

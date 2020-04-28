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

import org.contextmapper.dsl.refactoring.ContextSplittingIntegrationType;
import org.contextmapper.dsl.refactoring.SplitSystemTier.SplitBoundedContextRelationshipType;

public class SplitSystemTierContext {

	private String originalSystemName;
	private String existingContextTierName;
	private String newTierName;
	private SplitBoundedContextRelationshipType relationshipType = SplitBoundedContextRelationshipType.EXISTING_CONTEXT_BECOMES_DOWNSTREAM;
	private ContextSplittingIntegrationType integrationType = ContextSplittingIntegrationType.CONFORMIST;
	private boolean copyDomainModel = false;
	private String newTierImplementationTechnology = "";
	private String newRelationshipImplementationTechnology = "";
	private Set<String> existingBoundedContexts;

	public SplitSystemTierContext(String originalSystemName, String existingContextTierName, String newTierName, Set<String> existingBoundedContexts) {
		this.originalSystemName = originalSystemName;
		this.existingContextTierName = existingContextTierName;
		this.newTierName = newTierName;
		this.existingBoundedContexts = existingBoundedContexts;
	}

	public String getOriginalSystemName() {
		return originalSystemName;
	}

	public String getExistingContextTierName() {
		return existingContextTierName;
	}

	public void setExistingContextTierName(String existingContextTierName) {
		this.existingContextTierName = existingContextTierName;
	}

	public String getNewTierName() {
		return newTierName;
	}

	public void setNewTierName(String newTierName) {
		this.newTierName = newTierName;
	}

	public SplitBoundedContextRelationshipType getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(SplitBoundedContextRelationshipType relationshipType) {
		this.relationshipType = relationshipType;
	}

	public ContextSplittingIntegrationType getIntegrationType() {
		return integrationType;
	}

	public void setIntegrationType(ContextSplittingIntegrationType integrationType) {
		this.integrationType = integrationType;
	}

	public boolean copyDomainModel() {
		return copyDomainModel;
	}

	public void copyDomainModel(boolean copyDomainModel) {
		this.copyDomainModel = copyDomainModel;
	}

	public String getNewTierImplementationTechnology() {
		return newTierImplementationTechnology;
	}

	public void setNewTierImplementationTechnology(String newTierImplementationTechnology) {
		this.newTierImplementationTechnology = newTierImplementationTechnology;
	}

	public String getNewRelationshipImplementationTechnology() {
		return newRelationshipImplementationTechnology;
	}

	public void setNewRelationshipImplementationTechnology(String newRelationshipImplementationTechnology) {
		this.newRelationshipImplementationTechnology = newRelationshipImplementationTechnology;
	}

	public Set<String> getExistingBoundedContexts() {
		return existingBoundedContexts;
	}

}

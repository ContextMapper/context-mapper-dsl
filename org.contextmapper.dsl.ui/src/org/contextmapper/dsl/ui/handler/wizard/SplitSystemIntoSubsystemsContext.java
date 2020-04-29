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
import org.contextmapper.dsl.refactoring.SplitSystemIntoSubsystems.SplitBoundedContextRelationshipType;

public class SplitSystemIntoSubsystemsContext {

	private String originalSystemName;
	private String existingContextSubsystemName;
	private String newSubsystemName;
	private SplitBoundedContextRelationshipType relationshipType = SplitBoundedContextRelationshipType.NEW_CONTEXT_BECOMES_UPSTREAM;
	private ContextSplittingIntegrationType integrationType = ContextSplittingIntegrationType.CONFORMIST;
	private boolean copyDomainModel = false;
	private String newSubsystemImplementationTechnology = "";
	private String newRelationshipImplementationTechnology = "";
	private Set<String> existingBoundedContexts;

	public SplitSystemIntoSubsystemsContext(String originalSystemName, String existingContextSubsystemName, String newSubsystemName, Set<String> existingBoundedContexts) {
		this.originalSystemName = originalSystemName;
		this.existingContextSubsystemName = existingContextSubsystemName;
		this.newSubsystemName = newSubsystemName;
		this.existingBoundedContexts = existingBoundedContexts;
	}

	public String getOriginalSystemName() {
		return originalSystemName;
	}

	public String getExistingContextSubsystemName() {
		return existingContextSubsystemName;
	}

	public void setExistingContextSubsystemName(String existingContextSubsystemName) {
		this.existingContextSubsystemName = existingContextSubsystemName;
	}

	public String getNewSubsystemName() {
		return newSubsystemName;
	}

	public void setNewSubsystemName(String newSubsystemName) {
		this.newSubsystemName = newSubsystemName;
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

	public String getNewSubsystemImplementationTechnology() {
		return newSubsystemImplementationTechnology;
	}

	public void setNewSubsystemImplementationTechnology(String newSubsystemImplementationTechnology) {
		this.newSubsystemImplementationTechnology = newSubsystemImplementationTechnology;
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

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

import java.util.List;
import java.util.Set;

import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.interpreter.UnitApplication;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class SplitBoundedContextByDuplicateEntityInAggregatesRefactoring extends AbstractHenshinRefactoring {

	private String splitEntityName;

	@Override
	protected String getHenshinTransformationFilename() {
		return HenshinTransformationFileProvider.SPLIT_BY_DUPLICATE_ENTITY_NAME;
	}

	@Override
	protected String getTransformationUnitName() {
		return "splitBoundedContextsBySameEntityName";
	}

	@Override
	protected void setUnitParameters(UnitApplication refactoringUnit) {
		List<String> duplicateEntities = findDuplicateEntities();
		if (duplicateEntities.isEmpty())
			throw new NoDuplicateEntityFoundException();
		// this is a proof of concept! just take the first duplicate found...
		splitEntityName = duplicateEntities.get(0);
		refactoringUnit.setParameterValue("entityName", splitEntityName);
	}

	@Override
	protected void throwTransformationError() {
		throw new RuntimeException("Error splitting by entity '" + splitEntityName + "' ...");
	}

	private List<String> findDuplicateEntities() {
		List<String> duplicates = Lists.newArrayList();
		Set<String> uniqueNameCheckSet = Sets.newHashSet();
		List<Entity> entities = EcoreUtil2.<Entity>getAllContentsOfType(EcoreUtil.getRootContainer(model), Entity.class);
		for (Entity entity : entities) {
			if (!uniqueNameCheckSet.contains(entity.getName())) {
				uniqueNameCheckSet.add(entity.getName());
			} else {
				duplicates.add(entity.getName());
			}
		}
		return duplicates;
	}

	public class NoDuplicateEntityFoundException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public NoDuplicateEntityFoundException() {
			super("No duplicate entity found on this context map!");
		}
	}

}

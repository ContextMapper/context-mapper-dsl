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

import org.contextmapper.dsl.refactoring.ContextMappingModelHelper;
import org.eclipse.emf.henshin.interpreter.UnitApplication;

public class SplitBoundedContextByDuplicateEntityInAggregatesRefactoring extends AbstractHenshinRefactoring {

	private String splitEntityName;
	private String boundedContextName;

	public SplitBoundedContextByDuplicateEntityInAggregatesRefactoring(String boundedContextName) {
		this.boundedContextName = boundedContextName;
	}

	@Override
	protected String getHenshinTransformationFilename() {
		return HenshinTransformationFileProvider.SPLIT_BC_BY_DUPLICATE_ENTITY_NAME;
	}

	@Override
	protected String getTransformationUnitName() {
		return "splitBoundedContextsBySameEntityName";
	}

	@Override
	protected void setUnitParameters(UnitApplication refactoringUnit) {
		List<String> duplicateEntities = new ContextMappingModelHelper(model).findDuplicateEntities(boundedContextName);
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

	public class NoDuplicateEntityFoundException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public NoDuplicateEntityFoundException() {
			super("No duplicate entity found on this context map!");
		}
	}

}

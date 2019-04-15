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
package org.contextmapper.dsl.ui.handler;

import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.refactoring.henshin.Refactoring;
import org.contextmapper.dsl.refactoring.henshin.SplitAggregateByEntitiesRefactoring;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.eclipse.emf.ecore.EObject;

public class SplitAggregateByEntitiesRefactoringHandler extends AbstractRefactoringHandler {

	@Override
	protected Refactoring getRefactoring() {
		Aggregate aggregate = (Aggregate) getSelectedElement();
		return new SplitAggregateByEntitiesRefactoring(aggregate.getName());
	}

	@Override
	public boolean isEnabled() {
		EObject obj = getSelectedElement();

		if (obj == null || !super.isEnabled())
			return false;

		// only allowed on aggregates
		if (!(obj instanceof Aggregate))
			return false;

		// aggregate should at least have two entities
		Aggregate aggregate = (Aggregate) obj;
		List<SimpleDomainObject> entities = aggregate.getDomainObjects().stream().filter(so -> so instanceof Entity).collect(Collectors.toList());
		if (entities.size() < 2)
			return false;

		return true;
	}

}

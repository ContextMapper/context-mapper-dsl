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
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.LikelihoodForChange;
import org.contextmapper.dsl.refactoring.ExtractAggregatesLikelyToChange;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

public class ExtractAggregatesLikelyToChangeRefactoringHandler extends AbstractRefactoringHandler {

	@Override
	protected void executeRefactoring(Resource resource, ExecutionEvent event) {
		BoundedContext bc = (BoundedContext) getSelectedElement();
		new ExtractAggregatesLikelyToChange(bc.getName()).doRefactor(resource);
	}

	@Override
	public boolean isEnabled() {
		EObject obj = getSelectedElement();

		if (obj == null || !super.isEnabled())
			return false;

		// only allowed on bounded contexts
		if (!(obj instanceof BoundedContext))
			return false;

		BoundedContext bc = (BoundedContext) obj;
		List<Aggregate> aggregatesLikelyToChange = bc.getAggregates().stream().filter(agg -> agg.getLikelihoodForChange().equals(LikelihoodForChange.OFTEN))
				.collect(Collectors.toList());
		return aggregatesLikelyToChange.size() > 0 && aggregatesLikelyToChange.size() < bc.getAggregates().size();
	}

}

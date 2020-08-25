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
package org.contextmapper.dsl.ide.actions.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.ide.actions.CMLCodeAction;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.lsp4j.Command;

import com.google.common.collect.Lists;

/**
 * Action that calls the "Split Aggregate by Entities" refactoring.
 * 
 * @author Stefan Kapferer
 *
 */
public class SplitAggregateByEntitiesAction implements CMLCodeAction {

	private CMLResource cmlResource;
	private List<EObject> editorSelection;

	public SplitAggregateByEntitiesAction(CMLResource cmlResource, List<EObject> editorSelection) {
		this.cmlResource = cmlResource;
		this.editorSelection = editorSelection;
	}

	@Override
	public boolean isApplicable() {
		Set<Aggregate> aggregates = getSelectedAggregates();

		if (aggregates.isEmpty() || aggregates.size() > 1)
			return false;

		Aggregate aggregate = aggregates.iterator().next();
		List<SimpleDomainObject> entities = aggregate.getDomainObjects().stream().filter(so -> so instanceof Entity).collect(Collectors.toList());
		return entities.size() > 1;
	}

	@Override
	public Command getCommand() {
		List<Object> commandArguments = Lists.newLinkedList();
		commandArguments.add(cmlResource.getURI().toString());
		commandArguments.add(getSelectedAggregate().getName());
		return new Command("Split Aggregate By Entities", "cml.ar.splitAggregateByEntities", commandArguments);
	}

	private Aggregate getSelectedAggregate() {
		Set<Aggregate> aggregates = getSelectedAggregates();
		return aggregates.iterator().next();
	}

	private Set<Aggregate> getSelectedAggregates() {
		return editorSelection.stream().filter(o -> o instanceof Aggregate).map(o -> (Aggregate) o).collect(Collectors.toSet());
	}

}

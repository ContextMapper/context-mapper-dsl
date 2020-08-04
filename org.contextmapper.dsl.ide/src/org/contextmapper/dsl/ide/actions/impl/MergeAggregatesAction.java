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

import org.contextmapper.dsl.cml.CMLModelObjectsResolvingHelper;
import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.ide.actions.CMLCodeAction;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.lsp4j.Command;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Lists;

/**
 * Action that calls the "Merge Aggregates" refactoring.
 * 
 * @author Stefan Kapferer
 *
 */
public class MergeAggregatesAction implements CMLCodeAction {

	private CMLResourceContainer cmlResource;
	private List<EObject> editorSelection;

	public MergeAggregatesAction(CMLResourceContainer cmlResource, List<EObject> editorSelection) {
		this.cmlResource = cmlResource;
		this.editorSelection = editorSelection;
	}

	@Override
	public boolean isApplicable() {
		Set<Aggregate> aggregates = getSelectedAggregates();
		if (aggregates.isEmpty() || aggregates.size() > 1)
			return false;

		BoundedContext parentContext = new CMLModelObjectsResolvingHelper().resolveBoundedContext(aggregates.iterator().next());
		return EcoreUtil2.eAllOfType(parentContext, Aggregate.class).size() > 1;
	}

	@Override
	public Command getCommand() {
		Aggregate aggregate = getSelectedAggregate();
		BoundedContext parentContext = new CMLModelObjectsResolvingHelper().resolveBoundedContext(aggregate);
		List<Object> commandArguments = Lists.newLinkedList();
		commandArguments.add(cmlResource.getResource().getURI().toString());
		commandArguments.add(aggregate.getName());
		commandArguments.addAll(EcoreUtil2.eAllOfType(parentContext, Aggregate.class).stream().map(agg -> agg.getName()).filter(name -> !name.equals(aggregate.getName())).collect(Collectors.toSet()));

		return new Command("Merge Aggregates", "cml.ar.mergeAggregates.proxy", commandArguments);
	}

	private Aggregate getSelectedAggregate() {
		return getSelectedAggregates().iterator().next();
	}

	private Set<Aggregate> getSelectedAggregates() {
		return editorSelection.stream().filter(o -> o instanceof Aggregate).map(o -> (Aggregate) o).collect(Collectors.toSet());
	}

}

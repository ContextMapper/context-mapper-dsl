/*
 * Copyright 2019 The Context Mapper Project Team
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
package org.contextmapper.dsl.refactoring;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Import;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.eclipse.emf.common.util.URI;

import com.google.common.collect.Sets;

public class MergeBoundedContextsRefactoring extends AbstractRefactoring implements SemanticCMLRefactoring {

	private String boundedContext1;
	private String boundedContext2;
	private boolean takeAttributesFromSecondBoundedContext = false;

	public MergeBoundedContextsRefactoring(String boundedContext1, String boundedContext2) {
		this.boundedContext1 = boundedContext1;
		this.boundedContext2 = boundedContext2;
	}

	public MergeBoundedContextsRefactoring(String boundedContext1, String boundedContext2, boolean takeAttributesFromSecondBoundedContext) {
		this(boundedContext1, boundedContext2);
		this.takeAttributesFromSecondBoundedContext = takeAttributesFromSecondBoundedContext;
	}

	@Override
	protected void doRefactor() {
		// do nothing if the two parameters point to same BC
		if (boundedContext1.equals(boundedContext2))
			return;

		Optional<BoundedContext> optionalBC1 = getAllBoundedContexts().stream().filter(bc -> bc.getName().equals(boundedContext1)).findFirst();
		Optional<BoundedContext> optionalBC2 = getAllBoundedContexts().stream().filter(bc -> bc.getName().equals(boundedContext2)).findFirst();

		// do nothing if one of the BCs does not exist
		if (!optionalBC1.isPresent() || !optionalBC2.isPresent())
			return;

		BoundedContext bc1 = optionalBC1.get();
		BoundedContext bc2 = optionalBC2.get();

		// inverse merging, if requested
		if (takeAttributesFromSecondBoundedContext) {
			bc1 = optionalBC2.get();
			bc2 = optionalBC1.get();
		}

		// move content from BC2 to BC1
		addElementsToEList(bc1.getAggregates(), bc2.getAggregates());
		addElementsToEList(bc1.getModules(), bc2.getModules());
		bc1.getImplementedDomainParts().addAll(bc2.getImplementedDomainParts());
		if (bc1.getType().equals(BoundedContextType.TEAM))
			bc1.getRealizedBoundedContexts().addAll(bc2.getRealizedBoundedContexts());
		bc1.getResponsibilities().addAll(bc2.getResponsibilities());
		bc1.setImplementationTechnology(mergeImplementationTechnologies(bc1.getImplementationTechnology(), bc2.getImplementationTechnology()));

		// remove BC2
		handleContextMapChanges(bc1, bc2);
		handleImportsToRemovedBC(bc1, bc2);
		ContextMappingModel bc2Model = getResource(bc2).getContextMappingModel();
		removeElementFromEList(bc2Model.getBoundedContexts(), bc2);
		bc2Model.eAllContents();

		// create comment if BC2 file is empty
		if (bc2Model.getMap() == null && bc2Model.getBoundedContexts().isEmpty())
			bc2Model.setTopComment("// Due to the application of 'Merge Bounded Contexts' this file no longer contains any Bounded Contexts.");
	}

	private String mergeImplementationTechnologies(String implementationTechnology1, String implementationTechnology2) {
		if (implementationTechnology1 == null && implementationTechnology2 == null)
			return null;

		Set<String> implementationTechnologies = Sets.newHashSet();
		if (implementationTechnology1 != null && !"".equals(implementationTechnology1))
			implementationTechnologies.add(implementationTechnology1);
		if (implementationTechnology2 != null && !"".equals(implementationTechnology2))
			implementationTechnologies.add(implementationTechnology2);
		return String.join(", ", implementationTechnologies);
	}

	private void handleContextMapChanges(BoundedContext mergedBC, BoundedContext removedBC) {
		for (ContextMap map : getAllContextMaps()) {
			ContextMappingModelHelper helper = new ContextMappingModelHelper(map);
			List<Relationship> relationshipsToRemove = helper.findAnyRelationshipsBetweenTwoContexts(mergedBC, removedBC);
			map.getRelationships().removeAll(relationshipsToRemove);
			helper.replaceBCInAllRelationships(removedBC, mergedBC);

			if (map.getBoundedContexts().stream().map(bc -> bc.getName()).collect(Collectors.toSet()).contains(removedBC.getName())) {
				BoundedContext bcToRemove = map.getBoundedContexts().stream().filter(bc -> bc.getName().equals(removedBC.getName())).findFirst().get();
				removeElementFromEList(map.getBoundedContexts(), bcToRemove);
			}
		}
	}

	private void handleImportsToRemovedBC(BoundedContext mergedBC, BoundedContext removedBC) {
		CMLResourceContainer mergedBCResource = getResource(mergedBC);
		CMLResourceContainer removedBCResource = getResource(removedBC);
		URI mergedBCURI = mergedBCResource.getResource().getURI();
		URI removedBCURI = removedBCResource.getResource().getURI();

		// precondition: if BCs are specified in same resource, imports are okay
		if (mergedBCResource.getResource().getURI().toString().equals(removedBCResource.getResource().getURI().toString()))
			return;

		// create import to removed BC resource
		Import importToRemovedBCResource = ContextMappingDSLFactory.eINSTANCE.createImport();
		importToRemovedBCResource.setImportURI(removedBCURI.toString());
		mergedBCResource.getContextMappingModel().getImports().add(importToRemovedBCResource);
		
		for (CMLResourceContainer resource : additionalResourcesToCheck) {
			Set<URI> importedURIs = resource.getContextMappingModel().getImports().stream().map(i -> URI.createURI(i.getImportURI()).resolve(resource.getResource().getURI()))
					.collect(Collectors.toSet());

			if (mergedBCURI.toString().equals(resource.getResource().getURI().toString()))
				continue;

			if (importedURIs.contains(removedBCURI) && !importedURIs.contains(mergedBCURI)) {
				Import importToAdd = ContextMappingDSLFactory.eINSTANCE.createImport();
				importToAdd.setImportURI(mergedBCURI.toString());
				resource.getContextMappingModel().getImports().add(importToAdd);
			}
		}
	}

}

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
package org.contextmapper.dsl.refactoring;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.DomainEvent;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Maps;

import ch.hsr.servicecutter.solver.SolverAlgorithm;

/**
 * This refactoring can be applied after having generated a service cut
 * suggestion with Service Cutter. It allows to extract a suggested service in
 * the original CML model.
 */
public class ExtractSuggestedService extends AbstractRefactoring implements SemanticCMLRefactoring {

	public static final String SERVICE_CUTTER_SUGGESTION_FILE_NAME_PATTERN = "(^.*)_("
			+ String.join("|", Arrays.asList(SolverAlgorithm.values()).stream().map(a -> a.toString().replace(" ", "_")).collect(Collectors.toList())) + ")_Cut_[0-9].cml";

	private BoundedContext suggestedService;
	private String newBoundedContextName;

	private Map<String, DomainObject> domainObjectMap;

	public ExtractSuggestedService(BoundedContext suggestedService, String newBoundedContextName) {
		this.suggestedService = suggestedService;
		checkPreconditions();
		this.newBoundedContextName = newBoundedContextName;
		this.domainObjectMap = Maps.newHashMap();
	}

	@Override
	protected void doRefactor() {
		BoundedContext bc = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		bc.setName(newBoundedContextName);
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		aggregate.setName(newBoundedContextName + "Aggregate");
		addElementToEList(bc.getAggregates(), aggregate);

		List<Attribute> allAttributesInSuggestedService = EcoreUtil2.eAllOfType(suggestedService, Attribute.class);
		List<Reference> allReferencesInSuggestedService = EcoreUtil2.eAllOfType(suggestedService, Reference.class);
		if (allAttributesInSuggestedService.isEmpty() && allReferencesInSuggestedService.isEmpty())
			throw new ContextMapperApplicationException(
					"This suggested service (Bounded Context) does not contain any Entities with data fields. We cannot extract such an empty Bounded Context. Please choose suggested Bounded Contexts that are not empty.");

		for (Attribute serviceCutAttribute : allAttributesInSuggestedService) {
			Attribute attribute = findAttributeInOriginalModel(serviceCutAttribute);
			if (attribute != null)
				moveAttribute(attribute, aggregate);
		}
		for (Reference serviceCutReference : allReferencesInSuggestedService) {
			Reference reference = findReferenceInOriginalModel(serviceCutReference);
			if (reference != null)
				moveReference(reference, aggregate);
		}

		model.getBoundedContexts().add(bc);
	}

	private void moveAttribute(Attribute attribute, Aggregate newBCAggregate) {
		DomainObject parentObject = (DomainObject) attribute.eContainer();

		// create attribute in new domain object
		DomainObject newDomainObject = getNewDomainObject(parentObject, newBCAggregate);
		addElementToEList(newDomainObject.getAttributes(), EcoreUtil2.copy(attribute));

		// remove attribute from original domain object
		removeElementFromEList(parentObject.getAttributes(), attribute);
	}

	private void moveReference(Reference reference, Aggregate newBCAggregate) {
		DomainObject parentObject = (DomainObject) reference.eContainer();

		// create attribute in new domain object
		DomainObject newDomainObject = getNewDomainObject(parentObject, newBCAggregate);
		addElementToEList(newDomainObject.getReferences(), EcoreUtil2.copy(reference));

		// remove attribute from original domain object
		removeElementFromEList(parentObject.getReferences(), reference);
	}

	private Attribute findAttributeInOriginalModel(Attribute serviceCutAttribute) {
		if (!(serviceCutAttribute.eContainer() instanceof DomainObject))
			throw new ContextMapperApplicationException(
					"We unexpectedly found attributes that are not contained by 'DomainObject' objects. We currently only support Entities, ValueObjects and Events here.");

		String domainObjectName = ((DomainObject) serviceCutAttribute.eContainer()).getName();
		String attributeName = serviceCutAttribute.getName();

		for (Attribute attribute : EcoreUtil2.eAllOfType(model, Attribute.class).stream().filter(a -> a.getName().equals(attributeName)).collect(Collectors.toList())) {
			if (attribute.eContainer() instanceof DomainObject && ((DomainObject) attribute.eContainer()).getName().equals(domainObjectName))
				return attribute;
		}

		return null;
	}

	private Reference findReferenceInOriginalModel(Reference serviceCutReference) {
		if (!(serviceCutReference.eContainer() instanceof DomainObject))
			throw new ContextMapperApplicationException(
					"We unexpectedly found references that are not contained by 'DomainObject' objects. We currently only support Entities, ValueObjects and Events here.");

		String domainObjectName = ((DomainObject) serviceCutReference.eContainer()).getName();
		String attributeName = serviceCutReference.getName();

		for (Reference reference : EcoreUtil2.eAllOfType(model, Reference.class).stream().filter(r -> r.getName().equals(attributeName)).collect(Collectors.toList())) {
			if (reference.eContainer() instanceof DomainObject && ((DomainObject) reference.eContainer()).getName().equals(domainObjectName))
				return reference;
		}

		return null;
	}

	private DomainObject getNewDomainObject(DomainObject originalDomainObject, Aggregate newBCAggregate) {
		if (this.domainObjectMap.containsKey(originalDomainObject.getName()))
			return this.domainObjectMap.get(originalDomainObject.getName());

		DomainObject domainObject = TacticdslFactory.eINSTANCE.createEntity();
		if (originalDomainObject instanceof ValueObject)
			domainObject = TacticdslFactory.eINSTANCE.createValueObject();
		if (originalDomainObject instanceof DomainEvent)
			domainObject = TacticdslFactory.eINSTANCE.createDomainEvent();
		domainObject.setName(getUniqueDomainObjectName(originalDomainObject.getName()));
		addElementToEList(newBCAggregate.getDomainObjects(), domainObject);
		this.domainObjectMap.put(originalDomainObject.getName(), domainObject);
		return domainObject;
	}

	private String getUniqueDomainObjectName(String inputName) {
		String name = inputName;
		int counter = 2;
		Set<String> existingNames = EcoreUtil2.eAllOfType(model, DomainObject.class).stream().map(o -> o.getName()).collect(Collectors.toSet());
		while (existingNames.contains(name)) {
			name = inputName + "_" + counter;
			counter++;
		}
		return name;
	}

	public URI constructOriginalModelUri() {
		Resource currentResource = suggestedService.eResource();
		String fileName = currentResource.getURI().lastSegment();
		if (!fileName.matches(SERVICE_CUTTER_SUGGESTION_FILE_NAME_PATTERN))
			throw new ContextMapperApplicationException("The given file name (" + fileName
					+ ") does not match with the pattern generated by Service Cutter. Please do not rename files generated by Service Cutter. This is how we identify whether a model is a decomposition suggestion or not.");
		Pattern r = Pattern.compile(SERVICE_CUTTER_SUGGESTION_FILE_NAME_PATTERN);
		Matcher m = r.matcher(fileName);
		m.find();
		return currentResource.getURI().trimFileExtension().trimSegments(1).appendSegment(m.group(1)).appendFileExtension("cml");
	}

	private void checkPreconditions() {
		if (suggestedService.eResource() == null)
			throw new ContextMapperApplicationException("The given Bounded Context is not part of a persisted CML resource. Only Bounded Contexts loaded from a persisted CML file are allowed here.");
		constructOriginalModelUri();
	}

}

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
package org.contextmapper.dsl.quickfixes.tactic;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.quickfixes.CMLQuickFix;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.CollectionType;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.Reference;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;

public class ExtractIDValueObjectQuickFix implements CMLQuickFix<Attribute> {

	@Override
	public void applyQuickfix(Attribute attribute) {
		if (!(attribute.eContainer() instanceof DomainObject))
			throw new ContextMapperApplicationException("This Quickfix is not applicable on the selected object.");

		DomainObject parentObject = (DomainObject) attribute.eContainer();
		ValueObject vo = createValueObject(attribute);
		addVOToContainer(parentObject, vo);
		fixReference(attribute, parentObject, vo);
	}

	private void addVOToContainer(DomainObject object, ValueObject vo) {
		if (object.eContainer() instanceof Aggregate) {
			((Aggregate) object.eContainer()).getDomainObjects().add(vo);
		} else if (object.eContainer() instanceof SculptorModule) {
			((SculptorModule) object.eContainer()).getDomainObjects().add(vo);
		}
	}

	private ValueObject createValueObject(Attribute attribute) {
		ValueObject vo = TacticdslFactory.eINSTANCE.createValueObject();
		String voName = attribute.getName().substring(0, 1).toUpperCase() + attribute.getName().substring(1);
		vo.setName(voName);
		Attribute idAttribute = TacticdslFactory.eINSTANCE.createAttribute();
		idAttribute.setName("id");
		idAttribute.setType(attribute.getType());
		vo.getAttributes().add(attribute);
		return vo;
	}

	private void fixReference(Attribute originalAttribute, DomainObject object, ValueObject vo) {
		object.getAttributes().remove(originalAttribute);
		Reference reference = TacticdslFactory.eINSTANCE.createReference();
		reference.setName(originalAttribute.getName());
		reference.setCollectionType(CollectionType.NONE);
		reference.setDomainObjectType(vo);
		object.getReferences().add(reference);
	}

	@Override
	public String getName() {
		return "Extract Value Object";
	}

	@Override
	public String getDescription() {
		return "Extracts a Value Object for the given identifier attribute.";
	}

}

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
package org.contextmapper.dsl.quickfixes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.quickfixes.tactic.ExtractIDValueObjectQuickFix;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;
import org.eclipse.xtext.EcoreUtil2;
import org.junit.jupiter.api.Test;

public class ExtractIDValueObjectQuickFixTest extends AbstractQuickFixTest {

	@Test
	public void canExtractValueObjectFromPrimitiveAttribute() throws IOException {
		// given
		CMLResourceContainer cmlResource = getResourceCopyOfTestCML("extract-vo-for-primitive-id-test-1.cml");
		ContextMappingModel model = cmlResource.getContextMappingModel();
		Attribute idAttr = EcoreUtil2.getAllContentsOfType(model, Attribute.class).stream().filter(a -> a.getName().equals("customerId")).findFirst().get();

		// when
		new ExtractIDValueObjectQuickFix().applyQuickfix(idAttr);

		// then
		Aggregate aggregate = model.getBoundedContexts().get(0).getAggregates().get(0);
		assertEquals("TestAggregate", aggregate.getName());
		assertEquals(2, aggregate.getDomainObjects().size());
		Entity entity = (Entity) aggregate.getDomainObjects().stream().filter(o -> o.getName().equals("Customer")).findFirst().get();
		assertEquals(2, entity.getAttributes().size());
		assertEquals(1, entity.getReferences().size());
		ValueObject vo = (ValueObject) aggregate.getDomainObjects().stream().filter(o -> o.getName().equals("CustomerId")).findFirst().get();
		assertNotNull(vo);
		assertEquals(1, vo.getAttributes().size());
		assertEquals("id", vo.getAttributes().get(0).getName());
	}

	@Test
	public void canExtractValueObjectFromPrimitiveAttributeInModule() throws IOException {
		// given
		CMLResourceContainer cmlResource = getResourceCopyOfTestCML("extract-vo-for-primitive-id-test-2.cml");
		ContextMappingModel model = cmlResource.getContextMappingModel();
		Attribute idAttr = EcoreUtil2.getAllContentsOfType(model, Attribute.class).stream().filter(a -> a.getName().equals("customerId")).findFirst().get();

		// when
		new ExtractIDValueObjectQuickFix().applyQuickfix(idAttr);

		// then
		SculptorModule module = model.getBoundedContexts().get(0).getModules().get(0);
		assertEquals("TestModule", module.getName());
		assertEquals(2, module.getDomainObjects().size());
		Entity entity = (Entity) module.getDomainObjects().stream().filter(o -> o.getName().equals("Customer")).findFirst().get();
		assertEquals(2, entity.getAttributes().size());
		assertEquals(1, entity.getReferences().size());
		ValueObject vo = (ValueObject) module.getDomainObjects().stream().filter(o -> o.getName().equals("CustomerId")).findFirst().get();
		assertNotNull(vo);
		assertEquals(1, vo.getAttributes().size());
		assertEquals("id", vo.getAttributes().get(0).getName());
	}

	@Test
	public void canCheckThatAttributeIsContainedInDomainObject() {
		// given
		Attribute attr = TacticdslFactory.eINSTANCE.createAttribute();
		attr.setName("singleAttr");

		// when, then
		assertThrows(ContextMapperApplicationException.class, () -> {
			new ExtractIDValueObjectQuickFix().applyQuickfix(attr);
		});
	}

	@Test
	public void canProvideName() {
		// given
		ExtractIDValueObjectQuickFix quickFix = new ExtractIDValueObjectQuickFix();

		// when
		String name = quickFix.getName();

		// then
		assertEquals("Extract Value Object", name);
	}

	@Test
	public void canProvideDescription() {
		// given
		ExtractIDValueObjectQuickFix quickFix = new ExtractIDValueObjectQuickFix();

		// when
		String description = quickFix.getDescription();

		// then
		assertEquals("Extracts a Value Object for the given identifier attribute.", description);
	}

}

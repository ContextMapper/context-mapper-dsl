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
package org.contextmapper.dsl.cml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;

import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.UseCase;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CMLModelObjectsResolvingHelperTest extends AbstractCMLInputFileTest {

	@ParameterizedTest
	@ValueSource(strings = { "TestEntity1", "TestEntity2", "TestEntity4" })
	public void canResolveBoundedContextThatContainsDomainObject(String domainObjectName) throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("bc-resolving-test-1.cml");
		DomainObject domainObject = EcoreUtil2.eAllOfType(input.getContextMappingModel(), DomainObject.class).stream().filter(o -> o.getName().equals(domainObjectName)).findFirst()
				.get();

		// when
		CMLModelObjectsResolvingHelper helper = new CMLModelObjectsResolvingHelper(input.getContextMappingModel());
		BoundedContext bc = helper.resolveBoundedContext(domainObject);

		// then
		assertEquals("TestContext", bc.getName());
	}

	@Test
	public void canReturnNullInCaseDomainObjectIsNotPartOfBC() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("bc-resolving-test-1.cml");
		DomainObject domainObject = EcoreUtil2.eAllOfType(input.getContextMappingModel(), DomainObject.class).stream().filter(o -> o.getName().equals("TestEntity3")).findFirst()
				.get();

		// when
		CMLModelObjectsResolvingHelper helper = new CMLModelObjectsResolvingHelper(input.getContextMappingModel());
		BoundedContext bc = helper.resolveBoundedContext(domainObject);

		// then
		assertNull(bc);
	}

	@Test
	public void canResolveAggregate() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		Entity subObject = TacticdslFactory.eINSTANCE.createEntity();
		BoundedContext bc = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		aggregate.getDomainObjects().add(subObject);
		bc.getAggregates().add(aggregate);
		model.getBoundedContexts().add(bc);

		// when
		CMLModelObjectsResolvingHelper helper = new CMLModelObjectsResolvingHelper(model);
		Aggregate direct = helper.resolveAggregate(aggregate);
		Aggregate sub = helper.resolveAggregate(subObject);
		Aggregate noContainingObject = helper.resolveAggregate(bc);

		// then
		assertEquals(aggregate, direct);
		assertEquals(aggregate, sub);
		assertNull(noContainingObject);
	}

	@Test
	public void canResolveBoundedContext() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
		Entity subObject = TacticdslFactory.eINSTANCE.createEntity();
		BoundedContext bc = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		aggregate.getDomainObjects().add(subObject);
		bc.getAggregates().add(aggregate);
		model.getBoundedContexts().add(bc);
		UseCase useCase = ContextMappingDSLFactory.eINSTANCE.createUseCase();
		model.getUserRequirements().add(useCase);

		// when
		CMLModelObjectsResolvingHelper helper = new CMLModelObjectsResolvingHelper(model);
		BoundedContext direct = helper.resolveBoundedContext((EObject) bc);
		BoundedContext sub = helper.resolveBoundedContext((EObject) aggregate);
		BoundedContext noContainingObject = helper.resolveBoundedContext((EObject) useCase);

		// then
		assertEquals(bc, direct);
		assertEquals(bc, sub);
		assertNull(noContainingObject);
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/common/";
	}

}

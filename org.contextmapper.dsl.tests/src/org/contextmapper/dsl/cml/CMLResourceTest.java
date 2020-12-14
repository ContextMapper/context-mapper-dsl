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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.contextmapper.dsl.cml.exception.ResourceIsNoCMLModelException;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CMLResourceTest {

	@Test
	public void canCreateCMLResource() {
		// given
		Resource resource = new ResourceImpl(URI.createURI("testresource"));

		// when
		resource.getContents().add(ContextMappingDSLFactory.eINSTANCE.createContextMappingModel());
		CMLResource cmlRes = new CMLResource(resource);

		// then
		assertNotNull(cmlRes);
	}

	@Test
	public void canGetContextMappingModel() {
		// given
		Resource resource = new ResourceImpl(URI.createURI("testresource"));

		// when
		resource.getContents().add(ContextMappingDSLFactory.eINSTANCE.createContextMappingModel());
		CMLResource cmlRes = new CMLResource(resource);
		ContextMappingModel contextMappingModel = cmlRes.getContextMappingModel();

		// then
		assertNotNull(contextMappingModel);
		assertTrue(contextMappingModel instanceof ContextMappingModel);
	}

	@Test
	public void canHandleEmptyResource() {
		// given
		Resource resource = new ResourceImpl(URI.createURI("testresource"));

		// when, then
		Assertions.assertThrows(ResourceIsNoCMLModelException.class, () -> {
			new CMLResource(resource);
		});
	}

	@Test
	public void canHandleEmptyResourceWithoutURI() {
		// given
		Resource resource = new ResourceImpl();

		// when, then
		Assertions.assertThrows(ResourceIsNoCMLModelException.class, () -> {
			new CMLResource(resource);
		});
	}

	@Test
	public void canHandleOtherModelType() {
		// given
		Resource resource = new ResourceImpl(URI.createURI("testresource"));

		// when
		resource.getContents().add(TacticdslFactory.eINSTANCE.createTacticDDDApplication());

		// then
		Assertions.assertThrows(ResourceIsNoCMLModelException.class, () -> {
			new CMLResource(resource);
		});
	}

	@Test
	public void isNotEqualToObjectOfOtherType() {
		// given
		Resource resource = new ResourceImpl(URI.createURI("testresource"));
		resource.getContents().add(ContextMappingDSLFactory.eINSTANCE.createContextMappingModel());
		CMLResource cmlRes = new CMLResource(resource);

		// when
		boolean isEqual = cmlRes.equals(new Object());

		// then
		assertFalse(isEqual);
	}

	@Test
	public void isEqualIfResourceURIIsEqual() {
		// given
		Resource resource1 = new ResourceImpl(URI.createURI("testresource"));
		resource1.getContents().add(ContextMappingDSLFactory.eINSTANCE.createContextMappingModel());
		CMLResource cmlRes1 = new CMLResource(resource1);

		Resource resource2 = new ResourceImpl(URI.createURI("testresource"));
		resource2.getContents().add(ContextMappingDSLFactory.eINSTANCE.createContextMappingModel());
		CMLResource cmlRes2 = new CMLResource(resource2);

		// when
		boolean isEqual = cmlRes1.equals(cmlRes2);

		// then
		assertTrue(isEqual);
	}

	@Test
	public void isNotEqualIfResourceURIIsNotEqual() {
		// given
		Resource resource1 = new ResourceImpl(URI.createURI("testresource1"));
		resource1.getContents().add(ContextMappingDSLFactory.eINSTANCE.createContextMappingModel());
		CMLResource cmlRes1 = new CMLResource(resource1);

		Resource resource2 = new ResourceImpl(URI.createURI("testresource2"));
		resource2.getContents().add(ContextMappingDSLFactory.eINSTANCE.createContextMappingModel());
		CMLResource cmlRes2 = new CMLResource(resource2);

		// when
		boolean isEqual = cmlRes1.equals(cmlRes2);

		// then
		assertFalse(isEqual);
	}

}

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
package org.contextmapper.dsl.tests.generators.contextmap;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.contextmapper.contextmap.generator.model.BoundedContext;
import org.contextmapper.contextmap.generator.model.ContextMap;
import org.contextmapper.contextmap.generator.model.DownstreamPatterns;
import org.contextmapper.contextmap.generator.model.Partnership;
import org.contextmapper.contextmap.generator.model.SharedKernel;
import org.contextmapper.contextmap.generator.model.UpstreamDownstreamRelationship;
import org.contextmapper.contextmap.generator.model.UpstreamPatterns;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.contextmap.ContextMapModelConverter;
import org.contextmapper.dsl.tests.AbstractCMLInputFileTest;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Iterators;

public class ContextMapModelConverterTest extends AbstractCMLInputFileTest {

	@Test
	public void canConvertContextMap() throws IOException {
		// given
		String inputModelName = "test-context-map-1.cml";
		Resource input = getResourceCopyOfTestCML(inputModelName);
		List<ContextMappingModel> models = IteratorExtensions.<ContextMappingModel>toList(Iterators.<ContextMappingModel>filter(input.getAllContents(), ContextMappingModel.class));
		ContextMappingModel model = models.get(0);

		// when
		ContextMap contextMap = new ContextMapModelConverter().convert(model.getMap());

		// then
		assertEquals(6, contextMap.getBoundedContexts().size());
		assertEquals(5, contextMap.getRelationships().size());

		assertTrue(contextMap.getBoundedContexts().contains(new BoundedContext("CustomerManagementContext")));
		assertTrue(contextMap.getBoundedContexts().contains(new BoundedContext("CustomerSelfServiceContext")));
		assertTrue(contextMap.getBoundedContexts().contains(new BoundedContext("PrintingContext")));
		assertTrue(contextMap.getBoundedContexts().contains(new BoundedContext("PolicyManagementContext")));
		assertTrue(contextMap.getBoundedContexts().contains(new BoundedContext("RiskManagementContext")));
		assertTrue(contextMap.getBoundedContexts().contains(new BoundedContext("DebtCollection")));

		UpstreamDownstreamRelationship rel1 = contextMap.getRelationships().stream().filter(r -> r instanceof UpstreamDownstreamRelationship)
				.map(r -> (UpstreamDownstreamRelationship) r).filter(r -> r.getUpstreamBoundedContext().getName().equals("CustomerManagementContext")
						&& r.getDownstreamBoundedContext().getName().equals("CustomerSelfServiceContext"))
				.findFirst().get();
		assertNotNull(rel1);
		assertTrue(rel1.isCustomerSupplier());

		UpstreamDownstreamRelationship rel2 = contextMap.getRelationships().stream().filter(r -> r instanceof UpstreamDownstreamRelationship)
				.map(r -> (UpstreamDownstreamRelationship) r)
				.filter(r -> r.getUpstreamBoundedContext().getName().equals("PrintingContext") && r.getDownstreamBoundedContext().getName().equals("CustomerManagementContext"))
				.findFirst().get();
		assertNotNull(rel2);
		assertFalse(rel2.isCustomerSupplier());
		assertTrue(rel2.getUpstreamPatterns().contains(UpstreamPatterns.OPEN_HOST_SERVICE));
		assertTrue(rel2.getUpstreamPatterns().contains(UpstreamPatterns.PUBLISHED_LANGUAGE));
		assertTrue(rel2.getDownstreamPatterns().contains(DownstreamPatterns.ANTICORRUPTION_LAYER));

		Partnership rel3 = contextMap.getRelationships().stream().filter(r -> r instanceof Partnership).map(r -> (Partnership) r)
				.filter(r -> r.getFirstParticipant().getName().equals("RiskManagementContext") && r.getSecondParticipant().getName().equals("PolicyManagementContext")).findFirst()
				.get();
		assertNotNull(rel3);

		UpstreamDownstreamRelationship rel4 = contextMap.getRelationships().stream().filter(r -> r instanceof UpstreamDownstreamRelationship)
				.map(r -> (UpstreamDownstreamRelationship) r).filter(r -> r.getUpstreamBoundedContext().getName().equals("CustomerManagementContext")
						&& r.getDownstreamBoundedContext().getName().equals("PolicyManagementContext"))
				.findFirst().get();
		assertNotNull(rel4);
		assertFalse(rel4.isCustomerSupplier());
		assertTrue(rel4.getUpstreamPatterns().contains(UpstreamPatterns.OPEN_HOST_SERVICE));
		assertTrue(rel4.getDownstreamPatterns().contains(DownstreamPatterns.CONFORMIST));

		SharedKernel rel5 = contextMap.getRelationships().stream().filter(r -> r instanceof SharedKernel).map(r -> (SharedKernel) r)
				.filter(r -> r.getFirstParticipant().getName().equals("PolicyManagementContext") && r.getSecondParticipant().getName().equals("DebtCollection")).findFirst().get();
		assertNotNull(rel5);
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/contextmapgenerator/";
	}

}

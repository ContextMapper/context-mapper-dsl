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
package org.contextmapper.dsl.generators.contextmap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.contextmapper.contextmap.generator.model.BoundedContext;
import org.contextmapper.contextmap.generator.model.BoundedContextType;
import org.contextmapper.contextmap.generator.model.ContextMap;
import org.contextmapper.contextmap.generator.model.DownstreamPatterns;
import org.contextmapper.contextmap.generator.model.Partnership;
import org.contextmapper.contextmap.generator.model.SharedKernel;
import org.contextmapper.contextmap.generator.model.UpstreamDownstreamRelationship;
import org.contextmapper.contextmap.generator.model.UpstreamPatterns;
import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.generator.contextmap.ContextMapModelConverter;
import org.junit.jupiter.api.Test;

public class ContextMapModelConverterTest extends AbstractCMLInputFileTest {

	@Test
	public void canConvertContextMap() throws IOException {
		// given
		String inputModelName = "test-context-map-1.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);
		ContextMappingModel model = input.getContextMappingModel();

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

		UpstreamDownstreamRelationship rel1 = contextMap.getRelationships().stream().filter(r -> r instanceof UpstreamDownstreamRelationship).map(r -> (UpstreamDownstreamRelationship) r)
				.filter(r -> r.getUpstreamBoundedContext().getName().equals("CustomerManagementContext") && r.getDownstreamBoundedContext().getName().equals("CustomerSelfServiceContext")).findFirst()
				.get();
		assertNotNull(rel1);
		assertTrue(rel1.isCustomerSupplier());

		UpstreamDownstreamRelationship rel2 = contextMap.getRelationships().stream().filter(r -> r instanceof UpstreamDownstreamRelationship).map(r -> (UpstreamDownstreamRelationship) r)
				.filter(r -> r.getUpstreamBoundedContext().getName().equals("PrintingContext") && r.getDownstreamBoundedContext().getName().equals("CustomerManagementContext")).findFirst().get();
		assertNotNull(rel2);
		assertFalse(rel2.isCustomerSupplier());
		assertTrue(rel2.getUpstreamPatterns().contains(UpstreamPatterns.OPEN_HOST_SERVICE));
		assertTrue(rel2.getUpstreamPatterns().contains(UpstreamPatterns.PUBLISHED_LANGUAGE));
		assertTrue(rel2.getDownstreamPatterns().contains(DownstreamPatterns.ANTICORRUPTION_LAYER));

		Partnership rel3 = contextMap.getRelationships().stream().filter(r -> r instanceof Partnership).map(r -> (Partnership) r)
				.filter(r -> r.getFirstParticipant().getName().equals("RiskManagementContext") && r.getSecondParticipant().getName().equals("PolicyManagementContext")).findFirst().get();
		assertNotNull(rel3);

		UpstreamDownstreamRelationship rel4 = contextMap.getRelationships().stream().filter(r -> r instanceof UpstreamDownstreamRelationship).map(r -> (UpstreamDownstreamRelationship) r)
				.filter(r -> r.getUpstreamBoundedContext().getName().equals("CustomerManagementContext") && r.getDownstreamBoundedContext().getName().equals("PolicyManagementContext")).findFirst()
				.get();
		assertNotNull(rel4);
		assertFalse(rel4.isCustomerSupplier());
		assertTrue(rel4.getUpstreamPatterns().contains(UpstreamPatterns.OPEN_HOST_SERVICE));
		assertTrue(rel4.getDownstreamPatterns().contains(DownstreamPatterns.CONFORMIST));

		SharedKernel rel5 = contextMap.getRelationships().stream().filter(r -> r instanceof SharedKernel).map(r -> (SharedKernel) r)
				.filter(r -> r.getFirstParticipant().getName().equals("PolicyManagementContext") && r.getSecondParticipant().getName().equals("DebtCollection")).findFirst().get();
		assertNotNull(rel5);
	}

	@Test
	public void canConvertSimpleTeamMap() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("test-team-map-1.cml");
		ContextMappingModel model = input.getContextMappingModel();

		// when
		ContextMap contextMap = new ContextMapModelConverter().convert(model.getMap());

		// then
		assertEquals(1, contextMap.getRelationships().size());
		assertEquals(2, contextMap.getBoundedContexts().size());
		assertEquals(BoundedContextType.TEAM, contextMap.getBoundedContexts().stream().filter(bc -> bc.getName().equals("CustomersTeam")).findFirst().get().getType());
		assertEquals(BoundedContextType.TEAM, contextMap.getBoundedContexts().stream().filter(bc -> bc.getName().equals("ContractsTeam")).findFirst().get().getType());
	}

	@Test
	public void canConvertTeamMapWithRealizingRelationships() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("test-team-map-2.cml");
		ContextMappingModel model = input.getContextMappingModel();

		// when
		ContextMap contextMap = new ContextMapModelConverter().convert(model.getMap());

		// then
		assertEquals(2, contextMap.getRelationships().size());
		assertEquals(4, contextMap.getBoundedContexts().size());
		BoundedContext customersTeam = contextMap.getBoundedContexts().stream().filter(bc -> bc.getName().equals("CustomersTeam")).findFirst().get();
		BoundedContext contractsTeam = contextMap.getBoundedContexts().stream().filter(bc -> bc.getName().equals("ContractsTeam")).findFirst().get();
		BoundedContext customerContext = contextMap.getBoundedContexts().stream().filter(bc -> bc.getName().equals("CustomerManagementContext")).findFirst().get();
		BoundedContext policyContext = contextMap.getBoundedContexts().stream().filter(bc -> bc.getName().equals("PolicyManagementContext")).findFirst().get();
		assertEquals(BoundedContextType.TEAM, customersTeam.getType());
		assertEquals(BoundedContextType.TEAM, contractsTeam.getType());
		assertEquals(BoundedContextType.GENERIC, customerContext.getType());
		assertEquals(BoundedContextType.GENERIC, policyContext.getType());
		assertEquals(1, customersTeam.getRealizedBoundedContexts().size());
		assertEquals(1, contractsTeam.getRealizedBoundedContexts().size());
		assertEquals("CustomerManagementContext", customersTeam.getRealizedBoundedContexts().iterator().next().getName());
		assertEquals("PolicyManagementContext", contractsTeam.getRealizedBoundedContexts().iterator().next().getName());
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/contextmapgenerator/";
	}

}

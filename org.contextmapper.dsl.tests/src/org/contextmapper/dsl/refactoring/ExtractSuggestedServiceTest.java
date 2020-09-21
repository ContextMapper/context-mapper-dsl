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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.SculptorModule;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ExtractSuggestedServiceTest extends AbstractRefactoringTest {

	@Test
	public void canExtractServiceFromDDDSample() throws IOException {
		// given
		CMLResource model = getResourceCopyOfTestCML("extract-suggested-service-DDD-sample.cml");
		BoundedContext contextToExtract = getResourceCopyOfTestCML("extract-suggested-service-DDD-sample_Markov_Clustering_Cut_1.cml").getContextMappingModel().getBoundedContexts().stream()
				.filter(bc -> bc.getName().equals("Service_A")).findFirst().get();

		// when
		ExtractSuggestedService ar = new ExtractSuggestedService(contextToExtract, "ExtractedBC");
		ar.refactor(model);
		ar.persistChanges();
		model = reloadResource(model);

		// then
		assertEquals(2, model.getContextMappingModel().getBoundedContexts().size());
		BoundedContext newBC = model.getContextMappingModel().getBoundedContexts().stream().filter(bc -> bc.getName().equals("ExtractedBC")).findFirst().get();
		assertNotNull(newBC);
		assertEquals(1, newBC.getAggregates().size());
		Aggregate aggregate = newBC.getAggregates().get(0);
		assertNotNull(aggregate);
		assertEquals(10, aggregate.getDomainObjects().size());
	}

	@Test
	public void canExtractServiceUsingModules() throws IOException {
		// given
		CMLResource model = getResourceCopyOfTestCML("extract-suggested-service-with-modules-test.cml");
		BoundedContext contextToExtract = getResourceCopyOfTestCML("extract-suggested-service-with-modules-test_Markov_Clustering_Cut_1.cml").getContextMappingModel().getBoundedContexts().stream()
				.filter(bc -> bc.getName().equals("Service_A")).findFirst().get();

		// when
		ExtractSuggestedService ar = new ExtractSuggestedService(contextToExtract, "ExtractedBC");
		ar.refactor(model);
		ar.persistChanges();
		model = reloadResource(model);

		// then
		assertEquals(2, model.getContextMappingModel().getBoundedContexts().size());
		BoundedContext origBC = model.getContextMappingModel().getBoundedContexts().stream().filter(bc -> bc.getName().equals("Monolith")).findFirst().get();
		BoundedContext newBC = model.getContextMappingModel().getBoundedContexts().stream().filter(bc -> bc.getName().equals("ExtractedBC")).findFirst().get();
		assertNotNull(origBC);
		assertNotNull(newBC);
		assertEquals(2, origBC.getModules().size());
		assertEquals(1, newBC.getAggregates().size());
		SculptorModule mod1 = origBC.getModules().stream().filter(m -> m.getName().equals("Module1")).findFirst().get();
		assertNotNull(mod1);
		assertEquals(1, mod1.getDomainObjects().size());
	}

	@Test
	public void canThrowExceptionInCaseThereAreNoAttributes() throws IOException {
		// given
		CMLResource model = getResourceCopyOfTestCML("extract-suggested-service-empty-model-test.cml");
		BoundedContext contextToExtract = getResourceCopyOfTestCML("extract-suggested-service-DDD-sample_Markov_Clustering_Cut_1.cml").getContextMappingModel().getBoundedContexts().stream()
				.filter(bc -> bc.getName().equals("Service_P")).findFirst().get();

		// when, then
		assertThrows(ContextMapperApplicationException.class, () -> {
			new ExtractSuggestedService(contextToExtract, "Service_P").refactor(model);
		});
	}

	@ParameterizedTest
	@ValueSource(strings = { "extract-suggested-service-unsupported-type-test-1", "extract-suggested-service-unsupported-type-test-2" })
	public void canThrowException4UnsupportedType(String baseTestFileName) throws IOException {
		// given
		CMLResource model = getResourceCopyOfTestCML(baseTestFileName + ".cml");
		BoundedContext contextToExtract = getResourceCopyOfTestCML(baseTestFileName + "_Markov_Clustering_Cut_1.cml").getContextMappingModel().getBoundedContexts().stream()
				.filter(bc -> bc.getName().equals("Service_A")).findFirst().get();

		// when, then
		assertThrows(ContextMapperApplicationException.class, () -> {
			new ExtractSuggestedService(contextToExtract, "Service_A").refactor(model);
		});
	}

	@Test
	public void canThrowExceptionIfCurrentResourceCannotBeFound() {
		// given
		BoundedContext testBC = ContextMappingDSLFactory.eINSTANCE.createBoundedContext(); // not persisted BC

		// when, then
		assertThrows(ContextMapperApplicationException.class, () -> {
			new ExtractSuggestedService(testBC, "ExtractedBC");
		});
	}

	@Test
	public void canThrowExceptionIfInputFileDoesNotMatchNamePattern() throws IOException {
		// given
		BoundedContext testContext = getResourceCopyOfTestCML("just-an-empty-model.cml").getContextMappingModel().getBoundedContexts().get(0); // there is one empty BC available

		// when, then
		assertThrows(ContextMapperApplicationException.class, () -> {
			new ExtractSuggestedService(testContext, "ExtractedBC");
		});
	}

}

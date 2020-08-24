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

import java.io.IOException;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.junit.jupiter.api.Test;

public class ExtractSuggestedServiceTest extends AbstractRefactoringTest {

	@Test
	public void canExtractServiceFromDDDSample() throws IOException {
		// given
		CMLResourceContainer model = getResourceCopyOfTestCML("extract-suggested-service-DDD-sample.cml");
		BoundedContext contextToExtract = getResourceCopyOfTestCML("extract-suggested-service-DDD-sample_Markov_Clustering_Cut_1.cml").getContextMappingModel().getBoundedContexts().stream()
				.filter(bc -> bc.getName().equals("Service_A")).findFirst().get();

		// when
		ExtractSuggestedService ar = new ExtractSuggestedService(contextToExtract, "ExtractedBC");
		ar.refactor(model);
		ar.persistChanges();
		model = reloadResource(model);

		// then
		assertEquals(2, model.getContextMappingModel().getBoundedContexts().size());
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

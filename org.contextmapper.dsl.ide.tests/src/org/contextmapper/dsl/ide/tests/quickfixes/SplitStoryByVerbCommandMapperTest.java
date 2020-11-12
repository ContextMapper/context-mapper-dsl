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
package org.contextmapper.dsl.ide.tests.quickfixes;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.Feature;
import org.contextmapper.dsl.contextMappingDSL.UseCase;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.ide.quickfix.impl.SplitStoryByVerbCommandMapper;
import org.contextmapper.dsl.quickfixes.SplitStoryByVerb;
import org.junit.jupiter.api.Test;

public class SplitStoryByVerbCommandMapperTest {

	@Test
	public void canThrowExceptionIfSelectedObjectIsNotAStory() {
		// given
		SplitStoryByVerbCommandMapper mapper = new SplitStoryByVerbCommandMapper(new SplitStoryByVerb());

		// when, then
		assertThrows(ContextMapperApplicationException.class, () -> {
			mapper.getCodeAction(null, ContextMappingDSLFactory.eINSTANCE.createUseCase());
		});
	}

	@Test
	public void canThrowExceptionIfSelectedFeatureIsNotInAStory() {
		// given
		SplitStoryByVerbCommandMapper mapper = new SplitStoryByVerbCommandMapper(new SplitStoryByVerb());
		UseCase useCase = ContextMappingDSLFactory.eINSTANCE.createUseCase();
		useCase.setName("TestCase");
		Feature feature = ContextMappingDSLFactory.eINSTANCE.createFeature();
		useCase.getFeatures().add(feature);

		// when, then
		assertThrows(ContextMapperApplicationException.class, () -> {
			mapper.getCodeAction(null, feature);
		});
	}

}

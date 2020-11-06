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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.Feature;
import org.contextmapper.dsl.contextMappingDSL.StoryFeature;
import org.contextmapper.dsl.contextMappingDSL.UserStory;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.junit.jupiter.api.Test;

public class SplitStoryByVerbTest {

	@Test
	public void canSplitStoryFeature() {
		// given
		UserStory story = ContextMappingDSLFactory.eINSTANCE.createUserStory();
		story.setName("TestStory");
		StoryFeature feature = ContextMappingDSLFactory.eINSTANCE.createStoryFeature();
		story.setRole("Tester"); // as a _
		feature.setVerb("create"); // I want to _
		feature.setEntity("UnitTest"); // a _
		story.getFeatures().add(feature);

		// when
		SplitStoryByVerb qf = new SplitStoryByVerb();
		qf.applyQuickfix(feature);

		// then
		assertEquals(2, story.getFeatures().size());
		Set<String> verbs = story.getFeatures().stream().map(f -> f.getVerb()).collect(Collectors.toSet());
		assertTrue(verbs.contains("create"));
		assertTrue(verbs.contains("\"{verb}\""));
	}

	@Test
	public void cannotApplyQuickFix4OtherType() {
		// given
		SplitStoryByVerb qf = new SplitStoryByVerb();

		// when, then
		assertThrows(ContextMapperApplicationException.class, () -> {
			qf.applyQuickfix2EObject(ContextMappingDSLFactory.eINSTANCE.createBoundedContext());
		});
	}

	@Test
	public void cannotApplyQuickFixIfFeatureIsNotEmbeddedInStory() {
		// given
		SplitStoryByVerb qf = new SplitStoryByVerb();

		// when, then
		assertThrows(ContextMapperApplicationException.class, () -> {
			qf.applyQuickfix2EObject(ContextMappingDSLFactory.eINSTANCE.createFeature());
		});
	}

	@Test
	public void canProvideName() {
		// given
		SplitStoryByVerb qf = new SplitStoryByVerb();

		// when
		String name = qf.getName();

		// then
		assertEquals("Split Story by Verb/Operation", name);
	}

	@Test
	public void canProvideDescription() {
		// given
		SplitStoryByVerb qf = new SplitStoryByVerb();

		// when
		String description = qf.getDescription();

		// then
		assertEquals("Splits a feature in a user story or use case by the verb/operation.", description);
	}

}

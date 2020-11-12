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

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Feature;
import org.contextmapper.dsl.contextMappingDSL.StoryFeature;
import org.contextmapper.dsl.contextMappingDSL.UserStory;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;

public class SplitStoryByVerbTest {

	@Test
	public void canSplitStoryFeature() {
		// given
		ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
		UserStory story = ContextMappingDSLFactory.eINSTANCE.createUserStory();
		story.setName("TestStory");
		StoryFeature feature = ContextMappingDSLFactory.eINSTANCE.createStoryFeature();
		story.setRole("Tester"); // as a _
		feature.setVerb("develop"); // I want to _
		feature.setEntity("UnitTest"); // a _
		story.getFeatures().add(feature);
		model.getUserRequirements().add(story);

		// when
		SplitStoryByVerb qf = new SplitStoryByVerb();
		qf.setVerbs(Sets.newHashSet(Arrays.asList(new String[] { "create", "run", "evolve" })));
		qf.applyQuickfix(feature);

		// then
		assertEquals(2, model.getUserRequirements().size());
		UserStory originalStory = (UserStory) model.getUserRequirements().get(0);
		UserStory splitStory = (UserStory) model.getUserRequirements().get(1);
		assertEquals("TestStory", originalStory.getName());
		assertEquals("TestStory_Split", splitStory.getName());
		assertEquals("TestStory_Split", originalStory.getSplittingStory().getName());
		assertEquals(3, splitStory.getFeatures().size());
		Set<String> verbs = splitStory.getFeatures().stream().map(f -> f.getVerb()).collect(Collectors.toSet());
		assertTrue(verbs.contains("create"));
		assertTrue(verbs.contains("\"run\""));
		assertTrue(verbs.contains("\"evolve\""));
	}

	@Test
	public void cannotApplyQuickFixIfStoryIsNotEmbeddedInCMLModel() {
		UserStory story = ContextMappingDSLFactory.eINSTANCE.createUserStory();
		story.setName("TestStory");
		StoryFeature feature = ContextMappingDSLFactory.eINSTANCE.createStoryFeature();
		story.setRole("Tester"); // as a _
		feature.setVerb("develop"); // I want to _
		feature.setEntity("UnitTest"); // a _
		story.getFeatures().add(feature);

		// when, then
		assertThrows(ContextMapperApplicationException.class, () -> {
			SplitStoryByVerb qf = new SplitStoryByVerb();
			qf.applyQuickfix(feature);
		});
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

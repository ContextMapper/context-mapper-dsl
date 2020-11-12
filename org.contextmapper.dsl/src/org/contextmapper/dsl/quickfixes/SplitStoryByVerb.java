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

import java.util.Arrays;
import java.util.Set;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Feature;
import org.contextmapper.dsl.contextMappingDSL.StoryFeature;
import org.contextmapper.dsl.contextMappingDSL.UserStory;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com.google.common.collect.Sets;

public class SplitStoryByVerb implements CMLQuickFix<Feature> {

	private Set<String> defaultVerbs = Sets.newHashSet(Arrays.asList(new String[] { "create", "read", "update", "delete" }));
	private Set<String> verbs = Sets.newHashSet(Arrays.asList(new String[] { "create", "read", "update", "delete" }));

	@Override
	public void applyQuickfix(Feature contextObject) {
		if (!(contextObject.eContainer() instanceof UserStory))
			throw new ContextMapperApplicationException("Cannot apply quickfix, as the provided feature is not embedded into to a user story or use case.");

		UserStory story = (UserStory) contextObject.eContainer();
		if (!(story.eContainer() instanceof ContextMappingModel))
			throw new ContextMapperApplicationException("Cannot apply quickfix, as the given story is not part of a CML model.");

		UserStory splitStory = EcoreUtil2.copy(story);
		splitStory.setName(story.getName() + "_Split");
		StoryFeature feature = (StoryFeature) splitStory.getFeatures().get(0);
		splitStory.getFeatures().clear();

		for (String verb : verbs) {
			StoryFeature newFeature = EcoreUtil2.copy(feature);
			if (defaultVerbs.contains(verb))
				newFeature.setVerb(verb);
			else
				newFeature.setVerb("\"" + verb + "\"");
			splitStory.getFeatures().add(newFeature);
		}

		ContextMappingModel model = (ContextMappingModel) story.eContainer();
		model.getUserRequirements().add(splitStory);
		story.setSplittingStory(splitStory);
	}

	@Override
	public void applyQuickfix2EObject(EObject contextObject) {
		if (contextObject instanceof Feature)
			applyQuickfix((Feature) contextObject);
		else
			throw new ContextMapperApplicationException("Cannot apply quickfix, as the given context object is no user story feature.");
	}

	public void setVerbs(Set<String> verbs) {
		this.verbs = Sets.newHashSet(verbs);
	}

	@Override
	public String getName() {
		return "Split Story by Verb/Operation";
	}

	@Override
	public String getDescription() {
		return "Splits a feature in a user story or use case by the verb/operation.";
	}

}

/*
 * Copyright 2023 The Context Mapper Project Team
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
package org.contextmapper.dsl.generator.plantuml;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.UseCase;
import org.contextmapper.dsl.contextMappingDSL.UserRequirement;

public class PlantUMLUseCaseDiagramCreator extends AbstractPlantUMLDiagramCreator<ContextMappingModel>
		implements PlantUMLDiagramCreator<ContextMappingModel> {

	private Set<String> useCasesAndStories;
	private Set<Actor> actors;
	private Set<Actor> secondaryActors;
	private int actorCounter = 0;

	@Override
	protected void printDiagramContent(ContextMappingModel model) {
		prepareData(model.getUserRequirements());

		printUseCases();
		linebreak();

		printActors(actors);
		printActors(secondaryActors);
		linebreak();

		sb.append("left to right direction");
		linebreak();
		printConnections(actors, false);
		printConnections(secondaryActors, true);
		linebreak();
	}

	private void printUseCases() {
		for (String useCase : useCasesAndStories) {
			sb.append("\"").append(useCase).append("\"").append(" as ").append("(").append(useCase).append(")");
			linebreak();
		}
	}

	private void printActors(final Set<Actor> actors) {
		for (Actor actor : actors) {
			sb.append("\"").append(actor.name).append("\"").append(" as ").append(actor.id);
			linebreak();
		}
	}

	private void printConnections(final Set<Actor> actors, final boolean secondaryActors) {
		for (Actor actor : actors) {
			printActorsConnections(actor, secondaryActors);
		}
	}

	private void printActorsConnections(final Actor actor, final boolean secondaryActor) {
		for (String useCase : actor.connectedUseCasesAndStories) {
			if (!secondaryActor) {
				sb.append(actor.id).append(" -- ").append(useCase);
			} else {
				sb.append(useCase).append(" -- ").append(actor.id);
			}
			linebreak();
		}
	}

	private void prepareData(final List<UserRequirement> userRequirements) {
		initializeDataStructures();
		for (UserRequirement req : userRequirements) {
			final Actor primaryActor = getActorForName(req.getRole().trim());
			useCasesAndStories.add(req.getName().trim());
			primaryActor.addUseCaseOrStory(req.getName().trim());
			if (req instanceof UseCase) {
				initSecondaryActors(((UseCase) req).getSecondaryActors(), req.getName().trim());
			}
		}
	}

	private void initSecondaryActors(final List<String> actorNames, final String useCaseOrStoryName) {
		for (String name : actorNames) {
			final Actor secondaryActor = getActorSecondaryForName(name.trim());
			secondaryActor.addUseCaseOrStory(useCaseOrStoryName);
		}
	}

	private void initializeDataStructures() {
		this.useCasesAndStories = new TreeSet<>();
		this.actors = new TreeSet<>();
		this.secondaryActors = new TreeSet<>();
		this.actorCounter = 0;
	}

	private Actor getActorForName(final String actorName) {
		return getActorForName(actorName, this.actors);
	}

	private Actor getActorSecondaryForName(final String actorName) {
		return getActorForName(actorName, this.secondaryActors);
	}

	private Actor getActorForName(final String actorName, final Set<Actor> actors) {
		final Optional<Actor> potentiallyExistingActor = actors.stream().filter(a -> a.name.equals(actorName))
				.findFirst();
		if (potentiallyExistingActor.isPresent()) {
			return potentiallyExistingActor.get();
		} else {
			final Actor actor = new Actor(actorName, "Actor_" + actorCounter++);
			actors.add(actor);
			return actor;
		}
	}

	private class Actor implements Comparable<Actor> {
		private String name;
		private String id;
		private Set<String> connectedUseCasesAndStories;

		private Actor(final String name, final String id) {
			this.name = name;
			this.id = id;
			this.connectedUseCasesAndStories = new TreeSet<>();
		}

		private void addUseCaseOrStory(final String useCaseOrStoryName) {
			this.connectedUseCasesAndStories.add(useCaseOrStoryName);
		}

		@Override
		public int compareTo(Actor otherActor) {
			return id.compareTo(otherActor.id);
		}

	}

}

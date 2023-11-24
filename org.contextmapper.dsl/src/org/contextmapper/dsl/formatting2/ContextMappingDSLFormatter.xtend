/*
 * Copyright 2018 The Context Mapper Project Team
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
package org.contextmapper.dsl.formatting2

import com.google.inject.Inject
import org.contextmapper.dsl.contextMappingDSL.Aggregate
import org.contextmapper.dsl.contextMappingDSL.Application
import org.contextmapper.dsl.contextMappingDSL.BoundedContext
import org.contextmapper.dsl.contextMappingDSL.CommandInvokationStep
import org.contextmapper.dsl.contextMappingDSL.ContextMap
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.contextMappingDSL.Domain
import org.contextmapper.dsl.contextMappingDSL.DomainEventProductionStep
import org.contextmapper.dsl.contextMappingDSL.EitherCommandOrOperation
import org.contextmapper.dsl.contextMappingDSL.Flow
import org.contextmapper.dsl.contextMappingDSL.Relationship
import org.contextmapper.dsl.contextMappingDSL.SculptorModule
import org.contextmapper.dsl.contextMappingDSL.Subdomain
import org.contextmapper.dsl.contextMappingDSL.UseCase
import org.contextmapper.dsl.contextMappingDSL.UserRequirement
import org.contextmapper.dsl.contextMappingDSL.UserStory
import org.contextmapper.dsl.services.ContextMappingDSLGrammarAccess
import org.contextmapper.tactic.dsl.formatting2.TacticDDDLanguageFormatter
import org.eclipse.xtext.formatting2.IFormattableDocument

class ContextMappingDSLFormatter extends TacticDDDLanguageFormatter {

	@Inject extension ContextMappingDSLGrammarAccess

	def dispatch void format(ContextMappingModel contextMappingModel, extension IFormattableDocument document) {
		var ignoredFirst = contextMappingModel.topComment !== null && !"".equals(contextMappingModel.topComment)
		for (cmlImport : contextMappingModel.imports) {
			if (ignoredFirst) {
				cmlImport.prepend[newLine]
			} else {
				ignoredFirst = true
			}
		}

		if (contextMappingModel.map !== null)
			contextMappingModel.map.format

		for (boundedContext : contextMappingModel.boundedContexts) {
			boundedContext.format
		}
		for (domain : contextMappingModel.domains) {
			domain.format
		}
		for (userRequirement : contextMappingModel.userRequirements) {
			userRequirement.format
		}
	}

	def dispatch void format(ContextMap contextMap, extension IFormattableDocument document) {
		interior(
			contextMap.regionFor.ruleCallTo(OPENRule).append[newLine],
			contextMap.regionFor.ruleCallTo(CLOSERule).prepend[newLine].append[newLines = 2]
		)[indent]

		var model = contextMap.eContainer as ContextMappingModel
		var hasTopComment = model.topComment !== null && !"".equals(model.topComment)

		if (hasTopComment)
			contextMap.regionFor.keyword("ContextMap").prepend[newLine]
		contextMap.regionFor.keyword("type").prepend[newLine]
		contextMap.regionFor.keyword("state").prepend[newLine]

		contextMap.regionFor.keywords('contains').forEach [
			prepend[newLines = 1]
		]

		for (relationship : contextMap.relationships) {
			relationship.format
		}
	}

	def dispatch void format(BoundedContext boundedContext, extension IFormattableDocument document) {
		interior(
			boundedContext.regionFor.ruleCallTo(OPENRule).append[newLine],
			boundedContext.regionFor.ruleCallTo(CLOSERule).prepend[newLine].append[newLines = 2]
		)[indent]

		boundedContext.prepend[newLines = 2]
		if (boundedContext.comment !== null && !"".equals(boundedContext.comment))
			boundedContext.regionFor.keyword('BoundedContext').prepend[newLine]

		boundedContext.regionFor.keyword("domainVisionStatement").prepend[newLine]
		boundedContext.regionFor.keyword("type").prepend[newLine]
		boundedContext.regionFor.keyword("responsibilities").prepend[newLine]
		boundedContext.regionFor.keyword("implementationTechnology").prepend[newLine]
		boundedContext.regionFor.keyword("knowledgeLevel").prepend[newLine]

		for (aggregate : boundedContext.aggregates) {
			aggregate.format
		}
		for (module : boundedContext.modules) {
			module.format
		}
		if (boundedContext.application !== null)
			boundedContext.application.format
	}

	def dispatch void format(Application application, extension IFormattableDocument document) {
		interior(
			application.regionFor.ruleCallTo(OPENRule).append[newLine],
			application.regionFor.ruleCallTo(CLOSERule).prepend[newLine].append[newLines = 2]
		)[indent]

		for (command : application.commands) {
			command.format
		}
		for (event : application.events) {
			event.format
		}
		for (service : application.services) {
			service.format
		}
		for (flow : application.flows) {
			flow.format
		}
	}

	def dispatch void format(Flow flow, extension IFormattableDocument document) {
		interior(
			flow.regionFor.ruleCallTo(OPENRule).append[newLine],
			flow.regionFor.ruleCallTo(CLOSERule).prepend[newLine].append[newLines = 2]
		)[indent]
		
		flow.regionFor.keyword('Flow').prepend[newLine]

		for (step : flow.steps) {
			step.format
		}
	}

	def dispatch void format(DomainEventProductionStep step, extension IFormattableDocument document) {
		step.action.format
	}

	def dispatch void format(CommandInvokationStep step, extension IFormattableDocument document) {
		step.regionFor.keyword("event").prepend[newLine]
	}

	def dispatch void format(EitherCommandOrOperation commandOrOperation, extension IFormattableDocument document) {
		commandOrOperation.regionFor.keyword("command").prepend[newLine]
		commandOrOperation.regionFor.keyword("operation").prepend[newLine]
	}

	def dispatch void format(Domain domain, extension IFormattableDocument document) {
		interior(
			domain.regionFor.ruleCallTo(OPENRule).append[newLine],
			domain.regionFor.ruleCallTo(CLOSERule).prepend[newLines = 2].append[newLines = 2]
		)[indent]

		domain.regionFor.keyword("domainVisionStatement").prepend[newLine]

		for (subdomain : domain.subdomains) {
			subdomain.format
		}
	}

	def dispatch void format(Subdomain subdomain, extension IFormattableDocument document) {
		interior(
			subdomain.regionFor.ruleCallTo(OPENRule).append[newLine],
			subdomain.regionFor.ruleCallTo(CLOSERule).prepend[newLines = 1].append[newLines = 2]
		)[indent]

		subdomain.regionFor.keyword("domainVisionStatement").prepend[newLine]
		subdomain.regionFor.keyword("type").prepend[newLine]

		for (entity : subdomain.entities) {
			entity.format
		}

		for (service : subdomain.services) {
			service.format
		}
	}

	def dispatch void format(UserRequirement requirement, extension IFormattableDocument document) {
		interior(
			requirement.regionFor.ruleCallTo(OPENRule).append[newLine],
			requirement.regionFor.ruleCallTo(CLOSERule).prepend[newLine].append[newLines = 2]
		)[indent]

		if (requirement instanceof UseCase) {
			requirement.regionFor.keyword("UseCase").prepend[newLines = 2]
			requirement.regionFor.keyword("actor").prepend[newLine]
			requirement.regionFor.keyword("interactions").prepend[newLine]
			requirement.regionFor.keyword("benefit").prepend[newLine]
			requirement.regionFor.keyword("scope").prepend[newLine]
			requirement.regionFor.keyword("level").prepend[newLine]

			for (feature : requirement.features) {
				feature.prepend[newLine]
			}
		}

		if (requirement instanceof UserStory) {
			requirement.regionFor.keyword("UserStory").prepend[newLines = 2]
			requirement.regionFor.keyword("As a").prepend[newLine]
			requirement.regionFor.keyword("As an").prepend[newLine]

			if (requirement.features.size > 1) {
				for (feature : requirement.features) {
					feature.prepend[newLine]
				}
				requirement.regionFor.keyword("so that").prepend[newLine]
			}
		}

		requirement.regionFor.keyword("isLatencyCritical").prepend[newLine]
		requirement.regionFor.keyword("reads").prepend[newLine]
		requirement.regionFor.keyword("writes").prepend[newLine]

		requirement.prepend[newLines = 2]
	}

	def dispatch void format(Relationship relationship, extension IFormattableDocument document) {
		interior(
			relationship.regionFor.ruleCallTo(OPENRule).append[newLine],
			relationship.regionFor.ruleCallTo(CLOSERule).prepend[newLine].append[newLines = 2]
		)[indent]

		relationship.regionFor.keyword('implementationTechnology').prepend[newLine]
		relationship.regionFor.keyword('exposedAggregates').prepend[newLine]
		relationship.regionFor.keyword('downstreamRights').prepend[newLine]

		relationship.regionFor.keyword('U').surround[noSpace]
		relationship.regionFor.keyword('D').surround[noSpace]
		relationship.regionFor.keyword('OHS').surround[noSpace]
		relationship.regionFor.keyword('PL').surround[noSpace]
		relationship.regionFor.keyword('ACL').surround[noSpace]
		relationship.regionFor.keyword('CF').surround[noSpace]
		relationship.regionFor.keywords('SK').forEach [
			surround[noSpace]
		]
		relationship.regionFor.keywords('P').forEach [
			surround[noSpace]
		]
		relationship.regionFor.keywords(',').forEach [
			prepend[noSpace]
			append[oneSpace]
		]

		relationship.prepend[newLines = 2]
	}

	def dispatch void format(Aggregate aggregate, extension IFormattableDocument document) {
		interior(
			aggregate.regionFor.ruleCallTo(OPENRule).append[newLine],
			aggregate.regionFor.ruleCallTo(CLOSERule).prepend[newLine].append[newLine]
		)[indent]

		aggregate.prepend[newLines = 1]
		aggregate.regionFor.keyword('Aggregate').prepend[newLine]

		aggregate.regionFor.keyword('responsibilities').prepend[newLine]
		aggregate.regionFor.keyword('owner').prepend[newLine]
		aggregate.regionFor.keyword('useCases').prepend[newLine]
		aggregate.regionFor.keyword('userRequirements').prepend[newLine]
		aggregate.regionFor.keyword('knowledgeLevel').prepend[newLine]
		aggregate.regionFor.keyword('likelihoodForChange').prepend[newLine]

		aggregate.comment.format

		for (domainObject : aggregate.domainObjects) {
			domainObject.format
		}

		for (service : aggregate.services) {
			service.format
		}
	}

	def dispatch void format(SculptorModule module, extension IFormattableDocument document) {
		interior(
			module.regionFor.ruleCallTo(OPENRule).append[newLine],
			module.regionFor.ruleCallTo(CLOSERule).prepend[newLine].append[newLine]
		)[indent]
		for (aggregate : module.aggregates) {
			aggregate.format
		}
	}

}

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
import org.contextmapper.dsl.contextMappingDSL.BoundedContext
import org.contextmapper.dsl.contextMappingDSL.ContextMap
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.contextMappingDSL.Relationship
import org.contextmapper.dsl.services.ContextMappingDSLGrammarAccess
import org.contextmapper.tactic.dsl.formatting2.TacticDDDLanguageFormatter
import org.contextmapper.tactic.dsl.tacticdsl.Aggregate
import org.contextmapper.tactic.dsl.tacticdsl.Entity
import org.eclipse.xtext.formatting2.IFormattableDocument

class ContextMappingDSLFormatter extends TacticDDDLanguageFormatter {

	@Inject extension ContextMappingDSLGrammarAccess

	def dispatch void format(ContextMappingModel contextMappingModel, extension IFormattableDocument document) {
		contextMappingModel.map.format

		for (boundedContext : contextMappingModel.boundedContexts) {
			boundedContext.format
		}
		for (subdomain : contextMappingModel.subdomains) {
			subdomain.format
		}
	}

	def dispatch void format(ContextMap contextMap, extension IFormattableDocument document) {
		interior(
			contextMap.regionFor.ruleCallTo(OPENRule).append[newLine],
			contextMap.regionFor.ruleCallTo(CLOSERule).prepend[newLine].append[newLine]
		)[indent]

		// new line for each bounded context reference
		var semanticRegion = contextMap.allRegionsFor.assignment(contextMapAccess.boundedContextsAssignment_4_1)
		for (var i = 0; i <= (contextMap.boundedContexts.size - 1); i++) {
			if (i == (contextMap.boundedContexts.size - 1)) {
				semanticRegion.append[newLine]
			} else {
				semanticRegion.append[newLine]
				semanticRegion = semanticRegion.nextSemanticRegion.nextSemanticRegion
			}
		}

		for (relationship : contextMap.relationships) {
			relationship.format
			relationship.prepend[newLine]
			relationship.append[newLine]
		}
		for (boundedContext : contextMap.boundedContexts) {
			boundedContext.format
			boundedContext.append[newLine]
		}
	}

	def dispatch void format(BoundedContext boundedContext, extension IFormattableDocument document) {
		interior(
			boundedContext.regionFor.ruleCallTo(OPENRule).append[newLine],
			boundedContext.regionFor.ruleCallTo(CLOSERule).prepend[newLine].append[newLine]
		)[indent]

		for (aggregate : boundedContext.aggregates) {
			aggregate.format
		}
	}

	def dispatch void format(Relationship relationship, extension IFormattableDocument document) {
		interior(
			relationship.regionFor.ruleCallTo(OPENRule).append[newLine],
			relationship.regionFor.ruleCallTo(CLOSERule).prepend[newLine].append[newLine]
		)[indent]
	}

}

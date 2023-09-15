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
package org.contextmapper.dsl.generator.plantuml;

import org.contextmapper.dsl.contextMappingDSL.SubDomainType;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.eclipse.xtext.EcoreUtil2;

import java.util.HashMap;

import com.google.common.collect.Lists;

public class PlantUMLSubdomainClassDiagramCreator extends AbstractPlantUMLClassDiagramCreator<Subdomain> implements PlantUMLDiagramCreator<Subdomain> {

	private String domainName;

	public PlantUMLSubdomainClassDiagramCreator(String domainName) {
		this.domainName = domainName;
	}

	@Override
	protected void printDiagramContent(Subdomain subdomain) {
		this.associationInfos = new HashMap<>();
		this.extensions = Lists.newArrayList();
		this.domainObjects = EcoreUtil2.<SimpleDomainObject>getAllContentsOfType(subdomain, SimpleDomainObject.class);

		printSubdomain(subdomain, 0);
		printReferences(0);
		printLegend(subdomain);
	}

	private void printSubdomain(Subdomain subdomain, int indentation) {
		printIndentation(indentation);
		sb.append("package ").append("\"'").append(subdomain.getName()).append("' ").append(getSubdomainTypeAsString(subdomain.getType())).append("\"").append(" <<Rectangle>> ")
				.append("{");
		linebreak();
		for (SimpleDomainObject domainObject : subdomain.getEntities()) {
			printDomainObject(domainObject, indentation + 1);
		}
		printIndentation(indentation);
		sb.append("}");
		linebreak();
	}

	private void printLegend(Subdomain subdomain) {
		sb.append("legend left");
		linebreak();
		sb.append("  This subdomain is part of the '" + domainName + "' domain.");
		linebreak();
		if (subdomain.getDomainVisionStatement() != null) {
			linebreak();
			sb.append("  ").append(subdomain.getDomainVisionStatement());
			linebreak();
		}
		sb.append("end legend");
		linebreak();
	}

	private String getSubdomainTypeAsString(SubDomainType type) {
		switch (type) {
		case CORE_DOMAIN:
			return "Core Domain";
		case GENERIC_SUBDOMAIN:
			return "Generic Subdomain";
		case SUPPORTING_DOMAIN:
			return "Supporting Domain";
		default:
			return "";
		}
	}

}

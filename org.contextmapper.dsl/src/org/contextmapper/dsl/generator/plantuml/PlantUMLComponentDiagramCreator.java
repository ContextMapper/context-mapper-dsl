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
package org.contextmapper.dsl.generator.plantuml;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.CustomerSupplierRelationship;
import org.contextmapper.dsl.contextMappingDSL.DownstreamRole;
import org.contextmapper.dsl.contextMappingDSL.Partnership;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.contextMappingDSL.SharedKernel;
import org.contextmapper.dsl.contextMappingDSL.SymmetricRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamRole;
import org.contextmapper.dsl.validation.ValidationMessages;
import org.eclipse.emf.common.util.EList;

public class PlantUMLComponentDiagramCreator extends AbstractPlantUMLDiagramCreator<ContextMap> implements PlantUMLDiagramCreator<ContextMap> {

	private Set<String> interfaceNames = new HashSet<>();
	private int interfaceCounter = 0;

	@Override
	protected void printDiagramContent(ContextMap contextMap) {
		if (contextMap.getBoundedContexts().size() <= 0) {
			printEmptyDiagramNote();
			return;
		}
		for (BoundedContext boundedContext : contextMap.getBoundedContexts()) {
			printComponent(boundedContext);
		}
		linebreak();
		for (Relationship relationship : contextMap.getRelationships()) {
			if (relationship instanceof Partnership) {
				printPartnershipRelationship((Partnership) relationship);
			} else if (relationship instanceof SharedKernel) {
				printSharedKernelRelationship((SharedKernel) relationship);
			} else if (relationship instanceof UpstreamDownstreamRelationship) {
				printUpstreamDownstreamRelationship((UpstreamDownstreamRelationship) relationship);
			}
		}

	}

	private void printEmptyDiagramNote() {
		sb.append("note").append(" ").append("\"").append(ValidationMessages.EMPTY_UML_COMPONENT_DIAGRAM_MESSAGE).append("\"").append(" as EmptyDiagramError");
		linebreak();
	}

	private void printPartnershipRelationship(Partnership relationship) {
		printSymmetricComponentRelationship(((Partnership) relationship).getParticipant1().getName(), ((Partnership) relationship).getParticipant2().getName(),
				getRelationshipLabel(relationship));
		linebreak();
	}

	private void printSharedKernelRelationship(SharedKernel relationship) {
		printSymmetricComponentRelationship(((SharedKernel) relationship).getParticipant1().getName(), ((SharedKernel) relationship).getParticipant2().getName(),
				getRelationshipLabel(relationship));
		linebreak();
	}

	private void printUpstreamDownstreamRelationship(UpstreamDownstreamRelationship relationship) {
		UpstreamDownstreamRelationship upDownRelationship = (UpstreamDownstreamRelationship) relationship;
		String interfaceId = getUniqueInterfaceId(upDownRelationship.getName(), upDownRelationship.getUpstream().getName(), upDownRelationship.getDownstream().getName());
		printInterface(getRelationshipLabel(relationship), interfaceId);
		if (upDownRelationship instanceof CustomerSupplierRelationship)
			printCustomerSupplierInterfaceExposure(upDownRelationship.getUpstream().getName(), interfaceId, upstreamRolesToArray(upDownRelationship.getUpstreamRoles()));
		else
			printUpstreamDownstreamInterfaceExposure(upDownRelationship.getUpstream().getName(), interfaceId, upstreamRolesToArray(upDownRelationship.getUpstreamRoles()));
		printInterfaceUsage(interfaceId, upDownRelationship);
		linebreak();
	}

	private void printComponent(BoundedContext bc) {
		sb.append("component").append(" ").append("[" + bc.getName() + "]");
		linebreak();
		if (bc.getDomainVisionStatement() != null && !"".equals(bc.getDomainVisionStatement()))
			printNoteForComponent(bc.getDomainVisionStatement(), bc.getName());
	}

	private void printNoteForComponent(String note, String component) {
		sb.append("note right of ").append("[").append(component).append("]");
		linebreak();
		int charCounter = 0;
		for (String word : note.split(" ")) {
			sb.append(word).append(" ");
			charCounter += word.length() + 1;
			if (charCounter >= 30) {
				charCounter = 0;
				linebreak();
			}
		}
		linebreak();
		sb.append("end note");
		linebreak();
	}

	private void printSymmetricComponentRelationship(String component1, String component2, String name) {
		sb.append("[" + component1 + "]").append("<-->").append("[" + component2 + "]").append(" : ").append(name);
		linebreak();
	}

	private void printInterface(String interfaceName, String identifier) {
		sb.append("interface ").append("\"" + interfaceName + "\"").append(" as ").append(identifier);
		linebreak();
	}

	private void printInterfaceUsage(String interfaceId, UpstreamDownstreamRelationship relationship) {
		String downstreamRoleString = downstreamRoleToString(relationship.getDownstreamRoles());
		sb.append(interfaceId).append(" <.. ").append("[" + relationship.getDownstream().getName() + "]").append(" : ");
		if (relationship instanceof CustomerSupplierRelationship) {
			sb.append("Customer ");
		} else if (relationship.getUpstreamExposedAggregates().isEmpty() && "".equals(downstreamRoleString)) {
			sb.append("consume");
		} else {
			sb.append("use ");
		}
		if (!relationship.getUpstreamExposedAggregates().isEmpty()) {
			if (relationship instanceof CustomerSupplierRelationship)
				sb.append("of ");
			sb.append(relationship.getUpstreamExposedAggregates().size() > 1 ? "Aggregates " : "Aggregate ")
					.append(aggregatesToCommaSeparatedString(relationship.getUpstreamExposedAggregates())).append(" ");
		}
		if (!"".equals(downstreamRoleString))
			sb.append(downstreamRoleString);
		linebreak();
	}

	private void printUpstreamDownstreamInterfaceExposure(String component, String interfaceId, String[] roles) {
		sb.append("[" + component + "]").append(" --> ").append(interfaceId);
		if (roles.length > 0)
			sb.append(" : ").append(String.join(", ", roles));
		linebreak();
	}

	private void printCustomerSupplierInterfaceExposure(String component, String interfaceId, String[] roles) {
		sb.append("[" + component + "]").append(" --> ").append(interfaceId);
		sb.append(" : Supplier");
		if (roles.length > 0)
			sb.append(" of ").append(String.join(", ", roles));
		linebreak();
	}

	private String[] upstreamRolesToArray(EList<UpstreamRole> roles) {
		return roles.stream().map(role -> role.getName()).collect(Collectors.toList()).toArray(new String[roles.size()]);
	}

	private String downstreamRoleToString(EList<DownstreamRole> roles) {
		if (roles.isEmpty())
			return "";
		// currently CML only supports one downstream role (ACL or CONFORMIST)
		if (roles.get(0).equals(DownstreamRole.ANTICORRUPTION_LAYER)) {
			return "via " + roles.get(0).getName();
		} else {
			return "as " + roles.get(0).getName();
		}
	}

	private String aggregatesToCommaSeparatedString(EList<Aggregate> aggregates) {
		return String.join(", ", aggregates.stream().map(aggregate -> aggregate.getName()).collect(Collectors.toList()).toArray(new String[aggregates.size()]));
	}

	private String getRelationshipLabel(Relationship relationship) {
		if (relationship instanceof SymmetricRelationship)
			return getSymmetricRelationshipLabel((SymmetricRelationship) relationship);
		else
			return getAsymmetricRelationshipLabel((UpstreamDownstreamRelationship) relationship);
	}

	private String getSymmetricRelationshipLabel(SymmetricRelationship relationship) {
		StringBuilder label = new StringBuilder();
		boolean relationshipNameDefined = relationship.getName() != null && !"".equals(relationship.getName());
		if (relationshipNameDefined) {
			label.append(relationship.getName());
		} else {
			label.append(getRelationshipTypeLabel(relationship));
		}
		boolean implementationTechnologyDefined = relationship.getImplementationTechnology() != null && !"".equals(relationship.getImplementationTechnology());
		if (relationshipNameDefined && implementationTechnologyDefined) {
			label.append(" (").append(getRelationshipTypeLabel(relationship)).append(" implemented with ").append(relationship.getImplementationTechnology()).append(")");
		} else if (implementationTechnologyDefined) {
			label.append(" (").append(relationship.getImplementationTechnology()).append(")");
		}
		return label.toString();
	}

	private String getAsymmetricRelationshipLabel(UpstreamDownstreamRelationship relationship) {
		StringBuilder label = new StringBuilder();
		if (relationship.getName() != null && !"".equals(relationship.getName())) {
			label.append(relationship.getName());
		} else if (relationship instanceof CustomerSupplierRelationship) {
			label.append(getRelationshipTypeLabel(relationship));
		}
		if ("".equals(label.toString()) && relationship.getImplementationTechnology() != null && !"".equals(relationship.getImplementationTechnology())) {
			label.append(relationship.getImplementationTechnology());
		} else if (relationship.getImplementationTechnology() != null && !"".equals(relationship.getImplementationTechnology())) {
			label.append(" (").append(relationship.getImplementationTechnology()).append(")");
		}
		if ("".equals(label.toString())) {
			label.append(getRelationshipTypeLabel(relationship));
		}
		return label.toString();
	}

	private String getRelationshipTypeLabel(Relationship relationship) {
		if (relationship instanceof Partnership)
			return "Partnership";
		else if (relationship instanceof SharedKernel)
			return "Shared Kernel";
		else if (relationship instanceof CustomerSupplierRelationship)
			return "Customer-Supplier";
		else
			return "Upstream-Downstream";
	}

	private String getUniqueInterfaceId(String relationshipName, String upstreamName, String downStreamName) {
		if (relationshipName != null && !this.interfaceNames.contains(relationshipName)) {
			this.interfaceNames.add(relationshipName);
			return relationshipName;
		} else if (!this.interfaceNames.contains(downStreamName + "_to_" + upstreamName)) {
			this.interfaceNames.add(downStreamName + "_to_" + upstreamName);
			return downStreamName + "_to_" + upstreamName;
		}
		return "Interface_" + interfaceCounter++;
	}

}

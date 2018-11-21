package org.contextmapper.dsl.generator.plantuml;

import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.CustomerSupplierRelationship;
import org.contextmapper.dsl.contextMappingDSL.DownstreamRole;
import org.contextmapper.dsl.contextMappingDSL.Partnership;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.contextMappingDSL.SharedKernel;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamRole;
import org.eclipse.emf.common.util.EList;

public class PlantUMLDiagramCreator {

	private final StringBuilder sb;

	public PlantUMLDiagramCreator() {
		this.sb = new StringBuilder();
	}

	public String createComponentDiagram(ContextMap contextMap) {
		printHeader();
		for (BoundedContext boundedContext : contextMap.getBoundedContexts()) {
			printComponent(boundedContext.getName());
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
		printFooter();
		return sb.toString();
	}

	private void printPartnershipRelationship(Partnership relationship) {
		String relationName = "Partnership";
		if (!"".equals(relationship.getImplementationTechnology()))
			relationName = relationName + " (" + relationship.getImplementationTechnology() + ")";
		printSymmetricComponentRelationship(((Partnership) relationship).getParticipant1().getName(), ((Partnership) relationship).getParticipant2().getName(), relationName);
		linebreak();
	}

	private void printSharedKernelRelationship(SharedKernel relationship) {
		String relationName = "Shared Kernel";
		if (!"".equals(relationship.getImplementationTechnology()))
			relationName = relationName + " (" + relationship.getImplementationTechnology() + ")";
		printSymmetricComponentRelationship(((SharedKernel) relationship).getParticipant1().getName(), ((SharedKernel) relationship).getParticipant2().getName(), relationName);
		linebreak();
	}

	private void printUpstreamDownstreamRelationship(UpstreamDownstreamRelationship relationship) {
		UpstreamDownstreamRelationship upDownRelationship = (UpstreamDownstreamRelationship) relationship;
		String interfaceId = upDownRelationship.getDownstream().getName() + "_to_" + upDownRelationship.getUpstream().getName();
		String technology = upDownRelationship.getImplementationTechnology() != null && !"".equals(upDownRelationship.getImplementationTechnology())
				? upDownRelationship.getImplementationTechnology()
				: "Technology not defined!";
		String interfaceName = upDownRelationship instanceof CustomerSupplierRelationship ? "Customer-Supplier (" + technology + ")" : technology;
		printInterface(interfaceName, interfaceId);
		printInterfaceExposure(upDownRelationship.getUpstream().getName(), interfaceId, upstreamRolesToArray(upDownRelationship.getUpstreamRoles()));
		printInterfaceUsage(upDownRelationship.getDownstream().getName(), interfaceId, downstreamRolesToArray(upDownRelationship.getDownstreamRoles()));
		linebreak();
	}

	public String createClassDiagram(BoundedContext boundedContext) {
		return "bounded context class diagram...";
	}

	private void printComponent(String name) {
		sb.append("component").append(" ").append("[" + name + "]");
		linebreak();
	}

	private void printSymmetricComponentRelationship(String component1, String component2, String name) {
		sb.append("[" + component1 + "]").append("<->").append("[" + component2 + "]").append(" : ").append(name);
		linebreak();
	}

	private void printInterface(String interfaceName, String identifier) {
		sb.append("interface ").append("\"" + interfaceName + "\"").append(" as ").append(identifier);
		linebreak();
	}

	private void printInterfaceUsage(String component, String interfaceId, String[] roles) {
		sb.append(interfaceId).append(" <. ").append("[" + component + "]").append(" : ").append("use");
		if (roles.length > 0)
			sb.append(" : ").append(String.join(", ", roles));
		linebreak();
	}

	private void printInterfaceExposure(String component, String interfaceId, String[] roles) {
		sb.append("[" + component + "]").append(" -> ").append(interfaceId);
		if (roles.length > 0)
			sb.append(" : ").append(String.join(", ", roles));
		linebreak();
	}

	private String[] upstreamRolesToArray(EList<UpstreamRole> roles) {
		return roles.stream().map(role -> role.getName()).collect(Collectors.toList()).toArray(new String[roles.size()]);
	}

	private String[] downstreamRolesToArray(EList<DownstreamRole> roles) {
		return roles.stream().map(role -> role.getName()).collect(Collectors.toList()).toArray(new String[roles.size()]);
	}

	private void printHeader() {
		sb.append("@startuml");
		linebreak(2);
		sb.append("skinparam componentStyle uml2");
		linebreak(2);
	}

	private void printFooter() {
		linebreak(2);
		sb.append("@enduml");
		linebreak();
	}

	private void linebreak() {
		sb.append(System.lineSeparator());
	}

	private void linebreak(int amount) {
		for (int i = 0; i < amount; i++)
			linebreak();
	}

}

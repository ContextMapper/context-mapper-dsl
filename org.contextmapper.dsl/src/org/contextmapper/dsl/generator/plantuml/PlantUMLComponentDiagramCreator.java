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

public class PlantUMLComponentDiagramCreator extends AbstractPlantUMLDiagramCreator<ContextMap> implements PlantUMLDiagramCreator<ContextMap> {

	@Override
	protected void printDiagramContent(ContextMap contextMap) {
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

	}

	private void printPartnershipRelationship(Partnership relationship) {
		String relationName = "Partnership";
		if (relationship.getImplementationTechnology() != null && !"".equals(relationship.getImplementationTechnology()))
			relationName = relationName + " (" + relationship.getImplementationTechnology() + ")";
		printSymmetricComponentRelationship(((Partnership) relationship).getParticipant1().getName(), ((Partnership) relationship).getParticipant2().getName(), relationName);
		linebreak();
	}

	private void printSharedKernelRelationship(SharedKernel relationship) {
		String relationName = "Shared Kernel";
		if (relationship.getImplementationTechnology() != null && !"".equals(relationship.getImplementationTechnology()))
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

	private void printComponent(String name) {
		sb.append("component").append(" ").append("[" + name + "]");
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

	private void printInterfaceUsage(String component, String interfaceId, String[] roles) {
		sb.append(interfaceId).append(" <.. ").append("[" + component + "]").append(" : ").append("use");
		if (roles.length > 0)
			sb.append(" : ").append(String.join(", ", roles));
		linebreak();
	}

	private void printInterfaceExposure(String component, String interfaceId, String[] roles) {
		sb.append("[" + component + "]").append(" --> ").append(interfaceId);
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

}

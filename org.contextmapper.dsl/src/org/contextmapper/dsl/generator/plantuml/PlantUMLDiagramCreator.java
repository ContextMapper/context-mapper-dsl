package org.contextmapper.dsl.generator.plantuml;

import org.eclipse.emf.ecore.EObject;

public interface PlantUMLDiagramCreator<T extends EObject> {

	public String createDiagram(T modelObject);

}

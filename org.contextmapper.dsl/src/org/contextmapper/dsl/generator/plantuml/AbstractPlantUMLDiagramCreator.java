package org.contextmapper.dsl.generator.plantuml;

import org.eclipse.emf.ecore.EObject;

public abstract class AbstractPlantUMLDiagramCreator<T extends EObject> implements PlantUMLDiagramCreator<T> {

	protected StringBuilder sb;

	public AbstractPlantUMLDiagramCreator() {
		this.sb = new StringBuilder();
	}

	@Override
	public String createDiagram(T modelObject) {
		printHeader();
		printDiagramContent(modelObject);
		printFooter();
		return sb.toString();
	}

	/*
	 * Override this method to print diagram content
	 */
	protected abstract void printDiagramContent(T modelObject);

	protected void printHeader() {
		sb.append("@startuml");
		linebreak(2);
		sb.append("skinparam componentStyle uml2");
		linebreak(2);
	}

	protected void printFooter() {
		linebreak(2);
		sb.append("@enduml");
		linebreak();
	}

	protected void linebreak() {
		sb.append(System.lineSeparator());
	}

	protected void linebreak(int amount) {
		for (int i = 0; i < amount; i++)
			linebreak();
	}

}

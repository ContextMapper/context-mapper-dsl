/*
 * Copyright 2024 The Context Mapper Project Team
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

import org.eclipse.emf.ecore.EObject;

public abstract class AbstractPlantUMLMindMapDiagramCreator<T extends EObject> implements PlantUMLDiagramCreator<T> {

	protected StringBuilder sb;

	public AbstractPlantUMLMindMapDiagramCreator() {
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
		sb.append("@startmindmap");
		linebreak(2);
	}

	protected void printFooter() {
		linebreak(2);
		sb.append("@endmindmap");
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

/*
 * Copyright 2020 The Context Mapper Project Team
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
package org.contextmapper.dsl.generators.sketchminer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Flow;
import org.contextmapper.dsl.generator.sketchminer.SketchMinerLinkCreator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.junit.jupiter.api.Test;

public class SketchMinerLinkCreatorTest extends AbstractCMLInputFileTest {

	@Test
	public void canCreateSketchMinerLinkForFlowModel() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("link-integ-test.cml").getContextMappingModel();
		Flow flow = EcoreUtil2.eAllOfType(model, Flow.class).get(0);

		// when
		String link = new SketchMinerLinkCreator().createSketchMinerLink(flow);

		// then
		assertEquals(
				"https://www.bpmn-sketch-miner.ai/index.html#EYBwNgdgXAbgjAKALRIQSQgZwK4CcCGEAxgKYCK2A9gC4mYAEAgiOAJZH7WuXQIID0-ehRokASiQCO2OtXoBtekgB89AMoBVAEIBZNABV9AUQAiAXQSYSuGOxLrswALatqE6bIQAKdzMzU1RxdqWgATAEoBIRFaX1kFTV0DYxMlVTEjAGEjNAA1U3oADXoMgCkslIsrGzt6IgALEiIAazj-bxjxJpJWGBIIqOEqWKk-OXkM7LyClRKjcsyUoqZM7IAFJeKjQrW0DPNLa1tSelxu3pJGCFDMxpbOjuGugCsmsMjBzrbxtPVtPUMpiqR1qgWcrm+3m+YOC70+T2+CX+yRm6SyOXyqWKZQqQMONRODSarVGnh8pP8EleRDh8NEiMUs0SAMq+OO9hhEIp1Ch3M5IX6kUEQ3p3KRSUBqVmkwxBWx81xB2q7LqdxJHnaXi+5z6A2F2o14xl0ylaIWmxW6wt212+2BBPsZ1IFyuNzVDy1T0YRFIIDh+oRYvkjFWRg2qPoawA8gAZNCZACaAH1MhlGNadns8cravgfSQ-Y9REYAB4gVhnAZ0kaGhS-ZkopUgk78yHkw38-3RQO1+QNyW-Y2Y5Y4xbZ5v2IktNva526oXd0W9ocR0cWkNWuX0G1ZpsO046y7XW7Ej2db2+rsimtjBQbsNLWbRuOJlNpjO28f7vOX7xrShgOwACemRnJwgp8AGS63oyqj9qyOYtkEXKGryHbIQKeqLje8R9siA7SuiJojgqY57iqU7qmMRYjHOEFQTh-gKCuppzOaW73uGWLbpmdpsrUTo9H0ronvcTw0SQpblpWHxAA",
				link);
	}

	@Test
	public void canCreateSketchMinerLinkWithEObject() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("link-integ-test.cml").getContextMappingModel();
		EObject flow = EcoreUtil2.eAllOfType(model, Flow.class).get(0);

		// when
		String link = new SketchMinerLinkCreator().createSketchMinerLink(flow);

		// then
		assertEquals(
				"https://www.bpmn-sketch-miner.ai/index.html#EYBwNgdgXAbgjAKALRIQSQgZwK4CcCGEAxgKYCK2A9gC4mYAEAgiOAJZH7WuXQIID0-ehRokASiQCO2OtXoBtekgB89AMoBVAEIBZNABV9AUQAiAXQSYSuGOxLrswALatqE6bIQAKdzMzU1RxdqWgATAEoBIRFaX1kFTV0DYxMlVTEjAGEjNAA1U3oADXoMgCkslIsrGzt6IgALEiIAazj-bxjxJpJWGBIIqOEqWKk-OXkM7LyClRKjcsyUoqZM7IAFJeKjQrW0DPNLa1tSelxu3pJGCFDMxpbOjuGugCsmsMjBzrbxtPVtPUMpiqR1qgWcrm+3m+YOC70+T2+CX+yRm6SyOXyqWKZQqQMONRODSarVGnh8pP8EleRDh8NEiMUs0SAMq+OO9hhEIp1Ch3M5IX6kUEQ3p3KRSUBqVmkwxBWx81xB2q7LqdxJHnaXi+5z6A2F2o14xl0ylaIWmxW6wt212+2BBPsZ1IFyuNzVDy1T0YRFIIDh+oRYvkjFWRg2qPoawA8gAZNCZACaAH1MhlGNadns8cravgfSQ-Y9REYAB4gVhnAZ0kaGhS-ZkopUgk78yHkw38-3RQO1+QNyW-Y2Y5Y4xbZ5v2IktNva526oXd0W9ocR0cWkNWuX0G1ZpsO046y7XW7Ej2db2+rsimtjBQbsNLWbRuOJlNpjO28f7vOX7xrShgOwACemRnJwgp8AGS63oyqj9qyOYtkEXKGryHbIQKeqLje8R9siA7SuiJojgqY57iqU7qmMRYjHOEFQTh-gKCuppzOaW73uGWLbpmdpsrUTo9H0ronvcTw0SQpblpWHxAA",
				link);
	}

	@Test
	public void canReturnEmptyString4WrongObject() {
		// given
		EObject obj = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();

		// when
		String link = new SketchMinerLinkCreator().createSketchMinerLink(obj);

		// then
		assertEquals("", link);
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/sketchminer/";
	}

}

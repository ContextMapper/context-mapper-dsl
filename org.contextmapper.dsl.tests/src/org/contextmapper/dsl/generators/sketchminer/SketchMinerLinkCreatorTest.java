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
import org.contextmapper.dsl.contextMappingDSL.Coordination;
import org.contextmapper.dsl.contextMappingDSL.Flow;
import org.contextmapper.dsl.generator.sketchminer.SketchMinerLinkCreator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

public class SketchMinerLinkCreatorTest extends AbstractCMLInputFileTest {

	@Test
	@DisabledOnOs(OS.WINDOWS)
	public void canCreateSketchMinerLinkForFlowModelUnix() throws IOException {
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
	@DisabledOnOs(OS.WINDOWS)
	public void canCreateSketchMinerLinkWithEObjectUnix() throws IOException {
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
	@DisabledOnOs({ OS.LINUX, OS.MAC })
	public void canCreateSketchMinerLinkForFlowModelWin() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("link-integ-test.cml").getContextMappingModel();
		Flow flow = EcoreUtil2.eAllOfType(model, Flow.class).get(0);

		// when
		String link = new SketchMinerLinkCreator().createSketchMinerLink(flow);

		// then
		assertEquals(
				"https://www.bpmn-sketch-miner.ai/index.html#EYBwNgdgXAbgjAWAFAFoXIJIQM4FcBOAhhAMYCmAirgPYAuZ2ABAIIjgCWJht710yyAPSDGVOmQBKZAI64GtRgG1GKAHyMAygFUAQgFkMAFUMBRACIBdZNjL4YnMptzAAtu1pTZ85AApPc7FoNZzdaegATAEohETF6f3klbX0jUzMVdQkTAGETDAA1c0YADUYsgCkctKskGzsHRhIACzISAGsEwN84yVaydhgyKJjRGniZAIVFLNyCorUyk0rstJKWbNyABVXSk2LNjCzLa1t7ckZ8PoGyZghw7Jb2nu6x3oArVojopBGezqmMppdAZjOYanUzo5gq53P9fP9oaEvr9Xv8ksDUvNMjk8oV0qUKlUwSd6udmq0OhNvEg-FTAlIPiRkT8kMJRuI0coFskQdUSZCnDCPHTaPCRYj3My2X8ReiUqD0gsZriigSlkTjrVTg1ye04TSZeRrsNWbFUbLpji5orscsdustva9gcjuDtedLkbBrd7o82s8Da9mCRyCApWaORbmBsTNssYxNgB5AAyGGyAE0APrZLLMJ37Q7ErWkxyEENkMMvcQmAAeIHYlxNKMjXkCSkBPMxmohDQlwtboppCJCkqG32l5oHct58eV1rWhJWRZ7ZL9+p8hv6gxNE5bkyUc7xgMX9ujjtVjGdhe77scnq3NzuDwpAY3QfLYbHzfGU8UZ9jqwLEmqYZtmub5i6y63owZahoOPibNQYCcAAntklzcF+LK7j++5cuonYKm6JaCqE67DkK4bsrhiSKIRgHYrMR5qnaUEkbqlIDlW4xelhOG9L+h6zuqS74g6AEXlerr8g097XD6z5PK83FkLW9aNt8yBAA",
				link);
	}

	@Test
	@DisabledOnOs({ OS.LINUX, OS.MAC })
	public void canCreateSketchMinerLinkWithEObjectWin() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("link-integ-test.cml").getContextMappingModel();
		EObject flow = EcoreUtil2.eAllOfType(model, Flow.class).get(0);

		// when
		String link = new SketchMinerLinkCreator().createSketchMinerLink(flow);

		// then
		assertEquals(
				"https://www.bpmn-sketch-miner.ai/index.html#EYBwNgdgXAbgjAWAFAFoXIJIQM4FcBOAhhAMYCmAirgPYAuZ2ABAIIjgCWJht710yyAPSDGVOmQBKZAI64GtRgG1GKAHyMAygFUAQgFkMAFUMBRACIBdZNjL4YnMptzAAtu1pTZ85AApPc7FoNZzdaegATAEohETF6f3klbX0jUzMVdQkTAGETDAA1c0YADUYsgCkctKskGzsHRhIACzISAGsEwN84yVaydhgyKJjRGniZAIVFLNyCorUyk0rstJKWbNyABVXSk2LNjCzLa1t7ckZ8PoGyZghw7Jb2nu6x3oArVojopBGezqmMppdAZjOYanUzo5gq53P9fP9oaEvr9Xv8ksDUvNMjk8oV0qUKlUwSd6udmq0OhNvEg-FTAlIPiRkT8kMJRuI0coFskQdUSZCnDCPHTaPCRYj3My2X8ReiUqD0gsZriigSlkTjrVTg1ye04TSZeRrsNWbFUbLpji5orscsdustva9gcjuDtedLkbBrd7o82s8Da9mCRyCApWaORbmBsTNssYxNgB5AAyGGyAE0APrZLLMJ37Q7ErWkxyEENkMMvcQmAAeIHYlxNKMjXkCSkBPMxmohDQlwtboppCJCkqG32l5oHct58eV1rWhJWRZ7ZL9+p8hv6gxNE5bkyUc7xgMX9ujjtVjGdhe77scnq3NzuDwpAY3QfLYbHzfGU8UZ9jqwLEmqYZtmub5i6y63owZahoOPibNQYCcAAntklzcF+LK7j++5cuonYKm6JaCqE67DkK4bsrhiSKIRgHYrMR5qnaUEkbqlIDlW4xelhOG9L+h6zuqS74g6AEXlerr8g097XD6z5PK83FkLW9aNt8yBAA",
				link);
	}
	
	@Test
	@DisabledOnOs(OS.WINDOWS)
	public void canCreateSketchMinerLinkForCoordinationModelUnix() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("link-integ-test2.cml").getContextMappingModel();
		Coordination coordination = EcoreUtil2.eAllOfType(model, Coordination.class).get(0);

		// when
		String link = new SketchMinerLinkCreator().createSketchMinerLink(coordination);

		// then
		assertEquals(
				"https://www.bpmn-sketch-miner.ai/index.html#EYBwNgdgXAbgjAKALRIQVQM4FMBOBhAewgBcsAPYqAAmxxgEsBjLKzXAZVweYDoMBXYAFt6xAEpYAjvywZiCAJIQBOAIYRmARX4FSGarW4tturBOmzinOkyw9GACyyMA1uZlzFy-mo1YTegZctlQBZlIeVsG8OM5Y9DBYAIIQACZ4Tq5hXirqWjqBNNHGBeEWctZGPKqMzCDyCEA", 
				link);
	}
	
	@Test
	@DisabledOnOs(OS.WINDOWS)
	public void canCreateSketchMinerLinkWithCoordinationEObjectUnix() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("link-integ-test2.cml").getContextMappingModel();
		EObject coordination = EcoreUtil2.eAllOfType(model, Coordination.class).get(0);

		// when
		String link = new SketchMinerLinkCreator().createSketchMinerLink(coordination);

		// then
		assertEquals(
				"https://www.bpmn-sketch-miner.ai/index.html#EYBwNgdgXAbgjAKALRIQVQM4FMBOBhAewgBcsAPYqAAmxxgEsBjLKzXAZVweYDoMBXYAFt6xAEpYAjvywZiCAJIQBOAIYRmARX4FSGarW4tturBOmzinOkyw9GACyyMA1uZlzFy-mo1YTegZctlQBZlIeVsG8OM5Y9DBYAIIQACZ4Tq5hXirqWjqBNNHGBeEWctZGPKqMzCDyCEA", 
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

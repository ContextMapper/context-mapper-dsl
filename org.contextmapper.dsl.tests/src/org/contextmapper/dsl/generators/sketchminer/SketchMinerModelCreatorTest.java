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
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.Flow;
import org.contextmapper.dsl.generator.sketchminer.SketchMinerModelCreator;
import org.eclipse.xtext.EcoreUtil2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SketchMinerModelCreatorTest extends AbstractCMLInputFileTest {

	@ParameterizedTest
	@ValueSource(strings = { "simple-sequence-test-1", "simple-sequence-test-2" })
	public void canGenerateSimpleSequenceStartingWithCommand(String inputFileName) throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML(inputFileName + ".cml").getContextMappingModel();
		Flow flow = EcoreUtil2.eAllOfType(model, Flow.class).get(0);

		// when
		String output = new SketchMinerModelCreator().createText(flow);

		// then
		assertEquals(
				"service StartCommand" + System.lineSeparator() + "FirstEvent" + System.lineSeparator() + "service EndCommand" + System.lineSeparator() + System.lineSeparator(),
				output);
	}

	@Test
	public void canGenerateParallelStep4EventProduction() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("simple-sequence-with-parallel-step-test-1.cml").getContextMappingModel();
		Flow flow = EcoreUtil2.eAllOfType(model, Flow.class).get(0);

		// when
		String output = new SketchMinerModelCreator().createText(flow);

		// then
		assertEquals("service StartCommand" + System.lineSeparator() + "FirstEvent|SecondEvent" + System.lineSeparator() + "service EndCommand" + System.lineSeparator()
				+ System.lineSeparator(), output);
	}

	@ParameterizedTest
	@ValueSource(strings = { "simple-sequence-with-parallel-step-test-2", "simple-sequence-with-parallel-step-test-3" })
	public void canGenerateParallelStep4CommandInvokation(String inputFileName) throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML(inputFileName + ".cml").getContextMappingModel();
		Flow flow = EcoreUtil2.eAllOfType(model, Flow.class).get(0);

		// when
		String output = new SketchMinerModelCreator().createText(flow);

		// then
		assertEquals("StartEvent" + System.lineSeparator() + "service FirstCommand|service SecondCommand" + System.lineSeparator() + "EndEvent" + System.lineSeparator()
				+ System.lineSeparator(), output);
	}

	@ParameterizedTest
	@ValueSource(strings = { "exclusive-gate-event-test-1", "exclusive-gate-event-test-2", "exclusive-gate-event-test-3" })
	public void canGenerateExclusiveAlternativeGate4EventProductionSteps(String inputFileName) throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML(inputFileName + ".cml").getContextMappingModel();
		Flow flow = EcoreUtil2.eAllOfType(model, Flow.class).get(0);

		// when
		String output = new SketchMinerModelCreator().createText(flow);

		// then
		assertEquals("service StartCommand" + System.lineSeparator() + "FirstEvent" + System.lineSeparator() + "service EndCommand" + System.lineSeparator()
				+ System.lineSeparator() + "service StartCommand" + System.lineSeparator() + "SecondEvent" + System.lineSeparator() + "service EndCommand" + System.lineSeparator()
				+ System.lineSeparator(), output);
	}

	@ParameterizedTest
	@ValueSource(strings = { "exclusive-gate-command-test-1", "exclusive-gate-command-test-2", "exclusive-gate-command-test-3" })
	public void canGenerateExclusiveAlternativeGate4CommandInvokationSteps(String inputFileName) throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML(inputFileName + ".cml").getContextMappingModel();
		Flow flow = EcoreUtil2.eAllOfType(model, Flow.class).get(0);

		// when
		String output = new SketchMinerModelCreator().createText(flow);

		// then
		assertEquals("service StartCommand" + System.lineSeparator() + "FirstEvent" + System.lineSeparator() + "service MiddleCommand1" + System.lineSeparator() + "EndEvent"
				+ System.lineSeparator() + System.lineSeparator() + "service StartCommand" + System.lineSeparator() + "FirstEvent" + System.lineSeparator()
				+ "service MiddleCommand2" + System.lineSeparator() + "EndEvent" + System.lineSeparator() + System.lineSeparator(), output);
	}

	@Test
	public void canGenerateStepsForLoop() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("loop-test-1.cml").getContextMappingModel();
		Flow flow = EcoreUtil2.eAllOfType(model, Flow.class).get(0);

		// when
		String output = new SketchMinerModelCreator().createText(flow);

		// then
		assertEquals("service StartCommand" + System.lineSeparator() + "FirstEvent" + System.lineSeparator() + "service MiddleCommand" + System.lineSeparator() + "EndEvent"
				+ System.lineSeparator() + "service StartCommand" + System.lineSeparator() + "FirstEvent" + System.lineSeparator() + "service MiddleCommand"
				+ System.lineSeparator() + "EndEvent" + System.lineSeparator() + System.lineSeparator(), output);
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/sketchminer/";
	}

}
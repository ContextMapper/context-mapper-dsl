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
import org.contextmapper.dsl.contextMappingDSL.Coordination;
import org.contextmapper.dsl.generator.sketchminer.SketchMinerCoordinationModelCreator;
import org.eclipse.xtext.EcoreUtil2;
import org.junit.jupiter.api.Test;

public class SketchMinerCoordinationModelCreatorTest extends AbstractCMLInputFileTest {

	@Test
	public void canGenerateSimpleSequenceWithTwoActors() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("simple-coordination-test-1.cml").getContextMappingModel();
		Coordination coordination = EcoreUtil2.eAllOfType(model, Coordination.class).get(0);

		// when
		String output = new SketchMinerCoordinationModelCreator().createText(coordination);

		// then
		assertEquals("ContextA: service ServiceA.operationA" + System.lineSeparator() +
				"ContextB: service ServiceB.operationB" + System.lineSeparator() +
				System.lineSeparator(), output);
	}
	
	@Test
	public void canGenerateSimpleSequenceWithThreeActors() throws IOException {
		// given
		ContextMappingModel model = getOriginalResourceOfTestCML("simple-coordination-test-2.cml").getContextMappingModel();
		Coordination coordination = EcoreUtil2.eAllOfType(model, Coordination.class).get(0);

		// when
		String output = new SketchMinerCoordinationModelCreator().createText(coordination);

		// then
		assertEquals("ContextA: service ServiceA.operationA1" + System.lineSeparator() +
				"ContextB: service ServiceB.operationB" + System.lineSeparator() +
				"ContextA: service ServiceA.operationA2" + System.lineSeparator() +
				"ContextC: service ServiceC.operationC" + System.lineSeparator() +
				System.lineSeparator(), output);
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/sketchminer/";
	}

}

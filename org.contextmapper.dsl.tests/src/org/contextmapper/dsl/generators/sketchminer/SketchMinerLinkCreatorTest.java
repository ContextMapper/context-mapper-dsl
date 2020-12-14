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
				"https://www.bpmn-sketch-miner.ai/index.html#EYBwNgdgXAbgjAKALRIQSQgZwK4CcCGEAxgKYCK2A9gC4mYAEAgiOAJZH7WuXQIID0-ehRokASiQCO2OtXoBtekgB89AMoBVAEIBZNABV9AUQAiAXQSYSuGOxLrswALatqE6bITuZmamscu1LQAJgJCIrTesgqaugbGJkqqYkYAwkZoAGqm9AAa9CkAUmkJFlY2dvREABYkRADWUb4IEeJ1JKwwJKGCwlSRUj5y8inpWTkqBUbFqQl5TKnpAApz+Ua5S2gp5pbWtqT0uO2dJIwQwam1Da0t-adEpCAhYX2iTcOMi0YrE6pLAPIAGTQqQAmgB9VIpRirejrTbbMp7Sr4B4kJ63URGAAeIFYR1CL1a7wUSXU2j0hlMSIqB38zlc7y8g1k9MCz16xJZvhiFPivymY2yiXyRRK1N2tPsNTqjW51ExA1IJx64TuJJGaQywrJYtmOXyn2WsPhWwl5X29iOyq6ZwuV3qN1ajDRT26RPV8oURu+c0mAOBYMh0JNGzNOwtKNdCqWlDA7AAnqkjpx3R63l7FJNYpTSpLLQ4GW55cyPL42a4OWqM2Xhjn+YlJqNtQapjM85GDjKGkyuTb3ZzPbWFM3xo3ktNxSKFsbW6bEfnKtaOrbzpdZU7PQArOrPdMDYdZ1T1qkR5F0gKMkvvCtBAfVg9DXlxU+6rVj+Z6jvn6UO3ue-tVVeR9ok1IUBS-VsfR+ad53NH9DmOVd7Q3O5FRIHE8QJPhBxrJ8j3JF9vylQtAiZG9LzvICuUPE8-QncDp0gs8SO7OVa1LIYJB3Ig9yAA",
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
				"https://www.bpmn-sketch-miner.ai/index.html#EYBwNgdgXAbgjAKALRIQSQgZwK4CcCGEAxgKYCK2A9gC4mYAEAgiOAJZH7WuXQIID0-ehRokASiQCO2OtXoBtekgB89AMoBVAEIBZNABV9AUQAiAXQSYSuGOxLrswALatqE6bITuZmamscu1LQAJgJCIrTesgqaugbGJkqqYkYAwkZoAGqm9AAa9CkAUmkJFlY2dvREABYkRADWUb4IEeJ1JKwwJKGCwlSRUj5y8inpWTkqBUbFqQl5TKnpAApz+Ua5S2gp5pbWtqT0uO2dJIwQwam1Da0t-adEpCAhYX2iTcOMi0YrE6pLAPIAGTQqQAmgB9VIpRirejrTbbMp7Sr4B4kJ63URGAAeIFYR1CL1a7wUSXU2j0hlMSIqB38zlc7y8g1k9MCz16xJZvhiFPivymY2yiXyRRK1N2tPsNTqjW51ExA1IJx64TuJJGaQywrJYtmOXyn2WsPhWwl5X29iOyq6ZwuV3qN1ajDRT26RPV8oURu+c0mAOBYMh0JNGzNOwtKNdCqWlDA7AAnqkjpx3R63l7FJNYpTSpLLQ4GW55cyPL42a4OWqM2Xhjn+YlJqNtQapjM85GDjKGkyuTb3ZzPbWFM3xo3ktNxSKFsbW6bEfnKtaOrbzpdZU7PQArOrPdMDYdZ1T1qkR5F0gKMkvvCtBAfVg9DXlxU+6rVj+Z6jvn6UO3ue-tVVeR9ok1IUBS-VsfR+ad53NH9DmOVd7Q3O5FRIHE8QJPhBxrJ8j3JF9vylQtAiZG9LzvICuUPE8-QncDp0gs8SO7OVa1LIYJB3Ig9yAA",
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

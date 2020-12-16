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
				"https://www.bpmn-sketch-miner.ai/index.html#EYBwNgdgXAbgjAKALRIQSQgZwK4CcCGEAxgKYCK2A9gC4mYAEAgiOAJZH7WuXQIID0-ehRokASiQCO2OtXoBtekgB89AMoBVAEIBZNABV9AUQAiAXQSYSuGOxLrswALatqE6bITuZmamscu1LQAJgJCIrTesgqaugbGJkqqYkYAwkZoAGqm9AAa9CkAUmkJFlY2dvREABYkRADWUb4IEeJ1JKwwJKGCwlSRUj5y8inpWTkqBUbFqQl5TKnpAApz+Ua5S2gp5pbWtqT0uO2dJIwQwam1Da0t-W0AVnUhfL2tTcNJ6tp6hqZle5V-M5XO8vINZEDAs9Xnd3jFvvEJsk0hlsol8kUSn9dhUDjU6o1wc13hJHkRnmE+qI4YpJrEfqUcft7JCQUTqGCPL5WUFupS3uz4XFfolJqNUTkMdMsTtysyqldCVyOQLSCceuFYYKRijxqLkTNVgtlkb1pttv9cfYjmqumcLoqbq1GERSCBoZrqdrGIsjCskfQlgB5AAyaFSAE0APqpFKMU0bLbYuWVfCukju26iIwADxArCOoX5WuVCk+9MRsoBBx5oPePI9VIGpfkFZFn3FevmmNmyer9nxDVBqo6XQ1TbaLc7aI70t76ONfoT5r7VsOxzt50uBKddxdbsbApbPpNAeDYcjMbjy6TVbXaYPCCWlDA7AAnqkjpw+cWvS3ywiIqWvKtbspyQwNnyMJ-kMQoMgG06SlMhqrvKg5KkMWYDLaUGes2sE6mMM5inORonkuSFmrewGVDao6nFujp3FhJC5vmhZ8EAA",
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
				"https://www.bpmn-sketch-miner.ai/index.html#EYBwNgdgXAbgjAKALRIQSQgZwK4CcCGEAxgKYCK2A9gC4mYAEAgiOAJZH7WuXQIID0-ehRokASiQCO2OtXoBtekgB89AMoBVAEIBZNABV9AUQAiAXQSYSuGOxLrswALatqE6bITuZmamscu1LQAJgJCIrTesgqaugbGJkqqYkYAwkZoAGqm9AAa9CkAUmkJFlY2dvREABYkRADWUb4IEeJ1JKwwJKGCwlSRUj5y8inpWTkqBUbFqQl5TKnpAApz+Ua5S2gp5pbWtqT0uO2dJIwQwam1Da0t-W0AVnUhfL2tTcNJ6tp6hqZle5V-M5XO8vINZEDAs9Xnd3jFvvEJsk0hlsol8kUSn9dhUDjU6o1wc13hJHkRnmE+qI4YpJrEfqUcft7JCQUTqGCPL5WUFupS3uz4XFfolJqNUTkMdMsTtysyqldCVyOQLSCceuFYYKRijxqLkTNVgtlkb1pttv9cfYjmqumcLoqbq1GERSCBoZrqdrGIsjCskfQlgB5AAyaFSAE0APqpFKMU0bLbYuWVfCukju26iIwADxArCOoX5WuVCk+9MRsoBBx5oPePI9VIGpfkFZFn3FevmmNmyer9nxDVBqo6XQ1TbaLc7aI70t76ONfoT5r7VsOxzt50uBKddxdbsbApbPpNAeDYcjMbjy6TVbXaYPCCWlDA7AAnqkjpw+cWvS3ywiIqWvKtbspyQwNnyMJ-kMQoMgG06SlMhqrvKg5KkMWYDLaUGes2sE6mMM5inORonkuSFmrewGVDao6nFujp3FhJC5vmhZ8EAA",
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

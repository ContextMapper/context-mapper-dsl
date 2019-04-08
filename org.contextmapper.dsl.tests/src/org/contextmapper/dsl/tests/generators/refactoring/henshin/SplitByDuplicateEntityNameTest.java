package org.contextmapper.dsl.tests.generators.refactoring.henshin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.contextmapper.dsl.refactoring.HenshinSplitBoundedContextRefactoring;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.Test;

public class SplitByDuplicateEntityNameTest extends AbstractHenshinTransformationTest {

	@Test
	void refactorTest() throws IOException {
		// given
		String inputModelName = "split-by-duplicate-entity-test-1-input.cml";
		String outputModeName = "split-by-duplicate-entity-test-1-output.cml";

		// when
		Resource input = getResourceCopyOfTestCML(inputModelName);
		HenshinSplitBoundedContextRefactoring refactoring = new HenshinSplitBoundedContextRefactoring();
		refactoring.doRefactor(input);

		// then
		assertTrue(FileUtils.contentEquals(new File(input.getURI().devicePath()), getTestFile(outputModeName)));
	}

}

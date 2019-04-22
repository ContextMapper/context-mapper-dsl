package org.contextmapper.dsl.tests.generators.refactoring.henshin;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.contextmapper.dsl.refactoring.henshin.SplitBoundedContextByDuplicateEntityInAggregatesRefactoring;
import org.contextmapper.dsl.refactoring.henshin.SplitBoundedContextByDuplicateEntityInAggregatesRefactoring.NoDuplicateEntityFoundException;
import org.contextmapper.dsl.tests.generators.refactoring.AbstractRefactoringTest;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.Test;

public class SplitByDuplicateEntityNameTest extends AbstractRefactoringTest {

	@Test
	void refactorTest() throws IOException {
		// given
		String inputModelName = "split-by-duplicate-entity-test-1-input.cml";
		String outputModeName = "split-by-duplicate-entity-test-1-output.cml";

		// when
		Resource input = getResourceCopyOfTestCML(inputModelName);
		SplitBoundedContextByDuplicateEntityInAggregatesRefactoring refactoring = new SplitBoundedContextByDuplicateEntityInAggregatesRefactoring("CustomerManagement");
		refactoring.doRefactor(input);

		// then
		assertTrue(FileUtils.contentEquals(new File(input.getURI().devicePath()), getTestFile(outputModeName)));
	}

	@Test
	void expectExceptionIfThereAreNoDuplicates() throws IOException {
		// given
		String inputModelName = "split-by-duplicate-entity-test-2-no-duplicates.cml";

		// when
		Resource input = getResourceCopyOfTestCML(inputModelName);
		SplitBoundedContextByDuplicateEntityInAggregatesRefactoring refactoring = new SplitBoundedContextByDuplicateEntityInAggregatesRefactoring("CustomerManagement");
		
		// then
		assertThrows(NoDuplicateEntityFoundException.class, () -> refactoring.doRefactor(input));
	}

}

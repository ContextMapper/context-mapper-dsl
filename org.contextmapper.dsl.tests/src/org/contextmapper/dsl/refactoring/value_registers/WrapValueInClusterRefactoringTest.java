package org.contextmapper.dsl.refactoring.value_registers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.ValueCluster;
import org.contextmapper.dsl.refactoring.AbstractRefactoringTest;
import org.junit.jupiter.api.Test;

public class WrapValueInClusterRefactoringTest extends AbstractRefactoringTest {

	@Test
	void canWrapValueIntoCluster() throws IOException {
		// given
		String inputModelName = "wrap-value-in-cluster-1.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		new WrapValueInClusterRefactoring("TestValue").refactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getValueRegisters().size());
		assertEquals(0, model.getValueRegisters().get(0).getValues().size());
		assertEquals(1, model.getValueRegisters().get(0).getValueClusters().size());
		ValueCluster cluster = model.getValueRegisters().get(0).getValueClusters().get(0);
		assertEquals(1, cluster.getValues().size());
		assertEquals("TestValue", cluster.getValues().get(0).getName());
		assertEquals("TestValue", cluster.getCoreValue());
	}

}

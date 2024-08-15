package org.contextmapper.dsl.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel;
import org.contextmapper.dsl.contextMappingDSL.UserStory;
import org.junit.jupiter.api.Test;

public class AddEthicalValueAssessmentToStoryRefactoringTest extends AbstractRefactoringTest {

	@Test
	void canAddValuation2Story() throws IOException {
		// given
		String inputModelName = "add-ethical-value-assessment-1.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);

		// when
		new AddEthicalValueAssessmentToStory("SameDayDelivery_1").refactor(input);

		// then
		ContextMappingModel model = input.getContextMappingModel();
		assertEquals(1, model.getUserRequirements().size());
		UserStory story = (UserStory) model.getUserRequirements().get(0);
		assertNotNull(story.getValuation());
		assertEquals("tbd_harmed", story.getValuation().getHarmedValues().get(0));
		assertEquals("tbd_promoted", story.getValuation().getPromotedValues().get(0));
	}

}

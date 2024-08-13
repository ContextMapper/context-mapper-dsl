package org.contextmapper.dsl.generators.plantuml.value_impact_mapping.model;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.contextmapper.dsl.generator.plantuml.value_impact_mapping.model.Stakeholder;
import org.contextmapper.dsl.generator.plantuml.value_impact_mapping.model.Value;

class StakeholderTest {

	@Test
	void ensureStakeholdersWithSameNameAreEqual() {
		Stakeholder stakeholder1 = new Stakeholder("User");
		Stakeholder stakeholder2 = new Stakeholder("User");

		assertTrue(stakeholder1.equals(stakeholder2));
		assertTrue(stakeholder1.equals(stakeholder1));
		assertFalse(stakeholder1.equals(null));
		assertFalse(stakeholder1.equals(new Value("just a test")));
	}

}

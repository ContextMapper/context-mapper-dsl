/*
 * Copyright 2022 The Context Mapper Project Team
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
package org.contextmapper.dsl.generators.plantuml;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.contextmapper.dsl.generator.plantuml.AssociationLink;
import org.junit.jupiter.api.Test;

class AssociationLinkTest {

	@Test
	void isEqualIfParticipantsAreTheSame() {
		// given
		AssociationLink link1 = new AssociationLink("participant1", "participant2", "label");
		AssociationLink link2 = new AssociationLink("participant1", "participant2", "label");

		// when
		boolean isEqual = link1.equals(link2);

		// then
		assertTrue(isEqual);
	}

	@Test
	void isEqualIfParticipantsAreSameButSwitched() {
		// given
		AssociationLink link1 = new AssociationLink("participant1", "participant2", "label");
		AssociationLink link2 = new AssociationLink("participant2", "participant1", "label");

		// when
		boolean isEqual = link1.equals(link2);

		// then
		assertTrue(isEqual);
	}

	@Test
	void isNotEqualIfLabelIsDifferent1() {
		// given
		AssociationLink link1 = new AssociationLink("participant1", "participant2", "label");
		AssociationLink link2 = new AssociationLink("participant1", "participant2", "otherLabel");

		// when
		boolean isEqual = link1.equals(link2);

		// then
		assertFalse(isEqual);
	}
	
	@Test
	void isNotEqualIfLabelIsDifferent2() {
		// given
		AssociationLink link1 = new AssociationLink("participant1", "participant2", "label");
		AssociationLink link2 = new AssociationLink("participant2", "participant1", "otherLabel");

		// when
		boolean isEqual = link1.equals(link2);

		// then
		assertFalse(isEqual);
	}

	@Test
	void isNotEqualIfOtherObjectIsNull() {
		// given
		AssociationLink link = new AssociationLink("participant1", "participant2", "label");

		// when
		boolean isEqual = link.equals(null);

		// then
		assertFalse(isEqual);
	}

	@Test
	void isNotEqualIfOtherObjectIsOfOtherType() {
		// given
		AssociationLink link = new AssociationLink("participant1", "participant2", "label");

		// when
		boolean isEqual = link.equals(new Object());

		// then
		assertFalse(isEqual);
	}

	@Test
	void isEqualIfOtherObjectIsSameRef() {
		// given
		AssociationLink link = new AssociationLink("participant1", "participant2", "label");

		// when
		boolean isEqual = link.equals(link);

		// then
		assertTrue(isEqual);
	}

	@Test
	void isSelfReferenceIfNamesAreEqual() {
		// given
		AssociationLink link = new AssociationLink("participant1", "participant1", "label");

		// when
		boolean isSelfReference = link.isSelfReference();

		// then
		assertTrue(isSelfReference);
	}

	@Test
	void isNoSelfReferenceIfNamesAreNotEqual() {
		// given
		AssociationLink link = new AssociationLink("participant1", "participant2", "label");

		// when
		boolean isSelfReference = link.isSelfReference();

		// then
		assertFalse(isSelfReference);
	}

}

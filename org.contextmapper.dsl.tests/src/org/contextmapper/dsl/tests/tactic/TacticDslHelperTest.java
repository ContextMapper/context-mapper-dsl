/*
 * Copyright 2018 The Context Mapper Project Team
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
package org.contextmapper.dsl.tests.tactic;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.contextmapper.tactic.dsl.TacticDslHelper;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.junit.jupiter.api.Test;

class TacticDslHelperTest {


	@Test
	void cannotGetSubClassesForOthersThanDomainObjectsAndDTOs() {
		// no enums
		assertThrows(IllegalArgumentException.class, () -> {
			TacticDslHelper.getSubclasses(TacticdslFactory.eINSTANCE.createEnum());
		});

		// no traits
		assertThrows(IllegalArgumentException.class, () -> {
			TacticDslHelper.getSubclasses(TacticdslFactory.eINSTANCE.createTrait());
		});

		// no basic type
		assertThrows(IllegalArgumentException.class, () -> {
			TacticDslHelper.getSubclasses(TacticdslFactory.eINSTANCE.createBasicType());
		});
	}

	@Test
	void canStopGeneratorWithError() {
		assertThrows(RuntimeException.class, () -> {
			TacticDslHelper.error("just an error");
			;
		});
	}

}

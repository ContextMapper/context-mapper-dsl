/*
 * Copyright 2019 The Context Mapper Project Team
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
package org.contextmapper.dsl.validation;

import static org.contextmapper.dsl.validation.ValidationMessages.AGGREGATE_CAN_ONLY_HAVE_ONE_AGGREGATE_ROOT;
import static org.contextmapper.dsl.validation.ValidationMessages.AGGREGATE_CAN_ONLY_HAVE_ONE_STATES_ENUM;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.DOMAIN_OBJECT__AGGREGATE_ROOT;
import static org.contextmapper.tactic.dsl.tacticdsl.TacticdslPackage.Literals.ENUM__DEFINES_AGGREGATE_STATES;

import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.Enum;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

public class AggregateSemanticsValidator extends AbstractDeclarativeValidator {

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}

	@Check
	public void validateThatAggregateContainsOnlyOneAggregateRoot(final Aggregate aggregate) {
		List<DomainObject> aggregateRoots = aggregate.getDomainObjects().stream().filter(o -> o instanceof DomainObject).map(o -> (DomainObject) o).filter(o -> o.isAggregateRoot())
				.collect(Collectors.toList());

		if (aggregateRoots.size() > 1) {
			for (DomainObject object : aggregateRoots) {
				error(String.format(AGGREGATE_CAN_ONLY_HAVE_ONE_AGGREGATE_ROOT, aggregate.getName()), object, DOMAIN_OBJECT__AGGREGATE_ROOT);
			}
		}
	}

	@Check
	public void validateThatAggregateContainsOnlyOneStatesEnum(final Aggregate aggregate) {
		List<Enum> stateEnums = aggregate.getDomainObjects().stream().filter(o -> o instanceof Enum).map(o -> (Enum) o).filter(o -> o.isDefinesAggregateStates()).collect(Collectors.toList());

		if (stateEnums.size() > 1) {
			for (Enum enumm : stateEnums) {
				error(String.format(AGGREGATE_CAN_ONLY_HAVE_ONE_STATES_ENUM, aggregate.getName()), enumm, ENUM__DEFINES_AGGREGATE_STATES);
			}
		}
	}

}

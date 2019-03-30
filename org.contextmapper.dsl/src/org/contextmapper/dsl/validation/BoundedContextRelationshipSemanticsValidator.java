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
package org.contextmapper.dsl.validation;

import static org.contextmapper.dsl.validation.ValidationMessages.CUSTOMER_SUPPLIER_WITH_ACL_WARNING_MESSAGE;
import static org.contextmapper.dsl.validation.ValidationMessages.CUSTOMER_SUPPLIER_WITH_CONFORMIST_ERROR_MESSAGE;
import static org.contextmapper.dsl.validation.ValidationMessages.CUSTOMER_SUPPLIER_WITH_OHS_ERROR_MESSAGE;
import static org.contextmapper.dsl.validation.ValidationMessages.DUPLICATE_RELATIONSHIP_DECLARATION;

import java.util.HashSet;
import java.util.Set;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMap;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage;
import org.contextmapper.dsl.contextMappingDSL.CustomerSupplierRelationship;
import org.contextmapper.dsl.contextMappingDSL.DownstreamRole;
import org.contextmapper.dsl.contextMappingDSL.Relationship;
import org.contextmapper.dsl.contextMappingDSL.SymmetricRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship;
import org.contextmapper.dsl.contextMappingDSL.UpstreamRole;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

import com.google.common.base.Objects;

public class BoundedContextRelationshipSemanticsValidator extends AbstractDeclarativeValidator {

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}

	@Check
	public void customerSupplierRolesValidator(final CustomerSupplierRelationship relationship) {
		// Upstream in Customer-Supplier relationship should not implement OHS
		if (relationship.getUpstreamRoles().contains(UpstreamRole.OPEN_HOST_SERVICE))
			error(CUSTOMER_SUPPLIER_WITH_OHS_ERROR_MESSAGE, relationship, ContextMappingDSLPackage.Literals.UPSTREAM_DOWNSTREAM_RELATIONSHIP__UPSTREAM_ROLES);

		// Downstream in Customer-Supplier relationship should not implement ACL
		if (relationship.getDownstreamRoles().contains(DownstreamRole.ANTICORRUPTION_LAYER))
			warning(CUSTOMER_SUPPLIER_WITH_ACL_WARNING_MESSAGE, relationship, ContextMappingDSLPackage.Literals.UPSTREAM_DOWNSTREAM_RELATIONSHIP__DOWNSTREAM_ROLES);

		// Downstream in Customer-Supplier relationship should not implement CONFORMIST
		if (relationship.getDownstreamRoles().contains(DownstreamRole.CONFORMIST))
			error(CUSTOMER_SUPPLIER_WITH_CONFORMIST_ERROR_MESSAGE, relationship, ContextMappingDSLPackage.Literals.UPSTREAM_DOWNSTREAM_RELATIONSHIP__DOWNSTREAM_ROLES);
	}

	@Check
	public void ensureRelationshipUniqueness(final ContextMap contextMap) {
		Set<BoundedContextPair> pairs = new HashSet<>();
		int relationshipIndex = 0;
		for (Relationship relationship : contextMap.getRelationships()) {
			BoundedContextPair pair;
			if (relationship instanceof SymmetricRelationship) {
				pair = new BoundedContextPair(((SymmetricRelationship) relationship).getParticipant1(), ((SymmetricRelationship) relationship).getParticipant2());
			} else {
				pair = new BoundedContextPair(((UpstreamDownstreamRelationship) relationship).getUpstream(), ((UpstreamDownstreamRelationship) relationship).getDownstream());
			}
			if (pairs.contains(pair)) {
				error(String.format(DUPLICATE_RELATIONSHIP_DECLARATION, pair.context1.getName(), pair.context2.getName()), contextMap,
						ContextMappingDSLPackage.Literals.CONTEXT_MAP__RELATIONSHIPS, relationshipIndex);
			} else {
				pairs.add(pair);
			}
			relationshipIndex++;
		}
	}

	private class BoundedContextPair {
		private BoundedContext context1;
		private BoundedContext context2;

		public BoundedContextPair(BoundedContext context1, BoundedContext context2) {
			this.context1 = context1;
			this.context2 = context2;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof BoundedContextPair))
				return false;
			BoundedContextPair otherPair = (BoundedContextPair) obj;
			return (this.context1 == otherPair.context1 && this.context2 == otherPair.context2) || (this.context1 == otherPair.context2 && this.context2 == otherPair.context1);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(this.context1, this.context2);
		}
	}

}

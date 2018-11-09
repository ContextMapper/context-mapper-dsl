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

import static org.contextmapper.dsl.validation.ValidationMessages.CUSTOMER_SUPPLIER_NOT_ALLOW_OHS_ACL_CONFORMIST_ERROR_MESSAGE;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage;
import org.contextmapper.dsl.contextMappingDSL.CustomerSupplierRelationship;
import org.contextmapper.dsl.contextMappingDSL.DownstreamRole;
import org.contextmapper.dsl.contextMappingDSL.UpstreamRole;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

public class BoundedContextRelationshipSemanticsValidator extends AbstractDeclarativeValidator {

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}

	@Check
	public void customerSupplierRolesValidator(final CustomerSupplierRelationship relationship) {
		// Upstream in Customer-Supplier relationship should not implement OHS
		if (relationship.getUpstreamRoles().contains(UpstreamRole.OPEN_HOST_SERVICE))
			error(CUSTOMER_SUPPLIER_NOT_ALLOW_OHS_ACL_CONFORMIST_ERROR_MESSAGE, relationship,
					ContextMappingDSLPackage.Literals.UPSTREAM_DOWNSTREAM_RELATIONSHIP__UPSTREAM_ROLES);

		// Downstream in Customer-Supplier relationship should not implement ACL
		if (relationship.getDownstreamRoles().contains(DownstreamRole.ANTICORRUPTION_LAYER))
			error(CUSTOMER_SUPPLIER_NOT_ALLOW_OHS_ACL_CONFORMIST_ERROR_MESSAGE, relationship,
					ContextMappingDSLPackage.Literals.UPSTREAM_DOWNSTREAM_RELATIONSHIP__DOWNSTREAM_ROLES);

		// Downstream in Customer-Supplier relationship should not implement CONFORMIST
		if (relationship.getDownstreamRoles().contains(DownstreamRole.CONFORMIST))
			error(CUSTOMER_SUPPLIER_NOT_ALLOW_OHS_ACL_CONFORMIST_ERROR_MESSAGE, relationship,
					ContextMappingDSLPackage.Literals.UPSTREAM_DOWNSTREAM_RELATIONSHIP__DOWNSTREAM_ROLES);
	}

}

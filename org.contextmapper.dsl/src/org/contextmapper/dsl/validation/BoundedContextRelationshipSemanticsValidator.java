package org.contextmapper.dsl.validation;

import static org.contextmapper.dsl.validation.ValidationMessages.CUSTOMER_SUPPLIER_NOT_ALLOW_OHS_ACL_CONFORMIST_ERROR_MESSAGE;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage;
import org.contextmapper.dsl.contextMappingDSL.CustomerSupplierRelationship;
import org.contextmapper.dsl.contextMappingDSL.DownstreamRoles;
import org.contextmapper.dsl.contextMappingDSL.UpstreamRoles;
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
		if (relationship.getUpstream().getRoles().contains(UpstreamRoles.OPEN_HOST_SERVICE))
			error(CUSTOMER_SUPPLIER_NOT_ALLOW_OHS_ACL_CONFORMIST_ERROR_MESSAGE, relationship.getUpstream(),
					ContextMappingDSLPackage.Literals.UPSTREAM_CONTEXT__ROLES);

		// Downstream in Customer-Supplier relationship should not implement ACL
		if (relationship.getDownstream().getRoles().contains(DownstreamRoles.ANTICORRUPTION_LAYER))
			error(CUSTOMER_SUPPLIER_NOT_ALLOW_OHS_ACL_CONFORMIST_ERROR_MESSAGE, relationship.getDownstream(),
					ContextMappingDSLPackage.Literals.DOWNSTREAM_CONTEXT__ROLES);

		// Downstream in Customer-Supplier relationship should not implement CONFORMIST
		if (relationship.getDownstream().getRoles().contains(DownstreamRoles.CONFORMIST))
			error(CUSTOMER_SUPPLIER_NOT_ALLOW_OHS_ACL_CONFORMIST_ERROR_MESSAGE, relationship.getDownstream(),
					ContextMappingDSLPackage.Literals.DOWNSTREAM_CONTEXT__ROLES);
	}

}

package org.contextmapper.dsl.validation;

public class ValidationMessages {
	public static final String CUSTOMER_SUPPLIER_NOT_ALLOW_OHS_ACL_CONFORMIST_ERROR_MESSAGE = "Two contexts within a Customer-Supplier relationship should not implement OHS/ACL/CONFORMIST.";
	public static final String RELATIONSHIP_CONTEXT_NOT_ON_MAP_ERROR_MESSAGE = "The Bounded Context '%s' is not part of the Context Map.";
	public static final String ORGANIZATIONAL_MAP_CONTEXT_IS_NOT_TYPE_TEAM = "A Bounded Context of type '%s' is not allowed on organizational maps. Please use Contexts of type 'Team'.";
	public static final String SYSTEM_LANDSCAPE_MAP_CONTAINS_TEAM = "A Bounded Context of type 'TEAM' is not allowed on a SYSTEM_LANDSCAPE map.";
}

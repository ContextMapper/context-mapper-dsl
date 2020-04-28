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

public class ValidationMessages {
	/* Semantical problems in model */
	public static final String CUSTOMER_SUPPLIER_WITH_CONFORMIST_ERROR_MESSAGE = "The CONFORMIST pattern is not applicable for a Customer-Supplier relationship.";
	public static final String CUSTOMER_SUPPLIER_WITH_OHS_ERROR_MESSAGE = "The OPEN-HOST SERVICE pattern is not applicable for a Customer-Supplier relationship.";
	public static final String CUSTOMER_SUPPLIER_WITH_ACL_WARNING_MESSAGE = "Are you sure you need an ANTICORRUPTION LAYER here? This pattern should not be necessarily needed in a Customer-Supplier relationship.";
	public static final String RELATIONSHIP_CONTEXT_NOT_ON_MAP_ERROR_MESSAGE = "The Bounded Context '%s' is not part of the Context Map.";
	public static final String ORGANIZATIONAL_MAP_CONTEXT_IS_NOT_TYPE_TEAM = "A Bounded Context of type '%s' is not allowed on organizational maps. Please use Contexts of type 'Team'.";
	public static final String SYSTEM_LANDSCAPE_MAP_CONTAINS_TEAM = "A Bounded Context of type 'TEAM' is not allowed on a SYSTEM_LANDSCAPE map.";
	public static final String ONLY_TEAMS_CAN_REALIZE_OTHER_BOUNDED_CONTEXT = "Only teams can realize bounded contexts. '%s' is not a team!";
	public static final String EXPOSED_AGGREGATE_NOT_PART_OF_UPSTREAM_CONTEXT = "The aggregate '%s' is not part of the upstream context '%s'.";
	public static final String SELF_RELATIONSHIP_NOT_ALLOWED = "Bounded context relationships must be declared between two different bounded contexts.";
	public static final String OWNER_BC_IS_NOT_TEAM = "'%s' is not a team. The owner attribute must refer to a Bounded Context representing a team (type = TEAM)!";
	public static final String AGGREGATE_CAN_ONLY_HAVE_ONE_AGGREGATE_ROOT = "Your aggregate '%s' contains multiple aggregate roots. An aggregate must only contain one root.";
	public static final String ALREADY_IMPLEMENTED_SUBDOMAIN = "The subdomain '%s' is already implemented through its domain '%s'.";
	public static final String MULTIPLE_DOMAINS_IMPLEMENTED = "Are you sure you want to implement multiple Domains within one Bounded Context? A Bounded Context should typically implement only a part of your Domain; one or multiple Subdomains.";

	/* Uniqueness problems */
	public static final String BOUNDED_CONTEXT_NAME_NOT_UNIQUE = "Multiple bounded contexts with the name '%s' have been declared.";
	public static final String AGGREGATE_NAME_NOT_UNIQUE = "Multiple aggregates with the name '%s' have been declared.";
	public static final String MODULE_NAME_NOT_UNIQUE = "Duplicate name. There is already an existing Module named '%s'.";
	public static final String USE_CASE_NAME_NOT_UNIQUE = "Multiple use cases with the name '%s' have been declared.";
	public static final String DOMAIN_OBJECT_NOT_UNIQUE = "Multiple domain objects with the name '%s' have been declared.";
	public static final String SUBDOMAIN_OBJECT_NOT_UNIQUE = "Multiple subdomains with the name '%s' have been declared.";
	public static final String SERVICE_NAME_NOT_UNIQUE_IN_BC = "Multiple services with the name '%s' have been declared in this Bounded Context.";
	public static final String SERVICE_NAME_NOT_UNIQUE_IN_SUBDOMAIN = "Multiple services with the name '%s' have been declared in this Subdomain.";

	/* Generator problems */
	public static final String EMPTY_UML_COMPONENT_DIAGRAM_MESSAGE = "Sorry, we cannot generate a component diagram. Your Context Map seems to be empty.";
	public static final String EMPTY_UML_CLASS_DIAGRAM_MESSAGE = "Sorry, we cannot generate a class diagram for this Bounded Context. There are no Aggregates or none of the Aggregates contain any domain objects (entities, value objects or domain events).";

	/* General input problems */
	public static final String ENTITY_NAME_CONTAINS_INVALID_CHARACTERS = "The entity name should consist of the following characters: a-z, A-Z, 0-9, _. Please rename the entity accordingly before applying transformations.";
	public static final String VERB_CONTAINS_INVALID_CHARACTERS = "The verb should consist of the following characters: a-z, A-Z, 0-9, _. Please rename it accordingly before applying transformations.";
	
	/* Warnings and suggestions */
	public static final String MODULE_CONTAINS_POTENTIALLY_IGNORED_OBJECTS = "The module '%s' contains Services and/or Entities that are not part of an Aggregate. The MDSL and Service Cutter generators ignore them. Please move them into an Aggregate.";
}

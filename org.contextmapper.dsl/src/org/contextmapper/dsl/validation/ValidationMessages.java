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
	public static final String CUSTOMER_SUPPLIER_NOT_ALLOW_OHS_ACL_CONFORMIST_ERROR_MESSAGE = "Two contexts within a Customer-Supplier relationship should not implement OHS/ACL/CONFORMIST.";
	public static final String RELATIONSHIP_CONTEXT_NOT_ON_MAP_ERROR_MESSAGE = "The Bounded Context '%s' is not part of the Context Map.";
	public static final String ORGANIZATIONAL_MAP_CONTEXT_IS_NOT_TYPE_TEAM = "A Bounded Context of type '%s' is not allowed on organizational maps. Please use Contexts of type 'Team'.";
	public static final String SYSTEM_LANDSCAPE_MAP_CONTAINS_TEAM = "A Bounded Context of type 'TEAM' is not allowed on a SYSTEM_LANDSCAPE map.";
	public static final String ONLY_TEAMS_CAN_REALIZE_OTHER_BOUNDED_CONTEXT = "Only teams can realize bounded contexts. '%s' is not a team!";
}

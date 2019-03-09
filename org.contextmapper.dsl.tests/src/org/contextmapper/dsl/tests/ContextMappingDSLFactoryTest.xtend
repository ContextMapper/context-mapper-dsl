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
package org.contextmapper.dsl.tests

import com.google.inject.Inject
import org.contextmapper.dsl.contextMappingDSL.BoundedContext
import org.contextmapper.dsl.contextMappingDSL.ContextMap
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.contextmapper.dsl.contextMappingDSL.CustomerSupplierRelationship
import org.contextmapper.dsl.contextMappingDSL.Partnership
import org.contextmapper.dsl.contextMappingDSL.Relationship
import org.contextmapper.dsl.contextMappingDSL.SharedKernel
import org.contextmapper.dsl.contextMappingDSL.Subdomain
import org.contextmapper.dsl.contextMappingDSL.SymmetricRelationship
import org.contextmapper.dsl.contextMappingDSL.UpstreamDownstreamRelationship
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.junit.jupiter.api.Assertions.*
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLPackage
import org.contextmapper.dsl.contextMappingDSL.UpstreamRole
import org.contextmapper.dsl.contextMappingDSL.DownstreamRole
import org.contextmapper.dsl.contextMappingDSL.ContextMapState
import org.contextmapper.dsl.contextMappingDSL.ContextMapType
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType
import org.contextmapper.dsl.contextMappingDSL.SubDomainType

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class ContextMappingDSLFactoryTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper

	ValidationTestHelper validationTestHelper = new ValidationTestHelper();

	@Test
	def void canCreateContextMappingModel() {
		// given
		val factory = ContextMappingDSLFactory.eINSTANCE
		// when
		val contextMappingModel = factory.createContextMappingModel
		// then
		assertTrue(contextMappingModel instanceof ContextMappingModel);
	}

	@Test
	def void canCreateContextMap() {
		// given
		val factory = ContextMappingDSLFactory.eINSTANCE
		// when
		val contextMap = factory.createContextMap
		// then
		assertTrue(contextMap instanceof ContextMap);
	}

	@Test
	def void canCreateBoundedContext() {
		// given
		val factory = ContextMappingDSLFactory.eINSTANCE
		// when
		val boundedContext = factory.createBoundedContext
		// then
		assertTrue(boundedContext instanceof BoundedContext);
	}

	@Test
	def void canCreateSubdomain() {
		// given
		val factory = ContextMappingDSLFactory.eINSTANCE
		// when
		val subdomain = factory.createSubdomain
		// then
		assertTrue(subdomain instanceof Subdomain);
	}

	@Test
	def void canCreateRelationship() {
		// given
		val factory = ContextMappingDSLFactory.eINSTANCE
		// when
		val relationship = factory.createRelationship
		// then
		assertTrue(relationship instanceof Relationship);
	}

	@Test
	def void canCreateSymmetricRelationship() {
		// given
		val factory = ContextMappingDSLFactory.eINSTANCE
		// when
		val relationship = factory.createSymmetricRelationship
		// then
		assertTrue(relationship instanceof SymmetricRelationship);
	}

	@Test
	def void canCreatePartnership() {
		// given
		val factory = ContextMappingDSLFactory.eINSTANCE
		// when
		val partnership = factory.createPartnership
		// then
		assertTrue(partnership instanceof Partnership);
	}

	@Test
	def void canCreateSharedKernel() {
		// given
		val factory = ContextMappingDSLFactory.eINSTANCE
		// when
		val sharedKernel = factory.createSharedKernel
		// then
		assertTrue(sharedKernel instanceof SharedKernel);
	}

	@Test
	def void canCreateUpstreamDownstreamRelationship() {
		// given
		val factory = ContextMappingDSLFactory.eINSTANCE
		// when
		val upstreamDownstreamRelationship = factory.createUpstreamDownstreamRelationship
		// then
		assertTrue(upstreamDownstreamRelationship instanceof UpstreamDownstreamRelationship);
	}

	@Test
	def void canCreateCustomerSupplierRelationship() {
		// given
		val factory = ContextMappingDSLFactory.eINSTANCE
		// when
		val customerSupplierRelationship = factory.createCustomerSupplierRelationship
		// then
		assertTrue(customerSupplierRelationship instanceof CustomerSupplierRelationship);
	}

	@Test
	def void canCreateByType() {
		// given
		val factory = ContextMappingDSLFactory.eINSTANCE
		// when
		val relationship = factory.create(ContextMappingDSLPackage.Literals.RELATIONSHIP);
		val symmetricRelationship = factory.create(ContextMappingDSLPackage.Literals.SYMMETRIC_RELATIONSHIP);
		// then
		assertTrue(relationship instanceof Relationship);
		assertTrue(symmetricRelationship instanceof SymmetricRelationship);
	}

	@Test
	def void createUpstreamRoleFromString() {
		// given
		val factory = ContextMappingDSLFactory.eINSTANCE
		// when
		val upstreamRoleOHS = factory.createFromString(ContextMappingDSLPackage.Literals.UPSTREAM_ROLE,
			"OHS");
		val asString = factory.convertToString(ContextMappingDSLPackage.Literals.UPSTREAM_ROLE, upstreamRoleOHS);
		// then
		assertTrue(upstreamRoleOHS.equals(UpstreamRole.OPEN_HOST_SERVICE));
		assertEquals("OHS", asString);
	}

	@Test
	def void createDownstreamRoleFromString() {
		// given
		val factory = ContextMappingDSLFactory.eINSTANCE
		// when
		val downstreamRoleCONFORMIST = factory.createFromString(ContextMappingDSLPackage.Literals.DOWNSTREAM_ROLE,
			"CF");
		val asString = factory.convertToString(ContextMappingDSLPackage.Literals.DOWNSTREAM_ROLE,
			downstreamRoleCONFORMIST);
		// then
		assertTrue(downstreamRoleCONFORMIST.equals(DownstreamRole.CONFORMIST));
		assertEquals("CF", asString);
	}

	@Test
	def void createContextMapStateFromString() {
		// given
		val factory = ContextMappingDSLFactory.eINSTANCE
		// when
		val contextMapState = factory.createFromString(ContextMappingDSLPackage.Literals.CONTEXT_MAP_STATE, "AS_IS");
		val asString = factory.convertToString(ContextMappingDSLPackage.Literals.CONTEXT_MAP_STATE, contextMapState);
		// then
		assertTrue(contextMapState.equals(ContextMapState.AS_IS));
		assertEquals("AS_IS", asString);
	}

	@Test
	def void createContextMapTypeFromString() {
		// given
		val factory = ContextMappingDSLFactory.eINSTANCE
		// when
		val contextMapType = factory.createFromString(ContextMappingDSLPackage.Literals.CONTEXT_MAP_TYPE,
			"SYSTEM_LANDSCAPE");
		val asString = factory.convertToString(ContextMappingDSLPackage.Literals.CONTEXT_MAP_TYPE, contextMapType);
		// then
		assertTrue(contextMapType.equals(ContextMapType.SYSTEM_LANDSCAPE));
		assertEquals("SYSTEM_LANDSCAPE", asString);
	}

	@Test
	def void createBoundedContextTypeFromString() {
		// given
		val factory = ContextMappingDSLFactory.eINSTANCE
		// when
		val boundedContextType = factory.createFromString(ContextMappingDSLPackage.Literals.BOUNDED_CONTEXT_TYPE,
			"APPLICATION");
		val asString = factory.convertToString(ContextMappingDSLPackage.Literals.BOUNDED_CONTEXT_TYPE,
			boundedContextType);
		// then
		assertTrue(boundedContextType.equals(BoundedContextType.APPLICATION));
		assertEquals("APPLICATION", asString);
	}

	@Test
	def void createSubDomainTypeFromString() {
		// given
		val factory = ContextMappingDSLFactory.eINSTANCE
		// when
		val subdomainType = factory.createFromString(ContextMappingDSLPackage.Literals.SUB_DOMAIN_TYPE, "CORE_DOMAIN");
		val asString = factory.convertToString(ContextMappingDSLPackage.Literals.SUB_DOMAIN_TYPE, subdomainType);
		// then
		assertTrue(subdomainType.equals(SubDomainType.CORE_DOMAIN));
		assertEquals("CORE_DOMAIN", asString);
	}

}

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
package org.contextmapper.dsl.generators.plantuml;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.SubDomainType;
import org.contextmapper.dsl.contextMappingDSL.Subdomain;
import org.contextmapper.dsl.generator.plantuml.PlantUMLSubdomainClassDiagramCreator;
import org.contextmapper.tactic.dsl.tacticdsl.Attribute;
import org.contextmapper.tactic.dsl.tacticdsl.CollectionType;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlantUMLSubdomainClassDiagramCreatorTest extends AbstractCMLInputFileTest {

	private PlantUMLSubdomainClassDiagramCreator creator;

	@BeforeEach
	public void prepare() {
		super.prepare();
		this.creator = new PlantUMLSubdomainClassDiagramCreator("TestDomain");
	}

	@Test
	public void canCreatePackageFromSubdomain() {
		// given
		Subdomain subdomain1 = ContextMappingDSLFactory.eINSTANCE.createSubdomain();
		Subdomain subdomain2 = ContextMappingDSLFactory.eINSTANCE.createSubdomain();
		Subdomain subdomain3 = ContextMappingDSLFactory.eINSTANCE.createSubdomain();

		subdomain1.setName("myTestSubdomain1");
		subdomain2.setName("myTestSubdomain2");
		subdomain3.setName("myTestSubdomain3");

		subdomain1.setType(SubDomainType.CORE_DOMAIN);
		subdomain2.setType(SubDomainType.GENERIC_SUBDOMAIN);
		subdomain3.setType(SubDomainType.SUPPORTING_DOMAIN);

		// when
		String plantUML1 = this.creator.createDiagram(subdomain1);
		String plantUML2 = this.creator.createDiagram(subdomain2);
		String plantUML3 = this.creator.createDiagram(subdomain3);

		// then
		assertTrue(plantUML1.contains("package \"'myTestSubdomain1' Core Domain\" <<Rectangle>>"));
		assertTrue(plantUML2.contains("package \"'myTestSubdomain2' Generic Subdomain\" <<Rectangle>>"));
		assertTrue(plantUML3.contains("package \"'myTestSubdomain3' Supporting Domain\" <<Rectangle>>"));
	}

	@Test
	public void canCreateClasses4Entities() {
		// given
		Subdomain subdomain = ContextMappingDSLFactory.eINSTANCE.createSubdomain();
		subdomain.setName("MyTestSubdomain");
		Entity entity = TacticdslFactory.eINSTANCE.createEntity();
		entity.setName("Test");
		Attribute attribute = TacticdslFactory.eINSTANCE.createAttribute();
		attribute.setType("int");
		attribute.setName("amount");
		entity.getAttributes().add(attribute);
		Attribute listAttribute = TacticdslFactory.eINSTANCE.createAttribute();
		listAttribute.setCollectionType(CollectionType.LIST);
		listAttribute.setName("myList");
		listAttribute.setType("String");
		entity.getAttributes().add(listAttribute);
		subdomain.getEntities().add(entity);

		// when
		String plantUML = this.creator.createDiagram(subdomain);

		// then
		assertTrue(plantUML.contains("	class Test <<(E,DarkSeaGreen) Entity>> {" + System.lineSeparator() + "		int amount" + System.lineSeparator() + "		List<String> myList"
				+ System.lineSeparator() + "	}" + System.lineSeparator()));
	}

	@Test
	public void canCreateLegend() {
		// given
		Subdomain subdomain = ContextMappingDSLFactory.eINSTANCE.createSubdomain();
		subdomain.setName("MyTestSubdomain");
		String domainVisionStatement = "this is the vision of this test subdomain ...";
		subdomain.setDomainVisionStatement(domainVisionStatement);

		// when
		String plantUML = this.creator.createDiagram(subdomain);

		// then
		assertTrue(plantUML.contains("legend left"));
		assertTrue(plantUML.contains("  This subdomain is part of the 'TestDomain' domain."));
		assertTrue(plantUML.contains("  " + domainVisionStatement));
		assertTrue(plantUML.contains("end legend"));
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/plantuml/";
	}

}

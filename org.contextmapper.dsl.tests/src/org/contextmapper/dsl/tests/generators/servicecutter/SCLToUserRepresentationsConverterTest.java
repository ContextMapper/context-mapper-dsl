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
package org.contextmapper.dsl.tests.generators.servicecutter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.file.Paths;

import org.contextmapper.dsl.generator.servicecutter.input.converter.SCLToUserRepresentationsConverter;
import org.contextmapper.servicecutter.dsl.ServiceCutterConfigurationDSLStandaloneSetup;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterUserRepresentationsModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.Test;

import ch.hsr.servicecutter.api.model.UserRepresentationContainer;

public class SCLToUserRepresentationsConverterTest {

	private static final String TEST_SCL_FILE = "/integ-test-files/servicecutter/DDD_Sample_ServiceCutter-User-Representations.scl";

	@Test
	void canConvertSCLFileToServiceCutterUserRepresentations() {
		// given
		ServiceCutterConfigurationDSLStandaloneSetup.doSetup();
		Resource resource = new ResourceSetImpl().getResource(URI.createURI(new File(Paths.get("").toAbsolutePath().toString(), TEST_SCL_FILE).getAbsolutePath()), true);
		ServiceCutterUserRepresentationsModel sclModel = (ServiceCutterUserRepresentationsModel) resource.getContents().get(0);
		
		// when
		UserRepresentationContainer container = new SCLToUserRepresentationsConverter().convert(sclModel);
		
		// then
		assertEquals(4, container.getAggregates().size());
		assertEquals(0, container.getCompatibilities().getAvailabilityCriticality().size());
		assertEquals(0, container.getCompatibilities().getConsistencyCriticality().size());
		assertEquals(2, container.getCompatibilities().getContentVolatility().size());
		assertEquals(0, container.getCompatibilities().getSecurityCriticality().size());
		assertEquals(0, container.getCompatibilities().getStorageSimilarity().size());
		assertEquals(1, container.getCompatibilities().getStructuralVolatility().size());
		assertEquals(0, container.getEntities().size());
		assertEquals(3, container.getPredefinedServices().size());
		assertEquals(0, container.getSecurityAccessGroups().size());
		assertEquals(0, container.getSeparatedSecurityZones().size());
		assertEquals(4, container.getSharedOwnerGroups().size());
		assertEquals(9, container.getUseCases().size());
	}

}

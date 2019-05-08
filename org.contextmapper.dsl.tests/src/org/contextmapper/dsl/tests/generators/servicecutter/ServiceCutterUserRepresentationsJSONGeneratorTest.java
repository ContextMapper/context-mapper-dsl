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
package org.contextmapper.dsl.tests.generators.servicecutter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.contextmapper.dsl.generator.ServiceCutterUserRepresentationsJSONGenerator;
import org.contextmapper.dsl.tests.generators.mocks.IFileSystemAccess2Mock;
import org.contextmapper.dsl.tests.generators.mocks.IGeneratorContextMock;
import org.contextmapper.dsl.tests.generators.mocks.ServiceCutterConfigurationModelResourceMock;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterConfigurationDSLFactory;
import org.contextmapper.servicecutter.dsl.serviceCutterConfigurationDSL.ServiceCutterUserRepresentationsModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceCutterUserRepresentationsJSONGeneratorTest {

	private ServiceCutterUserRepresentationsJSONGenerator generator;

	@BeforeEach
	public void prepare() {
		this.generator = new ServiceCutterUserRepresentationsJSONGenerator();
	}

	@Test
	public void canCreateUserRepresentationsJSONFile() {
		// given
		ServiceCutterUserRepresentationsModel model = ServiceCutterConfigurationDSLFactory.eINSTANCE.createServiceCutterUserRepresentationsModel();

		// when
		IFileSystemAccess2Mock filesystem = new IFileSystemAccess2Mock();
		this.generator.doGenerate(new ServiceCutterConfigurationModelResourceMock(model, "testmodel", "scl"), filesystem, new IGeneratorContextMock());

		// then
		assertTrue(filesystem.getGeneratedFilesMap().containsKey("testmodel.json"));
	}

}

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
package org.contextmapper.dsl.generators.mdsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;

import org.contextmapper.dsl.AbstractCMLInputFileTest;
import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.contextmapper.dsl.generator.mdsl.MDSLModelCreator;
import org.contextmapper.dsl.generator.mdsl.model.DataType;
import org.contextmapper.dsl.generator.mdsl.model.EndpointClient;
import org.contextmapper.dsl.generator.mdsl.model.EndpointContract;
import org.contextmapper.dsl.generator.mdsl.model.EndpointOffer;
import org.contextmapper.dsl.generator.mdsl.model.EndpointOperation;
import org.contextmapper.dsl.generator.mdsl.model.EndpointProvider;
import org.contextmapper.dsl.generator.mdsl.model.IntegrationScenario;
import org.contextmapper.dsl.generator.mdsl.model.OrchestrationFlow;
import org.contextmapper.dsl.generator.mdsl.model.ServiceSpecification;
import org.contextmapper.dsl.generator.mdsl.model.Story;
import org.junit.jupiter.api.Test;

public class MDSLModelCreatorTest extends AbstractCMLInputFileTest {

	@Test
	void canCreateMDSLModel() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("basic-mdsl-model-test.cml");
		MDSLModelCreator mdslCreator = new MDSLModelCreator(input.getContextMappingModel());

		// when
		List<ServiceSpecification> serviceSpecifications = mdslCreator.createServiceSpecifications();

		// then
		assertEquals(1, serviceSpecifications.size());

		ServiceSpecification spec = serviceSpecifications.get(0);
		assertEquals("CustomerManagementContextAPI", spec.getName());
		assertEquals(1, spec.getEndpoints().size());

		EndpointContract endpoint = spec.getEndpoints().get(0);
		assertEquals("Customers", endpoint.getName());
		assertEquals(2, endpoint.getOperations().size());

		EndpointOperation operation1 = endpoint.getOperations().get(0);
		assertEquals("updateAddress", operation1.getName());

		EndpointOperation operation2 = endpoint.getOperations().get(1);
		assertEquals("anotherMethod", operation2.getName());

		assertEquals(5, spec.getDataTypes().size());
		DataType dataType1 = spec.getDataTypes().get(0);
		assertEquals("Address", dataType1.getName());
		DataType dataType2 = spec.getDataTypes().get(1);
		assertEquals("Parameter1Type", dataType2.getName());
		DataType dataType3 = spec.getDataTypes().get(2);
		assertEquals("Parameter2Type", dataType3.getName());
		DataType dataType4 = spec.getDataTypes().get(3);
		assertEquals("ReturnType", dataType4.getName());
		DataType dataType5 = spec.getDataTypes().get(4);
		assertEquals("anotherMethodParameter", dataType5.getName());

		assertEquals("Address", operation1.getExpectingPayload().getName());
		assertEquals("ReturnType", operation1.getDeliveringPayload().getName());
		assertEquals("anotherMethodParameter", operation2.getExpectingPayload().getName());

		assertEquals(1, spec.getProviders().size());
		EndpointProvider provider = spec.getProviders().get(0);
		assertEquals("CustomerManagementContextProvider", provider.getName());
		assertEquals(1, provider.getEndpointOffers().size());
		EndpointOffer contractOffered = provider.getEndpointOffers().get(0);
		assertEquals("Customers", contractOffered.getOfferedEndpoint().getName());
		assertEquals("http://localhost:8000", contractOffered.getLocation());
		assertEquals("RESTful HTTP", contractOffered.getProtocol());

		assertEquals(1, spec.getClients().size());
		EndpointClient client = spec.getClients().get(0);
		assertEquals("ContractManagementContextClient", client.getName());
		assertEquals(1, client.getConsumedOfferNames().size());
		assertEquals("Customers", client.getConsumedOfferNames().get(0));
	}
	
	@Test
	void canCreateMDSLModelWithFlow() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("application-flow-example.cml");
		MDSLModelCreator mdslCreator = new MDSLModelCreator(input.getContextMappingModel());

		// when
		List<ServiceSpecification> serviceSpecifications = mdslCreator.createServiceSpecifications();

		// then
		assertEquals(1, serviceSpecifications.size());

		ServiceSpecification spec = serviceSpecifications.get(0);
		assertEquals("SampleSystemAPI", spec.getName());
		assertEquals(2, spec.getEndpoints().size()); // one endpoint for flow, one endpoint for aggregate
		
		EndpointContract endpoint = spec.getEndpoints().get(0);
		assertEquals("SampleApplication", endpoint.getName());
		assertEquals(6, endpoint.getOperations().size()); // one operation for each flow step
		
		OrchestrationFlow flow = spec.getFlows().get(0);
		assertEquals("SampleFlow", flow.getName());
		assertEquals(9, flow.getSteps().size()); // must match number of steps in CML input
	}

	@Test
	void canCreateMDSLModelWithoutContextMap() throws IOException {
		// given
		CMLResource input = getResourceCopyOfTestCML("basic-mdsl-model-test-without-contextmap.cml");
		MDSLModelCreator mdslCreator = new MDSLModelCreator(input.getContextMappingModel());

		// when
		List<ServiceSpecification> serviceSpecifications = mdslCreator.createServiceSpecifications();

		// then
		assertEquals(1, serviceSpecifications.size());

		ServiceSpecification spec = serviceSpecifications.get(0);
		assertEquals("CustomerManagementContextAPI", spec.getName());
		assertEquals(1, spec.getEndpoints().size());

		EndpointContract endpoint = spec.getEndpoints().get(0);
		assertEquals("Customers", endpoint.getName());
		assertEquals(2, endpoint.getOperations().size());

		EndpointOperation operation1 = endpoint.getOperations().get(0);
		assertEquals("updateAddress", operation1.getName());

		EndpointOperation operation2 = endpoint.getOperations().get(1);
		assertEquals("anotherMethod", operation2.getName());

		assertEquals(5, spec.getDataTypes().size());
		DataType dataType1 = spec.getDataTypes().get(0);
		assertEquals("Address", dataType1.getName());
		DataType dataType2 = spec.getDataTypes().get(1);
		assertEquals("Parameter1Type", dataType2.getName());
		DataType dataType3 = spec.getDataTypes().get(2);
		assertEquals("Parameter2Type", dataType3.getName());
		DataType dataType4 = spec.getDataTypes().get(3);
		assertEquals("ReturnType", dataType4.getName());
		DataType dataType5 = spec.getDataTypes().get(4);
		assertEquals("anotherMethodParameter", dataType5.getName());

		assertEquals("Address", operation1.getExpectingPayload().getName());
		assertEquals("ReturnType", operation1.getDeliveringPayload().getName());
		assertEquals("anotherMethodParameter", operation2.getExpectingPayload().getName());

		assertEquals(1, spec.getProviders().size());
		EndpointProvider provider = spec.getProviders().get(0);
		assertEquals("CustomerManagementContextProvider", provider.getName());
		assertEquals(1, provider.getEndpointOffers().size());
		EndpointOffer contractOffered = provider.getEndpointOffers().get(0);
		assertEquals("Customers", contractOffered.getOfferedEndpoint().getName());
		assertEquals("http://localhost:8000", contractOffered.getLocation());
	}

	@Test
	void doesOnlyCreateOneDataTypeIfSameNameApearsMultipleTimes() throws IOException {
		// given
		String inputModelName = "same-data-type-in-multiple-methods.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);
		MDSLModelCreator mdslCreator = new MDSLModelCreator(input.getContextMappingModel());

		// when
		List<ServiceSpecification> serviceSpecifications = mdslCreator.createServiceSpecifications();

		// then
		assertEquals(1, serviceSpecifications.size());
		assertEquals(1, serviceSpecifications.get(0).getDataTypes().size());
		assertEquals("Address", serviceSpecifications.get(0).getDataTypes().get(0).getName());
	}

	@Test
	void createsOnlyOneAPIForUpstreamContextWhichOccursInMultipleRelationships() throws IOException {
		// given
		String inputModelName = "context-is-upstream-in-multiple-relationships.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);
		MDSLModelCreator mdslCreator = new MDSLModelCreator(input.getContextMappingModel());

		// when
		List<ServiceSpecification> serviceSpecifications = mdslCreator.createServiceSpecifications();

		// then
		assertEquals(1, serviceSpecifications.size());
		assertEquals(2, serviceSpecifications.get(0).getClients().size());
	}

	@Test
	void canHandleReferencesInMethodTypes() throws IOException {
		// given
		String inputModelName = "use-references-in-methods.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);
		MDSLModelCreator mdslCreator = new MDSLModelCreator(input.getContextMappingModel());

		// when
		List<ServiceSpecification> serviceSpecifications = mdslCreator.createServiceSpecifications();

		// then
		assertEquals(1, serviceSpecifications.size());
		ServiceSpecification spec = serviceSpecifications.get(0);
		assertEquals(2, spec.getDataTypes().size());
		DataType dataType1 = spec.getDataTypes().get(0);
		DataType dataType2 = spec.getDataTypes().get(1);
		assertEquals("Address", dataType1.getName());
		assertEquals("ReturnType", dataType2.getName());
	}

	@Test
	void canHandleMDSLKeywords() throws IOException {
		// given
		String inputModelName = "mdsl-can-handle-keyword-clashes.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);
		MDSLModelCreator mdslCreator = new MDSLModelCreator(input.getContextMappingModel());

		// when
		List<ServiceSpecification> serviceSpecifications = mdslCreator.createServiceSpecifications();

		// then
		assertEquals(1, serviceSpecifications.size());
		ServiceSpecification spec = serviceSpecifications.get(0);
		assertEquals(2, spec.getDataTypes().size());
		DataType dataType1 = spec.getDataTypes().get(0);
		DataType dataType2 = spec.getDataTypes().get(1);
		assertEquals("ReturnType", dataType1.getName());
		assertEquals("^Link", dataType2.getName());
	}

	@Test
	void throwExceptionIfThereAreNoOperations() throws IOException {
		// given
		String inputModelName = "no-operation.cml";
		CMLResource input = getResourceCopyOfTestCML(inputModelName);
		MDSLModelCreator mdslCreator = new MDSLModelCreator(input.getContextMappingModel());

		// when -> then throw exception
		assertThrows(GeneratorInputException.class, () -> {
			mdslCreator.createServiceSpecifications();
		});
	}
	
	@Test
	void canCreateUpdateAndReadModelWithScenarioAndStory() {
		// given 
		ServiceSpecification mdslModel = new ServiceSpecification();
		IntegrationScenario scenarioModel = new org.contextmapper.dsl.generator.mdsl.model.IntegrationScenario();
		scenarioModel.setName("SampleScenarioName");
		Story storyModel = new org.contextmapper.dsl.generator.mdsl.model.Story("SampleStoryName", "SampleActor", "SampleAction", "SampleGoal"); 

		//when 
		scenarioModel.addStory(storyModel);
		mdslModel.addScenario(scenarioModel);

		assertEquals("SampleScenarioName", mdslModel.getScenarios().get(0).getName());
		assertEquals("SampleStoryName", scenarioModel.getStories().get(0).getName());
		
		// when
		storyModel.setName("NewName");
		storyModel.setPersona("NewActor");
		storyModel.setAction("NewAction");
		storyModel.setGoal("NewGoal");

		assertEquals("NewName", scenarioModel.getStories().get(0).getName());
		assertEquals("NewActor", scenarioModel.getStories().get(0).getPersona());
		assertEquals("NewAction", scenarioModel.getStories().get(0).getAction());
		assertEquals("NewGoal", scenarioModel.getStories().get(0).getGoal());
	}

	@Override
	protected String getTestFileDirectory() {
		return "/integ-test-files/mdsl/";
	}

}

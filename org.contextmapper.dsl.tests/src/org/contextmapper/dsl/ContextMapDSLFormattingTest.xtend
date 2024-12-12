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
package org.contextmapper.dsl

import com.google.inject.Inject
import org.contextmapper.dsl.contextMappingDSL.ContextMappingModel
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.junit.jupiter.api.Assertions.*
import org.contextmapper.dsl.tests.ContextMappingDSLInjectorProvider
import org.eclipse.xtext.resource.SaveOptions
import org.eclipse.xtext.serializer.ISerializer
import org.contextmapper.dsl.standalone.ContextMapperStandaloneSetup
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory

@ExtendWith(InjectionExtension)
@InjectWith(ContextMappingDSLInjectorProvider)
class ContextMapDSLFormattingTest {
	@Inject
	ParseHelper<ContextMappingModel> parseHelper
	@Inject extension ISerializer

	/**
	 * Aggregate with entities, traits, basic-types and enums with interspersed comments.
	 * The traits are used with the 'with' keyword
	 */
	@Test
	def void formatsMapLoadedFromFile() {
		val testInput = '''
			BoundedContext BC1 { Aggregate A1 {  Entity E1 { String prop1 "prop1Comment" String prop2 }
			        enum State1 { A, B } Trait T1 {}
			         "State2Comment" enum State2 { "AA" A, B } "E2Comment" Entity E2 
			         with@T1 { package = abc validate = "validation" aggregateRoot "E2Comment" 
			                                  String prop3 }  Entity E3 { String prop1 }  
			                                  Trait T2 { "prop3Comment" String prop3 "ref1Comment" - @E1 ref1 }
			                                    BasicType BT1 { "prop4Comment" int prop4 - List< @T2>     prop5 }
			                    "V1Comment"  ValueObject V1 with@T1 with@T2 { int intProp }}} BoundedContext BC2
			  { Aggregate A2 { Entity E3 extends @E2 { "prop5comment" String prop5 String prop6 "prop6comment" int prop6}}}
			        ContextMap TestContextMap  { contains BC1 }
		''';
		val expectedResult = '''
			ContextMap TestContextMap  {
				contains BC1
			}
			
			BoundedContext BC1 {
				Aggregate A1 {
					Entity E1 {
						String prop1
						"prop1Comment"
						String prop2
					}
					enum State1 {
						A, B
					}
					Trait T1 {
					}
					"State2Comment"
					enum State2 {
						"AA" A, B
					}
					"E2Comment"
					Entity E2 with@T1 {
						package = abc
						validate = "validation"
						aggregateRoot
						"E2Comment"
						String prop3
					}
					Entity E3 {
						String prop1
					}
					Trait T2 {
						"prop3Comment"
						String prop3
						"ref1Comment"
						- @E1 ref1
					}
					BasicType BT1 {
						"prop4Comment"
						int prop4
						- List<@T2>     prop5
					}
					"V1Comment"
					ValueObject V1 with@T1 with@T2 {
						int intProp
					}
				}
			}
			
			BoundedContext BC2
			  {
				Aggregate A2 {
					Entity E3 extends @E2 {
						"prop5comment"
						String prop5
						String prop6
						"prop6comment"
						int prop6
					}
				}
			}
			
		     ''';
		// given
		val model = parseHelper.parse(testInput)
		// when, then
		assertEquals(expectedResult, model.serialize(SaveOptions.newBuilder.format().getOptions()))
	}

	@Test
	def void formatsMapLoadedFromAPI() {
		val contextMapper = ContextMapperStandaloneSetup.getStandaloneAPI();
		val factory = ContextMappingDSLFactory.eINSTANCE;
		val tacticdslFactory = TacticdslFactory.eINSTANCE;

		val resource = contextMapper.createCML("target/test-create-cm.cml");

		// given 
		val model = resource.getContextMappingModel();
		val contextMap = factory.createContextMap();
		contextMap.setName("TestContextMap");
		model.setMap(contextMap);

		val boundedContext = factory.createBoundedContext();
		boundedContext.setName("TestContext");
		contextMap.getBoundedContexts().add(boundedContext);

		val aggregate = factory.createAggregate();
		aggregate.setName("A1");
		model.getBoundedContexts().add(boundedContext);
		boundedContext.getAggregates().add(aggregate);

		val trait1 = tacticdslFactory.createTrait();
		trait1.setName("T1");
		aggregate.getDomainObjects().add(trait1);

		val entity1 = tacticdslFactory.createEntity();
		aggregate.getDomainObjects().add(entity1);
		entity1.setName("E100");
		entity1.setAggregateRoot(true);

		val entity2 = tacticdslFactory.createEntity();
		entity2.setExtends(entity1);
		entity2.setName("E2");
		entity2.getTraits().add(trait1);
		aggregate.getDomainObjects().add(entity2);

		val attribute1 = tacticdslFactory.createAttribute();
		attribute1.setName("prop1");
		attribute1.setType("String");
		attribute1.doc = "prop1Comment"
		entity2.getAttributes().add(attribute1);

		var reference1 = tacticdslFactory.createReference();
		reference1.setName("ref1");
		reference1.setDomainObjectType(entity2);
		entity2.getReferences().add(reference1);

		val entity3 = tacticdslFactory.createEntity();
		entity3.setExtends(entity1);
		entity3.setName("E3");
		aggregate.getDomainObjects().add(entity3);

		val expectedResult = '''
			ContextMap TestContextMap {
				contains TestContext
			}
			
			BoundedContext TestContext {
				Aggregate A1 {
					Trait T1
					Entity E100 {
						aggregateRoot
					}
					Entity E2 extends @E100 with@T1 {
						"prop1Comment"
						String prop1
						- @E2 ref1
					}
					Entity E3 extends @E100
				}
			}
			
		'''

		// when, then
		assertEquals(expectedResult, model.serialize(SaveOptions.newBuilder.format().getOptions()))
	}

}

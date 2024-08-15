/*
 * Copyright 2024 The Context Mapper Project Team
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
package org.contextmapper.dsl.refactoring.value_registers;

import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.Value;
import org.contextmapper.dsl.contextMappingDSL.ValueCluster;
import org.contextmapper.dsl.contextMappingDSL.ValueRegister;
import org.contextmapper.dsl.refactoring.AbstractRefactoring;
import org.contextmapper.dsl.refactoring.SemanticCMLRefactoring;
import org.eclipse.xtext.EcoreUtil2;

public class WrapValueInClusterRefactoring extends AbstractRefactoring implements SemanticCMLRefactoring {

	private String valueName;

	public WrapValueInClusterRefactoring(String valueName) {
		this.valueName = valueName;
	}

	@Override
	protected void doRefactor() {
		Value value = getSelectedValue();
		ValueRegister register = (ValueRegister) value.eContainer();

		register.getValues().remove(value);

		ValueCluster newCluster = ContextMappingDSLFactory.eINSTANCE.createValueCluster();
		newCluster.setName("Cluster_Name_To_Be_Changed");
		newCluster.setCoreValue(value.getName());
		newCluster.getValues().add(value);
		register.getValueClusters().add(newCluster);
	}

	private Value getSelectedValue() {
		return EcoreUtil2.<Value>getAllContentsOfType(model, Value.class).stream()
				.filter(s -> s.getName().equals(valueName)).findFirst().get();
	}

}

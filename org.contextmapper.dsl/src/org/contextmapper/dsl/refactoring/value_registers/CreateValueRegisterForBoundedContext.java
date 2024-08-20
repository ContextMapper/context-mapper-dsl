package org.contextmapper.dsl.refactoring.value_registers;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.ValueRegister;
import org.contextmapper.dsl.refactoring.AbstractRefactoring;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.EcoreUtil2;

public class CreateValueRegisterForBoundedContext extends AbstractRefactoring {

	private String contextName;

	public CreateValueRegisterForBoundedContext(String contextName) {
		this.contextName = contextName;
	}

	@Override
	protected void doRefactor() {
		if (!boundedContextAlreadyHasAValueRegister()) {
			BoundedContext selectedBC = getSelectedBoundedContext();
			if (selectedBC != null) {
				ValueRegister newVR = ContextMappingDSLFactory.eINSTANCE.createValueRegister();
				newVR.setName("ValueRegisterFor_" + contextName);
				newVR.setContext(selectedBC);
				model.getValueRegisters().add(newVR);
				addComment(selectedBC);
			} else {
				System.err.println("Did not find bounded context " + this.contextName);
			}
		}
	}

	private void addComment(BoundedContext boundedContext) {
		boundedContext.getResponsibilities().add("Compliance with values in value register"); // TODO improve formatter
	}

	private boolean boundedContextAlreadyHasAValueRegister() {
		EList<ValueRegister> vrs = this.model.getValueRegisters();
		for (ValueRegister vr : vrs) {
			if (vr.getContext() != null && vr.getContext().getName().equals(contextName)) {
				return true;
			}
		}
		return false;
	}

	private BoundedContext getSelectedBoundedContext() {
		return EcoreUtil2.<BoundedContext>getAllContentsOfType(model, BoundedContext.class).stream()
				.filter(s -> s.getName().equals(contextName)).findFirst().get();
	}

}

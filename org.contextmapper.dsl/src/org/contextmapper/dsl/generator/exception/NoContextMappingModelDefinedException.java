package org.contextmapper.dsl.generator.exception;

public class NoContextMappingModelDefinedException extends GeneratorInputException {

	public NoContextMappingModelDefinedException() {
		super("No CML model defined in this resource. Please select a file which contains a CML model.");
	}

}

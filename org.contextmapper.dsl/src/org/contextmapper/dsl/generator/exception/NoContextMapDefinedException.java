package org.contextmapper.dsl.generator.exception;

public class NoContextMapDefinedException extends GeneratorInputException {

	public NoContextMapDefinedException() {
		super("No Context Map defined in this model. Please select a file which contains a context map.");
	}

}

package org.contextmapper.dsl.generator.servicecutter.output.factory;

public class ServiceCutterOutputModelReadingException extends RuntimeException {

	private static final long serialVersionUID = -1103030709732713303L;

	public ServiceCutterOutputModelReadingException(String filePath) {
		super("Error reading ServiceCutter output file. The file '" + filePath
				+ "' does not seam to have the proper format.");
	}
}

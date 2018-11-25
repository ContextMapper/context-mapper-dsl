package org.contextmapper.dsl.tests.generators.mocks;

import org.eclipse.emf.common.util.URI;

public class URIMock extends URI {

	private String filename;
	private String extension;

	public URIMock(String filenameWithoutExt, String fileExtension) {
		super(0);
		this.filename = filenameWithoutExt;
		this.extension = fileExtension;
	}

	@Override
	public URI trimFileExtension() {
		return this;
	}

	@Override
	public String lastSegment() {
		return filename;
	}

}

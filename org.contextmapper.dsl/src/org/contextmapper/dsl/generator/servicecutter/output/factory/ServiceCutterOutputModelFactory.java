package org.contextmapper.dsl.generator.servicecutter.output.factory;

import java.io.File;

import org.contextmapper.dsl.generator.servicecutter.output.model.ServiceCutterOutputModel;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceCutterOutputModelFactory {

	private ObjectMapper objectMapper;

	public ServiceCutterOutputModelFactory() {
		this.objectMapper = new ObjectMapper();
	}

	public ServiceCutterOutputModel createFromJsonFile(URI jsonFileURI) {
		try {
			URI resolvedFile = CommonPlugin.resolve(jsonFileURI);
			IFile jsonFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(resolvedFile.toFileString()));
			return createFromJsonFile(jsonFile.getFullPath().toFile());
		} catch (Exception e) {
			throw new ServiceCutterOutputModelReadingException(jsonFileURI.toPlatformString(true));
		}
	}

	public ServiceCutterOutputModel createFromJsonFile(File jsonFile) {
		try {
			return objectMapper.readValue(jsonFile, ServiceCutterOutputModel.class);
		} catch (Exception e) {
			throw new ServiceCutterOutputModelReadingException(jsonFile.getAbsolutePath());
		}
	}

}

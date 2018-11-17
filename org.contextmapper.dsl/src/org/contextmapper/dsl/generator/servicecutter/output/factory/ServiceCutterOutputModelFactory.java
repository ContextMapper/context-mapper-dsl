package org.contextmapper.dsl.generator.servicecutter.output.factory;

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
			return objectMapper.readValue(jsonFile.getFullPath().toFile(), ServiceCutterOutputModel.class);
		} catch (Exception e) {
			throw new ServiceCutterOutputModelReadingException(jsonFileURI.toPlatformString(true));
		}
	}

}

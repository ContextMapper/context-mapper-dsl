package org.contextmapper.dsl.generator;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;

public class ServiceCutterInputGenerator extends AbstractGenerator {

	@Override
	public void doGenerate(final Resource resource, final IFileSystemAccess2 fsa, final IGeneratorContext context) {
		//List<ContextMappingModel> contextMappingModels = IteratorExtensions.<ContextMappingModel>toList(
		//		Iterators.<ContextMappingModel>filter(resource.getAllContents(), ContextMappingModel.class));
		StringConcatenation builder = new StringConcatenation();
		builder.append("Generator not yet implemented.");
		fsa.generateFile("serviceCutterInput.txt", builder);
	}

}

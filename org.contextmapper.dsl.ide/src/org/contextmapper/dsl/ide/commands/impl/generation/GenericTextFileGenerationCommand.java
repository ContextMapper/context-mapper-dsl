/*
 * Copyright 2020 The Context Mapper Project Team
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
package org.contextmapper.dsl.ide.commands.impl.generation;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.contextmapper.dsl.cml.CMLResourceContainer;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.generator.GenericContentGenerator;
import org.contextmapper.dsl.generator.exception.GeneratorInputException;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.xtext.generator.IGenerator2;
import org.eclipse.xtext.ide.server.Document;
import org.eclipse.xtext.ide.server.ILanguageServerAccess;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * This command calls the generic text file generator that generates an output
 * file based on a Freemarker template.
 * 
 * @author Stefan Kapferer
 *
 */
public class GenericTextFileGenerationCommand extends AbstractGenerationCommand {

	private GenericContentGenerator generator = new GenericContentGenerator();

	@Override
	IGenerator2 getGenerator() {
		return generator;
	}

	@Override
	public void executeCommand(CMLResourceContainer cmlResource, Document document, ILanguageServerAccess access, ExecuteCommandParams params) {
		if (params.getArguments().size() != 2 || params.getArguments().get(1).getClass() != JsonArray.class)
			throw new ContextMapperApplicationException(
					"This command expects a JSON array with the following values as second parameter: URI to Freemarker template, filename string for output file");

		JsonArray paramArray = (JsonArray) params.getArguments().get(1);
		JsonObject paramObject = paramArray.get(0).getAsJsonObject();
		try {
			URI templateURI = new URI(paramObject.get("templateUri").getAsString());
			if (!templateURI.toString().startsWith("file:"))
				throw new GeneratorInputException("Please provide a URI to a local file (Freemarker template)!");
			generator.setFreemarkerTemplateFile(Paths.get(templateURI).toFile());
			generator.setTargetFileName(paramObject.get("outputFileName").getAsString());
		} catch (URISyntaxException e) {
			throw new ContextMapperApplicationException("The passed template URI is not a valid URI.", e);
		}
		super.executeCommand(cmlResource, document, access, params);
	}

}

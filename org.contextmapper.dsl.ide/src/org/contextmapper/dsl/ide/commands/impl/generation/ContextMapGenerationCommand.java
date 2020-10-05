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

import java.util.Set;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.exception.ContextMapperApplicationException;
import org.contextmapper.dsl.generator.ContextMapGenerator;
import org.contextmapper.dsl.generator.contextmap.ContextMapFormat;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.xtext.generator.IGenerator2;
import org.eclipse.xtext.ide.server.Document;
import org.eclipse.xtext.ide.server.ILanguageServerAccess;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * This command calls the (graphical) Context Map generator based on Graphviz.
 * 
 * @author Stefan Kapferer
 *
 */
public class ContextMapGenerationCommand extends AbstractGenerationCommand {

	private ContextMapGenerator generator = new ContextMapGenerator();

	@Override
	IGenerator2 getGenerator() {
		return generator;
	}

	@Override
	public void executeCommand(CMLResource cmlResource, Document document, ILanguageServerAccess access, ExecuteCommandParams params) {
		if (params.getArguments().size() != 2 || params.getArguments().get(1).getClass() != JsonArray.class)
			throw new ContextMapperApplicationException("This command expects a JSON array with the generator parameters as a second parameter.");

		JsonArray paramArray = (JsonArray) params.getArguments().get(1);
		JsonObject paramObject = paramArray.get(0).getAsJsonObject();
		ContextMapFormat[] formats = getFormatsFromInputArray(paramObject.get("formats").getAsJsonArray());
		boolean fixWidth = paramObject.get("fixWidth").getAsBoolean();
		boolean fixHeight = paramObject.get("fixHeight").getAsBoolean();
		boolean generateLabels = paramObject.get("generateLabels").getAsBoolean();
		int labelSpacingFactor = paramObject.get("labelSpacingFactor").getAsInt();
		boolean clusterTeams = paramObject.get("clusterTeams").getAsBoolean();

		generator.setContextMapFormats(formats);
		generator.setLabelSpacingFactor(labelSpacingFactor);
		generator.printAdditionalLabels(generateLabels);
		generator.clusterTeams(clusterTeams);
		if (fixWidth)
			generator.setWidth(paramObject.get("width").getAsInt());
		else if (fixHeight)
			generator.setHeight(paramObject.get("height").getAsInt());
		super.executeCommand(cmlResource, document, access, params);
	}

	private ContextMapFormat[] getFormatsFromInputArray(JsonArray formatsArray) {
		Set<ContextMapFormat> formats = Sets.newHashSet();
		for (JsonElement element : formatsArray) {
			formats.add(ContextMapFormat.valueOf(element.getAsString().toUpperCase()));
		}
		return formats.toArray(new ContextMapFormat[formats.size()]);
	}

}

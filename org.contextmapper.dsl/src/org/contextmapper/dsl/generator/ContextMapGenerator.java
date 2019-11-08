/*
 * Copyright 2019 The Context Mapper Project Team
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
package org.contextmapper.dsl.generator;

import static org.contextmapper.dsl.generator.contextmap.ContextMapFormat.DOT;
import static org.contextmapper.dsl.generator.contextmap.ContextMapFormat.PNG;
import static org.contextmapper.dsl.generator.contextmap.ContextMapFormat.SVG;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.contextmapper.contextmap.generator.model.ContextMap;
import org.contextmapper.dsl.generator.contextmap.ContextMapFormat;
import org.contextmapper.dsl.generator.contextmap.ContextMapModelConverter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.service.CommandRunner;
import guru.nidi.graphviz.service.SystemUtils;

public class ContextMapGenerator extends AbstractContextMapGenerator {

	private ContextMapFormat format = PNG;
	private int labelSpacingFactor = 5;
	private int width = -1;
	private int height = -1;
	private boolean useWidth = true;

	@Override
	protected void generateFromContextMap(org.contextmapper.dsl.contextMappingDSL.ContextMap cmlContextMap, IFileSystemAccess2 fsa, URI inputFileURI) {
		String fileName = inputFileURI.trimFileExtension().lastSegment();

		try {
			ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
			ContextMap contextMap = new ContextMapModelConverter().convert(cmlContextMap);
			org.contextmapper.contextmap.generator.ContextMapGenerator generator = createContextMapGenerator();
			generator.setLabelSpacingFactor(labelSpacingFactor);
			if (this.width > 0 && useWidth)
				generator.setWidth(width);
			else if (this.height > 0)
				generator.setHeight(height);
			generator.generateContextMapGraphic(contextMap, getGraphvizLibFormat(), outputstream);
			InputStream inputstream = new ByteArrayInputStream(outputstream.toByteArray());
			fsa.generateFile(fileName + "_ContextMap." + format.getFileExtension(), inputstream);
		} catch (IOException e) {
			throw new RuntimeException("An error occured while generating the Context Map!", e);
		}

	}

	/**
	 * Changes the format which will be generated when calling the generator.
	 * 
	 * @param format the requested format
	 */
	public void setContextMapFormat(ContextMapFormat format) {
		this.format = format;
	}

	/**
	 * Changes the spacing used to avoid label overlappings (factor between 1 and
	 * 20).
	 * 
	 * @param labelSpacingFactor the factor to be used
	 */
	public void setLabelSpacingFactor(int labelSpacingFactor) {
		this.labelSpacingFactor = labelSpacingFactor;
	}

	/**
	 * Changes/fixes the width of the generated image. If the width is fixed, the
	 * height will be adjusted dynamically!
	 */
	public void setWidth(int width) {
		if (width < 1)
			throw new IllegalArgumentException("Please specify a width that is bigger that 0!");
		this.useWidth = true;
		this.width = width;
	}

	/**
	 * Changes/fixes the height of the generated image. If the height is fixed, the
	 * width will be adjusted dynamically!
	 */
	public void setHeight(int height) {
		if (height < 1)
			throw new IllegalArgumentException("Please specify a height that is bigger that 0!");
		this.useWidth = false;
		this.height = height;
	}

	private Format getGraphvizLibFormat() {
		if (format == SVG)
			return Format.SVG;
		if (format == DOT)
			return Format.DOT;
		return Format.PNG;
	}

	protected org.contextmapper.contextmap.generator.ContextMapGenerator createContextMapGenerator() {
		return new org.contextmapper.contextmap.generator.ContextMapGenerator();
	}

	public boolean isGraphvizInstalled() {
		String execName = SystemUtils.executableName("dot");
		String envPath = Optional.ofNullable(System.getenv("PATH")).orElse("");
		if (CommandRunner.isExecutableFound(execName, envPath))
			return true;
		return false;
	}

}

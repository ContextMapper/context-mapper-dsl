/*
 * Copyright 2018 The Context Mapper Project Team
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
package org.contextmapper.dsl.generators.mocks;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.util.RuntimeIOException;

import com.google.common.collect.Maps;

public class IFileSystemAccess2Mock implements IFileSystemAccess2 {

	Map<String, CharSequence> countingMapTextGenerators = Maps.newHashMap();
	Map<String, InputStream> countingMapBinaryGenerators = Maps.newHashMap();
	private Map<String, String> fileMap = Maps.newHashMap();

	public Set<String> getGeneratedFilesSet() {
		Set<String> fileNames = new HashSet<>();
		fileNames.addAll(countingMapBinaryGenerators.keySet());
		fileNames.addAll(countingMapTextGenerators.keySet());
		return fileNames;
	}

	@Override
	public void generateFile(String fileName, CharSequence contents) {
		this.countingMapTextGenerators.put(fileName, contents);
		this.fileMap.put(fileName, contents.toString());
	}

	@Override
	public void generateFile(String fileName, String outputConfigurationName, CharSequence contents) {
	}

	@Override
	public void deleteFile(String fileName) {
	}

	@Override
	public void deleteFile(String fileName, String outputConfigurationName) {
	}

	@Override
	public URI getURI(String path, String outputConfiguration) {
		return null;
	}

	@Override
	public URI getURI(String path) {
		return null;
	}

	@Override
	public void generateFile(String fileName, String outputCfgName, InputStream content) throws RuntimeIOException {
	}

	@Override
	public void generateFile(String fileName, InputStream content) throws RuntimeIOException {
		this.countingMapBinaryGenerators.put(fileName, content);
		this.fileMap.put(fileName, "");
	}

	@Override
	public InputStream readBinaryFile(String fileName, String outputCfgName) throws RuntimeIOException {
		return null;
	}

	@Override
	public InputStream readBinaryFile(String fileName) throws RuntimeIOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharSequence readTextFile(String fileName, String outputCfgName) throws RuntimeIOException {
		return null;
	}

	@Override
	public CharSequence readTextFile(String fileName) throws RuntimeIOException {
		return this.fileMap.get(fileName);
	}

	@Override
	public boolean isFile(String path, String outputConfigurationName) throws RuntimeIOException {
		return false;
	}

	@Override
	public boolean isFile(String path) throws RuntimeIOException {
		return this.fileMap.containsKey(path);
	}

	public void storeFile(String path, String content) {
		this.fileMap.put(path, content);
	}

}
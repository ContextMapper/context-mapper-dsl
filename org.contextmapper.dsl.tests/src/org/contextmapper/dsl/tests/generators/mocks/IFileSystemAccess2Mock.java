package org.contextmapper.dsl.tests.generators.mocks;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.util.RuntimeIOException;

public class IFileSystemAccess2Mock implements IFileSystemAccess2 {

	Map<String, CharSequence> countingMap = new HashMap<String, CharSequence>();

	public Map<String, CharSequence> getGeneratedFilesMap() {
		return countingMap;
	}

	@Override
	public void generateFile(String fileName, CharSequence contents) {
		this.countingMap.put(fileName, contents);
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
		return null;
	}

	@Override
	public boolean isFile(String path, String outputConfigurationName) throws RuntimeIOException {
		return false;
	}

	@Override
	public boolean isFile(String path) throws RuntimeIOException {
		return false;
	}

}
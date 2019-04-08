package org.contextmapper.dsl.refactoring.henshin;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class HenshinTransformationFileProvider {

	public static final String FILE_CONTEXTMAP_REFACTORINGS = "ContextMapRefactorings.henshin";

	private File henshinDirectory;

	public HenshinTransformationFileProvider() {
		this.henshinDirectory = new File(System.getProperty("java.io.tmpdir"), getTempFolderName());
	}

	public String getTransformationFilePath(String transformationFileName) {
		File tempFile = new File(henshinDirectory, transformationFileName);
		if (!tempFile.exists())
			copyFileToTempDirectory(transformationFileName);
		return tempFile.getAbsolutePath();
	}

	public void prepareAllHenshinFiles() {
		try {
			if (henshinDirectory.exists())
				FileUtils.cleanDirectory(henshinDirectory);
			copyFileToTempDirectory(FILE_CONTEXTMAP_REFACTORINGS);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void copyFileToTempDirectory(String henshinFile) {
		if (!henshinDirectory.exists())
			henshinDirectory.mkdir();
		URL url = getClass().getResource(henshinFile);
		try {
			FileUtils.copyURLToFile(url, new File(henshinDirectory, henshinFile));
		} catch (Exception e) {
			throw new HenshinFileExportException();
		}
	}

	private String getTempFolderName() {
		return "context-mapper-henshin-transformations-" + getPID();
	}

	public File getHenshinDirectory() {
		return this.henshinDirectory;
	}

	public long getPID() {
		String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		return Long.parseLong(processName.split("@")[0]);
	}

}

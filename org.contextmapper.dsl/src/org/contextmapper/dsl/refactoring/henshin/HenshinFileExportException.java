package org.contextmapper.dsl.refactoring.henshin;

public class HenshinFileExportException extends RuntimeException {
	public HenshinFileExportException() {
		super("Cannot copy the Henshin transformation file to temporary directory!");
	}
}

package org.contextmapper.dsl.refactoring.henshin;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.contextmapper.dsl.refactoring.henshin.HenshinFileExportException;
import org.contextmapper.dsl.refactoring.henshin.HenshinTransformationFileProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class HenshinTransformationFileProviderTest {

	@Test
	void canExportFileToTempDir() {
		// given
		HenshinTransformationFileProvider fileProvider = new HenshinTransformationFileProvider();

		// when
		fileProvider.getHenshinDirectory().delete();
		String tempFilePath = fileProvider.getTransformationFilePath(HenshinTransformationFileProvider.SPLIT_AGGREGATE_BY_ENTITIES);

		// then
		assertTrue(tempFilePath.startsWith(System.getProperty("java.io.tmpdir")));
		assertTrue(new File(tempFilePath).exists());
	}

	@Test
	void canExportAllFiles() {
		// given
		HenshinTransformationFileProvider fileProvider = new HenshinTransformationFileProvider();
		String expectedPath = new File(fileProvider.getHenshinDirectory(), HenshinTransformationFileProvider.SPLIT_AGGREGATE_BY_ENTITIES).getAbsolutePath();

		// when
		fileProvider.prepareAllHenshinFiles();

		// then
		assertTrue(new File(expectedPath).exists());
	}

	@Test
	void expectException4NonExistingFile() {
		// given
		HenshinTransformationFileProvider fileProvider = new HenshinTransformationFileProvider();

		// when / then
		assertThrows(HenshinFileExportException.class, () -> fileProvider.getTransformationFilePath("not existing file"));
	}

	@AfterEach
	public void cleanup() throws IOException {
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		for (File file : tmpDir.listFiles()) {
			if (file.getName().startsWith("context-mapper-henshin-transformations-")) {
				Path directory = Paths.get(file.getAbsolutePath());
				Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Files.delete(file);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					}
				});
			}
		}
	}
}

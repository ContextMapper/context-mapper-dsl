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
package org.contextmapper.dsl;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractDirectoryIntegrationTest {
	protected File testDir;

	@BeforeEach
	public void prepare() {
		String dirName = UUID.randomUUID().toString();
		this.testDir = new File(new File(System.getProperty("java.io.tmpdir")), dirName);
		this.testDir.mkdir();
	}

	@AfterEach
	void cleanup() throws IOException {
		FileUtils.forceDeleteOnExit(this.testDir);
	}
}

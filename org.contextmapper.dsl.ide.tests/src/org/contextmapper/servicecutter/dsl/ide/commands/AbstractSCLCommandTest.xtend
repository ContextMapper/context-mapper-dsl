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
package org.contextmapper.servicecutter.dsl.ide.commands

import java.io.File
import org.apache.commons.io.FileUtils
import org.contextmapper.dsl.ide.tests.AbstractCMLLanguageServerTest
import org.eclipse.lsp4j.ClientCapabilities
import org.eclipse.lsp4j.ExecuteCommandCapabilities
import org.eclipse.lsp4j.WorkspaceClientCapabilities

abstract class AbstractSCLCommandTest extends AbstractCMLLanguageServerTest {

	def void initializeCommandsDynamically() {
		initialize[
			capabilities = new ClientCapabilities => [
				workspace = new WorkspaceClientCapabilities => [
					executeCommand = new ExecuteCommandCapabilities => [
						dynamicRegistration = true
					]
				]
			]
		]
	}

	def void deleteGeneratedFiles(String ext) {
		var srcGenFolder = srcGenFolder()
		val iterator = FileUtils.iterateFiles(srcGenFolder, #[ext], false)
		while (iterator.hasNext) {
			val file = iterator.next
			System.out.println("delete " + file.name)
			file.delete
		}
		if (srcGenFolder.exists)
			srcGenFolder.delete
	}

	def File srcGenFolder() {
		val srcGenFolder =  new File("src-gen")
		if(!srcGenFolder.exists)
			srcGenFolder.mkdir
		return srcGenFolder
	}

}

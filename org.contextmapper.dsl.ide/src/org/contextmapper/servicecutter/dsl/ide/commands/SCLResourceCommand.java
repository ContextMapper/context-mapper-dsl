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
package org.contextmapper.servicecutter.dsl.ide.commands;

import org.contextmapper.servicecutter.dsl.scl.SCLResourceContainer;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.xtext.ide.server.Document;
import org.eclipse.xtext.ide.server.ILanguageServerAccess;

/**
 * Interface for all SCL LSP commands that can be executed on SCL resource.
 * 
 * @author Stefan Kapferer
 *
 */
public interface SCLResourceCommand {

	void executeCommand(SCLResourceContainer cmlResource, Document document, ILanguageServerAccess access, ExecuteCommandParams params);

}

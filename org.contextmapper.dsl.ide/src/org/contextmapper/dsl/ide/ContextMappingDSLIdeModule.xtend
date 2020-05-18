/*
 * Copyright 2018-2020 The Context Mapper Project Team
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
package org.contextmapper.dsl.ide

import org.contextmapper.dsl.ide.actions.CMLActionService
import org.contextmapper.dsl.ide.commands.CMLCommandService
import org.eclipse.xtext.ide.server.codeActions.ICodeActionService2
import org.eclipse.xtext.ide.server.commands.IExecutableCommandService

/**
 * Use this class to register ide components.
 */
class ContextMappingDSLIdeModule extends AbstractContextMappingDSLIdeModule {

	def Class<? extends IExecutableCommandService> bindIExecutableCommandService() {
		return CMLCommandService
	}
	
	def Class<? extends ICodeActionService2> bindICodeActionService2() {
		return CMLActionService
	}

}

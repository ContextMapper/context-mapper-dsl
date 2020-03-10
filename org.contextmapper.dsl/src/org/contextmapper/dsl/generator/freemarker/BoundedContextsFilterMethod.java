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
package org.contextmapper.dsl.generator.freemarker;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.BoundedContextType;

import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class BoundedContextsFilterMethod implements TemplateMethodModelEx {

	@Override
	public Object exec(List arguments) throws TemplateModelException {
		if (arguments.size() != 1)
			throw new TemplateModelException("The method 'filterBoundedContexts' takes only one parameter (list of BoundedContexts)!");
		Object object = ((WrapperTemplateModel) arguments.get(0)).getWrappedObject();
		if (!(object instanceof Collection))
			throw new TemplateModelException(
					"The method 'filterBoundedContexts' takes an object of the type List<BoundedContext> only! The given value is of the type '" + object.getClass().getName() + "'.");

		List<BoundedContext> boundedContextList = (List<BoundedContext>) object;
		return boundedContextList.stream().filter(bc -> !bc.getType().equals(BoundedContextType.TEAM)).collect(Collectors.toList());
	}

}

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

import java.util.List;

import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class InstanceOfMethod implements TemplateMethodModelEx {

	@Override
	public Object exec(List arguments) throws TemplateModelException {
		if (arguments.size() != 2)
			throw new TemplateModelException("Wrong amount of arguments for method 'instanceOf'. Two parameters are required: object and class");

		Object object = ((WrapperTemplateModel) arguments.get(0)).getWrappedObject();
		Object clazz = ((WrapperTemplateModel) arguments.get(1)).getWrappedObject();
		if (!(clazz instanceof Class))
			throw new TemplateModelException("The second parameter of 'instanceOf' is no class!");

		Class<?> c = (Class<?>) clazz;
		return c.isAssignableFrom(object.getClass());
	}

}

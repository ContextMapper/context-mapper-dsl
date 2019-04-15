/*
 * Copyright 2018 The Context Mapper Project Team
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

import org.eclipse.xtext.util.formallang.Cfg;
import org.eclipse.xtext.util.formallang.FollowerFunction;
import org.eclipse.xtext.util.formallang.FollowerFunctionImpl;
import org.eclipse.xtext.util.formallang.FollowerFunctionImpl.UnorderedStrategy;
import org.eclipse.xtext.util.formallang.Pda;
import org.eclipse.xtext.util.formallang.PdaFactory;
import org.eclipse.xtext.util.formallang.PdaUtil;

/**
 *
 * Ugly fix for serialization problem:
 * https://www.eclipse.org/forums/index.php/t/1080047/
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=369175
 * 
 * Otherwise our refactorings produce CML's which cannot be parsed :(
 * Problem: Order of the root elements (ContextMap, BoundedContext, Domain, UseCases).
 *
 */
public class ContextMapperPDAUtil extends PdaUtil {
	
	@Override
	public <S, P, E, T, D extends Pda<S, P>> D create(Cfg<E, T> cfg, FollowerFunction<E> ff, PdaFactory<D, S, P, ? super E> fact) {
		((FollowerFunctionImpl<E, T>) ff).setUnorderedStrategy(UnorderedStrategy.SEQUENCE);
		return super.create(cfg, ff, fact);
	}
}

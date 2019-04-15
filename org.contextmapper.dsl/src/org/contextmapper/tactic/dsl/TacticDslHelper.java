/*
 * Copyright 2013 The Sculptor Project Team, including the original 
 * author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.contextmapper.tactic.dsl;

import java.util.List;

import org.contextmapper.tactic.dsl.tacticdsl.DataTransferObject;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;

/**
 * Java utilities for extension functions
 * 
 */
public class TacticDslHelper {
	public static void debugTrace(String msg) {
		System.out.println(msg);
	}

	/**
	 * Throws a RuntimeException to stop the generation with an error message.
	 * 
	 * @param msg message to log
	 */
	public static void error(String msg) {
		System.err.println(msg);
		throw new RuntimeException(msg);
	}

	public static List<? extends SimpleDomainObject> getSubclasses(SimpleDomainObject domainObject) {
		if (domainObject instanceof DomainObject) {
			return getSubclasses((DomainObject) domainObject);
		} else if (domainObject instanceof DataTransferObject) {
			return getSubclasses((DataTransferObject) domainObject);
		} else {
			throw new IllegalArgumentException(String.valueOf(domainObject));
		}
	}

	public static List<? extends SimpleDomainObject> getAllSubclasses(SimpleDomainObject domainObject) {
		if (domainObject instanceof DomainObject) {
			return getAllSubclasses((DomainObject) domainObject);
		} else if (domainObject instanceof DataTransferObject) {
			return getAllSubclasses((DataTransferObject) domainObject);
		} else {
			throw new IllegalArgumentException(String.valueOf(domainObject));
		}
	}

}

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

import java.util.ArrayList;
import java.util.List;

import org.contextmapper.tactic.dsl.tacticdsl.Application;
import org.contextmapper.tactic.dsl.tacticdsl.CommandEvent;
import org.contextmapper.tactic.dsl.tacticdsl.DataTransferObject;
import org.contextmapper.tactic.dsl.tacticdsl.DomainEvent;
import org.contextmapper.tactic.dsl.tacticdsl.DomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Module;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;

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

	public static List<DomainObject> getSubclasses(DomainObject domainObject) {
		List<DomainObject> subclasses = new ArrayList<DomainObject>();
		Application application = (Application) domainObject.eContainer().eContainer();
		List<Module> modules = application.getModules();
		for (Module module : modules) {
			List<SimpleDomainObject> domainObjects = module.getDomainObjects();
			for (SimpleDomainObject simpleDomainObj : domainObjects) {
				if (simpleDomainObj instanceof DomainObject) {
					DomainObject domainObj = (DomainObject) simpleDomainObj;
					if (domainObj.getExtendsName() != null
							&& domainObj.getExtendsName().equals(domainObject.getName())) {
						subclasses.add(domainObj);
					} else if (getSuperclass(domainObj) != null && getSuperclass(domainObj).equals(domainObject)) {
						subclasses.add(domainObj);
					}
				}
			}
		}
		return subclasses;
	}

	public static SimpleDomainObject getSuperclass(SimpleDomainObject domainObject) {
		SimpleDomainObject result = null;
		String extendsName = null;
		if (domainObject instanceof Entity) {
			result = ((Entity) domainObject).getExtends();
			extendsName = ((Entity) domainObject).getExtendsName();
		} else if (domainObject instanceof ValueObject) {
			result = ((ValueObject) domainObject).getExtends();
			extendsName = ((ValueObject) domainObject).getExtendsName();
		} else if (domainObject instanceof CommandEvent) {
			result = ((CommandEvent) domainObject).getExtends();
			extendsName = ((CommandEvent) domainObject).getExtendsName();
		} else if (domainObject instanceof DomainEvent) {
			result = ((DomainEvent) domainObject).getExtends();
			extendsName = ((DomainEvent) domainObject).getExtendsName();
		} else if (domainObject instanceof DataTransferObject) {
			result = ((DataTransferObject) domainObject).getExtends();
			extendsName = ((DataTransferObject) domainObject).getExtendsName();
		}

		if (result == null && extendsName != null) {
			Application application = (Application) domainObject.eContainer().eContainer();
			result = findDomainObjectByName(extendsName, application);
		}
		return result;
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

	public static List<DataTransferObject> getAllSubclasses(DataTransferObject domainObject) {
		List<DataTransferObject> subclasses = getSubclasses(domainObject);
		for (DataTransferObject subclass : new ArrayList<DataTransferObject>(subclasses)) {
			subclasses.addAll(getAllSubclasses(subclass));
		}
		return subclasses;
	}

	public static List<DataTransferObject> getSubclasses(DataTransferObject domainObject) {
		List<DataTransferObject> subclasses = new ArrayList<DataTransferObject>();
		Application application = (Application) domainObject.eContainer().eContainer();
		List<Module> modules = application.getModules();
		for (Module module : modules) {
			List<SimpleDomainObject> domainObjects = module.getDomainObjects();
			for (SimpleDomainObject simpleDomainObj : domainObjects) {
				if (simpleDomainObj instanceof DataTransferObject) {
					DataTransferObject domainObj = (DataTransferObject) simpleDomainObj;
					if (domainObj.getExtendsName() != null
							&& domainObj.getExtendsName().equals(domainObject.getName())) {
						subclasses.add(domainObj);
					} else if (domainObj.getExtends() != null && domainObj.getExtends().equals(domainObject)) {
						subclasses.add(domainObj);
					}
				}
			}
		}
		return subclasses;
	}

	public static List<DomainObject> getAllSubclasses(DomainObject domainObject) {
		List<DomainObject> subclasses = getSubclasses(domainObject);
		for (DomainObject subclass : new ArrayList<DomainObject>(subclasses)) {
			subclasses.addAll(getAllSubclasses(subclass));
		}
		return subclasses;
	}

	public static SimpleDomainObject findDomainObjectByName(String name, Application application) {
		List<Module> modules = application.getModules();
		for (Module module : modules) {
			for (SimpleDomainObject domainObj : module.getDomainObjects()) {
				if (domainObj.getName().equals(name)) {
					return domainObj;
				}
			}
		}
		return null;
	}

}

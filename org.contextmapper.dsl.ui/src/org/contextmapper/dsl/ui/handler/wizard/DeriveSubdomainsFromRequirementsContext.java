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
package org.contextmapper.dsl.ui.handler.wizard;

import java.util.Set;

public class DeriveSubdomainsFromRequirementsContext {

	private String initialDomain;
	private String domainName;
	private String subdomainName;
	private Set<String> allDomains;

	public DeriveSubdomainsFromRequirementsContext(String initialDomain, Set<String> allDomains) {
		this.initialDomain = initialDomain;
		this.allDomains = allDomains;
	}

	public String getInitialDomain() {
		return initialDomain;
	}

	public void setInitialDomain(String initialDomain) {
		this.initialDomain = initialDomain;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getSubdomainName() {
		return subdomainName;
	}

	public void setSubdomainName(String subdomainName) {
		this.subdomainName = subdomainName;
	}

	public Set<String> getAllDomains() {
		return allDomains;
	}

	public void setAllDomains(Set<String> allDomains) {
		this.allDomains = allDomains;
	}

}

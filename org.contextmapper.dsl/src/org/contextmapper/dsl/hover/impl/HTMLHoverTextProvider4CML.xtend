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
package org.contextmapper.dsl.hover.impl

import org.contextmapper.dsl.hover.impl.AbstractCMLHoverTextProvider
import org.contextmapper.dsl.hover.CMLHoverTextProvider

class HTMLHoverTextProvider4CML extends AbstractCMLHoverTextProvider implements CMLHoverTextProvider {

	override protected registerHoverTexts() {
		registerHoverText("BoundedContext", '''
		<strong>Bounded Context</strong>: A description of a boundary (typically a subsystem, or the work 
		of a particular team) within which a particular model is defined and applicable.
		<br/><br/>
		Find all DDD pattern descriptions in the DDD reference under 
		<a href="http://domainlanguage.com/ddd/reference/">http://domainlanguage.com/ddd/reference/</a>''');

		registerHoverText("ContextMap", '''<strong>Context Map</strong>: A model describing bounded contexts 
		and especially their relationships. Brandolini provides a very good introduction into context mapping here:
		<a href="https://www.infoq.com/articles/ddd-contextmapping/">https://www.infoq.com/articles/ddd-contextmapping/</a>
		<br/><br/> Find all DDD pattern descriptions in the DDD reference under 
		<a href="http://domainlanguage.com/ddd/reference/">http://domainlanguage.com/ddd/reference/</a>''');

		registerHoverText("U", '''<strong>U</strong>pstream in the upstream-downstream relationship.
		<br/><br/><strong>upstream-downstream</strong>:<br/>A relationship between two bounded contexts 
		in which the “upstream” contexts’s actions affect project success of the “downstream” context, but 
		the actions of the downstream do not significantly affect projects upstream. (e.g. If two cities 
		are along the same river, the upstream city’s pollution primarily affects the downstream city.) 
		The upstream team may succeed independently of the fate of the downstream team.<br/><br/>
		Find all DDD pattern descriptions in the DDD reference under 
		<a href="http://domainlanguage.com/ddd/reference/">http://domainlanguage.com/ddd/reference/</a>''');

		registerHoverText("D", '''<strong>D</strong>ownstream in the upstream-downstream relationship.
		<br/><br/><strong>upstream-downstream</strong>:<br/>A relationship between two bounded contexts 
		in which the “upstream” contexts’s actions affect project success of the “downstream” context, but 
		the actions of the downstream do not significantly affect projects upstream. (e.g. If two cities 
		are along the same river, the upstream city’s pollution primarily affects the downstream city.) 
		The upstream team may succeed independently of the fate of the downstream team.<br/><br/>
		Find all DDD pattern descriptions in the DDD reference under 
		<a href="http://domainlanguage.com/ddd/reference/">http://domainlanguage.com/ddd/reference/</a>''');

		registerHoverText("S", '''<strong>S</strong>upplier in the customer-supplier relationship.
		<br/><br/><strong>customer-supplier</strong>:<br/>Describes an upstream-downstream relationship 
		where one bounded context is customer and the other supplier which work closely together. 
		The supplier prioritizes the implementation with respect to the customers requirements.
		<br/><br/> Find all DDD pattern descriptions in the DDD reference under 
		<a href="http://domainlanguage.com/ddd/reference/">http://domainlanguage.com/ddd/reference/</a>''');

		registerHoverText("C", '''<strong>C</strong>ustomer in the customer-supplier relationship.
		<br/><br/><strong>customer-supplier</strong>:<br/>Describes an upstream-downstream relationship 
		where one bounded context is customer and the other supplier which work closely together. 
		The supplier prioritizes the implementation with respect to the customers requirements.
		<br/><br/> Find all DDD pattern descriptions in the DDD reference under 
		<a href="http://domainlanguage.com/ddd/reference/">http://domainlanguage.com/ddd/reference/</a>''');

		registerHoverText("OHS", '''<strong>Open Host Service (OHS)</strong>: Describes a role of a 
		bounded context which is providing certain functionality needed by many other contexts. 
		Because of the broad usage, a public API is provided.
		<br/><br/> Find all DDD pattern descriptions in the DDD reference under 
		<a href="http://domainlanguage.com/ddd/reference/">http://domainlanguage.com/ddd/reference/</a>''');

		registerHoverText("PL", '''<strong>Published Language (PL)</strong>: The published language describes 
		the shared knowledge two bounded contexts need for their interaction. Typically defined by the 
		upstream providing an Open Host Service.
		<br/><br/> Find all DDD pattern descriptions in the DDD reference under 
		<a href="http://domainlanguage.com/ddd/reference/">http://domainlanguage.com/ddd/reference/</a>''');

		registerHoverText("ACL", '''<strong>Anticorruption Layer (ACL)</strong>: Describes a mechanism used 
		by downstreams in order to protect themselves from changes of the upstream.
		<br/><br/> Find all DDD pattern descriptions in the DDD reference under 
		<a href="http://domainlanguage.com/ddd/reference/">http://domainlanguage.com/ddd/reference/</a>''');

		registerHoverText("CF", '''<strong>Conformist (CF)</strong>: Describes a role of a bounded context in 
		an upstream-downstream relationship. Since there is no influence on the upstream, the downstream 
		team has to deal with what they get and "conform" to it.
		<br/><br/> Find all DDD pattern descriptions in the DDD reference under 
		<a href="http://domainlanguage.com/ddd/reference/">http://domainlanguage.com/ddd/reference/</a>''');

		registerHoverText("Aggregate", '''<strong>Aggregate</strong>: An Aggregate is a cluster of domain 
		objects (such as Entities, Value Objects, etc.) which is kept consistent with respect to specific 
		invariants and typically also represents a boundary regarding transactions.
		<br/><br/> Find all DDD pattern descriptions in the DDD reference under 
		<a href="http://domainlanguage.com/ddd/reference/">http://domainlanguage.com/ddd/reference/</a>''');

		registerHoverText("Domain", '''<strong>Domain</strong>: The domain describes the world within your 
		organization is working. This is not a pattern in the original DDD book of Evans, but still a very 
		important term related to the patterns Subdomain and Bounded Context as described by 
		<a href="https://www.amazon.de/Implementing-Domain-Driven-Design-Vaughn-Vernon/dp/0321834577">Vernon</a> 
		<br/><br/> Find all DDD pattern descriptions in the DDD reference under 
		<a href="http://domainlanguage.com/ddd/reference/">http://domainlanguage.com/ddd/reference/</a>''');

		registerHoverText("Subdomain", '''<strong>Subdomain (Core, Supporting, Generic)</strong>: A subdomain 
		is a part of the domain. Regarding subdomains we differentiate between Core Domains, Supporting 
		Subdomains and Generic Subdomains. A bounded context implements parts of one or multiple subdomains.
		<br/><br/> Find all DDD pattern descriptions in the DDD reference under 
		<a href="http://domainlanguage.com/ddd/reference/">http://domainlanguage.com/ddd/reference/</a>''');

		registerHoverText("P", '''<strong>Partnership (P)</strong>: The partnership pattern describes an 
		intimate relationship between two bounded contexts. Their domain models somehow relate and have 
		to be evolved together.
		<br/><br/> Find all DDD pattern descriptions in the DDD reference under 
		<a href="http://domainlanguage.com/ddd/reference/">http://domainlanguage.com/ddd/reference/</a>''');

		registerHoverText("SK", '''<strong>Shared Kernel (SK)</strong>: Desribes an intimate relationship 
		between two bounded contexts which share a common part of the domain model and manage it as a 
		common library.
		<br/><br/> Find all DDD pattern descriptions in the DDD reference under 
		<a href="http://domainlanguage.com/ddd/reference/">http://domainlanguage.com/ddd/reference/</a>''');
	}

}

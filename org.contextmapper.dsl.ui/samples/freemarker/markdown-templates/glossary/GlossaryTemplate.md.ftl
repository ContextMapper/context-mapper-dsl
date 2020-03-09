Glossary for '${filename}'
=========================

This glossary forms the [ubiquituous language](https://martinfowler.com/bliki/UbiquitousLanguage.html) for '${filename}'.

Analysis Model 
--------------

### Domain Overview

<#list domains as d>
Domain `${d.name}` has the  following domain vision statement: 
${d.domainVisionStatement!"[*TODO create domain vision statement for ${d.name}*]"}
</#list> 

<#list domains as d>
<#if d.subdomains?has_content>
### Subdomains (in domain "${d.name}")
${d.name} contains the following subdomains:
| Name | Type | Domain Vision Statement | 
| -----| -----|-------------------------|
<#list d.subdomains as sd> 
| *${sd.name}* | ${sd.type} | ${sd.domainVisionStatement!"*[TODO add (sub-)domain vision statement for ${sd.name}]*"} |  
</#list>

### Entities
<#list d.subdomains as sd><#list sd.entities as e><#if e?has_content>
#### ${e.name}
Entity in subdomain ${sd.name} (in domain ${d.name}).
<#if e.hint?has_content>
Hint: ${e.hint!"*no hint given*"}
</#if>

<@renderDomainObjectOperationsAndAttributes e />

</#if>
</#list>
</#list>
</#if>
</#list>

<#if useCases?has_content>
### Use Cases
<#list useCases as uc>
 * ${uc.name}
</#list> 
</#if>

Design Model
============

## Overview

The following [bounded context](https://martinfowler.com/bliki/BoundedContext.html)s are defined:

<#list boundedContexts as bc>
* *${bc.name}*: ${bc.domainVisionStatement!"[domain vision of the context not defined]"} 
</#list>

<#list filterBoundedContexts(boundedContexts) as bc>
## Bounded Context ${bc.name}

*${bc.name}* has this domain vision statement: ${bc.domainVisionStatement!"[domain vision of the context not defined]"} 

A number of aggregates are part of the ubiquituous language of this Bounded Context. The following paragraphs describe them, as well as their inner structures.

<#list bc.aggregates as agg>
### Aggregate ${agg.name} 
This aggregate can be classified as ${agg.doc!"[no doc string]"}. Its responsibilities are: 

<#if useCases?has_content>
<#list agg.responsibilities as r>
* ${r}
</#list>
<#else>
* [no (more) responsibilities defined]
</#if>

<#if agg.domainObjects?has_content>
#### Entities (in Aggregate)
<#list agg.domainObjects as dob>
<#if instanceOf(dob, Entity)>
##### ${dob.name}
${dob.hint!"[hint missing]"}

<@renderDomainObjectOperationsAndAttributes dob />
</#if>
</#list>
</#if>

<#if agg.services?has_content>
#### Services 
<#list agg.services as s>
##### ${s.name}
${s.hint!"[hint missing]"}
</#list>
</#if>

#### Value Objects
<#if agg.domainObjects?has_content>
<#list agg.domainObjects as dob>
<#if instanceOf(dob, ValueObject)>
##### ${dob.name}
${dob.hint!"[hint missing]"}

<@renderDomainObjectOperationsAndAttributes dob />
</#if>
</#list>
</#if>

#### Enumerations (Enums)
<#list agg.domainObjects as dob>
<#if instanceOf(dob, Enum)>
##### ${dob.name}
${dob.hint!"[hint missing]"}

Values:
<#list dob.values as value>
 * ${value.name}
</#list>
</#if>

</#list>

<#if agg.domainObjects?has_content>
#### Events (Command Events, Domain Events)
<#list agg.domainObjects as dob>
<#if instanceOf(dob, Event)>
<#if instanceOf(dob, DomainEvent)>
##### ${dob.name} (DomainEvent)
<#elseif instanceOf(dob, CommandEvent)>
##### ${dob.name} (CommandEvent) 
</#if>
${dob.hint!"[hint missing]"}

<@renderDomainObjectOperationsAndAttributes dob />
</#if>
</#list>
</#if>

</#list>
</#list>

<#if contextMap??>
### Context Maps 
The Context Map '${contextMap.name}' in file '${filename}' is of type '${contextMap.type}'.
It contains the following Bounded Contexts:

<#list contextMap.boundedContexts as bc>
 * ${bc.name}
</#list>
</#if>


## Teams 
<#list filterTeams(boundedContexts) as team>
 * ${team.name}
</#list>

<#macro renderDomainObjectOperationsAndAttributes simpleDomainObject>
<#if simpleDomainObject.operations?has_content>
Operations:
| Operation | Doc string | Parameters | Return Type |
|-|-|-|-|
<#list simpleDomainObject.operations as op>
| ${op.name} | ${op.doc!"*no doc string*"} | <#if op.parameters?has_content> <#list op.parameters as p> ${p.name} of type ${getType(p.parameterType)}<br /> </#list> <#else> none </#if>| <#if op.returnType?has_content>${op.returnType.type}<#else>none</#if> |
</#list> 
</#if>

<#if simpleDomainObject.attributes?has_content>
Attributes:
| Attribute | Doc/doc string | Visibility | Type |
|-|-|-|-|
<#list simpleDomainObject.attributes as attr>
| ${attr.name} | ${attr.doc!"*no doc string*"} | ${attr.visibility} | ${attr.type} | 
</#list> 
</#if>
</#macro>

Glossary for '${filename}' <!-- R: I would like to print the project name here -->
=========================

<!-- R: and I would welcome a short key in Eclipse now Control-G or someting, to speed up incremental dev and testing... ;-) -->

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
<!-- R: it would be great to have a utility function that allows me to include a generated image (her: puml class diagram) -->
TODO something like [Class Diagram for dollar {sd.name}](cmlgen( dollar {sd.name}, puml))

### Entities
<#list d.subdomains as sd><#list sd.entities as e><#if e?has_content>
Entity in subdomain ${sd.name} (in domain ${d.name}): ${e.name}
Hint: ${e.hint!"*no hint given*"}

Operations: 
<#if e.operations?has_content>
| Operation | Doc/doc string | Parameters | Return Type |
|-|-|-|-|
<#list e.operations as op>
| ${op.name} | ${op.doc!"*no doc string*"} <!-- FB_ Doc or doc??? (looked at Javadoc) --> | <#if op.parameters?has_content> <#list op.parameters as p> ${p.name} of type ${p.parameterType.type} </#list> <#else> none </#if>| <#if op.returnType?has_content>${op.returnType.type}<#else>none</#if> |
</#list><#else>${e.name} does not expose any operations. 
</#if><#-- Done with operations. -->
<!-- FB: struggled a bit to extract the type name from the ETyrg.contextmapper.tactic.dsl.tacticdsl.impl.ComplexTypeImpl object, but seems to work ok now -->

Attributes:
<#if e.attributes?has_content>
| Attribute | Doc/doc string | Visibility | Type |
|-|-|-|-|
<#list e.attributes as attr>
| ${attr.name} | ${attr.doc!"*no doc string*"} | ${attr.visibility} | ${attr.type} | 
</#list><#else>${e.name} does not contain any attributes. 
</#if><#-- Done with attributes. -->

</#if>
</#list><#-- No more entity information. -->
</#list><#-- Done with subdomain. -->
</#if>
</#list><#-- End of domain report. -->


### Use cases

<!-- FB: document that variable names are case sensitive so usecase does not work -->
<#list useCases as uc>
Use case: ${uc.name} TODO print more attributes?
</#list> 




Design Model
============

## Overview

The following [bounded context](https://martinfowler.com/bliki/BoundedContext.html)s are defined:

<#list boundedContexts as bc>
* *${bc.name}*: ${bc.domainVisionStatement!"[domain vision of the context not defined]"} 
</#list>

<#list boundedContexts as bc>
## Bounded Context ${bc.name}

*${bc.name}* has this domain vision statement: ${bc.domainVisionStatement!"[domain vision of the context not defined]"} 

A number of aggregates are part of the ubiquituous language of this Bounded Context. The following paragraphs describe them, as well as their inner structures.

<#list bc.aggregates as agg>
### Aggregate ${agg.name} 
**${agg.name} (${bc.name})**. This aggregate can be classified as ${agg.doc!"[no doc string]"}. Its responsibilities are: 

<#-- Q/FB: switch to latest version of Freemarker? online docs feature 2.3.29 (which seems to be able to do more) and we use 2.3.22? -->
<#list agg.responsibilities as r>
<#-- iterate over respo array/list/sequence (or call an accumulating function): -->
* ${r}
</#list> 
* [no (more) responsibilities defined]


#### Entities (in Aggregate)

<#list agg.domainObjects as dob>
<#if instanceOf(dob, Entity)>
*${dob.name}:* ${dob.hint!"[hint missing]"} 
</#if>

</#list>


TODO tbc (similar to analysis-level entities, copy paste?)


#### Services 

<#list agg.services as s>
*${s.name}:* ${s.hint!"[hint missing]"}

</#list>


#### Value Objects 

<#list agg.domainObjects as dob>
<#if instanceOf(dob, ValueObject)>
*${dob.name}:* ${dob.hint!"[hint missing]"}
</#if>

</#list>


#### Enumerations (Enums)

<#list agg.domainObjects as dob>
<#if instanceOf(dob, Enum)>
*${dob.name}:* ${dob.hint!"[hint missing]"}
<#-- Q: how do I get the values of the enum? -->
</#if>

</#list>


#### Events (Command Events, Domain Events)
<#-- TODO also handle other/remaining Sculptor stuff? -->

<#list agg.domainObjects as dob>
<#if instanceOf(dob, DomainEvent)>
*${dob.name}* (a instance of DomainEvent): ${dob.hint!"[hint missing]"}
<#elseif instanceOf(dob, CommandEvent)>
* ${dob.name}* (a CommandEvent): ${dob.hint!"[hint missing]"} 
</#if>

</#list>

</#list>
</#list>



<#if contextMap??>
### Context Maps 

The Context Map '${contextMap.name}' in file '${filename}' is of type '${contextMap.type}'.
It contains the following Bounded Contexts:

<#list contextMap.boundedContexts as bc>
${bc.name}, which has these Aggregates: 
</#list>

</#if>


## Teams 

TODO teams (special type of BC, [R] isInstanceOf helper?)


<!-- EOF -->
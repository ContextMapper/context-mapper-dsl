<#import "domain-objects/domain-object-attribute-and-operation-tables.ftl" as attrOpsMacro>
<#include "./common/header.md.ftl">

Ubiquitous Language Glossary for '${fileName}'
=============================================

This glossary forms the [ubiquituous language](https://martinfowler.com/bliki/UbiquitousLanguage.html) for '${fileName}'.

Analysis Model
--------------
<#include "./domains/domain-list.md.ftl">

Design Model
------------
<#if filterBoundedContexts(boundedContexts)?has_content>
    ## Bounded Context Overview<#lt>

    <#include "./bounded-contexts/bounded-context-list-simple.ftl">
    <#include "./bounded-contexts/bounded-context-list-detailed.ftl">

    <#if contextMap??>
        ### Context Maps<#lt>
        The Context Map '${contextMap.name}' in file '${filename}' is of type '${contextMap.type}'.<#lt>
        It contains the following Bounded Contexts:<#lt>

        <#list contextMap.boundedContexts as bc>
            * ${bc.name}<#lt>
        </#list>
    </#if>
</#if>

<#if filterTeams(boundedContexts)?has_content>
    ## Teams<#lt>
    <#list filterTeams(boundedContexts) as team>
        * ${team.name}<#lt>
    </#list>
</#if>

<#include "./common/header.md.ftl">

Ubiquitous Language Glossary for '${fileName}' 
==============================================

This glossary forms the vocabulary of the [ubiquituous language](https://martinfowler.com/bliki/UbiquitousLanguage.html) established by this domain model.

<#if domains?has_content>
    Analysis Artifacts<#lt>
    ------------------<#lt>
    We distinguish between [analysis and design](https://en.wikipedia.org/wiki/Object-oriented_analysis_and_design) in this sample model: Elements in the analysis model part represent the current understanding of what the domain is about (for instance, context, vision, requirements from a business stakeholder point of view).<#lt>

    <#include "./domains/domain-list-glossary.md.ftl">
</#if>

<#if boundedContexts?has_content>
    Design Concepts<#lt>
    ---------------<#lt>
    The design model elements describe solution building blocks from a technical, software-oriented point of view (while the analysis is focused on the problems to be solved).<#lt>
    <#if filterStructuralBoundedContexts(boundedContexts)?has_content>

        <#include "./bounded-contexts/bounded-context-list-glossary.ftl">

        <#if contextMap??>
            ### Context Maps<#lt>
            The Context Map <#if contextMap.name?has_content>'${contextMap.name}'</#if> in file '${filename}' is of type '${contextMap.type}'.<#lt>
        </#if>
    </#if>

    <#if filterTeams(boundedContexts)?has_content>
        ## Teams<#lt>
        <#list filterTeams(boundedContexts) as team>
            * ${team.name}<#lt>
        </#list>
    </#if>
</#if>

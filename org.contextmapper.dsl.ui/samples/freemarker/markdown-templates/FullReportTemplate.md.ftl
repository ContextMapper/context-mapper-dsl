<#import "domain-objects/domain-object-attribute-and-operation-tables.ftl" as attrOpsMacro>
<#import "flows/application_flow.ftl" as appFlowMacro>
<#import "states/state_transition.ftl" as stateTransitionMacro>
<#include "./common/header.md.ftl">

Model Report for '${fileName}' 
==============================

*Note:* This domain model is part of project '${projectName!"undefined"}'.

<#if domains?has_content>
    Analysis Part<#lt>
    --------------<#lt>
    <#include "./domains/domain-list.md.ftl">
</#if>

<#if boundedContexts?has_content>
    Design Part<#lt>
    ------------<#lt>
    <#if filterStructuralBoundedContexts(boundedContexts)?has_content>
        
        ### Bounded Context Overview<#lt>

        <#include "./bounded-contexts/bounded-context-list-simple.ftl">
        <#include "./bounded-contexts/bounded-context-list-detailed.ftl">

        <#if contextMap??>
            ### Context Maps<#lt>
            The Context Map <#if contextMap.name?has_content>'${contextMap.name}'</#if> in file '${filename}' is of type '${contextMap.type}'.<#lt>
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
</#if>

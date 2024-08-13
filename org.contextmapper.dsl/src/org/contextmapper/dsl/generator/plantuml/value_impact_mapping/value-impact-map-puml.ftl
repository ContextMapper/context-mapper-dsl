@startmindmap

<style>
node {
    MinimumWidth 200
    MaximumWidth 200
}
mindmapDiagram {
  .green {
    BackgroundColor lightgreen
  }
  .red {
    BackgroundColor tomato
  }
}
</style>

* ${soi.name}
<#list soi.stakeholders as stakeholder>
** ${stakeholder.name}
  <#list stakeholder.values as value>
***:<b>${value.name}
    <#if value.priority?has_content || value.impact?has_content>
----
      <#if value.priority?has_content>
Priority ${value.priority}
      </#if>
      <#if value.impact?has_content>
Impact ${value.impact}
      </#if>
    </#if>
    <#if value.consequence?has_content>
-- Consequence --
${value.consequence}
    </#if>
    <#if value.demonstrators?has_content>
-- Demonstrators --
      <#list value.demonstrators as demonstrator>
* ${demonstrator}
      </#list>
    </#if>
; <#if value.consequenceType == "GOOD"><<green>><#elseif value.consequenceType == "BAD"><<red>></#if>
    <#list value.mitigationActions as mitigationAction>
**** ${mitigationAction.actionType}: ${mitigationAction.action}
    </#list>
  </#list>
</#list>
@endmindmap

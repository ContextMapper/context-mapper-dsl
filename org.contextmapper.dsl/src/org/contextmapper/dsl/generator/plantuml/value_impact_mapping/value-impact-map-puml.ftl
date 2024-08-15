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

*:Feature or System Characteristic:

<b>${soi.name}</b>;
<#list soi.stakeholders as stakeholder>
  <#if stakeholder.description?has_content>
**:<b>${stakeholder.name}</b>
----
${stakeholder.description};
  <#else>
** <b>${stakeholder.name}</b>
  </#if>
  <#list stakeholder.values as value>
***:<b>${value.name}</b>
    <#if value.priority?has_content || value.impact?has_content>
----
      <#if value.priority?has_content>
Priority ${value.priority}
      </#if>
      <#if value.impact?has_content>
Impact ${value.impact}
      </#if>
    </#if>
    <#if value.demonstrators?has_content>
-- Demonstrators --
      <#list value.demonstrators as demonstrator>
* <i>${demonstrator}</i>
      </#list>
    </#if>
    <#if value.consequence?has_content>
-- <#if value.consequenceType == "GOOD">Positive Consequences<#elseif value.consequenceType == "BAD">Negative Consequences<#else>Consequences</#if> --
<i>${value.consequence}</i>
    </#if>
;<#if value.consequenceType == "GOOD"> <<green>><#elseif value.consequenceType == "BAD"> <<red>></#if>
    <#list value.mitigationActions as mitigationAction>
**** <b>${mitigationAction.actionType}</b>: ${mitigationAction.action}
    </#list>
  </#list>
</#list>
@endmindmap

Context,Stakeholder Group,Stakeholder,description,influence,interest
<#list stakeholders as stakeholderContainer>
<#assign stakeholderGroups = stakeholderContainer.stakeholders?filter(s -> instanceOf(s, StakeholderGroup))>
<#assign singleStakeholders = stakeholderContainer.stakeholders?filter(s -> instanceOf(s, Stakeholder))>
<#if stakeholderContainer.contexts?has_content><#assign contextDescription = stakeholderContainer.contexts?map(c -> c.name)?join("; ")><#else><#assign contextDescription = ""></#if>
<#list stakeholderGroups as sg>
<#list sg.stakeholders as stakeholder>
${contextDescription},${sg.name},${stakeholder.name},${stakeholder.description!""},${stakeholder.influence!""},${stakeholder.interest!""}
</#list>
</#list>
<#list singleStakeholders as stakeholder>
${contextDescription},,${stakeholder.name},${stakeholder.description!""},${stakeholder.influence!""},${stakeholder.interest!""}
</#list>
</#list>
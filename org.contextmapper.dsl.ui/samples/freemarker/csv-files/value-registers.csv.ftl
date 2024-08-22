Value Register,Context,Value Cluster,Core Value,Demonstrators,Related Values,Opposing Values,Value,coreValue,Demonstrators,Related Values,Opposing Values,Stakeholder(s),Priority,Impact,Consequence Type,Consequence,Action Type, Action
<#list valueRegisters as valueRegister>
<#if valueRegister.context?has_content><#assign valueRegisterContext = valueRegister.context.name!""><#else><#assign valueRegisterContext = ""></#if>
<#list valueRegister.valueClusters as valueCluster>
<#if valueCluster.coreValue7000?has_content><#assign valueClusterCoreValue = valueCluster.coreValue7000!""><#else><#assign valueClusterCoreValue = valueCluster.coreValue!""></#if>
<#if valueCluster.demonstrators?has_content><#assign valueClusterDemonstrators = valueCluster.demonstrators?join("; ")><#else><#assign valueClusterDemonstrators = ""></#if>
<#if valueCluster.relatedValues?has_content><#assign valueClusterRelatedValues = valueCluster.relatedValues?join("; ")><#else><#assign valueClusterRelatedValues = ""></#if>
<#if valueCluster.opposingValues?has_content><#assign valueClusterOpposingValues = valueCluster.opposingValues?join("; ")><#else><#assign valueClusterOpposingValues = ""></#if>
<#if valueCluster.elicitations?has_content>
<#list valueCluster.elicitations as stakeholderElicitation>
<#if stakeholderElicitation.consequences?has_content>
<#list stakeholderElicitation.consequences as consequence>
<#if consequence.action?has_content>
${valueRegister.name},${valueRegisterContext},${valueCluster.name},${valueClusterCoreValue},${valueClusterDemonstrators},${valueClusterRelatedValues},${valueClusterOpposingValues},,,,,,${stakeholderElicitation.stakeholder.name},${stakeholderElicitation.priority!""},${stakeholderElicitation.impact!""},${consequence.type!""},${consequence.consequence},${consequence.action.type!""},${consequence.action.action!""}
<#else>
${valueRegister.name},${valueRegisterContext},${valueCluster.name},${valueClusterCoreValue},${valueClusterDemonstrators},${valueClusterRelatedValues},${valueClusterOpposingValues},,,,,,${stakeholderElicitation.stakeholder.name},${stakeholderElicitation.priority!""},${stakeholderElicitation.impact!""},${consequence.type!""},${consequence.consequence},,
</#if>
</#list>
<#else>
${valueRegister.name},${valueRegisterContext},${valueCluster.name},${valueClusterCoreValue},${valueClusterDemonstrators},${valueClusterRelatedValues},${valueClusterOpposingValues},,,,,,${stakeholderElicitation.stakeholder.name},${stakeholderElicitation.priority!""},${stakeholderElicitation.impact!""},,,,
</#if>
</#list>
<#else>
${valueRegister.name},${valueRegisterContext},${valueCluster.name},${valueClusterCoreValue},${valueClusterDemonstrators},${valueClusterRelatedValues},${valueClusterOpposingValues},,,,,,,,,,,,,
</#if>
<#list valueCluster.values as value>
<#if value.demonstrators?has_content><#assign valueDemonstrators = value.demonstrators?join("; ")><#else><#assign valueDemonstrators = ""></#if>
<#if value.relatedValues?has_content><#assign valueRelatedValues = value.relatedValues?join("; ")><#else><#assign valueRelatedValues = ""></#if>
<#if value.opposingValues?has_content><#assign valueOpposingValues = value.opposingValues?join("; ")><#else><#assign valueOpposingValues = ""></#if>
<#if value.elicitations?has_content>
<#list value.elicitations as stakeholderElicitation>
<#if stakeholderElicitation.consequences?has_content>
<#list stakeholderElicitation.consequences as consequence>
<#if consequence.action?has_content>
${valueRegister.name},${valueRegisterContext},${valueCluster.name},${valueClusterCoreValue},${valueClusterDemonstrators},${valueClusterRelatedValues},${valueClusterOpposingValues},${value.name},${value.coreValue?string("yes", "no")},${valueDemonstrators},${valueRelatedValues},${valueOpposingValues},${stakeholderElicitation.stakeholder.name},${stakeholderElicitation.priority!""},${stakeholderElicitation.impact!""},${consequence.type!""},${consequence.consequence},${consequence.action.type!""},${consequence.action.action!""}
<#else>
${valueRegister.name},${valueRegisterContext},${valueCluster.name},${valueClusterCoreValue},${valueClusterDemonstrators},${valueClusterRelatedValues},${valueClusterOpposingValues},${value.name},${value.coreValue?string("yes", "no")},${valueDemonstrators},${valueRelatedValues},${valueOpposingValues},${stakeholderElicitation.stakeholder.name},${stakeholderElicitation.priority!""},${stakeholderElicitation.impact!""},${consequence.type!""},${consequence.consequence},,
</#if>
</#list>
<#else>
${valueRegister.name},${valueRegisterContext},${valueCluster.name},${valueClusterCoreValue},${valueClusterDemonstrators},${valueClusterRelatedValues},${valueClusterOpposingValues},${value.name},${value.coreValue?string("yes", "no")},${valueDemonstrators},${valueRelatedValues},${valueOpposingValues},${stakeholderElicitation.stakeholder.name},${stakeholderElicitation.priority!""},${stakeholderElicitation.impact!""},,,,
</#if>
</#list>
<#else>
${valueRegister.name},${valueRegisterContext},${valueCluster.name},${valueClusterCoreValue},${valueClusterDemonstrators},${valueClusterRelatedValues},${valueClusterOpposingValues},${value.name},${value.coreValue?string("yes", "no")},${valueDemonstrators},${valueRelatedValues},${valueOpposingValues},,,,,,,,
</#if>
</#list>
</#list>
<#list valueRegister.values as value>
<#if value.demonstrators?has_content><#assign valueDemonstrators = value.demonstrators?join("; ")><#else><#assign valueDemonstrators = ""></#if>
<#if value.relatedValues?has_content><#assign valueRelatedValues = value.relatedValues?join("; ")><#else><#assign valueRelatedValues = ""></#if>
<#if value.opposingValues?has_content><#assign valueOpposingValues = value.opposingValues?join("; ")><#else><#assign valueOpposingValues = ""></#if>
<#if value.elicitations?has_content>
<#list value.elicitations as stakeholderElicitation>
<#if stakeholderElicitation.consequences?has_content>
<#list stakeholderElicitation.consequences as consequence>
<#if consequence.action?has_content>
${valueRegister.name},${valueRegisterContext},,,,,,${value.name},${value.coreValue?string("yes", "no")},${valueDemonstrators},${valueRelatedValues},${valueOpposingValues},${stakeholderElicitation.stakeholder.name},${stakeholderElicitation.priority!""},${stakeholderElicitation.impact!""},${consequence.type!""},${consequence.consequence},${consequence.action.type!""},${consequence.action.action!""}
<#else>
${valueRegister.name},${valueRegisterContext},,,,,,${value.name},${value.coreValue?string("yes", "no")},${valueDemonstrators},${valueRelatedValues},${valueOpposingValues},${stakeholderElicitation.stakeholder.name},${stakeholderElicitation.priority!""},${stakeholderElicitation.impact!""},${consequence.type!""},${consequence.consequence},,
</#if>
</#list>
<#else>
${valueRegister.name},${valueRegisterContext},,,,,,${value.name},${value.coreValue?string("yes", "no")},${valueDemonstrators},${valueRelatedValues},${valueOpposingValues},${stakeholderElicitation.stakeholder.name},${stakeholderElicitation.priority!""},${stakeholderElicitation.impact!""},,,,
</#if>
</#list>
<#else>
${valueRegister.name},${valueRegisterContext},,,,,,${value.name},${value.coreValue?string("yes", "no")},${valueDemonstrators},${valueRelatedValues},${valueOpposingValues},,,,,,,,
</#if>
</#list>
</#list>
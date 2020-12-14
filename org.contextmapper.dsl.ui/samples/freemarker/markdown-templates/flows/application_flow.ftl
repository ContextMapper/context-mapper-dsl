<#--
 Makro that prints out an application flow.
 -->
<#macro renderApplicationFlow flow>
#### Application/Processing Layer Flow: ${flow.name}
<#if flow.steps?has_content>
Steps:

	<#list flow.steps as step>
		<#if instanceOf(step, DomainEventProductionStep)>
			<#if instanceOf(step.eventProduction, MultipleEventProduction)>
				<#assign sym = " + ">
			<#elseif instanceOf(step.eventProduction, ExclusiveAlternativeEventProduction)>
				<#assign sym = " X ">
			<#elseif instanceOf(step.eventProduction, InclusiveAlternativeEventProduction)>
				<#assign sym = " O ">
			<#else>
				<#assign sym = "">
			</#if>
			* <#if step.action.command?has_content>command ${step.action.command.name}<#else>operation ${step.action.operation.name}</#if><#if step.aggregate?has_content> delegates to ${step.aggregate.name}<#if step.stateTransition?has_content>[<@stateTransitionMacro.renderStateTransition step.stateTransition />]</#if></#if> emits event ${step.eventProduction.events?map(e -> e.name)?join(sym)}<#lt>
		<#else>
			<#if instanceOf(step.action, ConcurrentCommandInvokation)>
				<#assign sym = " + ">
			<#elseif instanceOf(step.action, ConcurrentOperationInvokation)>
				<#assign sym = " + ">
			<#elseif instanceOf(step.action, ExclusiveAlternativeCommandInvokation)>
				<#assign sym = " X ">
			<#elseif instanceOf(step.action, ExclusiveAlternativeOperationInvokation)>
				<#assign sym = " X ">
			<#elseif instanceOf(step.action, InclusiveAlternativeCommandInvokation)>
				<#assign sym = " O ">
			<#elseif instanceOf(step.action, InclusiveAlternativeOperationInvokation)>
				<#assign sym = " O ">
			<#else>
				<#assign sym = "">
			</#if>
			* event ${step.events?map(e -> e.name)?join(" + ")} triggers <#if instanceOf(step.action, OperationInvokation)>operation<#else>command</#if> ${step.action.commands?map(c -> c.name)?join(sym)}<#lt>
		</#if>
	</#list>

<#else>
_Note: no steps have been defined for this flow._

</#if>
</#macro>

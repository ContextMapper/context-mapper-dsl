<#--
 Makro that prints out a state transition.
 -->
<#macro renderStateTransition transition>
<#if instanceOf(transition.target, ExclusiveAlternativeStateTransitionTarget)>
	<#assign sym = " X ">
<#else>
	<#assign sym = "">
</#if>
${transition.from?map(f -> f.name)?join(", ")} -> ${transition.target.to?map(t -> t.value.name)?join(sym)}</#macro>

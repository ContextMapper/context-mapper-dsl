<#if !model.hasMultipleActors()>
${model.defaultActorName}:

</#if>
<#list model.sequences as sequence>
<#if sequence.isMergingFragment()>
...
</#if>
<#list sequence.tasks as task>
<#if task.comment?has_content>
<#if task.type.name() == "COMMAND"><#if model.hasMultipleActors()><#if task.actor?has_content>${task.actor}<#else>${model.defaultActorName}</#if>: </#if></#if>// ${task.comment}
</#if>
<#if task.type.name() == "COMMAND"><#if model.hasMultipleActors()><#if task.actor?has_content>${task.actor}<#else>${model.defaultActorName}</#if>: </#if></#if><#if task.type.name() == "COMMAND">service ${task.name?replace("_", "")}<#else>(${task.name?replace("_", "")})</#if><#if task.parallelTasks?has_content><#list task.parallelTasks as parTask>|<#if parTask.type.name() == "COMMAND">service ${parTask.name?replace("_", "")}<#else>(${parTask.name?replace("_", "")})</#if></#list></#if>
</#list>
<#if sequence.isSplittingFragment()>
...
</#if>

</#list>
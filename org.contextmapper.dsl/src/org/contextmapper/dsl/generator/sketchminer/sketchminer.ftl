<#if !model.hasMultipleActors()>
${model.defaultActorName}:

</#if>
<#list model.sequences as sequence>
<#list sequence.tasks as task>
<#if task.comment?has_content>
<#if model.hasMultipleActors()><#if task.actor?has_content>${task.actor}<#else>${model.defaultActorName}</#if>: </#if>// ${task.comment}
</#if>
<#if model.hasMultipleActors()><#if task.actor?has_content>${task.actor}<#else>${model.defaultActorName}</#if>: </#if><#if task.type.name() == "COMMAND">service </#if>${task.name}<#if task.parallelTasks?has_content><#list task.parallelTasks as parTask>|<#if parTask.type.name() == "COMMAND">service </#if>${parTask.name}</#list></#if>
</#list>

</#list>
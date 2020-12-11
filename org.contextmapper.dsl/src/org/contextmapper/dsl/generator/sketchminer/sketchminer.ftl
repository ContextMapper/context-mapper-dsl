${model.defaultActorName}:

<#list model.sequences as sequence>
<#list sequence.tasks as task>
<#if task.comment?has_content>
// ${task.comment}
</#if>
<#if task.type.name() == "COMMAND">service </#if>${task.name}<#if task.parallelTasks?has_content><#list task.parallelTasks as parTask>|<#if parTask.type.name() == "COMMAND">service </#if>${parTask.name}</#list></#if>
</#list>

</#list>
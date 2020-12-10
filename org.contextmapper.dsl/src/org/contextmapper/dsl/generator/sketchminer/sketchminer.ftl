<#list model.sequences as sequence>
<#list sequence.tasks as task>
${task.toString()}
</#list>

</#list>
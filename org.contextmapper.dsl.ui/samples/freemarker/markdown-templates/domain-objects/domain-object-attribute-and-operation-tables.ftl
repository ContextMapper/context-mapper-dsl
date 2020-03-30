<#--
 Makro that takes a SimpleDomainObject and lists all operations and attributes in two tables.
 -->
<#macro renderDomainObjectOperationsAndAttributes simpleDomainObject>
    <#if simpleDomainObject.operations?has_content>
        Operations:<#lt>
        | Operation | Doc string | Parameters | Return Type |<#lt>
        |-|-|-|-|<#lt>
        <#list simpleDomainObject.operations as op>
            | ${op.name} | ${op.doc!"*no doc string*"} | <#if op.parameters?has_content> <#list op.parameters as p> ${p.name} of type ${getType(p.parameterType)} </#list> <#else> none </#if> | <#if op.returnType?has_content>${getType(op.returnType)}<#else>none</#if> |<#lt>
        </#list>
    </#if>

    <#if simpleDomainObject.attributes?has_content>
        Attributes:<#lt>
        | Attribute | Doc/doc string | Visibility | Type |<#lt>
        |-|-|-|-|<#lt>
        <#list simpleDomainObject.attributes as attr>
            | ${attr.name} | ${attr.doc!"*no doc string*"} | ${attr.visibility} | ${attr.type} |<#lt>
        </#list>
    </#if>
</#macro>

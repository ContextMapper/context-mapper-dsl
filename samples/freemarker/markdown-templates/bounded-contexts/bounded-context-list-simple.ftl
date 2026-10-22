The following [bounded context](https://martinfowler.com/bliki/BoundedContext.html)s are defined:<#lt>

<#list filterStructuralBoundedContexts(boundedContexts) as bc>
    * *${bc.name}*: ${bc.domainVisionStatement!"[domain vision of the context not defined]"}<#lt>
</#list>
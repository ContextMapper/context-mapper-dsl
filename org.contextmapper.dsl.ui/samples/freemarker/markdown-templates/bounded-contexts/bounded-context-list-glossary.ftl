<#list filterStructuralBoundedContexts(boundedContexts) as bc>
    ### Bounded Context ${bc.name}<#lt>

    *${bc.name}*. ${bc.domainVisionStatement!"[domain vision of the context not defined]"}<#lt>

    <#list bc.aggregates as agg>

        Aggregate ${agg.name}. <#if agg.responsibilities?has_content> The responsibilities of ${agg.name} are:<#lt> 
            <#list agg.responsibilities as r>
                * ${r}<#lt>
            </#list>
        </#if>

        <#assign entities = agg.domainObjects?filter(do -> instanceOf(do, Entity))>
        <#if entities?has_content>
            Entities:<#lt>

            <#list entities as dob>
                * ${dob.name}. ${dob.hint!"[hint missing]"}<#lt>
            </#list>
        </#if>

        <#if agg.services?has_content>
            Services:<#lt>
            
            <#list agg.services as s>
                * ${s.name}<#lt>. ${s.hint!"[hint missing]"}<#lt>
            </#list>
        </#if>

        <#assign valueObjects = agg.domainObjects?filter(do -> instanceOf(do, ValueObject))>
        <#if valueObjects?has_content>
            Value Objects: <#lt>

            <#list valueObjects as dob>
                * ${dob.name}<#lt>. ${dob.hint!"[hint missing]"}<#lt>
            </#list>
        </#if>
        
        <#assign events = agg.domainObjects?filter(do -> instanceOf(do, DomainEvent) || instanceOf(do, CommandEvent))>
        <#if events?has_content>
            Events (Command Events, Domain Events):<#lt>
            <#list events as dob>
                <#if instanceOf(dob, DomainEvent)>
                    * ${dob.name} (DomainEvent). ${dob.hint!"[hint missing]"}<#lt>
                <#elseif instanceOf(dob, CommandEvent)>
                    * ${dob.name} (CommandEvent). ${dob.hint!"[hint missing]"}<#lt>
                </#if>
            </#list>
        </#if>
    </#list>
</#list>

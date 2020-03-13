<#list filterStructuralBoundedContexts(boundedContexts) as bc>
    ## Bounded Context ${bc.name}<#lt>

    *${bc.name}* has this domain vision statement: ${bc.domainVisionStatement!"[domain vision of the context not defined]"}<#lt>

    A number of aggregates are part of the ubiquituous language of this Bounded Context. The following paragraphs describe them, as well as their inner structures.<#lt>

    <#list bc.aggregates as agg>
        ### Aggregate ${agg.name}<#lt>
        This aggregate can be classified as ${agg.doc!"[no doc string]"}. Its responsibilities are:<#lt>

        <#if useCases?has_content>
            <#list agg.responsibilities as r>
                * ${r}<#lt>
            </#list>
        <#else>
            * [no (more) responsibilities defined]<#lt>
        </#if>

        <#if agg.domainObjects?has_content>
            #### Entities (in Aggregate)<#lt>
            <#list agg.domainObjects as dob>
                <#if instanceOf(dob, Entity)>
                    ##### ${dob.name}<#lt>
                    ${dob.hint!"[hint missing]"}<#lt>

                    <@attrOpsMacro.renderDomainObjectOperationsAndAttributes dob />
                </#if>
            </#list>
        </#if>

        <#if agg.services?has_content>
            #### Services<#lt>
            <#list agg.services as s>
                ##### ${s.name}<#lt>
                ${s.hint!"[hint missing]"}<#lt>
            </#list>
        </#if>

        #### Value Objects<#lt>
        <#if agg.domainObjects?has_content>
            <#list agg.domainObjects as dob>
                <#if instanceOf(dob, ValueObject)>
                    ##### ${dob.name}<#lt>
                    ${dob.hint!"[hint missing]"}<#lt>

                    <@attrOpsMacro.renderDomainObjectOperationsAndAttributes dob />
                </#if>
            </#list>
        </#if>

        #### Enumerations (Enums)<#lt>
        <#list agg.domainObjects as dob>
            <#if instanceOf(dob, Enum)>
                ##### ${dob.name}<#lt>
                ${dob.hint!"[hint missing]"}<#lt>

                Values:<#lt>
                <#list dob.values as value>
                    * ${value.name}<#lt>
                </#list>
            </#if>

        </#list>

        <#if agg.domainObjects?has_content>
            #### Events (Command Events, Domain Events)<#lt>
            <#list agg.domainObjects as dob>
                <#if instanceOf(dob, Event)>
                    <#if instanceOf(dob, DomainEvent)>
                        ##### ${dob.name} (DomainEvent)<#lt>
                    <#elseif instanceOf(dob, CommandEvent)>
                        ##### ${dob.name} (CommandEvent)<#lt>
                    </#if>
                    ${dob.hint!"[hint missing]"}<#lt>

                    <@attrOpsMacro.renderDomainObjectOperationsAndAttributes dob />
                </#if>
            </#list>
        </#if>

    </#list>
</#list>

<#list filterStructuralBoundedContexts(boundedContexts) as bc>
    ## Bounded Context ${bc.name}<#lt>

    *${bc.name}* has this domain vision statement: ${bc.domainVisionStatement!"[domain vision of the context not defined]"}<#lt>

    <#list bc.aggregates as agg>
        ### Aggregate ${agg.name}<#lt>
        This aggregate can be classified as ${agg.doc!"[no doc string]"}. Its responsibilities are:<#lt>

        <#if agg?has_content>
            <#list agg.responsibilities as r>
                * ${r}<#lt>
            </#list>
        <#else>
            * [no (more) responsibilities defined]<#lt>
        </#if>

        <#assign entities = agg.domainObjects?filter(do -> instanceOf(do, Entity))>
        <#if entities?has_content>
            #### Entities (in Aggregate)<#lt>
            <#list entities as dob>
                ##### ${dob.name}<#lt>
                ${dob.hint!"[hint missing]"}<#lt>

                <@attrOpsMacro.renderDomainObjectOperationsAndAttributes dob />
            </#list>
        </#if>

        <#if agg.services?has_content>
            #### Services<#lt>
            <#list agg.services as s>
                ##### ${s.name}<#lt>
                ${s.hint!"[hint missing]"}<#lt>
            </#list>
        </#if>

        <#assign valueObjects = agg.domainObjects?filter(do -> instanceOf(do, ValueObject))>
        <#if valueObjects?has_content>
            #### Value Objects<#lt>
            <#list valueObjects as dob>
                ##### ${dob.name}<#lt>
                ${dob.hint!"[hint missing]"}<#lt>

                <@attrOpsMacro.renderDomainObjectOperationsAndAttributes dob />
            </#list>
        </#if>

        <#assign enums = agg.domainObjects?filter(do -> instanceOf(do, Enum))>
        <#if enums?has_content>
            #### Enumerations (Enums)<#lt>
            <#list enums as dob>
                ##### ${dob.name}<#lt>
                ${dob.hint!"[hint missing]"}<#lt>

                Values:<#lt>
                <#list dob.values as value>
                    * ${value.name}<#lt>
                </#list>
            </#list>
        </#if>

        <#assign events = agg.domainObjects?filter(do -> instanceOf(do, DomainEvent) || instanceOf(do, CommandEvent))>
        <#if events?has_content>
            #### Events (Command Events, Domain Events)<#lt>
            <#list events as dob>
                <#if instanceOf(dob, DomainEvent)>
                    ##### ${dob.name} (DomainEvent)<#lt>
                <#elseif instanceOf(dob, CommandEvent)>
                    ##### ${dob.name} (CommandEvent)<#lt>
                </#if>
                ${dob.hint!"[hint missing]"}<#lt>

                <@attrOpsMacro.renderDomainObjectOperationsAndAttributes dob />
            </#list>
        </#if>

    </#list>
    
    <#if bc.application?has_content>
    #### Application Layer<#lt>
    	<#if bc.application.services?has_content>
            Services:<#lt>
            
            <#list bc.application.services as s>
                * ${s.name}<#lt>. ${s.hint!"[hint missing]"}<#lt>
            </#list>
        </#if>
        
        <#if bc.application.events?has_content>
            Events:<#lt>
            <#list bc.application.events as event>
            	* ${event.name}. ${event.hint!"[hint missing]"}<#lt>
            </#list>
        </#if>
        
        <#if bc.application.commands?has_content>
            Commands:<#lt>
            <#list bc.application.commands as command>
            	* ${command.name}. ${command.hint!"[hint missing]"}<#lt>
            </#list>
        </#if>
    
    <#list bc.application.flows as flow>
	    <@appFlowMacro.renderApplicationFlow flow />
    </#list>
    </#if>
</#list>

<#import "/domain-objects/domain-object-attribute-and-operation-tables.ftl" as attrOpsMacro>

<#if domains?has_content>
    ### Domain Report<#lt>

    <#list domains as d>
        Domain `${d.name}` has the  following domain vision statement:<#lt>
        ${d.domainVisionStatement!"[*TODO create domain vision statement for ${d.name}*]"}<#lt>
    
    </#list>

    <#list domains as d>
        <#if d.subdomains?has_content>
            ### Subdomains (in domain "${d.name}")<#lt>
            ${d.name} contains the following subdomains:<#lt>
            | Name | Type | Domain Vision Statement |<#lt>
            | -----| -----|-------------------------|<#lt>
            <#list d.subdomains as sd>
                | *${sd.name}* | ${sd.type} | ${sd.domainVisionStatement!"*[TODO add (sub-)domain vision statement for ${sd.name}]*"} |<#lt>
            </#list>

            ### Entities<#lt>
            <#list d.subdomains as sd>
                <#list sd.entities as e>
                    <#if e?has_content>
                        #### ${e.name}<#lt>
                        Entity in subdomain ${sd.name} (in domain ${d.name}).<#lt>
                        <#if e.hint?has_content>
                            Hint: ${e.hint!"*no hint given*"}<#lt>
                        </#if>

                        <@attrOpsMacro.renderDomainObjectOperationsAndAttributes e />

                    </#if>
                </#list>
            </#list>
        </#if>
    </#list>

    <#if useCases?has_content>
        ### Use Cases<#lt>
        <#list useCases as uc>
            * ${uc.name}<#lt>
        </#list>
    </#if>
</#if>

<#import "/domain-objects/domain-object-attribute-and-operation-tables.ftl" as attrOpsMacro>

<#if domains?has_content>
    ### Domain Glossary<#lt>

    <#list domains as d>
        Domain *${d.name}*. ${d.domainVisionStatement!"[*TODO create domain vision statement for ${d.name}*]"}<#lt>

    </#list>

    <#list domains as d>
        <#if d.subdomains?has_content>
            Subdomains (in domain "${d.name}"):<#lt>
            
            <#list d.subdomains as sd>
                * *${sd.name}*. ${sd.domainVisionStatement!"*[TODO add (sub-)domain vision statement for ${sd.name}]*"} <#lt>
            </#list>

            <#list d.subdomains as sd>
                <#if sd?has_content>
                    <#list sd.entities as e>
                        <#if e?has_content>
                            *${e.name}* is an entity in the subdomain ${sd.name} (in domain ${d.name}). ${e.hint!"*no hint given*"}<#lt>
                        
                        </#if>
                    </#list>
                 </#if>

            </#list>
        </#if>
    </#list>
    <#-- use cases taken out of glossary -->
</#if>
